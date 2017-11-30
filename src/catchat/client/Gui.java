package catchat.client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import catchat.server.Client;

public class Gui extends JFrame {

	private static final long serialVersionUID = -6669011692663466124L;

	private JTextField userText;
	private JTextArea chatWindow;
	private JButton fileChooseButton;
	private JButton downloadFileButton;
	private String message;
	private String handle = "";
	private Font font1;
	private Socket connection;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Client client;

	public Gui() {
		font1 = new Font("SansSerif", Font.BOLD, 15);

		userText = new JTextField("Type here...");
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendMessage(event.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.SOUTH);
		userText.setFont(font1);

		chatWindow = new JTextArea("Welcome to Cat Chat");
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		this.setBounds(560, 100, 800, 800);
		setVisible(true);
		chatWindow.setEditable(false);
		this.setTitle("Cat Chat");
		chatWindow.setFont(font1);
/*
		fileChooseButton = new JButton("Files");
		add(fileChooseButton, BorderLayout.SOUTH);
		fileChooseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int choice = chooser.showOpenDialog(Gui.this);
				if (choice != JFileChooser.APPROVE_OPTION)
					return;
				File chosenFile = chooser.getSelectedFile();

				SerializableFile file = new SerializableFile(chosenFile);
				client.sendFile(file);
			}
		});
*/
		userText.requestFocusInWindow();
		userText.selectAll();
		
		do{
			handle = JOptionPane.showInputDialog("Enter your desired handle: ");
		}while(handle.equals(""));

		startRunning();
	}

	public void startRunning() {
		try {
			connectToServer();
			setUpStreams();
			whileChatting();
		} catch (EOFException eofException) {
			showMessage("\nClient terminated connection");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			close();
		}
	}

	private void connectToServer() throws IOException {
<<<<<<< HEAD
		connection = new Socket(/*"10.132.22.105"*/"localhost", 12345);
=======
		connection = new Socket("localhost", 12345); //127.0.0.1 104.236.244.255
>>>>>>> branch 'master' of https://github.com/Hanavan99/CatChat.git
	}

	private void setUpStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		client = new Client(input, output, handle);
		System.out.println(client);

	}

	public void whileChatting() throws IOException {
		ableToType(true);
		do {
			message = (String) client.getMessage();
			showMessage("\n" + message);

		} while (!message.equals("/exit"));
	}

	private void sendMessage(String message) {
		System.out.println("Client sending message");
		try {
			client.sendMessage(message);
		} catch (IOException ioException) {
			chatWindow.append("\nMessage could not be sent!");
		}
	}

	public void showMessage(final String TEXT) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatWindow.append(TEXT);
			}
		});
	}

	private void ableToType(final Boolean TOF) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userText.setEditable(TOF);
			}
		});
	}

	private void close() {
		showMessage("\nClsoing streams and sockets. DISCONNECTED");
		ableToType(false);
		try {
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

	}
}