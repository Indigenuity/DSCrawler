package crawling.discovery.html;

import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import crawling.discovery.planning.DerivationStrategy;

public interface Picker<R> extends DerivationStrategy<Elements, R> {

	default Extractor<String> text(){
		return (element) -> {
			if(element == null){
				return null;
			}
			return element.text();
		};
	}


}
