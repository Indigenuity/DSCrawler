package utilities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import global.Global;
import persistence.UrlCheck;

public class UrlSniffer {

	private static final int DEFAULT_REDIRECT_RECURSION_LEVEL = 10;
	private static final String DEFAULT_USER_AGENT_STRING = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
	//Visits a url and returns the 
	public static String getRedirectedUrl(String urlString) throws MalformedURLException, IOException{
		return getFinalUrl(urlString, DEFAULT_REDIRECT_RECURSION_LEVEL);
	}
	
	public static UrlCheck checkUrl(String seed) {
		return checkUrl(seed, DEFAULT_REDIRECT_RECURSION_LEVEL);
	}
	
	private static UrlCheck checkUrl(String seed, int numRecursions){
		UrlCheck check = new UrlCheck(seed);
		try{
			check = resolveCheck(check, numRecursions);
		}
		catch(Exception e){
			check.setError(true);
			System.out.println("Error while checking redirect : " + e);
			check.setErrorMessage(e.getMessage());
		}
		if(check.getResolvedSeed() != null){
			check.setResolvedSeed(check.getResolvedSeed().replace(":80", ""));
			if(DSFormatter.equals(check.getSeed(), check.getResolvedSeed())){
				check.setNoChange(true);
			}
		}
		
		return check;
	}
	
	private static UrlCheck resolveCheck(UrlCheck check, int numRecursions) throws IOException {
		String seed = check.getResolvedSeed();
		if(seed == null) {
			seed = check.getSeed();
		}
		System.out.println("Checking redirected url of : " + seed);
		URL url = new URL(seed);
		
		HttpURLConnection con;
		if(Global.useProxy()){
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(Global.getProxyUrl(), Global.getProxyPort()));
			con = (HttpURLConnection)(url.openConnection(proxy));
		}
		else {
			con = (HttpURLConnection) url.openConnection();
		}
		con.setInstanceFollowRedirects(false);	//Follow redirects only manually 
		con.setConnectTimeout(15 * 1000);
		con.setRequestProperty("User-Agent", DEFAULT_USER_AGENT_STRING);
		
		con.connect();
		int responseCode = con.getResponseCode();
		check.setStatusCode(responseCode);
		if(responseCode >= 300 && responseCode < 400) {
			String redirectLocation = con.getHeaderField("Location");
			if(redirectLocation.startsWith("/")){
				redirectLocation = url.getProtocol() + "://" + url.getHost() + redirectLocation;
			}
			check.setResolvedSeed(redirectLocation);
			System.out.println("*********redirected to url (" + responseCode + "): " + redirectLocation);
			if(numRecursions < 1)
				throw new IOException("Number of redirects has exceeded the limit for url : " + redirectLocation);
			
			return resolveCheck(check, --numRecursions);
		}
		//If no redirect, just return what we get
		check.setResolvedSeed(con.getURL().toString());
		return check;
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
