package crawling.discovery.html;

import java.net.URI;

import org.apache.commons.lang.StringUtils;

public class HtmlUtils {

	public static String findBaseUriString(URI uri){
		return uri.getScheme() + "://" + uri.getHost();
	}
	
	public static boolean isInternalLink(URI baseUri, URI uri){
		return StringUtils.equals(uri.getHost(), baseUri.getHost());
	}
}
