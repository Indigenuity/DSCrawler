package crawling.anansi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import com.google.common.io.Files;

import akka.actor.ActorRef;
import async.async.Asyncleton;
import async.functionalwork.FunctionalWorker;
import global.Global;
import persistence.PageCrawl;
import persistence.Site;
import play.Logger;
import utilities.DSFormatter;

public class SiteCrawler {
	
	/********** pre-initialized ********************/
	protected SiteCrawlConfig config = new SiteCrawlConfig();
	
	
	/********* on-demand initialization **************/
	protected ActorRef pageCrawlMaster;
	

	public SiteCrawler() {
		
	}
	
	protected ActorRef pageCrawlMaster(){
		synchronized(pageCrawlMaster){
			if(pageCrawlMaster == null) {
				pageCrawlMaster = Asyncleton.getInstance().getMonotypeMaster(config.getNumWorkers(), FunctionalWorker.class);
			}
		}
	}
	
	public void startCrawl(){
		
	}
	
	public PageCrawl getPageCrawl(URL url) throws IOException{
		String urlString = url.toString();
		PageCrawl pageCrawl = new PageCrawl();
		pageCrawl.setUrl(urlString);
		try{
			HttpURLConnection con;
			StringBuilder result = new StringBuilder();
			if(config.isUseProxy()){
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.getProxyUrl(), config.getProxyPort()));
				con= (HttpURLConnection) url.openConnection(proxy);
			}
			else{
				con= (HttpURLConnection) url.openConnection();
			}
			con.setConnectTimeout(config.getConnectTimeout());
			con.setReadTimeout(config.getReadyTimeout());
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
//			if(!out.exists()){
				Files.write(pageText.getBytes(), new File(filename));
//			}
			pageCrawl.setFilename(safePath);
			pageCrawl.setPath(path);
			pageCrawl.setQuery(query);
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
