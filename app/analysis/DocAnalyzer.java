package analysis;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import datadefinitions.newdefinitions.LinkTextMatch;

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
}
