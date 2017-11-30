// package CatChat;
import java.io.*;
import java.net.*;
public class FileTransfer// implements Serializable
{
	
	public OutputStream outputStreamObject;
	public InputStream inputStreamObject;
	public FileOutputStream fileOutputStreamObject;
	public FileInputStream fileInputStreamObject;
	public BufferedInputStream bufferedInputStreamObject;
	public BufferedOutputStream bufferedOutputStreamObject;
	public File fileObject;
	public ServerSocket serverSocketObject;
	public String fileName;

	public Socket sock;


	public FileTransfer(Socket inSocket) throws FileNotFoundException, IOException//is for setting up a client
	{
		sock = inSocket;
		outputStreamObject = sock.getOutputStream();		
	}
	public FileTransfer(String fileName, int socketPort)throws IOException//is for server
	{
		serverSocketObject = new ServerSocket(socketPort);
		fileObject = new File(fileName);
	}
	public void acceptIncomingFile()throws IOException, FileNotFoundException//is the client method
	{
		int numOfBytesRead;
		int currentByte =0;
		byte[] byteArray = new byte[10000];
		//set up input
		InputStream inputStreamObject = sock.getInputStream();
		bufferedOutputStreamObject = new BufferedOutputStream(new FileOutputStream(fileName));
		numOfBytesRead = inputStreamObject.read(byteArray, 0,byteArray.length);
		currentByte = numOfBytesRead;
		boolean shouldContinue = true;;
		do
		{
			numOfBytesRead = inputStreamObject.read(byteArray, currentByte, (byteArray.length - currentByte));
			if(numOfBytesRead>= 0)
			{
				currentByte += numOfBytesRead;
				shouldContinue = ( numOfBytesRead> -1);
			} 
		}while (shouldContinue);
		bufferedOutputStreamObject.write(byteArray, 0, currentByte);
		bufferedOutputStreamObject.flush();
		fileOutputStreamObject.close();
		bufferedOutputStreamObject.close();
		sock.close();


	}
	public void sendFile() throws FileNotFoundException, IOException//is a server sending 
	{	//reading in file to send
		byte[] byteArray = new byte[(int)fileObject.length()];
		bufferedInputStreamObject = new BufferedInputStream(new FileInputStream(fileObject));
		bufferedInputStreamObject.read(byteArray,0,byteArray.length);
		//setting up output
		outputStreamObject  = sock.getOutputStream();
		outputStreamObject.write(byteArray,0,byteArray.length);
		outputStreamObject.flush();
	
			bufferedInputStreamObject.close();
			outputStreamObject.close();
			sock.close();
			serverSocketObject.close();
		
	}

}