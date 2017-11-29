package catchat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

public class Gui extends JFrame {
	private JTextField userText;
	private JTextArea chatWindow;
	private JButton b;
	private String message;
	private String handle;
	private String oldHandle;
	private String[] commands;
	private Font font1;

	public Gui() {
		font1 = new Font("SansSerif", Font.BOLD, 15);

		userText = new JTextField("Type here...");
		userText.setEditable(true); // Needs to be changed to false
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				checkMessage(event.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.SOUTH);
		userText.setFont(font1);

		chatWindow = new JTextArea("Welcome to Cat Chat");
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(700, 700);
		setVisible(true);
		chatWindow.setEditable(false);
		this.setTitle("Cat Chat");
		chatWindow.setFont(font1);

		b = new JButton("Files");
		add(b, BorderLayout.EAST);

		userText.requestFocusInWindow();
		userText.selectAll();

		handle = JOptionPane.showInputDialog("Enter your desired handle: ");

		commands = new String[2];
		commands[0] = "/dance";
		commands[1] = "/handle";
	}

	public void showMessage(final String TEXT) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatWindow.append(TEXT);
			}
		});
	}

	private void checkMessage(String message) {
		String[] pieces = message.split(" ");
		if (pieces[0].equals("/dance"))
			showMessage("\nYour internet persona does a little dance.");
		else if (pieces[0].equals("/handle")) {
			oldHandle = handle;
			handle = message.substring(8);
			message = ("\n" + oldHandle + " has changed their handle to " + handle);
			showMessage(message);
		} else if (pieces[0].equals("/help")) {
			showMessage("\nCommands: ");
			for (int i = 0; i < commands.length; i++)
				showMessage("\n" + commands[i]);
		} else {
			message = "\n" + getTime() + " " + handle + " - " + message;
			showMessage(message);
		}
	}

	private void ableToType(final Boolean TOF) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userText.setEditable(TOF);
			}
		});
	}

	private String getTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return (sdf.format(cal.getTime()));
	}
}
