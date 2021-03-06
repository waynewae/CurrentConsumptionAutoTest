package handleFile;

import javax.swing.*;

import java.io.*;

public class FileHandler {
	String fileName;
	String prefix;
	String name;
	String durationStr;
	String configPath;
	String dirPath;
	
	int duration = 0;
	
	public FileHandler() {}
	
	public void exportBatchfile (ListModel<String> listModel, int CurrMeas) throws IOException {
		FileWriter bat = new FileWriter("AutoTest.bat");
   		for(int i = 0 ; i < listModel.getSize() ; i++) {
   			fileName = listModel.getElementAt(i);
   			if(fileName.contains(".")) {
   				prefix = fileName.substring(0, fileName.indexOf('.'));
   			} else {
   				prefix = fileName;
   			}
	   			name = prefix.substring(0, prefix.indexOf('_'));
	   			durationStr = prefix.substring(prefix.indexOf('_') + 1);
	   			
	   			// if not 1st script, wait 10s
	   			if(i != 0) {
	   				if(CurrMeas == 1) {
	   					bat.write("ping 127.0.0.1 -n 10 -w 1000 > nul" + "\n");
	   				} else {
		   				duration = Integer.parseInt(durationStr) + 10;
		   				bat.write("ping 127.0.0.1 -n "
		   						+ duration
		   						+ " -w 1000 > nul" + "\n");
		   			}
	   			}
	   			
	   			// write AutoTest cmd
	   			bat.write("adb shell am start -a com.fihtdc.autotesting.autoaction "
	   					+ "-n com.fihtdc.autotesting/.AutoTestingMain -e path /sdcard/AutoTesting/"
	   					+ prefix
	   					+ "/" + "\n");
	   			if(CurrMeas == 1) {
		   			// wait 5s between AutoTest and Current record
		   			bat.write("ping 127.0.0.1 -n 5 -w 1000 > nul" + "\n");
		   			// write PowerTool cmd
		   			bat.write("PowerToolCmd /savefile=" + name + ".pt4"
		   					+ " /trigger=DTYD" + duration + "A "
		   					+ "/vout=3.8 /USB=AUTO /keeppower /noexitwait" + "\n");
	   			}
   		}	        
        bat.flush();
        bat.close();
	}
	
	public void exportScript (ListModel<String> listModel, int CurrMeas) {
		for(int i = 0 ; i < listModel.getSize() ; i++) {
			fileName = listModel.getElementAt(i);			
			if(fileName.contains(".")) {
				prefix = fileName.substring(0, listModel.getElementAt(i).indexOf('.'));
				
				// create config.xml
				dirPath = "./" + prefix;
				configPath = dirPath + "/config.xml";
				File config = new File(dirPath);
				config.mkdirs();
				
				// write it
				try {
					createConfig(configPath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// move script into dir
				moveScript(fileName , dirPath + "/" + fileName + ".txt");
			}		
		}
		if(CurrMeas == 0){
			// create config.xml in <Tool_Home>/AutotestScripts/
			dirPath = "./AutotestScripts/";
			configPath = dirPath + "config.xml";
			File config = new File(dirPath);
			config.mkdirs();
			
			// write it
			try {
				createConfig(configPath, listModel);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// copy script to <Tool_Home>/AutotestScripts/
			for(int i = 0 ; i < listModel.getSize() ; i++) {
				fileName = listModel.getElementAt(i);	
				copyScript(fileName , dirPath + "/" + fileName + ".txt");
			}
		}
	}
	
	// export scripts and config file without current measurement
	private void createConfig(String configPath, ListModel<String> listModel) throws IOException {
		FileWriter configWriter = new FileWriter(configPath);
		configWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n");
		configWriter.write("<AutoTest>" + "\n");
		configWriter.write("<Prefs>" + "\n");
		configWriter.write("<LogReport>" + "\n");
		configWriter.write("<LogPathName>/sdcard/AutoTesting/.report/" + fileName + ".txt</LogPathName>" + "\n");
		configWriter.write("</LogReport>" + "\n");
		configWriter.write("</Prefs>" + "\n");
		configWriter.write("<LoopCount>1</LoopCount>" + "\n");
		// write test cases
		for(int i = 0 ; i < listModel.getSize() ; i++) {
			fileName = listModel.getElementAt(i);
			configWriter.write("<TestCase>" + "\n");
			configWriter.write("<check>true</check>" + "\n");
			configWriter.write("<name>" + fileName + ".txt</name>" + "\n");
			configWriter.write("<loop>1</loop> " + "\n");
			configWriter.write("</TestCase>" + "\n");
		}
		configWriter.write("</AutoTest>" + "\n");
		configWriter.flush();
		configWriter.close();
	}
	
	// export scripts and config files for current measurement
	private void createConfig(String configPath) throws IOException {
		FileWriter configWriter = new FileWriter(configPath);
		configWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n");
		configWriter.write("<AutoTest>" + "\n");
		configWriter.write("<Prefs>" + "\n");
		configWriter.write("<LogReport>" + "\n");
		configWriter.write("<LogPathName>/sdcard/AutoTesting/.report/" + fileName + ".txt</LogPathName>" + "\n");
		configWriter.write("</LogReport>" + "\n");
		configWriter.write("</Prefs>" + "\n");
		configWriter.write("<LoopCount>1</LoopCount>" + "\n");
		configWriter.write("<TestCase>" + "\n");
		configWriter.write("<check>true</check>" + "\n");
		configWriter.write("<name>" + fileName + ".txt</name>" + "\n");
		configWriter.write("<loop>1</loop> " + "\n");
		configWriter.write("</TestCase>" + "\n");
		configWriter.write("</AutoTest>" + "\n");
		configWriter.flush();
		configWriter.close();
	}
	
	private void moveScript(String OriFilePath, String NewFilePath) {
		File source = new File(OriFilePath);
		File dst = new File(NewFilePath);
		source.renameTo(dst);
	}
	
	private void copyScript(String OriFilePath, String NewFilePath) {
		File source = new File(OriFilePath);
		File dst = new File(NewFilePath);
		try {
			InputStream in = new FileInputStream(source);
			OutputStream out = new FileOutputStream(dst);
			
			// For creating a byte type buffer
			byte[] buf = new byte[1024];
			int len;
			// For writing to another specified file from buffer buf
			while ((len = in.read(buf)) > 0){
		        out.write(buf, 0, len);
	        }
			in.close();
		    out.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
