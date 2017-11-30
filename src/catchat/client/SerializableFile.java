package catchat.client;
import java.util.*;
import java.io.*;
import java.net.*;
//passed filename, byteArray of contents, stroe byteArray
// intenal methods to write file to path
//private read file
//
//client side they need WriteToFiel metdo given string
public class SerializableFile implements Serializable
{
	
	public OutputStream outStream;
	public InputStream inStream;
	public FileOutputStream fileOutStream;
	public FileInputStream f_inStream;
	public String fileName;
	public File testFile;


	public SerializableFile(String inFileName) throws FileNotFoundException, IOException
	{
		fileName = inFileName;
		File testFile = new File("." + File.pathSeparator  +inFileName);
		//inStream = inSocket.getInputStream();
		//ObjectInputStream s = new ObjectInputStream(inStream);
		
	}
	public SerializableFile(File inFile)
	{
		testFile = inFile;

	}
	public static File deSerialize(byte[] inByteArray) throws IOException
	{
		//String temp = Base64.encodeBase64String(inByteArray);
		String temp = new String(inByteArray);
		String fileName = "downloadedFile.txt";
		FileWriter file = new FileWriter(fileName,true);
		PrintWriter outFile = new PrintWriter(file);
		outFile.println(temp);
		outFile.close();


		return new File("testfile.txt");
	}
	public static byte[] serialize(File inFile) throws FileNotFoundException, IOException
	{
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		StringBuilder sb = new StringBuilder();
		Scanner s = new Scanner(inFile);
		while(s.hasNext())
		{
			sb.append(s.nextLine());

		}
		String contents = sb.toString();
		ObjectOutputStream out = new ObjectOutputStream(byteOutStream);
		byte[] byteArray = contents.getBytes();
		return byteArray;
		// try
		// {
		// 	out.writeObject(inFile);
		// 	out.flush();

		// 	byteArray = byteOutStream.toByteArray();
		// 	out.close();
			
		// }
		// catch (Exception e)
		// {
		// 	System.out.println("An error occurred serializing the object");
		// 	out.close();
		// 	return new byte[0];
		// }
		// return byteArray;

	}
	// public static void saveFile(File inFile) throws FileNotFoundException
	// {
	// 	Scanner s = new Scanner(System.in);
	// 	Scanner f = new Scanner(inFile);
	// 	if (inFile.exists())
	// 	{
	// 		System.out.println("It seems that the file " + inFile.toString() + "exists. Do you want to overwrite? Y or N");
	// 		if(s.nextLine().equals("Y"))
	// 		{
	// 			PrintWriter pw = new PrintWriter(inFile);
	// 			while (f.hasNext())
	// 			{
	// 				pw.println(f.nextLine());
	// 			}
	// 		}
	// 	}
	// }

}