package com.rapidminer.operator.python.cluster;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import net.razorvine.pickle.PickleException;
import net.razorvine.pyro.PyroException;
import net.razorvine.pyro.PyroProxy;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.python.util.TransformDataUtil;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.UndefinedParameterError;

public class KMeansOperator extends BaseClusterPythonOperator {

	private static final Logger logger = Logger.getLogger(KMeansOperator.class.getName());
	private String pObject = "KMeans";
	private PyroProxy model;
	protected ArrayList<Integer> prediction;
	private double homogeneity = 0.0;
	private double completeness = 0.0;
	private double vMeasure = 0.0;
	private double adjustedRand=0.0;
	private double adjustedMutualInfo=0.0;
	
	public KMeansOperator(OperatorDescription description) {
		super(description);
		try {
			logger.info("Looking for " + pObject + "registered object");
			model = new PyroProxy(ns.lookup(pObject));
		} catch (UnknownHostException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		} catch (PickleException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		getTransformer().addGenerationRule(predictionOutput, ExampleSet.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doWork() throws OperatorException {
		
		
		try {
			reset();
			input();
			logger.info("Start computation");
			logger.info("Fit model");
			logger.info("CALLING HELLO: " + model.call(BaseMethod.HELLO.toString()));
			String randomState = getParameterAsString(ClusterParameter.RANDOM_STATE.toString());
			Integer randomStateInt = null;
			if(!randomState.equals("None")){
				randomStateInt=Integer.parseInt(randomState);
			}
			
			if(!getParameterAsString(BaseParameter.PICKLE_MODE.toString()).equals(PickleMode.LOAD.getLabel())){
				model.call(BaseMethod.CREATE.toString(), new Object[]{
					getParameterAsInt(ClusterParameter.N_CLUSTERS.toString()),
					getParameterAsString(ClusterParameter.INIT.toString()),
					getParameterAsInt(ClusterParameter.N_INIT.toString()),
					getParameterAsInt(ClusterParameter.MAX_ITER.toString()),
					(getParameterAsDouble(ClusterParameter.TOL.toString())/Math.pow(10, 4)),
					getParameterAsBoolean(ClusterParameter.PRECOMPUTE_DISTANCES.toString()),
					randomStateInt,
					getParameterAsDouble(ClusterParameter.N_JOBS.toString())
				});
			}
	
			logger.info("Use model to predict");
				model.call(BaseMethod.PREDICT.toString(), new Object[]{featuresTrain});
				prediction = (ArrayList<Integer>) model.call(BaseMethod.PREDICTED.toString());
			
			if(targetTrue!=null){
				homogeneity = (Double)model.call(ClusterMethod.HOMOGENEITY.toString(), new Object[]{targetTrue});
				completeness = (Double)model.call(ClusterMethod.COMPLETENESS.toString(), new Object[]{targetTrue});
				vMeasure = (Double)model.call(ClusterMethod.V_MEASURE.toString(), new Object[]{targetTrue});
				adjustedRand = (Double)model.call(ClusterMethod.ADJUSTED_RAND.toString(), new Object[]{targetTrue});
				adjustedMutualInfo = (Double)model.call(ClusterMethod.ADJUSTED_MUTUAL_INFO.toString(), new Object[]{targetTrue});
			}
			
			output();
			
		} catch (PickleException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		} catch (PyroException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		
	}

	protected void output() throws UndefinedParameterError, PickleException, PyroException, IOException {
		
		outputModel();
		outputPrediction();
		
		List<String> labels = new ArrayList<String>(); 
		List<List<Double>> values = new ArrayList<List<Double>>();
		if(targetTrue!=null){
			labels.add(ClusterMethod.HOMOGENEITY.toString());
			labels.add(ClusterMethod.COMPLETENESS.toString());
			labels.add(ClusterMethod.V_MEASURE.toString());
			labels.add(ClusterMethod.ADJUSTED_RAND.toString());
			labels.add(ClusterMethod.ADJUSTED_MUTUAL_INFO.toString());
			values.add(Arrays.asList(homogeneity));
			values.add(Arrays.asList(completeness));
			values.add(Arrays.asList(vMeasure));
			values.add(Arrays.asList(adjustedRand));
			values.add(Arrays.asList(adjustedMutualInfo));
		}
		
		
		if(values.size()>0){
			summaryOutput.deliver(TransformDataUtil.getSummary(labels,values));
		}
		
		
	}

	protected void reset(){
		super.reset();
	}
	
	protected void outputPrediction(){
		if(prediction != null){
			predictionOutput.deliver(TransformDataUtil.getClusterPredictionExampleSet(prediction));
		}
	}
	
	@Override
	public List<ParameterType> getParameterTypes() {
		
		List<ParameterType> types = super.getParameterTypes();
		types.add(new ParameterTypeInt(ClusterParameter.N_CLUSTERS.toString(), "The number of clusters to form as well as the number of centroids to generate.", 0, Integer.MAX_VALUE, 8, true));
		types.add(new ParameterTypeCategory(ClusterParameter.INIT.toString(), "Method for initialization, defaults to ‘k-means++’: \n‘k-means++’ : selects initial cluster centers for k-mean clustering in a smart way to speed up convergence. See section Notes in k_init for more details.\n‘random’: choose k observations (rows) at random from data for the initial centroids.", new String[]{"k-means++", "random"}, 0));
		types.add(new ParameterTypeInt(ClusterParameter.N_INIT.toString(), "Number of time the k-means algorithm will be run with different centroid seeds. The final results will be the best output of n_init consecutive runs in terms of inertia.", 0, Integer.MAX_VALUE, 10, true));
		types.add(new ParameterTypeInt(ClusterParameter.MAX_ITER.toString(), "Maximum number of iterations of the k-means algorithm for a single run", 0, Integer.MAX_VALUE, 300, true));
		types.add(new ParameterTypeDouble(ClusterParameter.TOL.toString(), "Relative tolerance w.r.t. inertia to declare convergence", Double.MIN_VALUE, Double.MAX_VALUE, 1.0, true));
		types.add(new ParameterTypeBoolean(ClusterParameter.PRECOMPUTE_DISTANCES.toString(), "Precompute distances (faster but takes more memory).", true, true));
		types.add(new ParameterTypeString(ClusterParameter.RANDOM_STATE.toString(), "Description", "None", true));
		types.add(new ParameterTypeInt(ClusterParameter.N_JOBS.toString(), "The number of jobs to use for the computation. This works by breaking down the pairwise matrix into n_jobs even slices and computing them in parallel."
				+ "If -1 all CPUs are used. If 1 is given, no parallel computing code is used at all, which is useful for debugging. For n_jobs below -1, (n_cpus + 1 + n_jobs) are used. Thus for n_jobs = -2, all CPUs but one are used.",
				Integer.MIN_VALUE, Integer.MAX_VALUE, 1, true));
		return types;
		
	}

}

