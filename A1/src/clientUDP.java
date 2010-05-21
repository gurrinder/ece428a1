import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class clientUDP {
    
	public static void main(String[] args) throws IOException {

    	int port = -1;
		try {
			port = common.getPort("serverUDP.port");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
    	final int bufferSize = 1024;
        byte[] buffer = null;
        DatagramSocket socket = new DatagramSocket(); 
        Scanner fileIn = null;
        
        try {
            fileIn = new Scanner(new File(args[0]));
        } catch (FileNotFoundException e) {
        	System.err.println("Cannot open input file: " + args[0]);
        	System.exit(1);
        }

        String serverReply;
        String fileLine;
        final InetAddress host = InetAddress.getLocalHost();
        try {
	        while (fileIn.hasNextLine()) {
	            fileLine = fileIn.nextLine();
	            buffer = fileLine.getBytes();
	        	socket.send(new DatagramPacket(buffer, buffer.length, host, port));
	        	Thread.sleep(1);
	        }
      	} catch (InterruptedException e) {
			e.printStackTrace();
		}
        buffer = "@@@".getBytes();
        socket.send(new DatagramPacket(buffer, buffer.length, host, port));
        buffer = args[1].getBytes();
        socket.send(new DatagramPacket(buffer, buffer.length, host, port));
        
        BufferedWriter outFile = new BufferedWriter(new FileWriter("outUDP.dat"));
        buffer = new byte[bufferSize];
        List<String> players = new ArrayList<String>();
        while (true) {
        	DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        	socket.receive(packet);
            serverReply = new String(packet.getData(), 0, packet.getLength());
        	if (serverReply.equals("@@@")) break;
        	players.add(serverReply);
//        	outFile.write(serverReply);
//        	outFile.newLine();
        }
        
        for (String player : players) {
        	outFile.write(player);
        	outFile.newLine();
        }
        
        outFile.flush();
        outFile.close();
        fileIn.close();
        socket.close();
    }
}