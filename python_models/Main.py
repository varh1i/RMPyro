import sklearn

__author__ = 'varh1i'

import Pyro4

from j_cluster.JKMeans import JKMeans


print "Start registering objects..."

daemon = Pyro4.Daemon(port=59455)
ns = Pyro4.locateNS()

jKMeans = JKMeans()
uri = daemon.register(jKMeans)
ns.register(jKMeans.hello(), uri)
print uri


print 'Objects registered'
daemon.requestLoop()
