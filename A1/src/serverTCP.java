import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class serverTCP {
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(null);
        try
		{
			File portFile = new File("serverTCP.port");
			portFile.delete();
			portFile = null;

			BufferedWriter file = new BufferedWriter(new FileWriter(
					"serverTCP.port"));
			file.write("" + serverSocket.getLocalPort());
			file.close();
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}

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
    	Iterator<String> it = players.iterator();
        while (it.hasNext()) {
        	out.println(it.next());
        }
        out.println("@@@");
        
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}