all:
	javac serverTCP.java
	javac clientTCP.java
	javac serverUDP.java
	javac clientUDP.java
	
runTCP:
	java serverTCP & 
	java clientTCP in.dat Brazil

runUDP:
	java serverUDP & 
	java clientUDP in.dat Brazil

clean:
	rm -rf *.class