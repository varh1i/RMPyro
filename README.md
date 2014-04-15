This project shows how RapidMiner can communicate with python objects that wraps scikit-learn data modelling classes via Pyro


1. Install Pyro4

	Pyro project documentation: 

	- https://github.com/irmen/Pyro4
 	- http://pythonhosted.org/Pyro4/intro.html

2. Start nameserver: "python -m Pyro4.naming"

                balazs@balazs-VirtualBox:~$ python -m Pyro4.naming
                Not starting broadcast server for localhost.
                NS running on localhost:9090 (127.0.0.1)
                URI = PYRO:Pyro.NameServer@localhost:9090

3. (Optional) Checking the nameserver: "python -m Pyro4.nsc -p 9090 list"

                balazs@balazs-VirtualBox:~$ python -m Pyro4.nsc list
                --------START LIST
                Pyro.NameServer --> PYRO:Pyro.NameServer@localhost:9090
                --------END LIST


4. Start pyhon server (./test/pyro/server.py):

                balazs@balazs-VirtualBox:~$ python server.py
                Server ready, waiting for requests...

5. (Optional) Checking registered object:

                balazs@balazs-VirtualBox:~$ python -m Pyro4.nsc list
                --------START LIST
                Pyro.NameServer --> PYRO:Pyro.NameServer@localhost:9090
                myObject --> PYRO:obj_ba9ba337053443e091d264e881588bde@localhost:51300
                --------END LIST

