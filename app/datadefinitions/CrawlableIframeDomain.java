package datadefinitions;

public enum CrawlableIframeDomain implements StringMatch {
	
	// Currently no intent to crawl iframes, this was a failed attempt at getting inventory pages, but implementing InventoryTool to discover links was better
	
//	AUCTION_123			("showroom.auction123.com")
	
	;
	
	
	public final String definition;
	private CrawlableIframeDomain(String definition) {
		this.definition = definition;
	}
	public String getDefinition() {
		return definition;
	}
	@Override
	public String getDescription() {
		return "This signifies a domain which is valid for a crawlable iframe to have";
	}
	@Override
	public String getNotes() {
		return "This signifies a domain which is valid for a crawlable iframe to have";
	}
}
