package handleFile;

import java.io.*;
import java.util.*;

public class FileParser {
	
	public FileParser() {
	}
	
	public ArrayList<String> parseLocalFiles() {
		File localFolder = new File(".");
		File[] listFiles = localFolder.listFiles();
		ArrayList<String> scriptList = new ArrayList<String>();
		String fileName = null;
		String time = null;
		boolean isTime;
		
		for(File f : listFiles) {
			isTime = true;
			fileName = f.toString();
			if(fileName.contains("_")) {
				if(fileName.contains(".txt")) {
					time = fileName.substring(fileName.indexOf('_') + 1, fileName.indexOf('.'));
				} else if(f.isDirectory()) {
					time = fileName.substring(fileName.indexOf('_') + 1);
				}
				for(char ch : time.toCharArray()) {
					if(!Character.isDigit(ch)) isTime = false;
					break;
				}
				if(isTime) {
					scriptList.add(f.toString().substring(2));
				}
			}
		}
		return scriptList;
	}
}
