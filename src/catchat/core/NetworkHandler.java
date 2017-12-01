package catchat.core;

import catchat.client.SerializableFile;

public abstract class NetworkHandler {

	public abstract void fileRecieved(SerializableFile file);

	public abstract void messageRecieved(String message);
	
	public void directoryListRecieved(Directory directory) {
		// Does nothing by default
	}

	public void commandRecieved(Command command) {
		// Does nothing by default
	}

}