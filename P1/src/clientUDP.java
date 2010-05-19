import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class clientUDP
{
	public static void main(String[] args) throws IOException,
			InterruptedException
	{

		DatagramSocket echoSocket = null;
		BufferedReader file = null;

		echoSocket = new DatagramSocket();
		echoSocket.setReceiveBufferSize(64*1024*1024); // 64MB
		InetAddress IPAddress = InetAddress.getLocalHost();
		int port = -1;
		File portFile = null;
		while (true)
		{
			Thread.sleep(500);
			portFile = new File("ServerUDP.port");
			if (portFile.exists())
				break;
		}

		try
		{
			file = new BufferedReader(new FileReader("ServerUDP.port"));
			port = Integer.valueOf(file.readLine());
			file.close();
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}

		file = null;

		String line;
		try
		{
			file = new BufferedReader(new FileReader(args[0]));
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}

		ArrayList<String> lines = new ArrayList<String>();
		lines.add(args[1]);
		while ((line = file.readLine()) != null)
			lines.add(line);
		file.close();

		byte[] size = { 0, 0, 0, 0 };
		int sz = lines.size();
		size[3] = (byte) (sz);
		size[2] = (byte) (sz >> 8);
		size[1] = (byte) (sz >> 16);
		size[0] = (byte) (sz >> 24);

		UDPSend("*" + new String(size), IPAddress, port, echoSocket);
		Thread.sleep(10);
		
		for (int i = 0; i < lines.size(); i++)
		{
			UDPSend(lines.get(i), IPAddress, port, echoSocket);
			Thread.sleep(1);
		}

		HashMap<InetAddress, HashMap<Integer, ArrayList<String>>> ret = UDPRecieveAll(echoSocket);
		HashMap<Integer, ArrayList<String>> ret2 = ret.get(ret.keySet()
				.iterator().next());
		ArrayList<String> team = ret2.get(ret2.keySet().iterator().next());
		BufferedWriter file2 = null;

		try
		{
			file2 = new BufferedWriter(new FileWriter("outUDP.dat"));
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}

		for (int i = 0; i < team.size(); i++)
		{
			file2.write(team.get(i));
			if(i < team.size() - 1)
			{
				file2.newLine();
			}
		}
		file2.close();

		echoSocket.close();
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
				socket.receive(dpr);
			}
			catch(IOException e)
			{
				e.printStackTrace();
				System.exit(0);
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
