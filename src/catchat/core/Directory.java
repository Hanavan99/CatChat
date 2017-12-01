package catchat.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Directory implements Serializable {

	private static final long serialVersionUID = 7415496090249972399L;

	private List<String> files;

	public Directory(String[] files) {
		this.files = Arrays.asList(files);
	}

	public Directory(List<String> files) {
		this.files = files;
	}

	public String[] getFileNames() {
		return files.toArray(new String[0]);
	}

	public List<String> getFileNamesAsList() {
		return files;
	}

}
