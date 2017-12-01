package catchat.server;

import java.io.IOException;
import java.util.Scanner;

public class ServerMain {

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Not enough arguments. Usage: CatChat_Server.jar [port] [file upload path]");
			System.exit(1);
		}
		Server s = new Server("10.131.214.3", Integer.parseInt(args[0]), args[1]);
		s.start();

		Scanner in = new Scanner(System.in);
		System.out.println("Server Started. Type '/help' for help.");
		String[] command;
		while (in.hasNext()) {
			command = in.nextLine().split(" ", 2);
			switch (command[0]) {
			case "/exit":
			case "/quit":
				System.out.println("Closing server");
				s.stop();
				in.close();
				return;
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
				for (String file : s.getFileNames()) {
					System.out.println(file);
				}
				break;
			case "/say":
				if (command.length > 1)
					s.sendMessage("[SERVER] " + command[1]);
				break;
			case "/help":
				System.out.println("Commands are: /exit, /kick [username], /list, /listfiles, /quit, /say [message], /help");
				break;

			default:
				System.out.println("Invalid command");
				break;
			}
		}
	}

}
