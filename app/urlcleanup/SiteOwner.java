package urlcleanup;

import persistence.Site;

public interface SiteOwner {

	public Site getOriginalSite();
	public Site getResolvedSite();
	public Site setResolvedSite(Site site);
}
