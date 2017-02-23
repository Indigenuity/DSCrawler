package urlcleanup;

import persistence.Site;

public interface SiteOwner {

	public String getWebsiteString();
	public Site getUnresolvedSite();
	public Site setUnresolvedSite(Site site);
	public Site getResolvedSite();
	public Site setResolvedSite(Site site);
}
