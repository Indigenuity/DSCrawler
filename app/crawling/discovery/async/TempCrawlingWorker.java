package crawling.discovery.async;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;

import akka.actor.UntypedActor;
import global.Global;
import persistence.MobileCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import play.db.jpa.JPA;
 
public class TempCrawlingWorker extends UntypedActor{

	@Override
	public void onReceive(Object message) throws IOException  {
		Site site = (Site) message;
		System.out.println("received message");
//		final SiteCrawl siteCrawl = DealerCrawlController.crawlSite(site.getHomepage());
		System.out.println("Site : " + site.getHomepage());
		
		
		HttpURLConnection con;
		URL url = new URL(site.getHomepage());
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(Global.getProxyUrl(), Global.getProxyPort()));
		con = (HttpURLConnection) url.openConnection(proxy);
		con.setInstanceFollowRedirects(true); 
		con.setConnectTimeout(10000);
		con.setReadTimeout(10000);
		con.setRequestProperty("User-Agent", Global.getDefaultUserAgentString());
		
		con.connect();
		String text = IOUtils.toString(con.getInputStream());
		con.disconnect();
		System.out.println("doc : " + Jsoup.parse(text));
		
		
		
		
		
		
//		JPA.withTransaction(() -> {
//			siteCrawl.setSite(site);
//			JPA.em().persist(siteCrawl);
//		});
		
	}

}
