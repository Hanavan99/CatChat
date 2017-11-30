package catchat.client;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
//passed filename, byteArray of contents, stroe byteArray
// intenal methods to write file to path
//private read file
//
//client side they need WriteToFiel metdo given string
public class SerializableFile implements Serializable
{
	
	private String fileName;
	private byte[] byteArray;


	public SerializableFile(File inFile) throws FileNotFoundException, IOException
	{
		fileName = inFile.getName();
		InputStream inStream = new FileInputStream(inFile);
		//ObjectInputStream s = new ObjectInputStream(inStream);
		byteArray = new byte[(int)inFile.getTotalSpace()];
		inStream.read(byteArray,0, byteArray.length);
		inStream.close();

		
	}
	
	private File deSerialize(byte[] inByteArray) throws IOException
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
	public void saveFile() throws FileNotFoundException, IOException
	{
		File inFile = new File(fileName);
		OutputStream outStream = new FileOutputStream(inFile);
		Scanner f = new Scanner(inFile);
		if (inFile.exists())
		{
			//System.out.println("It seems that the file " + inFile.toString() + "exists. Do you want to overwrite? Y or N");
			int result = JOptionPane.showConfirmDialog(null,"It seems that the file " + inFile.toString() + "exists. Do you want to overwrite?");
			if(result == 1)
			{
				//PrintWriter pw = new PrintWriter(inFile);
				outStream.write(byteArray);
			}
		}
		else
		{
			outStream.write(byteArray);
		}
		outStream.close();
	}
}