package crawling.discovery.html;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import crawling.discovery.Endpoint;
import crawling.discovery.Fetcher;
import crawling.discovery.HttpEndpoint;

public class HtmlFetcher implements Fetcher<HttpEndpoint, Element>{

	private HttpConfig config;
	
	public HtmlFetcher(HttpConfig config) {
		this.config = config;
	}
	
	@Override
	public Element fetch(HttpEndpoint endpoint) {
		try{
			HttpURLConnection con;
			if(config.isUseProxy()){
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.getProxyAddress(), config.getProxyPort()));
				con = (HttpURLConnection) endpoint.getUrl().openConnection(proxy);
			}else {
				con = (HttpURLConnection) endpoint.getUrl().openConnection();
			}
			con.setInstanceFollowRedirects(config.isFollowRedirects()); 
			con.setConnectTimeout(config.getConnectTimeout());
			con.setReadTimeout(config.getReadTimeout());
			con.setRequestProperty("User-Agent", config.getUserAgent());
			
			con.connect();
			String text = IOUtils.toString(con.getInputStream());
			con.disconnect();
			return Jsoup.parse(text);
			
		} catch(Exception e) {
			System.out.println("sigh...");
		}
		
		return null;
	}
	
	
	
	
	public HttpConfig getConfig() {
		return config;
	}
	public void setConfig(HttpConfig config) {
		this.config = config;
	}

}
