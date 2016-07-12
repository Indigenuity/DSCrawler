package utilities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import javax.swing.text.ChangedCharSetException;

import global.Global;
import persistence.UrlCheck;
import play.Logger;

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
			String httpSeed = DSFormatter.toHttp(seed);
			check.setStandardizedSeed(httpSeed);
			check.setResolvedSeed(httpSeed);		//Initial value for recursion
			check = resolveCheck(check, numRecursions);
			fillMeta(check);
		}
		catch(MalformedURLException e) {
			check.setError(true);
		}
		catch(Exception e){
			check.setError(true);
			Logger.warn("Error while checking redirect : " + e);
			check.setErrorMessage(e.getMessage());
		}
		if(check.getResolvedSeed() != null){
			check.setResolvedSeed(check.getResolvedSeed().replace(":80", ""));
			
		}
		return check;
	}
	
	public static void fillMeta(UrlCheck check) {
		URL url;
		URL oldUrl;
		try{
			oldUrl = new URL(DSFormatter.toHttp(check.getSeed()));
			url = new URL(check.getResolvedSeed());
			check.setValid(true);
		}
		catch(MalformedURLException e) {
			check.setValid(false);
			return;
		}
		check.setStatusApproved((check.getStatusCode() == 200)? true:false);
		check.setSeedHost(oldUrl.getHost());
		check.setResolvedHost(url.getHost());
		check.setDomainChanged(!DSFormatter.equals(oldUrl.getHost(), url.getHost()));
		check.setDomainApproved(DSFormatter.isApprovedDomain(url.getHost()));
		
		if(DSFormatter.equals(check.getSeed(), check.getResolvedSeed())){
			check.setNoChange(true);
		}
		else if(isGenericRedirect(check.getSeed(), check.getResolvedSeed())){
			check.setGenericChange(true);
		}
		
		check.setQueryApproved(DSFormatter.isApprovedQuery(url));
		check.setPathApproved(DSFormatter.isApprovedPath(url));
		check.setLanguagePath(DSFormatter.isLanguagePath(url));
		check.setLanguageQuery(DSFormatter.isLanguageQuery(url));
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
		con.setReadTimeout(15 * 1000);
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
		con.disconnect();
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
	
	//Checks for http(s) additions/changes, www redirects, top-level (.com, .net) redirects, and language path and query string changes
	public static boolean isGenericRedirect(String redirectUrl, String original) {
		
		if(redirectUrl == null || original == null)
			return false;
		if(redirectUrl.equals(original))
			return true;
		String sansHttpRedirectUrl = DSFormatter.removeHttp(redirectUrl);
		String sansHttpOriginal= DSFormatter.removeHttp(original);
		if(sansHttpRedirectUrl.equals(sansHttpOriginal))
			return true;
		String sansWwwRedirectUrl = DSFormatter.removeWww(sansHttpRedirectUrl);
		String sansWwwOriginal = DSFormatter.removeWww(sansHttpOriginal);
		if(sansWwwRedirectUrl.equals(sansWwwOriginal))
			return true;
		
		String comRedirectUrl = DSFormatter.toCom(sansWwwRedirectUrl);
		String comOriginal = DSFormatter.toCom(sansWwwOriginal);
		
		if(comRedirectUrl.equals(comOriginal))
			return true;
		
		String langRedirectUrl = DSFormatter.removeLanguage(comRedirectUrl);
		String langOriginal = DSFormatter.removeLanguage(comOriginal);
		if(langRedirectUrl.equals(langOriginal))
			return true;
		
		String noTrailingSlashRedirectUrl = langRedirectUrl.replaceAll("/$", "");
		String noTrailingSlashOriginalUrl = langOriginal.replaceAll("/$", "");
		if(noTrailingSlashRedirectUrl.equals(noTrailingSlashOriginalUrl))
			return true;
		
		
		return false;
	}
}
