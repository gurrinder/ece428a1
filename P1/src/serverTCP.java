import java.io.*;
import java.net.*;
import java.util.ArrayList;

class serverTCP
{
	static final String PORT_FILE = "ServerTCP.port";
	
	public static void main(String argv[]) throws Exception
	{
		// open a socket and bind to a random open port
		ServerSocket welcomeSocket = new ServerSocket();
		welcomeSocket.bind(null);
		int port = welcomeSocket.getLocalPort();
		
		// write port used to a file which will be read by the client
		try
		{
			common.setPort(port, PORT_FILE);
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}

		// listen for connection from client and set up communication
		Socket connectionSocket = welcomeSocket.accept();
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(
				connectionSocket.getInputStream()));
		DataOutputStream outToClient = new DataOutputStream(connectionSocket
				.getOutputStream());

		String teamRequested = inFromClient.readLine();		// team requested by client
		ArrayList<String> team = new ArrayList<String>();	// players in requested team
		
		// read world cup roster from client
		String curPlayer;
		while ((curPlayer = inFromClient.readLine()) != null)
		{
			// check for end of transmission
			if (curPlayer.trim().equalsIgnoreCase(""))
				break;
			
			String[] tokens = curPlayer.split(" ");
			if (tokens[1].equalsIgnoreCase(teamRequested))
				team.add(tokens[0]);
		}
		
		// check if requested team qualified for the world cup
		if (team.size() == 0)
			team.add(teamRequested + " did not qualify to the world cup");
		
		// send list of players in requested team to client
		for (int i = 0; i < team.size(); i++)
		{
			outToClient.writeBytes(team.get(i) + "\n");
		}
		
		// close connections
		inFromClient.close();
		outToClient.close();
		connectionSocket.close();
		welcomeSocket.close();
	}
}