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
		username = user;
	}

	public boolean connected() {
		//return oin.;
		return true;
	}

	public String getUsername() {
		return username;
	}

	public String getMessage() throws IOException {
		return oin.readUTF();
	}

	public void sendMessage(String message) throws IOException {
		//out.println("message\n" + message);
		oout.writeUTF("message\n" + message);
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
			//out.println("getfile\n" + name);
			sendMessage("getfile\n");
			Object o = oin.readObject();
			return (SerializableFile) o;
		} catch (Exception e) {
			System.out.println("Failed to get file");
			return null;
		}
	}

}
