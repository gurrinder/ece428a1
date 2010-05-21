import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

class serverUDP
{
	static final String PORT_FILE = "ServerUDP.port";

	public static void main(String argv[]) throws Exception
	{
		ArrayList<String> team = new ArrayList<String>();
		DatagramSocket welcomeSocket = new DatagramSocket();
		welcomeSocket.setReceiveBufferSize(1024*1024); // 1MB
		InetAddress ip = null;
		Integer port = -1;
		String teamRequest = "";
		try
		{
			// if an old file exists, delete it before creating a new one
			File portFile = new File(PORT_FILE);
			portFile.delete();
			portFile = null;
			
			BufferedWriter file = new BufferedWriter(new FileWriter(PORT_FILE));
			file.write("" + welcomeSocket.getLocalPort() + "\n");
			file.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		HashMap<InetAddress, HashMap<Integer, ArrayList<String>>> ret = common.UDPRecieveAll(welcomeSocket);
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
			if (split.length >= 2 && split[1].equalsIgnoreCase(teamRequest))
			{
				team.add(split[0]);
			}
		}

		if(team.size() == 0)
		{
			team.add(teamRequest + " did not qualify to the world cup");
		}
		
		byte[] size = {0, 0, 0, 0, 0};
		size[4] = (byte) ((team.size()) & 0x000000FF);
		size[3] = (byte) ((team.size() >> 8) & 0x000000FF);
		size[2] = (byte) ((team.size() >> 16) & 0x000000FF);
		size[1] = (byte) ((team.size() >> 24) & 0x000000FF);
		size[0] = (byte) ('*');
		
		common.UDPSend(size, ip, port, welcomeSocket);
		for (String line : team)
		{
			Thread.sleep(10);
			common.UDPSend(line.getBytes(), ip, port, welcomeSocket);
		}
	}
}