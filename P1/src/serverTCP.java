import java.io.*;
import java.net.*;
import java.util.ArrayList;

class serverTCP
{
	public static void main(String argv[]) throws Exception
	{
		String clientString;
		String clientRequest;
		ArrayList<String> team = new ArrayList<String>();

		ServerSocket welcomeSocket = new ServerSocket();
		welcomeSocket.bind(null);
		try
		{
			BufferedWriter file = new BufferedWriter(new FileWriter(
					"ServerTCP.port"));
			file.write("" + welcomeSocket.getLocalPort() + "\n");
			file.close();
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}

		Socket connectionSocket = welcomeSocket.accept();
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(
				connectionSocket.getInputStream()));
		DataOutputStream outToClient = new DataOutputStream(connectionSocket
				.getOutputStream());

		clientRequest = inFromClient.readLine();
		while (!(clientString = inFromClient.readLine()).equals(""))
		{
			String[] clientSplit = clientString.split(" ");
			if (clientSplit[1].equals(clientRequest))
				team.add(clientSplit[0]);
		}
		for (int i = 0; i < team.size(); i++)
		{
			outToClient.writeBytes(team.get(i) + "\n");
		}
		inFromClient.close();
		outToClient.close();
	}
}