all:
	javac -1.6 serverTCP.java
	javac -1.6 clientTCP.java
	javac -1.6 serverUDP.java
	javac -1.6 clientUDP.java
	
runTCP:
	java serverTCP & 
	java clientTCP in.dat Brazil

runUDP:
	java serverUDP & 
	java clientUDP in.dat Brazil

clean:
	rm -rf *.class
	rm -rf *.port