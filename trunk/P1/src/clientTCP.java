import java.io.*;
import java.net.*;

public class clientTCP
{
	public static void main(String[] args) throws IOException,
			InterruptedException
	{

		Socket echoSocket = null;			// socket for sending to server
		PrintWriter out = null;				// writer for writing to server
		BufferedReader in = null;			// reader for reading from server
		BufferedReader file = null;			// reader for reading data file
		int port = -1;
		
		try {
			port = common.getPort("ServerTCP.port");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		file = null;

		// set up communication to the server 
		try
		{
			echoSocket = new Socket(InetAddress.getLocalHost(), port);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket
					.getInputStream()));
		} catch (UnknownHostException e)
		{
			System.err.println("Don't know about host: " + InetAddress.getLocalHost());
			System.exit(1);
		} catch (IOException e)
		{
			System.err.println("Couldn't get I/O for " + "the connection to: "
					+ InetAddress.getLocalHost());
			System.exit(1);
		}

		try
		{
			file = new BufferedReader(new FileReader(args[0]));
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}

		out.println(args[1]);	// send selected country to server
		
		// send list of data file contents to server
		String line;
		while ((line = file.readLine()) != null)
		{
			out.println(line);
		}
		file.close();
		
		out.println("");	// send empty string to indicate end of transmission

		// open output file for writing
		BufferedWriter outFile = null;
		try
		{
			outFile = new BufferedWriter(new FileWriter("outTCP.dat"));
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}
		
		// get players of selected country from server and write them to outFile 
		boolean newline = false;
		while ((line = in.readLine()) != null)
		{
			if(newline)
			{
				outFile.newLine();
			}
			outFile.write(line);
			newline = true;
		}
		
		outFile.close();
		out.close();
		in.close();
		echoSocket.close();
	}
}
