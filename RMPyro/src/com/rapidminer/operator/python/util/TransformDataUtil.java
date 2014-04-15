package com.rapidminer.operator.python.util;

import java.util.ArrayList;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DataRow;
import com.rapidminer.example.table.DataRowFactory;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.tools.Ontology;

public class TransformDataUtil {

	
	public static double[][] getXInput(ExampleSet xInput){
		
		if(xInput == null){
			throw new IllegalArgumentException("Input shouldn't be null");
		}
		int itemsSize = xInput.size();
		int featuresSize = xInput.getAttributes().allSize();
		
		
		double[][] X = new double[itemsSize][featuresSize];
		for(int i=0; i<xInput.size();i++){
			
			int j=0;
			for(Attribute attribute : xInput.getAttributes()){
				Example example = xInput.getExample(i);
				X[i][j] = example.getValue(attribute);
				j++;
			}
		}
		return X;
	}
	
	public static double[] getYInput(ExampleSet yInput){
		int numberOfY = yInput.size();
		double[] y = new double[numberOfY];
		Attribute target = yInput.getAttributes().get("target");
		for(int i=0; i<yInput.size();i++){
			Example example = yInput.getExample(i);
			
			y[i] = example.getValue(target);
		}
		
		return y;
	
	}
	
	public static ExampleSet getPredictionExampleSet(ArrayList<Double> prediction){
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute predictionAttribute = AttributeFactory.createAttribute("prediction", Ontology.NUMERICAL);
		attributes.add(predictionAttribute);
		MemoryExampleTable table = new MemoryExampleTable(attributes);
		
		DataRowFactory factory = new DataRowFactory(DataRowFactory.TYPE_DOUBLE_ARRAY, '.');
		for (int i = 0; i < prediction.size(); i++) {
		
			DataRow dataRow = factory.create(1);
			dataRow.set(attributes.get(0), prediction.get(i));
			table.addDataRow(dataRow);
		
		}
		
		return table.createExampleSet();
	
		
	}
	
	public static ExampleSet getSummary(List<String> labels, List<List<Double>> values){
		List<Attribute> attributes = new ArrayList<Attribute>();
		for(String label : labels){
			attributes.add(AttributeFactory.createAttribute(label, Ontology.NUMERICAL));
		}
		MemoryExampleTable table = new MemoryExampleTable(attributes);
		DataRowFactory factory = new DataRowFactory(DataRowFactory.TYPE_DOUBLE_ARRAY, '.');
		factory.create(labels.size());
		int tableHeight = getColumnHeight(values);
		for(int i=0; i < tableHeight; i++){
			
			DataRow row = factory.create(labels.size());
			int j = 0;
			for(Attribute attribute : attributes){
				double value = values.get(j).size() > i ? values.get(j).get(i) : Double.NaN;
				row.set(attribute, value);
				j++;
			}
			table.addDataRow(row);
			
		}
		return table.createExampleSet();
		
	}
	
	private static int getColumnHeight(List<List<Double>> values){
		int max = 0;
		for(int i=0; i<values.size(); i++){
			if(values.get(i).size() > max){
				max = values.get(i).size();
			}
		}
		return max;
	}
	
	public static ExampleSet getClusterPredictionExampleSet(ArrayList<Integer> prediction){
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		Attribute predictionAttribute = AttributeFactory.createAttribute("prediction", Ontology.NUMERICAL);
		attributes.add(predictionAttribute);
		MemoryExampleTable table = new MemoryExampleTable(attributes);
		
		DataRowFactory factory = new DataRowFactory(DataRowFactory.TYPE_INT_ARRAY, '.');
		for (int i = 0; i < prediction.size(); i++) {
		
			DataRow dataRow = factory.create(1);
			dataRow.set(attributes.get(0), prediction.get(i));
			table.addDataRow(dataRow);
		
		}
		
		return table.createExampleSet();
	
		
	}
	
}
