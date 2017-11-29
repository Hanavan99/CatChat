import java.io.*;
import java.net.*;
public class SerializableFile implements Serializable
{
	
	// public static void main(String[] args)
	// {
		
	// }
	public SerializableFile(String inFileName, Socket inSocket) throws FileNotFoundException, IOException
	{
		OutputStream outStream = inSocket.getOutputStream();
		File testFile = new File("." + File.pathSeparator  +/*inFileName*/"testFile.txt");
		FileInputStream inputStream = new FileInputStream(testFile);
		ObjectInputStream s = new ObjectInputStream(inputStream);
		
	}

}