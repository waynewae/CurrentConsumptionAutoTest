package handleFile;

import java.io.*;
import java.util.*;

public class FileParser {
	public FileParser() {}
	
	public ArrayList<String> parseLocalFiles() {
		File localFolder = new File(".");
		File[] listFiles = localFolder.listFiles();
		ArrayList<String> scriptList = new ArrayList<String>();
		for(File f : listFiles) {
			if(f.toString().contains("_")
					&& (f.toString().contains(".txt") 
							|| f.isDirectory()))
				scriptList.add(f.toString().substring(2));
		}
		return scriptList;
	}
}
