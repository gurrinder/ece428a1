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
		BufferedReader dataFileReader  = null;
		InetAddress localIP = null;
		String readContent = null;
		ArrayList<String> contentList = new ArrayList<String>();
		HashMap<InetAddress, HashMap<Integer, ArrayList<String>>> ret;
		BufferedWriter file2 = null;
		ArrayList<String> teamList = null;
		
		// we need to find the file with the port number
		// otherwise we cannot proceed
		try
		{
			port = common.getPort(PORT_FILE);
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
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
			common.UDPSend(size, localIP, port, echoSocket);
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
				common.UDPSend(contentList.get(i).getBytes(), localIP, port, echoSocket);
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		try 
		{			
			// we receive all the team members for the specified country from the server
			ret = common.UDPRecieveAll(echoSocket);
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
}
