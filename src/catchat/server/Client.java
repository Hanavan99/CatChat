package catchat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() throws IOException {
		try {
			String s = (String) oin.readObject();
			// System.out.println("Recieved message: " + s);
			return s;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void sendRaw(String raw) throws IOException {
		oout.writeObject(raw);
	}

	public Object readObject() throws IOException {
		try {
			return oin.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void sendMessage(String message) throws IOException {
		// out.println("message\n" + message);
		// System.out.println("Sending message: " + message);
		sendRaw("message");
		sendRaw(message);
	}

	public void sendFileNames(String[] files) throws IOException {
		oout.writeObject(files);
	}

	public String[] getFileNames() throws IOException {
		try {
			sendRaw("listfiles");
			String[] files = (String[]) oin.readObject();
			System.out.println(files.length);
			return files;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void writeFile(SerializableFile file) throws IOException {
		try {
			oout.writeObject(file);
		} catch (Exception e) {
			System.out.println("Failed to write file");
			e.printStackTrace();
		}
	}

	public void sendFile(SerializableFile file) {
		try {
			sendRaw("putfile");
			oout.writeObject(file);
		} catch (Exception e) {
			System.out.println("Failed to send file");
			e.printStackTrace();
		}
	}

	public SerializableFile getFile() throws IOException {
		try {
			return (SerializableFile) oin.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public SerializableFile requestFile(String name) {
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
	
	public String getTime(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return (sdf.format(cal.getTime())+" ");
    }

	public void close() throws IOException {
		oin.close();
		oout.close();
	}

}
