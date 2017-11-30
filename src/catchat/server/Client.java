package catchat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import catchat.client.SerializableFile;

public class Client {

	private ObjectInputStream oin;
	private ObjectOutputStream oout;
	private String username;

	public Client(ObjectInputStream oin, ObjectOutputStream oout, String user) throws IOException {
		this.oin = oin;
		this.oout = oout;
		if (user != null) {
			username = user;
			sendRaw(user);
		} else {
			username = getMessage();
		}
	}

	public boolean connected() {
		return true;
	}

	public String getUsername() {
		return username;
	}

	public String getMessage() throws IOException {
		try {
			String s = (String) oin.readObject();
			//System.out.println("Recieved message: " + s);
			return s;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void sendRaw(String raw) throws IOException {
		oout.writeObject(raw);
	}

	public void sendMessage(String message) throws IOException {
		// out.println("message\n" + message);
		//System.out.println("Sending message: " + message);
		oout.writeObject("message");
		oout.writeObject(message);
	}

	public void sendFile(SerializableFile file) {
		try {
			oout.writeObject(file);
		} catch (Exception e) {
			System.out.println("Failed to send file");
			e.printStackTrace();
		}
	}

	public SerializableFile getFile(String name) {
		try {
			// out.println("getfile\n" + name);
			sendRaw("getfile\n");
			Object o = oin.readObject();
			return (SerializableFile) o;
		} catch (Exception e) {
			System.out.println("Failed to get file");
			return null;
		}
	}

}
