package crawling.projects;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import crawling.discovery.html.Extractor;
import crawling.discovery.html.Scraper;
import crawling.discovery.html.Selector;
import crawling.discovery.planning.DerivationStrategy;

public class BhphCrawl {

	
	public static void runCrawl() throws IOException{
		URL url = new URL("http://buyherepayherevehicles.com/buy-here-pay-here-car-dealerships-directory/arkansas/arkadelphia/");
		Document doc = Jsoup.parse(url, 10000);
		System.out.println("doc : " + doc);
		
		Scraper<String> hrefScraper = Selector
				.byAttributeValueMatching("href", "/buy-here-pay-here-car-dealerships-directory/[a-zA-Z]+")
				.each(Extractor.attr("href"));
		
		Scraper<BasicDealer> vcardScraper = Selector.byClass("vcard")
				.each((element) -> {
					Selector.byClass("organization-name").first().text();
					
					BasicDealer dealer = new BasicDealer();
					dealer.setName(getText(element.getElementsByClass("organization-name").first()));
					dealer.setStreet(getText(element.getElementsByClass("street-address").first()));
					dealer.setCity(getText(element.getElementsByClass("locality").first()));
					dealer.setState(getText(element.getElementsByClass("region").first()));
					dealer.setPostal(getText(element.getElementsByClass("postal-code").first()));
					dealer.setPhone(getText(element.getElementsByClass("tel").first()));
					return dealer;
				});
		
		List<Scraper<?>> scrapers = new ArrayList<Scraper<?>>();
		for(Scraper<?> scraper : scrapers) {
			List<?> results = scraper.apply(doc);
		}
		
		Consumer<String> swallow = (food) -> System.out.println("swallow");
		Consumer<String> chew = (food) -> System.out.println("chew");
		
		
		
//		DerivationStrategy<Element,List<BasicDealer>> each = vcardSelector.each(vcardDealerExtractor);
		
		Selector stateLinksSelector = Selector.byAttributeValueMatching("href", "/buy-here-pay-here-car-dealerships-directory/[a-zA-Z]+");
		
		Function<Element, List<String>> attrDerivationStrategy = stateLinksSelector.attr("href");
		
//		ResourceBlueprint<Element, List<String>> stateLinksBlueprint = 
		DerivationStrategy stateLinksStrategy = Selector
				.byAttributeValueMatching("href", "/buy-here-pay-here-car-dealerships-directory/[a-zA-Z]+")
				.attr("href");
		
		List<String> hrefs = attrDerivationStrategy.apply(doc);
		
		System.out.println("hrefs : " + hrefs);
	}
	
	public static void parsePage(Document doc) {
		
		
	}
	
	public static <R> List<R> runScraper(Element element, Scraper<R> scraper){
		return scraper.apply(element);
	}
	
	public static String getText(Element element) {
		if(element == null) {
			return null;
		}
		return element.text();
	}
}
