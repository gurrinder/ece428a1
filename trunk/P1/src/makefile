JFLAGS = -g -1.6
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

TCPCLASSES = \
	serverTCP.java \
	clientTCP.java
        
runTCP: tcpclasses
	java serverTCP & java clientTCP in.dat Brazil

tcpclasses: $(TCPCLASSES:.java=.class)

UDPCLASSES = \
	serverUDP.java \
	clientUDP.java
        
runUDP: udpclasses
	java serverUDP & java clientUDP in.dat Brazil

udpclasses: $(UDPCLASSES:.java=.class)



clean:
	$(RM) *.class
