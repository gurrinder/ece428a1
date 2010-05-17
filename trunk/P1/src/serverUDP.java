import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

class serverUDP
{
	public static void main(String argv[]) throws Exception
	{
		ArrayList<String> team = new ArrayList<String>();
		DatagramSocket welcomeSocket = new DatagramSocket();
		InetAddress ip = null;
		Integer port = -1;
		String teamRequest = "";
		try
		{
			BufferedWriter file = new BufferedWriter(new FileWriter(
					"ServerUDP.port"));
			file.write("" + welcomeSocket.getLocalPort() + "\n");
			file.close();
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}

		HashMap<InetAddress, HashMap<Integer, ArrayList<String>>> ret = clientUDP
				.UDPRecieveAll(welcomeSocket);
		ip = ret.keySet().iterator().next();
		HashMap<Integer, ArrayList<String>> ret2 = ret.get(ip);
		port = ret2.keySet().iterator().next();
		ArrayList<String> lines = ret2.get(port);

		for (String line : lines)
		{
			String[] split = line.split(" ");
			if (split.length == 1)
			{
				teamRequest = split[0];
				break;
			}
		}

		for (String line : lines)
		{
			String[] split = line.split(" ");
			if (split.length != 1 && split[1].equalsIgnoreCase(teamRequest))
				team.add(split[0]);
		}

		byte[] size = { 0, 0, 0, 0 };
		size[3] = (byte) (team.size());
		size[2] = (byte) (team.size() >> 8);
		size[1] = (byte) (team.size() >> 16);
		size[0] = (byte) (team.size() >> 24);

		clientUDP.UDPSend("*" + new String(size), ip, port, welcomeSocket);
		for (String line : team)
		{
			clientUDP.UDPSend(line, ip, port, welcomeSocket);
		}

	}
}