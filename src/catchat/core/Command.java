package catchat.core;

import java.io.Serializable;

public class Command implements Serializable {

	private static final long serialVersionUID = -3688640147826151675L;

	private String command;

	public Command(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}

}
