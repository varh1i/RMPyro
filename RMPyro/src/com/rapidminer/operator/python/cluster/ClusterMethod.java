package com.rapidminer.operator.python.cluster;

enum ClusterMethod {

	HOMOGENEITY("homogeneity"),
	COMPLETENESS("completeness"),
	V_MEASURE("v_measure"),
	ADJUSTED_RAND("adjusted_rand"),
	ADJUSTED_MUTUAL_INFO("adjusted_mutual_info");
	
	private String name;
	private ClusterMethod(String name){
		this.name=name;
	}
	@Override
	public String toString(){
		return this.name;
	}
}
