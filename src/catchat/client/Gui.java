package catchat.client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
	private JComboBox<String> downloadFiles;
	private JPanel panel;
	private String message;
	private String handle = "";
	private Font font1;
	private Socket connection;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Client client;

	public Gui() {
		font1 = new Font("SansSerif", Font.BOLD, 15);

		panel = new JPanel();
		panel.setLayout(new BorderLayout());

		userText = new JTextField("Type here...");
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendMessage(event.getActionCommand());
				userText.setText("");
			}
		});
		panel.add(userText, BorderLayout.NORTH);
		userText.setFont(font1);

		chatWindow = new JTextArea("Welcome to Cat Chat");
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		this.setBounds(560, 100, 799, 749);
		setVisible(true);
		chatWindow.setEditable(false);
		this.setTitle("Cat Chat");
		chatWindow.setFont(font1);

		fileChooseButton = new JButton("Upload File");
		panel.add(fileChooseButton, BorderLayout.CENTER);
		fileChooseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int choice = chooser.showOpenDialog(Gui.this);
				if (choice != JFileChooser.APPROVE_OPTION)
					return;
				File chosenFile = chooser.getSelectedFile();

				SerializableFile file;
				try {
					file = new SerializableFile(chosenFile);
					client.sendFile(file);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});

		/*
		 * downloadFiles = new JComboBox<>(); downloadFiles.addActionListener(new
		 * ActionListener() { public void actionPerformed(ActionEvent event) { String
		 * download = (String) downloadFiles.getSelectedItem();
		 * 
		 * SerializableFile down = client.requestFile(download); try {
		 * System.out.println("Calling saveFile"); down.saveFile(); } catch (IOException
		 * i) { i.printStackTrace(); } } }); panel.add(downloadFiles,
		 * BorderLayout.SOUTH);
		 */

		add(panel, BorderLayout.SOUTH);
		panel.setVisible(true);

		userText.requestFocusInWindow();
		userText.selectAll();

		do {
			handle = JOptionPane.showInputDialog("Enter your desired handle: ");
		} while (handle.equals(""));

		this.setBounds(560, 100, 800, 750);
		this.repaint();

		startRunning();
	}

	public void startRunning() {
		try {
			connectToServer();
			setUpStreams();
			whileChatting();
		} catch (EOFException eofException) {
			showMessage("\nServer terminated connection");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			close();
		}
	}

	private void connectToServer() throws IOException {
		connection = new Socket(/* "10.132.22.105" *//* "104.236.244.255" */"localhost", 12345);
	}

	private void setUpStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		client = new Client(input, output, handle);
	}

	public void whileChatting() throws IOException {
		ableToType(true);
		do {
			Object message = client.readObject();

			if (message instanceof String) {
				showMessage("\n" + message);
			} else if (message instanceof SerializableFile) {
				/*
				   System.out.println("Recived file"); 
				   SerializableFile file = (SerializableFile) message; file.saveFile(this);
				 */

				SerializableFile file = (SerializableFile) message;
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORY_ONLY);
				int choice = chooser.showSaveDialog(Gui.this);
				if (choice == JFileChooser.APPROVE_OPTION) {
					file.saveFile(this);
				}

			}

		} while (true);
	}

	private void sendMessage(String message) {
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
		showMessage("\nClsoing streams and sockets. \nDISCONNECTED");
		ableToType(false);
		try {
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}
