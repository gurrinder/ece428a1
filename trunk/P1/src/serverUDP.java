import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

class serverUDP
{
	static final String PORT_FILE = "ServerUDP.port";

	public static void main(String argv[]) throws Exception
	{
		
		// set up socket for receiving data from client
		DatagramSocket welcomeSocket = new DatagramSocket();
		welcomeSocket.setReceiveBufferSize(1024*1024);	// set 1 MB buffer space for receiving packets
		InetAddress ip = null;							// IP of received packet
		Integer port = -1;								// port of received packet
		
		// get port used by welcomeSocket and write it to file for client
		int serverPort = welcomeSocket.getLocalPort();
		try
		{
			common.setPort(serverPort, PORT_FILE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		// get roster data from client
		HashMap<InetAddress, HashMap<Integer, ArrayList<String>>> ret = common.UDPRecieveAll(welcomeSocket);
		ip = ret.keySet().iterator().next();
		HashMap<Integer, ArrayList<String>> ret2 = ret.get(ip);
		port = ret2.keySet().iterator().next();
		ArrayList<String> lines = ret2.get(port);

		// find the team that is requested by the client
		String teamRequest = null;
		for (String line : lines)
		{
			String[] split = line.split(" ");
			if (split.length == 1)
			{
				teamRequest = split[0];
				break;
			}
		}
		
		// check if requested team exists in the world cup roster
		if (teamRequest == null) {
			System.err.println("requested team cannot be found");
			System.exit(1);
		}
		
		ArrayList<String> team = new ArrayList<String>();	// players in requested team
		
		// add players from requested team
		for (String line : lines)
		{
			String[] split = line.split(" ");
			if (split.length >= 2 && split[1].equalsIgnoreCase(teamRequest))
			{
				team.add(split[0]);
			}
		}

		// check if requested team exists in the world cup roster
		if(team.size() == 0)
		{
			team.add(teamRequest + " did not qualify to the world cup");
		}
		
		// encode number of players in the requested team to be sent back to the client
		byte[] size = {0, 0, 0, 0, 0};
		size[4] = (byte) ((team.size()) & 0x000000FF);
		size[3] = (byte) ((team.size() >> 8) & 0x000000FF);
		size[2] = (byte) ((team.size() >> 16) & 0x000000FF);
		size[1] = (byte) ((team.size() >> 24) & 0x000000FF);
		size[0] = (byte) ('*');
		
		// send size of the requested team along with all the players back to client
		common.UDPSend(size, ip, port, welcomeSocket);
		for (String line : team)
		{
			Thread.sleep(10);
			common.UDPSend(line.getBytes(), ip, port, welcomeSocket);
		}
	}
}