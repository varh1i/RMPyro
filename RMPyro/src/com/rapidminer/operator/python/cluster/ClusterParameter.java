package com.rapidminer.operator.python.cluster;

enum ClusterParameter {

	N_CLUSTERS("n_clusters"),
	INIT("init"), 
	N_INIT("n_init"), 
	MAX_ITER("max_iter"), 
	TOL("tol"), 
	PRECOMPUTE_DISTANCES("precompute_distances"), 
	RANDOM_STATE("random_state"),
	N_JOBS("n_jobs");
	
	private String name;
	private ClusterParameter(String name){
		this.name=name;
	}
	@Override
	public String toString(){
		return this.name;
	}
	
}
