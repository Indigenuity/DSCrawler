package crawling.discovery.html;

import org.jsoup.nodes.Document;

public interface HtmlDerivationStrategy<T> {

	public T derive(Document doc);
}
