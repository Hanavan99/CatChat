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

	private String filePath = ".";

	private ServerSocket server;
	private Map<String, String> messages = new HashMap<String, String>();
	private List<Client> clients = new ArrayList<Client>();
	private Map<String, String> messageQueue = new HashMap<String, String>();
	boolean running = false;
	Thread serverThread;
	Thread chatThread;

	public Server(String address, int port, String rootDir) throws IOException {
		server = new ServerSocket(port);
		filePath = rootDir;
		new File(filePath).mkdirs();
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
					Iterator<Entry<String, String>> itr = messageQueue.entrySet().iterator();
					while (itr.hasNext()) {
						Entry<String, String> entry = itr.next();
						sendMessage(entry.getKey() + ": " + entry.getValue());
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
					file.saveFile();
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

	public void kickClient(String username) {
		for (Client c : clients) {
			if (c.getUsername().equals(username)) {
				try {
					c.sendMessage("You have been kicked from the server.");
					c.close();
				} catch (IOException e) {
					System.out.println("Failed to kick client: " + e.getMessage());
				}
			}
		}
	}
	
	public void sendMessage(String message) {
		System.out.println(message);
		for (Client c : clients) {
			try {
				c.sendRaw(message);
			} catch (Exception ex) {
				System.out.println("Failed to send message to client");
			}
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
		File[] files = new File(filePath).listFiles();
		List<String> fileNames = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory())
				fileNames.add(files[i].getName());
		}
		return fileNames.toArray(new String[0]);
	}

}
