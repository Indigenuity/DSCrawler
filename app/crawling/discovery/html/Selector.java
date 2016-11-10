package crawling.discovery.html;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import crawling.discovery.planning.DerivationStrategy;


public interface Selector extends DerivationStrategy<Element, Elements>{
	
	
	public static Selector byTag(String tagName) {
		return (doc) -> doc.getElementsByTag(tagName);
	}
	
	public static Selector byAttributeValueContaining(String key, String match) {
		return (doc) -> doc.getElementsByAttributeValueContaining(key, match);
	}
	
	public static Selector byAttributeValueMatching(String key, String match) {
		return (doc) -> doc.getElementsByAttributeValueMatching(key, match);
	}
	
	public static Selector byClass(String className) {
		return (doc) -> doc.getElementsByClass(className);
	}
	
	
	//************** Other Derivations ****************************
	
	default DerivationStrategy<Element, List<String>> attr(String attrName) {
		return this.andThen((elements) -> {
			List<String> attrs = new ArrayList<String>();
			for(Element element : elements) {
				attrs.add(element.attr(attrName));
			}
			return attrs;
		});
	}
	
	default Picker<Element> first() {
		return (element) -> element.first();
	}
	
	public default <R> Scraper<R>  each(Extractor<R> extractor){
		return (element) -> apply(element).stream().map(extractor).collect(Collectors.toList());
	}
	
//	default <V> ExtractionStrategy<V, R> compose(ExtractionStrategy<? super V, ? extends T> before){
//		Objects.requireNonNull(before);
//		return(V v) -> apply(before.apply(v));
//	}
//	
	default <R> Extractor<R> andThen(Picker<? extends R> after) {
		Objects.requireNonNull(after);
		return (element) -> after.apply(apply(element));
	}
	
}
