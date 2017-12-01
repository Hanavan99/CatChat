package catchat.client;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Scanner;

import javax.swing.JOptionPane;

//passed filename, byteArray of contents, stroe byteArray
// intenal methods to write file to path
//private read file
//
//client side they need WriteToFiel metdo given string
public class SerializableFile implements Serializable {

	private String fileName;
	private byte[] byteArray;

	public SerializableFile(File inFile) throws FileNotFoundException, IOException {
		fileName = inFile.getName();
		InputStream inStream = new FileInputStream(inFile);
		// ObjectInputStream s = new ObjectInputStream(inStream);
		// System.out.println(inFile.getTotalSpace());
		int size = (int) inFile.length();
		System.out.println("File size is " + size + "B");
		byteArray = new byte[size];
		inStream.read(byteArray, 0, byteArray.length);
		inStream.close();

	}

	public void saveFile() throws FileNotFoundException, IOException {
		File inFile = new File(fileName);
		OutputStream outStream = new FileOutputStream(inFile);
		System.out.println("Saving file to " + inFile.getAbsolutePath());
		if (inFile.exists()) {
			// System.out.println("It seems that the file " + inFile.toString() + "exists.
			// Do you want to overwrite? Y or N");
			try {
				int result = JOptionPane.showConfirmDialog(null, "It seems that the file " + inFile.toString() + "exists. Do you want to overwrite?");
				if (result == JOptionPane.YES_OPTION) {
					// PrintWriter pw = new PrintWriter(inFile);
					outStream.write(byteArray);
				}
			} catch (HeadlessException e) {
				e.printStackTrace();
				outStream.write(byteArray);
			}
		} else {
			outStream.write(byteArray);

		}
		outStream.close();
	}
}