Important references:
Pyro project documentation: https://github.com/irmen/Pyro4, http://pythonhosted.org/Pyro4/intro.html

1. Install pyro4

2. Start nameserver: "python -m Pyro4.naming"
        example output:
        """
                balazs@balazs-VirtualBox:~$ python -m Pyro4.naming
                Not starting broadcast server for localhost.
                NS running on localhost:9090 (127.0.0.1)
                URI = PYRO:Pyro.NameServer@localhost:9090
        """

3. Checking the nameserver: "python -m Pyro4.nsc -p 9090 list"
        example output:
        """
                balazs@balazs-VirtualBox:~$ python -m Pyro4.nsc list
                --------START LIST
                Pyro.NameServer --> PYRO:Pyro.NameServer@localhost:9090
                --------END LIST
        """


4. Start pyhon server (./test/pyro/server.py):
        example output:
        """
                balazs@balazs-VirtualBox:~$ python server.py
                Server ready, waiting for requests...
        """

5. Checking registered object:
        """
                balazs@balazs-VirtualBox:~$ python -m Pyro4.nsc list
                --------START LIST
                Pyro.NameServer --> PYRO:Pyro.NameServer@localhost:9090
                myObject --> PYRO:obj_ba9ba337053443e091d264e881588bde@localhost:51300
                --------END LIST
        """

6. Start python client that connects and calls a method of registered object (./test/pyro/client.py)
        example output:
        """
                balazs@balazs-VirtualBox:~$ python client.py
                Hello World

        """
