import Pyro4

class MyObject(object):

	message = "Hello World"

	def displayMessage(self):
		return self.message

myObject = MyObject()
myObject.displayMessage()

daemon = Pyro4.Daemon(port=51300) # make a Pyro daemon
uri = daemon.register(myObject) # register the greeting object as a Pyro object
ns = Pyro4.locateNS(port=9090) # find the name server
ns.register("myObject", uri) # register the object with a name in the name server
print "Server ready, waiting for requests..."
daemon.requestLoop()
print "Server shutdown successfull."
