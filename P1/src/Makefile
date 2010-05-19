all:
	javac -1.6 serverTCP.java
	javac -1.6 clientTCP.java
	javac -1.6 serverUDP.java
	javac -1.6 clientUDP.java
	
runTCP:
	java serverTCP & 
	java clientTCP $(F) $(C)

runUDP:
	java serverUDP & 
	java clientUDP $(F) $(C)

clean:
	rm -rf *.class
	rm -rf *.port