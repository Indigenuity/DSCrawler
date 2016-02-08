 package datatransfer;

import global.Global;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import play.api.libs.Files;

public class Amalgamater {
	
	public final static String AMALGAMATED_FILENAME = "amalgamated.txt";
	public final static String AMALGAMATED_FOLDER_NAME = "/combined";
	
	//Combine all file in a folder, eliminating duplicate lines
	public static File amalgamateFiles(File folder, File destination) throws IOException{
		if(folder == null || !folder.exists() || !folder.isDirectory()){
			throw new IllegalArgumentException("Can't amalgamate on this directory -- bad directory");
		}
		LinkedHashSet<String> lines = new LinkedHashSet<String>();
		if(!destination.exists()){
			destination.mkdirs();
		}
		File out = new File(destination.getAbsolutePath() + "/" +  AMALGAMATED_FILENAME);
		
		int totalLines = 0;
		int totalFiles = 0;
		int amalg = 0;
		for(File file : folder.listFiles()){
			totalFiles++;
			if(file.isFile() && !isAmalgamation(file)){
				amalg++;
				FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
				String text = IOUtils.toString(inputStream);
				inputStream.close();
				List<String> tokens = Arrays.asList(text.split("\\n"));
//				System.out.println("tokens: " + tokens.size());
				totalLines += tokens.size();
				lines.addAll(tokens);
//				System.out.println("totalLines : " + totalLines);
//				System.out.println("actualines : " + lines.size());
			}
		}
		System.out.println("totalfiles : " + totalFiles);
		System.out.println("amalg : " + amalg);
		Files.writeFile(out, String.join("\n", lines));
		
		
		return out;
	}
	
	public static void splitFile(File f) throws IOException {
		splitFile(f, Global.getLargeFileThreshold());
	}
	
	 public static void splitFile(File f, int sizeOfFiles) throws IOException {
	        int partCounter = 1;//I like to name parts from 001, 002, 003, ...
	                            //you can change it to 0 if you want 000, 001, ...

	        byte[] buffer = new byte[sizeOfFiles];

	        try (BufferedInputStream bis = new BufferedInputStream(
	                new FileInputStream(f))) {//try-with-resources to ensure closing stream
	            String name = f.getName();

	            int tmp = 0;
	            while ((tmp = bis.read(buffer)) > 0) {
	                //write each chunk of data into separate file with different number in name
	                File newFile = new File(f.getParent(), String.format("%03d", partCounter++) + "split." + name);
	                try (FileOutputStream out = new FileOutputStream(newFile)) {
	                    out.write(buffer, 0, tmp);//tmp is chunk size
	                }
	            }
	        }
	    }
	 
	 public static boolean isAmalgamation(File file) {
		 if(file == null)
			 return false;
//		 System.out.println("inamalg filename : " + file.getName());
//		 System.out.println("inamalg contains : " + file.getName().contains(AMALGAMATED_FILENAME));
		 if(file.getName().contains(AMALGAMATED_FILENAME)){
			 return true;
		 }
		 return false;
	 }
	
//	public static List<File> splitFile(File in, int maxSize) throws FileNotFoundException{
//		if(in == null || !in.exists() || !in.isFile()){
//			throw new IllegalArgumentException("Can't split file -- bad file");
//		}
//		List<File> outs = new ArrayList<File>();
//		int partCounter = 1;
//		
//		byte[] buffer = new byte[maxSize];
//		FileInputStream inStream = new FileInputStream(in);
//		BufferedInputStream bis = new BufferedInputStream(inStream);
//		
//		return outs;
//	}

}
