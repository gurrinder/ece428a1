import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;


public class common {
	static void UDPSend(byte[] byteArray, InetAddress add, int port, DatagramSocket socket) throws IOException
	{
		int size = byteArray.length;
		byte[] bstr = new byte[4 + size];
		bstr[3] = (byte) (size);
		bstr[2] = (byte) (size >> 8);
		bstr[1] = (byte) (size >> 16);
		bstr[0] = (byte) (size >> 24);

		for(int i = 4; i < bstr.length; i++)
		{
			bstr[i] = byteArray[i-4];
		}
		
		DatagramPacket dp = new DatagramPacket(bstr, bstr.length, add, port);
		socket.send(dp);
	}

	static HashMap<InetAddress, HashMap<Integer, ArrayList<String>>> UDPRecieveAll(DatagramSocket socket) throws IOException
	{
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
