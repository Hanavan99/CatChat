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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import catchat.core.Command;
import catchat.core.Directory;
import catchat.core.NetworkHandler;
import catchat.server.Client;

public class Gui extends JFrame {

	private static final long serialVersionUID = -6669011692663466124L;

	private JTextField userText;
	private JTextArea chatWindow;
	private JButton fileChooseButton;
	private JPanel panel;
	private String handle;
	private String ip;
	private Font font1;
	private Socket connection;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Client client;

	public Gui() {
		font1 = new Font("SansSerif", Font.BOLD, 15);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (InstantiationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (UnsupportedLookAndFeelException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		panel = new JPanel();
		panel.setLayout(new BorderLayout());

		userText = new JTextField("Type here...");
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (!event.getActionCommand().equals("")) {
					String[] cmd = userText.getText().split(" ", 2);
					switch (cmd[0]) {
					case "/download":
						client.sendCommand(new Command("getfile " + cmd[1]));
						break;
					case "/listfiles":
						client.sendCommand(new Command("listfiles"));
						break;
					case "/handle":
						client.sendCommand(new Command("sethandle " + cmd[1]));
						break;
					default:
						sendMessage(event.getActionCommand());
						break;
					}
					userText.setText("");
				}
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

		add(panel, BorderLayout.SOUTH);
		panel.setVisible(true);

		userText.requestFocusInWindow();
		userText.selectAll();
		
		ip = "";
		boolean flag;
		do {
			flag = true;
			ip = JOptionPane.showInputDialog("Enter \"104.236.244.255\" for Ubuntu sever or \"localhost\" to connect locally");
			if(ip == null)
				System.exit(0);
			else if(ip.equals("104.236.244.255"))
				flag = false;
			else if(ip.equals("localhost"))
				flag = false;
		} while (flag);
		
		handle = "";
		do {
			handle = JOptionPane.showInputDialog("Enter your desired handle: ");
			if(handle == null)
				handle = "Handle not set";
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
		connection = new Socket(ip, 12345); /* "10.132.22.105" *//* "104.236.244.255" *//* "localhost" */
	}

	private void setUpStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		client = new Client(input, output, handle);
		client.setNetworkHandler(new NetworkHandler(client) {

			@Override
			public void fileRecieved(SerializableFile file) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int choice = chooser.showSaveDialog(Gui.this);
				if (choice == JFileChooser.APPROVE_OPTION) {
					try {
						file.saveFile(Gui.this, chooser.getSelectedFile());
					} catch (IOException e) {
						System.out.println("Error saving file");
					}
				}
				else if(choice == JFileChooser.CANCEL_OPTION) {
					showMessage("\nYou pressed cancel.");
				}
			}

			@Override
			public void messageRecieved(String message) {
				showMessage("\n" + message);
			}

			@Override
			public void directoryListRecieved(Directory directory) {
				showMessage("\nFiles on server:");
				for (String file : directory.getFileNames()) {
					showMessage("\n" + file);
				}
			}

		});
	}

	public void whileChatting() throws IOException {
		ableToType(true);
		do {
			client.pollEvents(true);
		} while (true);
	}

	private void sendMessage(String message) {
		client.sendMessage(message);

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
				fileChooseButton.setEnabled(TOF);
			}
		});
	}

	private void close() {
		showMessage("\nClosing streams and sockets. \nDISCONNECTED");
		ableToType(false);
		try {
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}
