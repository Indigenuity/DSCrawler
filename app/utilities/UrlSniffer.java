package utilities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

public class UrlSniffer {

	private static final int DEFAULT_REDIRECT_RECURSION_LEVEL = 10;
	private static final String DEFAULT_USER_AGENT_STRING = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
	//Visits a url and returns the 
	public static String getRedirectedUrl(String urlString) throws MalformedURLException, IOException{
		return getFinalUrl(urlString, DEFAULT_REDIRECT_RECURSION_LEVEL);
	}
	
	public static String getFinalUrl(String urlString, int numRecursions) throws IOException {
		System.out.println("Checking redirected url of : " + urlString);
		URL url = new URL(urlString);
		String domain = url.getHost();
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("52.25.252.253", 8888));
		HttpURLConnection con = (HttpURLConnection)(url.openConnection(proxy));
		con.setInstanceFollowRedirects(false);
		con.setConnectTimeout(15 * 1000);
		con.setRequestProperty("User-Agent", DEFAULT_USER_AGENT_STRING);
		
		con.connect();
		int responseCode = con.getResponseCode();
		if(responseCode >= 300 && responseCode < 400) {
			System.out.println("*********redirected to url (" + responseCode + "): " + con.getHeaderField("Location"));
			String location = con.getHeaderField("Location");
			String redirectUrl;
			if(location.startsWith("/")){
				redirectUrl = url.getProtocol() + "://" + url.getHost() + location;
			}
			else {
				redirectUrl = location;
			}
			
			if(numRecursions < 1)
				throw new IOException("Number of redirects has exceeded the limit for url : " + urlString);
			
			return getFinalUrl(redirectUrl, --numRecursions);
		}
		else if(responseCode != 200) {
			System.out.println("Unexpected response code : " + responseCode);
			throw new IllegalStateException("Unexpected response code : " + responseCode);
		}
		
		System.out.println("no redirect, returning this string : " + con.getURL().toString());
		return con.getURL().toString().replace(":80", "");
	}
	
	//Checks for www redirects, top-level (.com, .net) redirects, and language query strings
	public static boolean isGenericRedirect(String redirectUrl, String original) {
		
		if(redirectUrl == null || original == null)
			return false;
		if(redirectUrl.equals(original))
			return true;
		String sansRedirectUrl = DSFormatter.removeWww(redirectUrl);
		String sansOriginal = DSFormatter.removeWww(original);
		if(sansRedirectUrl.equals(sansOriginal))
			return true;
		
		String comRedirectUrl = DSFormatter.toCom(sansRedirectUrl);
		String comOriginal = DSFormatter.toCom(sansOriginal);
		
		if(comRedirectUrl.equals(comOriginal))
			return true;
		
		String langRedirectUrl = DSFormatter.removeLanguage(comRedirectUrl);
		String langOriginal = DSFormatter.removeLanguage(comOriginal);
		if(langRedirectUrl.equals(langOriginal))
			return true;
		
		
		try {
			URL validRedirectUrl = new URL(comRedirectUrl);
			URL validOriginal = new URL(comOriginal);
			String hostRedirectUrl = validRedirectUrl.getHost();
			String hostOriginal = validOriginal.getHost();
			if(hostRedirectUrl.equals(hostOriginal))
				return true;
		} catch (MalformedURLException e) {
			return false;
		}
		
		
		
		return false;
	}
}
