package catchat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import catchat.client.SerializableFile;
import catchat.core.Command;
import catchat.core.Directory;
import catchat.core.NetworkHandler;

/**
 * Represents a client on both the client and the server.
 * 
 * @author Hanavan Kuhn
 *
 */
public class Client {

	private ObjectInputStream oin;
	private ObjectOutputStream oout;
	private NetworkHandler handler;
	private String username;
	private boolean connected = true;

	/**
	 * Creates a new client with a {@code ObjectInputStream} and
	 * {@code ObjectOutputStream} that handles sending and receiving objects.
	 * 
	 * @param oin
	 *            the object input stream
	 * @param oout
	 *            the object output stream
	 * @param user
	 *            the username; if on creation the username is unknown, specify
	 *            {@code null}
	 * @throws IOException
	 *             if there is a problem sending the username to the server, if
	 *             applicable
	 */
	public Client(ObjectInputStream oin, ObjectOutputStream oout, String user) throws IOException {
		this.oin = oin;
		this.oout = oout;
		if (user != null) {
			username = user;
			putString(user);
		} else {
			username = getString();
		}
	}

	public void setNetworkHandler(NetworkHandler handler) {
		this.handler = handler;
	}

	/**
	 * Gets whether or not the client is connected. Used only on the server.
	 * 
	 * @return the connection status
	 */
	public boolean connected() {
		return connected;
	}

	/**
	 * Gets the username associated with this client.
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username associated with this client. Used only on the server side.
	 * 
	 * @param username
	 *            the new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets a string from the current {@code ObjectInputStream}.
	 * 
	 * @return the string
	 * @throws IOException
	 *             if reading the string fails
	 */
	private String getString() throws IOException {
		try {
			String s = (String) oin.readObject();
			return s;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Sends a string using the current {@code ObjectOutputStream}.
	 * 
	 * @param string
	 *            the string to send
	 * @throws IOException
	 *             if writing the string fails
	 */
	public void putString(String string) throws IOException {
		oout.writeObject(string);
	}

	// /**
	// * Reads a generic object from the current {@code ObjectInputStream}.
	// *
	// * @return the object
	// * @throws IOException
	// * if reading the string fails
	// */
	// public Object readObject() throws IOException {
	// try {
	// return oin.readObject();
	// } catch (ClassNotFoundException e) {
	// e.printStackTrace();
	// return null;
	// }
	// }

	/**
	 * Sends a formatted message using the current {@code ObjectOutputStream}.
	 * 
	 * @param message
	 *            the message to send
	 */
	public void sendMessage(String message) {
		writeObject(message);
	}

	public void sendDirectoryList(Directory directory) {
		writeObject(directory);
	}

	/**
	 * Writes an object to the current {@code ObjectOutputStream}.
	 * 
	 * @param o
	 *            the object
	 */
	private void writeObject(Object o) {
		try {
			oout.writeObject(o);
		} catch (IOException e) {
			System.out.println("Error writing object " + o.getClass());
		}
	}

	/**
	 * Writes a file to the current {@code ObjectOutputStream}.
	 * 
	 * @param file
	 *            the file
	 */
	public void sendFile(SerializableFile file) {
		writeObject(file);
	}

	public void sendCommand(Command command) {
		writeObject(command);
	}

	public void requestFile(String filename) {
		writeObject(new Command("getfile " + filename));
	}

	/**
	 * Polls the server for objects. If block is {@code true}, this method will
	 * block until an object is recieved.
	 * 
	 * @param block
	 *            if the method should block
	 * @throws IOException
	 *             if there is an error reading the object
	 */
	public void pollEvents(boolean block) throws IOException {
		try {
			if ((oin.available() > 0 || block) && handler != null) {
				Object o = oin.readObject();
				if (o instanceof String) {
					handler.messageRecieved(o.toString());
				} else if (o instanceof SerializableFile) {
					handler.fileRecieved((SerializableFile) o);
				} else if (o instanceof Command) {
					handler.commandRecieved((Command) o);
				} else {
					System.out.println("Server recieved a hot potato, throwing it to the next person");
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes this client's connection
	 * 
	 * @throws IOException
	 *             if the client disconnects
	 */
	public void close() throws IOException {
		oin.close();
		oout.close();
	}

}
