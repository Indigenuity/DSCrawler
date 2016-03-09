package crawling;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import com.google.common.io.Files;

import edu.uci.ics.crawler4j.parser.HtmlParseData;
import global.Global;
import persistence.PageCrawl;
import play.Logger;
import utilities.DSFormatter;

public class HttpFetcher {

	public static PageCrawl getPageCrawl(URL url, File storageFolder) throws IOException{
		String urlString = url.toString();
		PageCrawl pageCrawl = new PageCrawl();
		pageCrawl.setUrl(urlString);
		try{
			HttpURLConnection con;
			StringBuilder result = new StringBuilder();
			if(Global.useProxy()){
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(Global.getProxyUrl(), Global.getProxyPort()));
				con= (HttpURLConnection) url.openConnection(proxy);
			}
			else{
				con= (HttpURLConnection) url.openConnection();
			}
			con.setConnectTimeout(15 * 1000);
			con.setReadTimeout(15 * 1000);
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", Global.getDefaultUserAgentString());
			con.connect();
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
		    String line;
		    while ((line = rd.readLine()) != null) {
		       result.append(line);
		    }
		    rd.close();
		    con.disconnect();
		    String pageText = result.toString();
		    
		    
			pageCrawl.setHttpStatus(con.getResponseCode());
			
		    String path = url.getPath();
			String query = url.getQuery();
			String pathAndQuery = path + "?" + query;
			String safePath = DSFormatter.makeSafePath(pathAndQuery);
			String filename = storageFolder.getAbsolutePath() + "/" + safePath;
			 
			File out = new File(filename);
			if(!out.exists()){
				Files.write(pageText.getBytes(), new File(filename));
			}
			pageCrawl.setFilename(safePath);
		}
		catch(Exception e) {
			e.printStackTrace();
			Logger.error("Error while trying to visit and store : " + url + " " + e);
			System.out.println("Error while trying to visit and store : " + url + " " + e);
			pageCrawl.setErrorMessage(e.getMessage());
		}

		return pageCrawl;
	}
	
}
