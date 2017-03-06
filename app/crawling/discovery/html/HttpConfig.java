package crawling.discovery.html;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HttpConfig {
	
	public final static int DEFAULT_REQUESTS_PER_SECOND = 1;
	public final static int DEFAULT_CONNECT_TIMEOUT = 15 * 1000;
	public final static int DEFAULT_READ_TIMEOUT = 15 * 1000;
	public final static String DEFAULT_USER_AGENT_STRING = "Anansi Crawler";

	private String proxyAddress;
	private int proxyPort;
	private boolean useProxy = false;
	
	private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
	private int readTimeout = DEFAULT_READ_TIMEOUT;
	private String userAgent = DEFAULT_USER_AGENT_STRING;
	private int requestsPerSecond = DEFAULT_REQUESTS_PER_SECOND;
	private boolean followRedirects = true;
	
	
	protected RequestConfig requestConfig;
	{
		Builder requestConfigBuilder = RequestConfig.custom()
				.setSocketTimeout(readTimeout)
				.setConnectionRequestTimeout(connectTimeout);
		if(useProxy){
			requestConfigBuilder.setProxy(new HttpHost(proxyAddress, proxyPort));
		}
		requestConfig = requestConfigBuilder.build();
	}
	
	public HttpConfig(){
	}
	
	public CloseableHttpClient buildHttpClient(){
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		CloseableHttpClient httpClient = HttpClients.custom()
				.setUserAgent(userAgent)
				.setDefaultRequestConfig(requestConfig)
				.setConnectionManager(cm)
				.build();
		return httpClient;
	}
	
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
	public int getRequestsPerSecond() {
		return requestsPerSecond;
	}
	public void setRequestsPerSecond(int requestsPerSecond) {
		this.requestsPerSecond = requestsPerSecond;
	}
	
	
}
