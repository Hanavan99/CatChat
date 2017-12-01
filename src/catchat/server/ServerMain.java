package catchat.server;

import java.io.IOException;
import java.util.Scanner;

public class ServerMain {

	public static void main(String[] args) throws IOException {
		Server s = new Server("10.131.214.3", 12345);
		s.start();

		Scanner in = new Scanner(System.in);
		System.out.println("Server Started. Type '/help' for help.");
		String[] command;
		while (in.hasNext()) {
			command = in.nextLine().split(" ");
			switch (command[0]) {
			case "/exit":
			case "/quit":
				System.out.println("Closing server");
				s.stop();
				in.close();
				return;
			case "/kick":
				s.kickClient(command[1]);
				break;
			case "/list":
				System.out.println("Current clients:");
				s.printClients();
				break;
			case "/listfiles":
				System.out.println("Files: ");
				for (String file : s.getFileNames()) {
					System.out.println(file);
				}
				break;
			case "/help":
				System.out.println("Commands are: /exit, /quit, /kick [username], /list, /listfiles, /help");
				break;
			
			default:
				System.out.println("Invalid command");
				break;
			}
		}
	}

}
