all:
	javac -1.6 common.java serverTCP.java clientTCP.java serverUDP.java clientUDP.java
	
runTCP:
	java serverTCP & 
	java clientTCP $(F) $(C)

runUDP:
	java serverUDP & 
	java clientUDP $(F) $(C)

clean:
	rm -rf *.class
	rm -rf *.port
	rm -rf outTCP.dat
	rm -rf outUDP.dat