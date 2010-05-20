import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class clientTCP {
    public static void main(String[] args) throws IOException {

    	int port = -1;
		File portFile = null;
		while (true)
		{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			portFile = new File("serverTCP.port");
			if (portFile.exists())
				break;
		}
    	
		try
		{
			BufferedReader portReader = new BufferedReader(new FileReader(portFile));
			port = Integer.valueOf(portReader.readLine());
			portReader.close();
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}
		
    	Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        Scanner fileIn = null;
        final InetAddress host = InetAddress.getLocalHost();
        
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            fileIn = new Scanner(new File(args[0]));
        } catch (FileNotFoundException e) {
        	System.err.println("Cannot open input file: " + args[0]);
        	out.close();
            in.close();
            socket.close();
        	System.exit(1);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + host.getHostName());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + host.getHostName());
            System.exit(1);
        }

        String serverReply;
        String fileLine;

        while (fileIn.hasNextLine()) {
            fileLine = fileIn.nextLine();
            out.println(fileLine);
        }
        out.println("@@@");
        out.println(args[1]);
        
        BufferedWriter outFile = new BufferedWriter(new FileWriter("outTCP.dat"));
        while ((serverReply = in.readLine()) != null) {
            if (serverReply.equals("@@@")) break;
        	outFile.write(serverReply);
        	outFile.newLine();
        }
        outFile.flush();
        outFile.close();
        out.close();
        in.close();
        fileIn.close();
        socket.close();
    }
}