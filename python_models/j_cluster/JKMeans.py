__author__ = 'varh1i'

from sklearn import cluster
from j_cluster.JBaseModel import JBaseModel
from sklearn import metrics


class JKMeans(JBaseModel):

    def __init__(self):
        self.model = None
        self.name = "KMeans"

    def create(self, n_clusters, init, n_init, max_iter,
               tol, precompute_distances, random_state, n_jobs):
        print "Custom KMeans object created"
        self.model = cluster.KMeans(n_clusters, init, n_init, max_iter, tol, precompute_distances, 0, random_state, True, n_jobs)

    def homogeneity(self, labels):
        return float(metrics.homogeneity_score(labels, self.model.labels_))

    def completeness(self, labels):
        return float(metrics.completeness_score(labels, self.model.labels_))

    def v_measure(self, labels):
        return float(metrics.v_measure_score(labels, self.model.labels_))

    def adjusted_rand(self, labels):
        return float(metrics.adjusted_rand_score(labels, self.model.labels_))

    def adjusted_mutual_info(self, labels):
        return float(metrics.adjusted_mutual_info_score(labels, self.model.labels_))
