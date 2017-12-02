package catchat.core;

import catchat.client.SerializableFile;
import catchat.server.Client;

public abstract class NetworkHandler {

	protected Client client;

	public NetworkHandler(Client client) {
		this.client = client;
	}

	public abstract void fileRecieved(SerializableFile file);

	public abstract void messageRecieved(String message);

	public void directoryListRecieved(Directory directory) {
		// Does nothing by default
	}

	public void commandRecieved(Command command) {
		// Does nothing by default
	}

}
