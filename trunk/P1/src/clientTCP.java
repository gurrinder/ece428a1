import java.io.*;
import java.net.*;

public class clientTCP
{
	public static void main(String[] args) throws IOException,
			InterruptedException
	{

		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		String server = "localhost";
		BufferedReader file = null;
		int port = -1;
		File portFile = null;
		while (true)
		{
			Thread.sleep(500);
			portFile = new File("ServerTCP.port");
			if (portFile.exists())
				break;
		}

		try
		{
			file = new BufferedReader(new FileReader("ServerTCP.port"));
			port = Integer.valueOf(file.readLine());
			file.close();
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}

		file = null;

		try
		{
			echoSocket = new Socket(server, port);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket
					.getInputStream()));
		} catch (UnknownHostException e)
		{
			System.err.println("Don't know about host: " + server);
			System.exit(1);
		} catch (IOException e)
		{
			System.err.println("Couldn't get I/O for " + "the connection to: "
					+ server);
			System.exit(1);
		}

		String line;
		try
		{
			file = new BufferedReader(new FileReader(args[0]));
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}

		out.println(args[1]);
		while ((line = file.readLine()) != null)
		{
			out.println(line);
		}
		file.close();
		out.println("");

		BufferedWriter file2 = null;

		try
		{
			file2 = new BufferedWriter(new FileWriter("outTCP.dat"));
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}

		while ((line = in.readLine()) != null)
		{
			file2.write(line + "\n");
		}
		file2.close();

		out.close();
		in.close();
		echoSocket.close();
	}
}
