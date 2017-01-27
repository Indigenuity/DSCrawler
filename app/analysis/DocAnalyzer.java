package analysis;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import datadefinitions.UrlExtraction;
import datadefinitions.newdefinitions.LinkTextMatch;
import persistence.ExtractedUrl;

public class DocAnalyzer {

	public static Set<LinkTextMatch> getLinkTextMatches(Document doc) {
		Set<LinkTextMatch> matches = new HashSet<LinkTextMatch>();
		Elements links = doc.select("a");
		for(LinkTextMatch ltm : LinkTextMatch.values()){
			for(Element link : links){
				String text = link.text();
				if(text != null && text.contains(ltm.getDefinition()) && !matches.contains(ltm.getDefinition())){
					matches.add(ltm);
				}
			}
		}
		return matches;
	}

	public static Set<ExtractedUrl> extractUrls(Document doc) {
		Set<ExtractedUrl> extractedUrls = new HashSet<ExtractedUrl>();
		for(UrlExtraction enumElement : UrlExtraction.values()){
			Elements links = doc.select("a[href*=" +enumElement.getDefinition() + "]");
			for(Element element : links) {
				if(element.attr("href") != null){
					ExtractedUrl item = new ExtractedUrl(element.attr("href"), enumElement);
					extractedUrls.add(item);
				}
			}
		}
		return extractedUrls;
	}
}
