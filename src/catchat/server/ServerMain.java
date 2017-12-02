package catchat.server;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Main entry point for the server.
 * 
 * @author Hanavan Kuhn
 *
 */
public class ServerMain {

	/**
	 * Main method that starts the server.
	 * 
	 * @param args
	 *            the arguments from the console
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Not enough arguments. Usage: CatChat_Server.jar [port] [file upload path]");
			System.exit(1);
		}
		try {
			Server s = new Server("10.131.214.3", Integer.parseInt(args[0]), new File(args[1]));
			s.start();

			Scanner in = new Scanner(System.in);
			System.out.println("Server Started. Type '/help' for help.");
			String[] command;
			boolean running = true;
			while (running && in.hasNext()) {
				command = in.nextLine().split(" ", 2);
				switch (command[0]) {
				case "/execute":
					if (command.length > 1) {
						Process proc = null;
						if (((String) System.getProperties().get("os.name")).contains("Windows")) {
							proc = Runtime.getRuntime().exec("cmd.exe /c " + command[1]);
						} else {
							proc = Runtime.getRuntime().exec(command[1]);
						}
						InputStream procin = proc.getInputStream();
						int value;
						String line = "";
						while ((value = procin.read()) != -1) {
							char c = (char) value;
							if (c != '\n' && c != '\r') {
								line += c;
							} else {
								s.sendMessage(line);
								line = "";
							}
						}
					}
					break;
				case "/exit":
				case "/quit":
					System.out.println("Closing server");
					running = false;
					break;
				case "/kick":
					if (command.length > 1)
						s.kickClient(command[1]);
					break;
				case "/list":
					System.out.println("Current clients:");
					s.printClients();
					break;
				case "/listfiles":
					System.out.println("Files: ");
					for (String file : s.getFiles().getFileNames()) {
						System.out.println(file);
					}
					break;
				case "/say":
					if (command.length > 1)
						s.sendMessage("[SERVER] " + command[1]);
					break;
				case "/help":
					System.out.println("Commands are: /execute [command], /exit, /kick [username], /list, /listfiles, /quit, /say [message], /help");
					break;
				default:
					System.out.println("Invalid command");
					break;
				}
			}
			s.stop();
			in.close();
		} catch (Exception e) {
			System.out.println("Something went wrong on the server: " + e.getMessage());
		}
	}

}
