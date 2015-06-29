package handleFile;

import java.io.*;
import java.util.*;

public class FileParser {
	public FileParser() {}
	
	public ArrayList<String> parseLocalFiles() {
		File localFolder = new File(".");
		String[] localFileList = localFolder.list();
		ArrayList<String> scriptList = new ArrayList<String>();
		for(int i = 0 ; i < localFileList.length ; i++) {
			if(localFileList[i].contains("_")
					&& localFileList[i].contains(".txt"))
				scriptList.add(localFileList[i]);
		}
		return scriptList;
	}
}
