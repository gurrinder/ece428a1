import java.io.*;
import java.net.*;
import java.util.ArrayList;

class serverTCP
{
	static final String PORT_FILE = "ServerTCP.port";
	
	public static void main(String argv[]) throws Exception
	{
		String clientString;
		String clientRequest;		// stores requests coming in from client
		ArrayList<String> team = new ArrayList<String>();

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

		clientRequest = inFromClient.readLine();
		while ((clientString = inFromClient.readLine()) != null)
		{
			if (clientString.trim().equalsIgnoreCase(""))
				break;
			String[] clientSplit = clientString.split(" ");
			if (clientSplit[1].equalsIgnoreCase(clientRequest))
				team.add(clientSplit[0]);
		}
		if (team.size() == 0)
			team.add(clientRequest + " did not qualify to the world cup");
		for (int i = 0; i < team.size(); i++)
		{
			outToClient.writeBytes(team.get(i) + "\n");
		}
		inFromClient.close();
		outToClient.close();
	}
}