package catchat.server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
		//server = new ServerSocket();
		server = new ServerSocket(port);
		//server.bind(new InetSocketAddress(address, port));

		serverThread = new Thread(() -> {
			try {
				while (running) {
					try {
						Socket client = server.accept();
						System.out.println("Client connected");
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
					// System.out.println("Thread interrupted");
					Iterator<Entry<String, String>> itr = messageQueue.entrySet().iterator();
					while (itr.hasNext()) {
						Entry<String, String> entry = itr.next();
						for (Client c : clients) {
							try {
								// System.out.println("Sending message to " + c.getUsername());
								c.sendRaw(entry.getKey() + ": " + entry.getValue());
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
			Client c = new Client(oin, oout, null);
			clients.add(c);
			while (c.connected()) {
				String line = c.getMessage();
				switch (line) {
				case "message":
					// System.out.println("Recieved message from " + c.getUsername() + ": " +
					// c.getMessage());
					messageQueue.put(c.getUsername(), c.getMessage());
					chatThread.interrupt();
					break;
				case "getfile":
					c.sendFile(new SerializableFile(new File(c.getMessage())));
					break;
				case "putfile":
					SerializableFile file = c.getFile();
					file.saveFile(null);
					c.sendMessage("File uploaded.");
					break;
				case "listfiles":
					c.sendFileNames(getFileNames());
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

	public void printClients() {
		for (Client c : clients) {
			System.out.println(c.getUsername());
		}
	}
	
	public String[] getFileNames() {
		File[] files = new File(FILE_PATH).listFiles();
		String[] fileNames = new String[files.length];
		for (int i = 0; i < fileNames.length; i++) {
			fileNames[i] = files[i].getName();
		}
		return fileNames;
	}

}
