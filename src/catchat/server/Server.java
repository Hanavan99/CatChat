package catchat.server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import catchat.client.SerializableFile;

//import catchat.client.SerializableFile;

public class Server {

	private static final String FILE_PATH = "./files/";

	private ServerSocket server;
	private Map<String, String> messages = new HashMap<String, String>();
	private List<Client> clients = new ArrayList<Client>();
	private Map<String, String> messageQueue = new HashMap<String, String>();
	boolean running = false;
	Thread serverThread;
	Thread chatThread;

	public Server(String address, int port) throws IOException {
		server = new ServerSocket();
		server.bind(new InetSocketAddress(address, port));

		serverThread = new Thread(() -> {
			try {
				while (running) {
					try {
						Socket client = server.accept();
						Thread clientThread = new Thread(() -> handleClient(client));
						clientThread.start();
					} catch (IOException e) {
						if (running) {
							System.out.println("Something went wrong with a client, disconnecting");
						}
					}
					Thread.sleep(0);
				}
			} catch (InterruptedException e) {
				System.out.println("Server thread exited");
			}
		});

		chatThread = new Thread(() -> {
			while (running) {
				try {
					Thread.sleep(Long.MAX_VALUE);
				} catch (InterruptedException e) {
					Iterator<Entry<String, String>> itr = messageQueue.entrySet().iterator();
					while (itr.hasNext()) {
						Entry<String, String> entry = itr.next();
						for (Client c : clients) {
							try {
								c.sendMessage(entry.getKey() + ": " + entry.getValue());
							} catch (Exception ex) {
								System.out.println("Failed to send message to client");
							}
						}
					}
					messageQueue.clear();
				}
			}
			System.out.println("Chat thread exited");
		});
	}

	public void start() {
		running = true;
		serverThread.start();
		chatThread.start();
	}

	private void handleClient(Socket client) {
		try {
			ObjectInputStream oin = new ObjectInputStream(client.getInputStream());
			ObjectOutputStream oout = new ObjectOutputStream(client.getOutputStream());
			Client c = new Client(oin, oout);
			clients.add(c);
			while (c.connected()) {
				String line = c.getMessage();
				switch (line) {
				case "message":
					messageQueue.put(c.getUsername(), c.getMessage());
					chatThread.interrupt();
					break;
				case "putfile":
					//byte[] byteArray = SerializableFile.toBytes(readFile(oin));
					
					// TODO write the file to disk for reading later
					break;
				case "getfile":
					String filename = c.getMessage();
					oout.writeObject(getFileFromDisk(filename));
					break;
				}
			}
			clients.remove(c);
		} catch (IOException e) {
			System.out.println("Couldn't establish a connection");
			e.printStackTrace();
		}
	}

	public void stop() {
		running = false;
		try {
			server.close();
		} catch (IOException e) {
			System.out.println("Failed to close server");
		}
		serverThread.interrupt();
		chatThread.interrupt();
	}

	private File readFile(ObjectInputStream oin) {
		try {
			File f = (File)oin.readObject();
			return f;
		} catch (Exception e) {
			System.out.println("Error reading file");
			return null;
		}
	}

	private byte[] getFileFromDisk(String name) {
		File result = null;
		for (File f : new File(FILE_PATH).listFiles()) {
			if (!f.isDirectory()) {
				if (f.getName().equals(name)) {
					result = f;
					break;
				}
			}
		}
		if (result != null) {
			try {
				// TODO load file into class
				//return SerializableFile.toBytes(result);
			} catch (Exception e) {
				System.out.println("Failed to create a SerializableFile");
			}
		}
		return null;
	}
	
	public void printClients() {
		for (Client c : clients) {
			System.out.println(c.getUsername());
		}
	}

}
