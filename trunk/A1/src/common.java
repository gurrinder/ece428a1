import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class common {
	
	static void setPort(final int port, final String fileName) throws IOException
	{
		File portFile = new File(fileName);
		portFile.delete();

		BufferedWriter file = new BufferedWriter(new FileWriter(fileName));
		file.write("" + port);
		file.close();
	}
	
	static int getPort(final String fileName) throws Exception
	{
		int port = -1;
		File portFile = null;
		while (true) {
			Thread.sleep(500);
			portFile = new File(fileName);
			if (portFile.exists())
				break;
		}

		BufferedReader reader = new BufferedReader(new FileReader(portFile));
		port = Integer.valueOf(reader.readLine());
		reader.close();
		
		return port;
	}
}
