package crawling;

import java.util.HashSet;
import java.util.Set;

public class CrawlReport {
	public final Set<String> visitedUrls;
	public final Set<String> failedUrls;
	public final int repeatUrls;
	
	public CrawlReport(Set<String> visitedUrls, Set<String> failedUrls, int repeatUrls){
		this.visitedUrls = visitedUrls;
		this.failedUrls = failedUrls;
		this.repeatUrls = repeatUrls;
	}
}
