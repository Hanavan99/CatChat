package catchat.client;
import javax.swing.JFrame;

public class ClientMain {

	public static void main(String[] args) {
		Gui gui = new Gui();
		gui.setBounds(50, 50, 500, 500);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setVisible(true);
	}

}
