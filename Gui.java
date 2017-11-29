import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Gui extends JFrame{
	private JTextField userText;
	private JTextArea chatWindow;
	private String message;
	private String handle;
	private String oldHandle;
	private String [] commands;
	private Font font1;
	
	public Gui(){
		font1 = new Font("SansSerif", Font.BOLD, 15);
		
		userText = new JTextField();
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
		
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(700,700);
		setVisible(true);
		chatWindow.setEditable(false);
		this.setTitle("Chat Client");
		chatWindow.setFont(font1);
		
		userText.requestFocusInWindow();
		
		handle = "Chuck"; //JOptionPane.showInputDialog("Enter your desired handle: ");
		
		commands = new String[2];
		commands[0] = "/dance";
		commands[1] = "/handle";
	}
	
	public String sendMessage(String message, boolean normal){
		if(normal){
			message = "\n"+handle+" - "+message;
			showMessage(message);
			return message;
		}
		else{
			showMessage(message);
			return (message);
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
	
	private void checkMessage(String message){
		String [] pieces = message.split(" ");
		if(pieces[0].equals("/dance"))
			showMessage("\nYour internet persona does a little dance.");
		else if(pieces[0].equals("/handle")){
			oldHandle = handle;
			handle = message.substring(8);
			sendMessage("\n"+oldHandle+" has changed their handle to "+handle, false);
		}
		else if(pieces[0].equals("/help")){
			showMessage("\nCommands: ");
			for(int i = 0; i < commands.length; i++)
				showMessage("\n"+commands[i]);
		}
		else
			sendMessage(message, true);
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
}