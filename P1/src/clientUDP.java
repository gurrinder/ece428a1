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
		
		// get port for connecting to server
		int port = 0;
		try
		{
			port = common.getPort(PORT_FILE);
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// set up socket for communicating with server
		DatagramSocket echoSocket = null;
		InetAddress localIP = null;
		try 
		{
			echoSocket = new DatagramSocket();
			// ensure that the buffer size is at least 1 MB
			echoSocket.setReceiveBufferSize(1024 * 1024); // 1MB
			// used for getting the local IP address of the host
			localIP = InetAddress.getLocalHost();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(1);
		}		
		
		ArrayList<String> data = new ArrayList<String>();	// store roster data
		
		// read world cup roster from file
		BufferedReader dataFileReader  = null;
		try 
		{
			dataFileReader = new BufferedReader(new FileReader(args[0]));
			// add the country as the first entry in the list
			data.add(args[1]);
			String readContent = null;
			while ((readContent = dataFileReader.readLine()) != null)
			{
				data.add(readContent);
			}
			dataFileReader.close();
			dataFileReader = null;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
				
		// encode number of lines to be sent to server 
		byte[] size = {0,0,0,0,0};
		size[4] = (byte) ((data.size()) & 0x000000FF);
		size[3] = (byte) ((data.size() >> 8) & 0x000000FF);
		size[2] = (byte) ((data.size() >> 16) & 0x000000FF);
		size[1] = (byte) ((data.size() >> 24) & 0x000000FF);
		// this is a special-magic character indicating that the following
		// bytes are the size of the data
		size[0] = (byte) ('*');

		// send roster data to server
		try
		{
			// the first thing we send is how many packets should be expected
			// in future
			common.UDPSend(size, localIP, port, echoSocket);
			for (int i = 0; i < data.size(); i++) 
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
				common.UDPSend(data.get(i).getBytes(), localIP, port, echoSocket);
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		// get list of players from specified country from the server
		ArrayList<String> players = null;
		try 
		{			
			HashMap<InetAddress, HashMap<Integer, ArrayList<String>>> ret = 
				common.UDPRecieveAll(echoSocket);
			HashMap<Integer, ArrayList<String>> ret2 = ret.get(ret.keySet().iterator().next());
			players = ret2.get(ret2.keySet().iterator().next());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		// set up output file for requested team's player names
		BufferedWriter outWriter = null;
		try 
		{
			outWriter = new BufferedWriter(new FileWriter(OUTPUT_FILE));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(1);
		}

		// write players in the requested team to the output file
		try 
		{
			for (int i = 0; i < players.size(); i++) 
			{
					outWriter.write(players.get(i));
					if (i < players.size() - 1) 
					{
						outWriter.newLine();
					}
			}
			outWriter.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		echoSocket.close();
	}
}
