package crawling.discovery.html;


import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import crawling.discovery.entities.Endpoint;
import crawling.discovery.planning.DerivationStrategy;

public class DocDerivationStrategy implements DerivationStrategy<Endpoint, Document>{


	private HttpConfig config;
	
	public DocDerivationStrategy(HttpConfig config) {
		this.config = config;
	}
	
	
	@Override
	public Document derive(Endpoint source) {
		try(CloseableHttpClient client= HttpClientBuilder.create().build()){
			HttpEndpoint endpoint = (HttpEndpoint) source;
		
			HttpGet request = new HttpGet(endpoint.getUrl().toString());
			
			if(config.isUseProxy()){
				HttpHost proxy = new HttpHost(config.getProxyAddress(), config.getProxyPort(), "http");
				RequestConfig requestConfig = RequestConfig.custom()
						.setProxy(proxy)
						.setConnectTimeout(config.getConnectTimeout())
						.setSocketTimeout(config.getReadTimeout())
						.build();
				request.setConfig(requestConfig);
			}
			
			request.setHeader("User-Agent", config.getUserAgent());
			
			try(CloseableHttpResponse response = client.execute(request)){
				HttpEntity entity = response.getEntity();
				String responseText = EntityUtils.toString(entity);
//				System.out.println("responseText : " + responseText);
				return Jsoup.parse(responseText);
			}
			
//				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.getProxyAddress(), config.getProxyPort()));
//				con = (HttpURLConnection) endpoint.getUrl().openConnection(proxy);
//			}else {
//				con = (HttpURLConnection) endpoint.getUrl().openConnection();
//			}
//			con.setInstanceFollowRedirects(config.isFollowRedirects()); 
//			con.setConnectTimeout(config.getConnectTimeout());
//			con.setReadTimeout(config.getReadTimeout());
//			con.setRequestProperty("User-Agent", config.getUserAgent());
//			
//			con.connect();
//			
//			if(con.getResponseCode() >= 300){
//				
//			}
//			con.g
//			String text = IOUtils.toString(con.getInputStream(), "UTF-8");
//			con.disconnect();
//			return Jsoup.parse(text);
			
		} catch(Exception e) {
			System.out.println(e.getClass().getSimpleName() + " exception while fetching doc : " + e.getMessage());
		}
		
		return null;
	}

}
