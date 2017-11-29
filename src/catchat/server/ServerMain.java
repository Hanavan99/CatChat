package catchat.server;

import java.io.IOException;
import java.util.Scanner;

public class ServerMain {

	public static void main(String[] args) throws IOException {
		Server s = new Server("127.0.0.1", 12345);
		s.start();

		Scanner in = new Scanner(System.in);
		System.out.println("Server Started. Enter commands below.");
		String command;
		while (in.hasNext()) {
			command = in.nextLine();
			switch (command) {
			case "/exit":
				System.out.println("Closing server");
				s.stop();
				in.close();
				return;
			default:
				System.out.println("Invalid command");
				break;
			}
		}
	}

}
