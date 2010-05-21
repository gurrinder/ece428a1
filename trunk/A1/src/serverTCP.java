import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class serverTCP {
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(null);
        common.setPort(serverSocket.getLocalPort(), "serverTCP.port");

        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
				new InputStreamReader(
				clientSocket.getInputStream()));
        String inputLine;
        
        Map<String, List<String>> data = new HashMap<String, List<String>>();
        
        while (!(inputLine = in.readLine()).equals("@@@")) {
            if (inputLine.trim().equals("")) break;
        	String[] tokens = inputLine.split(" ");
            if (!data.containsKey((tokens[1]))) {
            	data.put(tokens[1], new ArrayList<String>());
            }
            data.get(tokens[1]).add(tokens[0]);
        }
        String team = in.readLine();
        List<String> players = data.get(team);
        if (players == null) {
        	players = new ArrayList<String>();
        	players.add(team + " did not qualify to the world cup");
        } 
    	for (String player : players) {
    		out.println(player);
    	}
        out.println("@@@");
        
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}