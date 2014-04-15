__author__ = 'varh1i'

from sklearn import cluster
import pickle
import numpy


class JBaseModel():

    def __init__(self):
        self.model = None
        self.name = "BaseLinearModel"
        self.data = None

    def fit(self,X):
        self.data = X
        self.model.fit(numpy.asmatrix(X))

    def predict(self,X):
        print "predict called"
        print X
        print "numpy matrix"
        print numpy.asmatrix(X)
        self.model.predicted = self.model.fit_predict(numpy.asmatrix(X))
        print self.model.predicted

    def predicted(self):
        print "predicted"
        print self.model.predicted
        print "reshape"
        print self.model.predicted.reshape(-1,).tolist()
        return self.model.predicted.reshape(-1,).tolist()

    def pickle_out(self, path):
        file_pi = open(path, 'wb')
        pickle.dump(self.model,file_pi)
        file_pi.close()

    def pickle_in(self, path):
        print 'pickle in lr'
        file_pi = open(path, 'rb')
        self.model = pickle.load(file_pi)
        file_pi.close()

    def hello(self):
        return self.name

    def score(self,X):
        return float(self.model.score(X))
