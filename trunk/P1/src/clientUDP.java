import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class clientUDP 
{
	static final String PORT_FILE = "ServerUDP.port";
	static final String OUTPUT_FILE = "outUDP.dat";
	
	public static void main(String[] args)
	{
		int port = 0;
		DatagramSocket echoSocket = null;
		BufferedReader portFileReader = null;
		BufferedReader dataFileReader  = null;
		InetAddress localIP = null;
		File portFile = null;
		String readContent = null;
		ArrayList<String> contentList = new ArrayList<String>();
		HashMap<InetAddress, HashMap<Integer, ArrayList<String>>> ret;
		BufferedWriter file2 = null;
		ArrayList<String> teamList = null;
		
		// we need to find the file with the port number
		// otherwise we cannot proceed
		while (true) 
		{
			portFile = new File(PORT_FILE);
			if (portFile.exists())
			{
				portFile = null;
				break;
			}
			try
			{
				Thread.sleep(500);
			}
			// non-fatal exception
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		try 
		{
			echoSocket = new DatagramSocket();
			// ensure that the buffer size is at least 1 MB
			echoSocket.setReceiveBufferSize(1024 * 1024); // 1MB
			// used for getting the local IP address of the host
			localIP = InetAddress.getLocalHost();
		} 
		catch (SocketException se) 
		{
			se.printStackTrace();
			System.exit(1);
		}
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}

		try
		{
			portFileReader = new BufferedReader(new FileReader(PORT_FILE));
			port = Integer.valueOf(portFileReader.readLine());
			portFileReader.close();
			portFileReader = null;
		} 
		catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
			System.exit(1);
		} 
		catch (NumberFormatException e) 
		{
			e.printStackTrace();
			System.exit(1);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		try 
		{
			dataFileReader = new BufferedReader(new FileReader(args[0]));
			// add the country as the first entry in the list
			contentList.add(args[1]);
			while ((readContent = dataFileReader.readLine()) != null)
			{
				contentList.add(readContent);
			}
			dataFileReader.close();
			dataFileReader = null;
		} 
		catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
			System.exit(1);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
				
		// split the size of contentList into 1 byte blocks 
		byte[] size = {0,0,0,0,0};
		size[4] = (byte) ((contentList.size()) & 0x000000FF);
		size[3] = (byte) ((contentList.size() >> 8) & 0x000000FF);
		size[2] = (byte) ((contentList.size() >> 16) & 0x000000FF);
		size[1] = (byte) ((contentList.size() >> 24) & 0x000000FF);
		// this is a special-magic character indicating that the following
		// bytes are the size of the contentList
		size[0] = (byte) ('*');

		try
		{
			// the first thing we send is how many packets should be expected
			// in future
			UDPSend(size, localIP, port, echoSocket);
			for (int i = 0; i < contentList.size(); i++) 
			{
				try 
				{
					Thread.sleep(10);
				}
				// non-fatal exception
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				UDPSend(contentList.get(i).getBytes(), localIP, port, echoSocket);
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		try 
		{			
			// we receive all the team members for the specified country from the server
			ret = UDPRecieveAll(echoSocket);
			HashMap<Integer, ArrayList<String>> ret2 = ret.get(ret.keySet().iterator().next());
			teamList = ret2.get(ret2.keySet().iterator().next());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		try 
		{
			file2 = new BufferedWriter(new FileWriter(OUTPUT_FILE));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(1);
		}

		try 
		{
			for (int i = 0; i < teamList.size(); i++) 
			{
					file2.write(teamList.get(i));
					if (i < teamList.size() - 1) 
					{
						file2.newLine();
					}
			}
			file2.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		echoSocket.close();
	}

	static void UDPSend(byte[] byteArray, InetAddress add, int port,
			DatagramSocket socket) throws IOException {
		int size = byteArray.length;
		byte[] bstr = new byte[4 + size];
		bstr[3] = (byte) (size);
		bstr[2] = (byte) (size >> 8);
		bstr[1] = (byte) (size >> 16);
		bstr[0] = (byte) (size >> 24);

		for (int i = 4; i < bstr.length; i++) {
			bstr[i] = byteArray[i - 4];
		}

		DatagramPacket dp = new DatagramPacket(bstr, bstr.length, add, port);
		socket.send(dp);
	}

	static HashMap<InetAddress, HashMap<Integer, ArrayList<String>>> UDPRecieveAll(
			DatagramSocket socket) throws IOException {
		ArrayList<String> lines = new ArrayList<String>();
		String bstr = "";
		int size = -1;
		InetAddress ip = null;
		int port = -1;
		byte[] justGot = new byte[1024];
		while (true) {
			DatagramPacket dpr = new DatagramPacket(justGot, justGot.length);
			try {
				socket.receive(dpr);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}

			int lineSize = 0;
			lineSize += ((justGot[3]) & (0x000000FF));
			lineSize += ((justGot[2] << 8) & (0x0000FF00));
			lineSize += ((justGot[1] << 16) & (0x00FF0000));
			lineSize += ((justGot[0] << 24) & (0xFF000000));

			bstr = new String(dpr.getData()).substring(4, lineSize + 4);
			if (size == -1 && justGot[4] == '*') {
				size = 0;
				size += ((justGot[8]) & (0x000000FF));
				size += ((justGot[7] << 8) & (0x0000FF00));
				size += ((justGot[6] << 16) & (0x00FF0000));
				size += ((justGot[5] << 24) & (0xFF000000));
				ip = dpr.getAddress();
				port = dpr.getPort();
			} 
			else 
			{
				lines.add(bstr);
				if(lines.size() % 100 == 0)
				{
					System.out.println("expected: " + size + " got: " + lines.size());
				}
			}

			if (size != -1 && lines.size() >= size)
			{
				break;
			}
		}
		HashMap<InetAddress, HashMap<Integer, ArrayList<String>>> ret = new HashMap<InetAddress, HashMap<Integer, ArrayList<String>>>();
		HashMap<Integer, ArrayList<String>> inter = new HashMap<Integer, ArrayList<String>>();
		inter.put(port, lines);
		ret.put(ip, inter);
		return ret;
	}
}
