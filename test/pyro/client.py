import Pyro4

myObject = Pyro4.Proxy('PYRONAME:myObject@localhost:9090')
print myObject.displayMessage()
