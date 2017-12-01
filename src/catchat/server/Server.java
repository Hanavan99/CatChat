package catchat.server;

import java.io.File;
import java.io.FileNotFoundException;
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
import catchat.core.NetworkHandler;

/**
 * The {@code Server} class manages a chat server that sends messages back and
 * forth, and allows for file upload and download.
 * 
 * @author Hanavan Kuhn
 *
 */
public class Server {

	private File fileDir = new File("files/");

	private ServerSocket server;
	private Map<Long, String> messages = new HashMap<Long, String>();
	private List<Client> clients = new ArrayList<Client>();
	private Map<Long, String> messageQueue = new HashMap<Long, String>();
	private Long messageID = 0L;
	boolean running = false;
	Thread serverThread;
	Thread chatThread;

	/**
	 * Creates a new server with the specified bind address, port, and
	 * 
	 * @param address
	 *            the address for the server to bind to
	 * @param port
	 *            the port to bind to
	 * @param fileDir
	 *            the root directory for users to store files in
	 * @throws IOException
	 *             if the server cannot be created
	 */
	public Server(String address, int port, File fileDir) throws IOException {
		server = new ServerSocket(port);
		this.fileDir = fileDir;
		fileDir.mkdirs();
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

					Iterator<Entry<Long, String>> itr = messageQueue.entrySet().iterator();
					while (itr.hasNext()) {

						Entry<Long, String> entry = itr.next();
						sendMessage(entry.getValue());
						messages.put(entry.getKey(), entry.getValue());
					}
					messageQueue.clear();
				}
			}
			System.out.println("Chat thread exited");
		});
	}

	/**
	 * Starts the server.
	 */
	public void start() {
		running = true;
		serverThread.start();
		chatThread.start();
	}

	/**
	 * Handles a connection to the server. This method covers the entire lifecycle
	 * of the client's connection.
	 * 
	 * @param client
	 *            The client that connected
	 */
	private void handleClient(Socket client) {
		try {
			ObjectInputStream oin = new ObjectInputStream(client.getInputStream());
			ObjectOutputStream oout = new ObjectOutputStream(client.getOutputStream());
			Client c = new Client(oin, oout, null, new NetworkHandler() {

				@Override
				public void fileRecieved(SerializableFile file) {
					try {
						file.saveFile(null, fileDir);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void messageRecieved(String message) {
					messageQueue.put(createMessageID(), message);
					chatThread.interrupt();
				}

			});
			clients.add(c);
			for (String message : messages.values()) {
				c.putString(message);
			}
			while (c.connected()) {
				c.pollEvents(true);
				// String line = c.getString();
				// switch (line) {
				// case "message":
				//
				// String message = c.getString();
				// String[] args = message.split(" ", 2);
				// switch (args[0]) {
				// case "/download":
				// if (args.length > 1) {
				// File f = new File(fileDir, args[1]);
				// System.out.println("Client requested file " + f.getAbsolutePath());
				// if (f.exists()) {
				// c.writeFile(new SerializableFile(f));
				// } else {
				// c.putString("File does not exist. List the files using /listfiles.");
				// }
				// }
				// break;
				// case "/handle":
				// if (args.length > 1) {
				// c.setUsername(args[1]);
				// c.putString("Handle changed to '" + args[1] + "'");
				// }
				// break;
				// case "/help":
				// c.sendMessage("Commands are: /download [filename], /handle [username],
				// /help");
				// break;
				// default:
				// messageQueue.put(createMessageID(), "[" + new
				// SimpleDateFormat("hh:mm:ss").format(Calendar.getInstance().getTime()) + "] "
				// + c.getUsername() + ": " + message);
				// chatThread.interrupt();
				// break;
				// }
				// break;
				// case "getfile":
				// c.sendFile(new SerializableFile(new File(c.getString())));
				// break;
				// case "putfile":
				// SerializableFile file = c.getFile();
				// file.saveFile(null);
				// c.sendMessage("File uploaded.");
				// break;
				// case "listfiles":
				// c.sendFileNames(getFileNames());
				// break;
				// }
			}
			clients.remove(c);
		} catch (IOException e) {
			System.out.println("Couldn't establish a connection");
			e.printStackTrace();
		}
	}

	/**
	 * Creates a message ID that is thread-safe.
	 * 
	 * @return the message ID
	 */
	public long createMessageID() {
		long id;
		synchronized (messageID) {
			id = messageID;
			messageID++;
		}
		return id;
	}

	/**
	 * Kicks a client with the specified username by sending them a message and then
	 * closing their connection.
	 * 
	 * @param username
	 *            the client's username to kick
	 */
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

	/**
	 * Sends a raw string message to every client, including the server.
	 * 
	 * @param message
	 *            the message to send
	 */
	public void sendMessage(String message) {
		System.out.println(message);
		for (Client c : clients) {
			try {
				c.putString(message);
			} catch (Exception ex) {
				System.out.println("Failed to send message to client");
			}
		}
	}

	/**
	 * Stops the server.
	 */
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

	/**
	 * Prints all of the clients to the console.
	 */
	public void printClients() {
		for (Client c : clients) {
			System.out.println(c.getUsername());
		}
	}

	/**
	 * Gets the list of downloadable files from the server.
	 * 
	 * @return the list of files
	 */
	public String[] getFileNames() {
		File[] files = fileDir.listFiles();
		List<String> fileNames = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory())
				fileNames.add(files[i].getName());
		}
		return fileNames.toArray(new String[0]);
	}

}
