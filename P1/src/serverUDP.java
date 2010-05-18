import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
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
			File portFile = new File("ServerUDP.port");
			portFile.delete();
			portFile = null;
			
			BufferedWriter file = new BufferedWriter(new FileWriter(
					"ServerUDP.port"));
			file.write("" + welcomeSocket.getLocalPort() + "\n");
			file.close();
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}

		HashMap<InetAddress, HashMap<Integer, ArrayList<String>>> ret = UDPRecieveAll(welcomeSocket);
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

		byte[] size = { 0, 0, 0, 0 };
		size[3] = (byte) (team.size());
		size[2] = (byte) (team.size() >> 8);
		size[1] = (byte) (team.size() >> 16);
		size[0] = (byte) (team.size() >> 24);

		UDPSend("*" + new String(size), ip, port, welcomeSocket);
		for (String line : team)
		{
			UDPSend(line, ip, port, welcomeSocket);
		}

	}

	static void UDPSend(String line, InetAddress add, int port,
			DatagramSocket socket) throws IOException
	{
		int size = line.length();
		byte[] bstr = ("    " + line).getBytes();
		bstr[3] = (byte) (size);
		bstr[2] = (byte) (size >> 8);
		bstr[1] = (byte) (size >> 16);
		bstr[0] = (byte) (size >> 24);

		DatagramPacket dp = new DatagramPacket(bstr, bstr.length, add, port);
		socket.send(dp);

	}

	static HashMap<InetAddress, HashMap<Integer, ArrayList<String>>> UDPRecieveAll(
			DatagramSocket socket) throws IOException
	{
		ArrayList<String> lines = new ArrayList<String>();
		String bstr = "";
		int size = -1;
		InetAddress ip = null;
		int port = -1;
		while (true)
		{
			byte[] justGot = new byte[1024];
			DatagramPacket dpr = new DatagramPacket(justGot, justGot.length);
			try
			{
				socket.setSoTimeout(30000); //timeout in 30 seconds
				socket.receive(dpr);
			}
			catch(SocketException se)
			{
				se.printStackTrace();
				System.exit(0);
			}
			catch(SocketTimeoutException ste)
			{
				// a timeout occurred
				break;
			}
			
			int lineSize = 0;
			lineSize += ((justGot[3]) & (0x000000FF));
			lineSize += ((justGot[2] << 8) & (0x0000FF00));
			lineSize += ((justGot[1] << 16) & (0x00FF0000));
			lineSize += ((justGot[0] << 24) & (0xFF000000));
						
			bstr = new String(dpr.getData()).substring(4, lineSize + 4);
			if (size == -1 && justGot[4] == '*')
			{
				size = 0;
				size += ((justGot[8]) & (0x000000FF));
				size += ((justGot[7] << 8) & (0x0000FF00));
				size += ((justGot[6] << 16) & (0x00FF0000));
				size += ((justGot[5] << 24) & (0xFF000000));
				ip = dpr.getAddress();
				port = dpr.getPort();
			} else
			{
				lines.add(bstr);
			}

			if (size != -1 && lines.size() >= size)
				break;

		}
		HashMap<InetAddress, HashMap<Integer, ArrayList<String>>> ret = new HashMap<InetAddress, HashMap<Integer, ArrayList<String>>>();
		HashMap<Integer, ArrayList<String>> inter = new HashMap<Integer, ArrayList<String>>();
		inter.put(port, lines);
		ret.put(ip, inter);
		return ret;
	}
}