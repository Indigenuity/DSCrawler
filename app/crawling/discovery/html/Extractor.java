package crawling.discovery.html;

import java.util.function.Consumer;

import org.jsoup.nodes.Element;

import crawling.discovery.DerivationStrategy;

public interface Extractor<R> extends DerivationStrategy<Element, R>{

	public static Extractor<String> attr(String key){
		return (element) -> element.attr(key);
	}
	
}
