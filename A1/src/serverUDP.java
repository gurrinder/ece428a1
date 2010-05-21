import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class serverUDP {
    
	public static void main(String[] args) throws IOException {

        DatagramSocket socket = new DatagramSocket();
        socket.setReceiveBufferSize(1024*1024); // 1 MB
        common.setPort(socket.getLocalPort(), "serverUDP.port");
		
		final int bufferSize = 1024;
        byte buffer[] = new byte[bufferSize];
        DatagramPacket packet = null;

        Map<String, List<String>> data = new HashMap<String, List<String>>();
        List<String> rawData = new ArrayList<String>();
        
        while (true) {
            packet = new DatagramPacket(buffer, buffer.length);
        	socket.receive(packet);
            String inputLine = new String(packet.getData(), 0, packet.getLength());
            if (inputLine.equals("@@@")) break;
            rawData.add(inputLine);
//        	String[] tokens = inputLine.split(" ");
//            if (!data.containsKey((tokens[1]))) {
//            	data.put(tokens[1], new ArrayList<String>());
//            }
//            data.get(tokens[1]).add(tokens[0]);
        }
        Iterator<String> rawDataIt = rawData.iterator();
        while (rawDataIt.hasNext()) {
        	String[] tokens = rawDataIt.next().split(" ");
        	if (!data.containsKey((tokens[1]))) {
        		data.put(tokens[1], new ArrayList<String>());
        	}
        	data.get(tokens[1]).add(tokens[0]);
        }
        
        packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String team = new String(packet.getData(), 0, packet.getLength());
        InetAddress ip = packet.getAddress();
        int clientPort = packet.getPort();
        
        List<String> players = data.get(team);
        if (players == null) {
        	players = new ArrayList<String>();
        	players.add(team + " did not qualify to the world cup");
        }
    	
    	try {
	    	for (String player : players) {
	    		buffer = player.getBytes();
	        	socket.send(new DatagramPacket(buffer, buffer.length, ip, clientPort));
	        	Thread.sleep(1);
	    	}
        	
    	} catch (InterruptedException e) {
			e.printStackTrace();
		}
        buffer = "@@@".getBytes();
        socket.send(new DatagramPacket(buffer, buffer.length, ip, clientPort));
        
        socket.close();
    }
}