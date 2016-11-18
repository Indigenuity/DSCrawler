package crawling.discovery.html;

public class HttpConfig {

	private String proxyAddress;
	private int proxyPort;
	private boolean useProxy = false;
	
	private int connectTimeout = 5000;
	private int readTimeout = 5000;
	private String userAgent = "";
	private int politenessDelay = 1000;
	private boolean followRedirects = true;
	
	
	public String getProxyAddress() {
		return proxyAddress;
	}
	public void setProxyAddress(String proxyAddress) {
		this.proxyAddress = proxyAddress;
	}
	public int getProxyPort() {
		return proxyPort;
	}
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}
	public boolean isUseProxy() {
		return useProxy;
	}
	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public int getPolitenessDelay() {
		return politenessDelay;
	}
	public void setPolitenessDelay(int politenessDelay) {
		this.politenessDelay = politenessDelay;
	}
	public int getConnectTimeout() {
		return connectTimeout;
	}
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	public int getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	public boolean isFollowRedirects() {
		return followRedirects;
	}
	public void setFollowRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
	}
	
	
}
