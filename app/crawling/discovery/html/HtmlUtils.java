package crawling.discovery.html;

import java.net.URI;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class HtmlUtils {

	protected final static Pattern NO_CRAWL_FILE_EXTENSIONS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz|pdf|jpeg|exe|gzip|rar|mov|bmp|tif|ini))$");
	
	public static String findBaseUriString(URI uri){
		return uri.getScheme() + "://" + uri.getHost();
	}
	
	public static boolean isInternalLink(URI baseUri, URI uri){
		return StringUtils.equals(uri.getHost(), baseUri.getHost())
				&& StringUtils.equals(uri.getScheme(), baseUri.getScheme());
	}
	
	public static boolean isCrawlableFileExtension(URI uri) {
		if(uri == null || StringUtils.isEmpty(uri.toString()) || StringUtils.isEmpty(uri.getPath())){
			return false;
		}
		return !NO_CRAWL_FILE_EXTENSIONS.matcher(uri.getPath()).matches();
	}
	
	public static boolean isRedirectStatus(int statusCode){
		return statusCode >= 300 && statusCode < 400;
	}
}
