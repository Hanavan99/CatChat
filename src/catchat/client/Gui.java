package catchat.client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Gui extends JFrame{
	private JTextField userText;
	private JTextArea chatWindow;
	private JButton b;
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
		userText.setEditable(true); //Needs to be changed to false
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					checkMessage(event.getActionCommand());
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
		
		b = new JButton("Files");
		add(b, BorderLayout.EAST);
		
		userText.requestFocusInWindow();
		userText.selectAll();
		
		handle = "Chuck"; //JOptionPane.showInputDialog("Enter your desired handle: ");
		
		commands = new String[2];
		commands[0] = "/dance";
		commands[1] = "/handle";
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
	
	private void checkMessage(String message){
		String [] pieces = message.split(" ");
		if(pieces[0].equals("/dance"))
			showMessage("\nYour internet persona does a little dance.");
		else if(pieces[0].equals("/handle")){
			oldHandle = handle;
			handle = message.substring(8);
			message = ("\n"+oldHandle+" has changed their handle to "+handle);
			showMessage(message);
		}
		else if(pieces[0].equals("/help")){
			showMessage("\nCommands: ");
			for(int i = 0; i < commands.length; i++)
				showMessage("\n"+commands[i]);
		}
		else{
			message = "\n"+getTime()+" "+handle+" - "+message;
			showMessage(message);
		}
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
	
	private String getTime(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return (sdf.format(cal.getTime()));
    }
	
	private void connectToServer() throws IOException{
		connection = new Socket("127.0.0.1" , 12345);
	}
	
	private void setUpStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		
	}
}