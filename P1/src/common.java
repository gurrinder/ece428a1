import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

/* This class contains utility code common to server and client code such as setting
 * setting up port file and performing send and receive operations of UDP packets
 */
public class common {
	
	static void setPort(final int port, final String fileName) throws IOException
	{
		// delete any old port file, since we dont want to append or overwrite it
		File portFile = new File(fileName);
		portFile.delete();

		// write the new port into the specified file
		BufferedWriter file = new BufferedWriter(new FileWriter(fileName));
		file.write("" + port + "\n");
		file.close();
	}
	
	static int getPort(final String fileName) throws Exception
	{
		int port = -1;
		File portFile = null;
		
		// we cannot exit until we find a file
		// this loop continually searches for the provided file
		// until it is created
		while (true)
		{
			Thread.sleep(500);
			portFile = new File(fileName);
			if (portFile.exists())
				break;
		}

		// open the port file and read in the port number from it
		BufferedReader reader = new BufferedReader(new FileReader(portFile));
		port = Integer.valueOf(reader.readLine());
		reader.close();
		
		return port;
	}
	
	static void UDPSend(byte[] byteArray, InetAddress add, int port, DatagramSocket socket) throws IOException
	{
		// byteArray will be send over the wire
		// the first four bytes of the packet will point
		// to how many bytes to read i.e. the number of elements in byteArray
		// so we get the size of the byteArray and then split it into
		// 4 - single byte and then send it over from the socket
		int size = byteArray.length;
		byte[] bstr = new byte[4 + size];
		bstr[3] = (byte) (size);
		bstr[2] = (byte) (size >> 8);
		bstr[1] = (byte) (size >> 16);
		bstr[0] = (byte) (size >> 24);

		for(int i = 4; i < bstr.length; i++)
		{
			// copy the content to send over into the bstr array
			bstr[i] = byteArray[i-4];
		}
		
		DatagramPacket dp = new DatagramPacket(bstr, bstr.length, add, port);
		socket.send(dp);
	}

	static HashMap<InetAddress, HashMap<Integer, ArrayList<String>>> UDPRecieveAll(DatagramSocket socket) throws IOException
	{
		// lines store all the elements sent over the packet
		ArrayList<String> lines = new ArrayList<String>();
		String bstr = "";
		int size = -1;
		InetAddress ip = null;
		int port = -1;
		byte[] justGot = new byte[1024];
		
		while (true)
		{
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

			// lineSize is the number of bytes to read in the packet
			int lineSize = 0;
			lineSize += ((justGot[3]) & (0x000000FF));
			lineSize += ((justGot[2] << 8) & (0x0000FF00));
			lineSize += ((justGot[1] << 16) & (0x00FF0000));
			lineSize += ((justGot[0] << 24) & (0xFF000000));
			
			// read the number of bytes in the packet and store it as string
			bstr = new String(dpr.getData()).substring(4, lineSize + 4);

			if (size == -1 && justGot[4] == '*')
			{
				// this is a special delimiter used for telling
				// how many more packets will be sent, so the loop will continue
				// on until we receive that many packets
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
				// add the content read from the packet to the arraylist
				// which stores string types
				lines.add(bstr);
/*				if(lines.size() % 100 == 0)
				{
					System.out.println("expected: " + size + " got: " + lines.size());
				}*/
			}
			
			// we only break the loop if the following conditions are met:
			// a) we dont know how many packets to expect
			// b) we know how many packets to expect and we get that many packets
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