package catchat.server;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class Gui extends JFrame{
	private JTextField userText;
	private JTextArea chatWindow;
	private JButton fileChooseButton;
	private JButton downloadFileButton;
	private String message;
	private String handle;
	private String oldHandle;
	private String [] commands;
	private Font font1;
	private Socket connection;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	public Gui(){
		font1 = new Font("SansSerif", Font.BOLD, 15);
		
		userText = new JTextField("Type here...");
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.SOUTH);
		userText.setFont(font1);
		
		chatWindow = new JTextArea("Welcome to Cat Chat");
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(700,700);
		setVisible(true);
		chatWindow.setEditable(false);
		this.setTitle("Cat Chat");
		chatWindow.setFont(font1);
		
		fileChooseButton = new JButton("Files");
		add(b, BorderLayout.SOUTH);
		fileChooseButton.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JFileChooser chooser= new JFileChooser();
				int choice = choose.showOpenDialog();
				if (choice != JFileChooser.APPROVE_OPTION) return;
				File chosenFile = chooser.getSelectedFile();
				
				SerializableFile file = new SerializableFile(chosenFile);
				sendFile(file);
			}
		});
		
		userText.requestFocusInWindow();
		userText.selectAll();
		
		handle = JOptionPane.showInputDialog("Enter your desired handle: ");

		startRunning();
	}
	
	public void startRunning(){
		try
		{
			connectToServer();
			setUpStreams();
			whileChatting();
		}
		catch(EOFException eofException)
		{
			showMessage("\nClient terminated connection");
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
		finally
		{
			close();
		}
	}
	
	private void connectToServer() throws IOException{
		connection = new Socket("127.0.0.1" , 12345);
	}
	
	private void setUpStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		Client c = new Client(input, output);
		
	}
	
	public void whileChatting() throws IOException{
		ableToType(true);
		do{
				message = (String)c.getMessage();
				showMessage("\n" + message);
			
		}while(!message.equals("/exit"));
	}
	
	private void sendMessage(String message)
	{
		try{
			c.sendMessage(message);
		}
		catch(IOException ioException){
			chatWindow.append("\nMessage could not be sent!");
		}
	}
	
	public void showMessage(final String TEXT){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(TEXT);
				}
			}
		);
	}
	
	private void ableToType(final Boolean TOF){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(TOF);
				}
			}
		);
	}
	
	private void close()
	{
		showMessage("\nClsoing streams and sockets.");
		ableToType(false);
		try
		{
			connection.close();
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
		
	}
}