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

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Provides a transportation method for physical files. Works with any file
 * type. Max. size of 2GB.
 * 
 * @author Hanavan Kuhn
 *
 */
public class SerializableFile implements Serializable {

	/**
	 * Leave this, dont delete
	 */
	private static final long serialVersionUID = 5224336157061489239L;

	private String fileName;
	private byte[] byteArray;

	/**
	 * Creates a new {@code SerializableFile} object with the specified file, and
	 * then loads it into memory.
	 * 
	 * @param inFile
	 *            the path to the file
	 * @throws FileNotFoundException
	 *             if the file could not be found
	 * @throws IOException
	 *             if there was an error reading the file
	 */
	public SerializableFile(File inFile) throws FileNotFoundException, IOException {
		fileName = inFile.getName();
		InputStream inStream = new FileInputStream(inFile);
		int size = (int) inFile.length();
		System.out.println("File size is " + size + "B");
		byteArray = new byte[size];
		inStream.read(byteArray, 0, byteArray.length);
		inStream.close();

	}

	/**
	 * Saves the file to disk, and uses the specified {@code JFrame} to properly
	 * display a dialog box if the file already exists.
	 * 
	 * @param parent
	 *            the parent frame
	 * @param path
	 *            the path to save to
	 * @throws FileNotFoundException
	 *             if the file could not be found
	 * @throws IOException
	 *             if there was an error writing the file
	 */
	public void saveFile(JFrame parent, File path) throws FileNotFoundException, IOException {
		File inFile = new File(path, fileName);
		boolean write = false;
		if (inFile.exists() && parent != null) {
			try {
				int result = JOptionPane.showConfirmDialog(parent, "It seems that the file " + inFile.toString() + "exists. Do you want to overwrite?");
				if (result == JOptionPane.YES_OPTION) {
					write = true;
				}
			} catch (HeadlessException e) {
				System.out.println("Program is running headless, overwriting file");
				write = true;
			}
		} else {
			write = true;
		}
		if (write) {
			OutputStream outStream = new FileOutputStream(inFile);
			System.out.println("Client uploaded file; saving to " + inFile.getAbsolutePath());
			outStream.write(byteArray);
			outStream.close();
		}
	}
}