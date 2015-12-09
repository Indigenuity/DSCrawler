package analysis;

import global.Global;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class LogAnalyzer {
	
	public final static Pattern CONNECTION_RESET_MATCH = Pattern.compile("(?<=Connection reset, while processing: )[^\\s]+");
	public final static Pattern NULL_PAGE = Pattern.compile("(?<=null, while processing: )[^\\s]+");
	public final static Pattern TIMED_OUT = Pattern.compile("(?<=timed out, while processing: )[^\\s]+");
	public final static Pattern TIMED_OUT_SECOND = Pattern.compile("(?<=timed out: connect, while processing: )[^\\s]+");
	
	
	
	public static Set<String> failedUrls() throws IOException{
		Set<String> urls = new HashSet<String>();
		
		File logFolder = new File(Global.OLD_LOGS_FOLDER);
		System.out.println("absolute : " + logFolder.getAbsolutePath());
		
		for(File log : logFolder.listFiles()) {
			FileInputStream inputStream = new FileInputStream(log.getAbsolutePath());
			String text = IOUtils.toString(inputStream);
			inputStream.close();
			Matcher matcher = CONNECTION_RESET_MATCH.matcher(text);
			
			while(matcher.find()){
				urls.add(matcher.group(0));
			}
			
			matcher = TIMED_OUT.matcher(text);
			while(matcher.find()){
				urls.add(matcher.group(0));
			}
			matcher = TIMED_OUT_SECOND.matcher(text);
			while(matcher.find()){
				urls.add(matcher.group(0));
			}
		}
		
		for(String url : urls) {
			System.out.println("failed : " + url);
		}
		System.out.println("failed size : " + urls.size());
		return urls;
	}
	
	public static Set<String> nullUrls() throws IOException{
		Set<String> urls = new HashSet<String>();
		
		File logFolder = new File(Global.OLD_LOGS_FOLDER);
		System.out.println("absolute : " + logFolder.getAbsolutePath());
		
		for(File log : logFolder.listFiles()) {
			FileInputStream inputStream = new FileInputStream(log.getAbsolutePath());
			String text = IOUtils.toString(inputStream);
			inputStream.close();
			Matcher matcher = NULL_PAGE.matcher(text);
			
			while(matcher.find()){
				urls.add(matcher.group(0));
			}
		}
		
		for(String url : urls) {
			System.out.println("null: " + url);
		}
		System.out.println("null size : " + urls.size());
		return urls;
	}

}
