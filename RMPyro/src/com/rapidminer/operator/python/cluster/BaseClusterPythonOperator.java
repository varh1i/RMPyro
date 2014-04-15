package com.rapidminer.operator.python.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.razorvine.pickle.PickleException;
import net.razorvine.pyro.NameServerProxy;
import net.razorvine.pyro.PyroException;
import net.razorvine.pyro.PyroProxy;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.python.util.TransformDataUtil;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.UndefinedParameterError;

public abstract class BaseClusterPythonOperator extends Operator {

	
	protected enum BaseMethod {
		
		HELLO("hello"),
		CREATE("create"), 
		FIT("fit"), 
		PREDICT("predict"), 
		SCORE("score"),
		PREDICTED("predicted"), 
		PICK_OUT("pickle_out"), 
		PICK_IN("pickle_in");
		
		private String methodName;
		
		private BaseMethod(String methodName){
			this.methodName = methodName;
		}
		
		@Override
		public String toString(){
			return this.methodName;
		}
	}
	
	protected enum PickleMode{

		NONE("none"),
		SAVE("save"),
		LOAD("load");
		
		private String label;
		private PickleMode(String label){
			this.label = label;
		}
		
		public String getLabel(){
			return this.label;
		}
	}
	
	protected enum BaseParameter{
		
		PYTHON_MODEL_FILE("python_model_file"),
		PICKLE_MODE("using_pickle"),
		ALPHA("alpha"),
		SCORE("score"),
		COEFFICIENT("coefficient"),
		INTERCEPT("intercept");
		
		private String name;
		private BaseParameter(String name){
			this.name=name;
		}
		@Override
		public String toString(){
			return this.name;
		}
	}
	
	private static final Logger logger = Logger.getLogger(BaseClusterPythonOperator.class.getName());
	private PyroProxy model;
	protected InputPort featuresTrainPort = getInputPorts().createPort("X-train-input");
	protected InputPort targetTruePort = getInputPorts().createPort("y-true");
	protected double[][] featuresTrain;
	protected double[] targetTrue; 
	protected OutputPort predictionOutput = getOutputPorts().createPort("prediction");
	protected OutputPort summaryOutput = getOutputPorts().createPort("summary");
	protected ArrayList<Integer> prediction;
	protected double scoreValue;
	
	
	private static final String URL = "localhost";
	private static final int port = 9090;
	protected static NameServerProxy ns = null;
	static {
		
		try {
			ns = NameServerProxy.locateNS(URL, port);
		} catch (IOException e) {
			throw new IllegalStateException("NameServer cannot be found!");
		}
		
	}
		
			
	public BaseClusterPythonOperator(OperatorDescription description) {
		super(description);
	}

	protected void input() throws UndefinedParameterError, PickleException, PyroException, IOException{
		
		logger.info("Read input.");
		
		
		ExampleSet featuresTrainES = null;
		ExampleSet targetTrueES = null;
		try {
			
			featuresTrainES = featuresTrainPort.getDataOrNull(ExampleSet.class);
			targetTrueES = targetTruePort.getDataOrNull(ExampleSet.class);
			//predictPort.getData(ExampleSet.class);
			
		} catch (UserError e1) {

			e1.printStackTrace();
			
		}
		if(getParameterAsString(BaseParameter.PICKLE_MODE.toString()).equals(PickleMode.LOAD.getLabel())){
			String filePath = getParameterAsString(BaseParameter.PYTHON_MODEL_FILE.toString());
			model.call(BaseMethod.PICK_IN.toString(), filePath);
		}else{
			featuresTrain = TransformDataUtil.getXInput(featuresTrainES);
		}
		
		if(targetTrueES != null){
			targetTrue = TransformDataUtil.getYInput(targetTrueES);
		}
		
	}
	
	protected void outputPrediction(){
		if(prediction != null){
			predictionOutput.deliver(TransformDataUtil.getClusterPredictionExampleSet(prediction));
		}
	}
	
	protected void outputModel() throws UndefinedParameterError, PickleException, PyroException, IOException{
		if(getParameterAsString(BaseParameter.PICKLE_MODE.toString()).equals(PickleMode.SAVE.getLabel())){
			String filePath = getParameterAsString(BaseParameter.PYTHON_MODEL_FILE.toString());
			model.call(BaseMethod.PICK_OUT.toString(), filePath);
		}
	}
	
	@Override
	public List<ParameterType> getParameterTypes() {

		List<ParameterType> types = super.getParameterTypes();
		types.add(new ParameterTypeFile(BaseParameter.PYTHON_MODEL_FILE.toString(), "Filename of the object file.", "p", true));
		types.add(new ParameterTypeCategory(BaseParameter.PICKLE_MODE.toString(), "Using or saving a model object.", new String[]{PickleMode.NONE.label, PickleMode.SAVE.label,PickleMode.LOAD.label}, 0));
		return types;
		
	}
	
	protected void reset(){
		featuresTrain=null;
		targetTrue=null;
		prediction=null;
		scoreValue=0.0;
	}
	
}
