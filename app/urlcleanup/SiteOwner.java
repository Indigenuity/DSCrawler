package urlcleanup;

import persistence.Site;
import sites.utilities.SiteLogic;

public interface SiteOwner {
	
	public default Site assignUnresolvedSite(Site site) {
		this.setUnresolvedSite(site);
//		this.setTrivialDifference(SiteLogic.redirectIsTrivial(this.getUnresolvedSite()));
		return site;
	}
	
	public default Site assignResolvedSite(Site site) {
		this.setUnresolvedSite(site);
//		this.setTrivialDifference(SiteLogic.redirectIsTrivial(this.getUnresolvedSite()));
		return site;
	}

	public String getWebsiteString();
	public Site getUnresolvedSite();
	public Site setUnresolvedSite(Site site);
	public Site getResolvedSite();
	public Site setResolvedSite(Site site);
	public Boolean isTrivialDifference();
	public void setTrivialDifference(boolean trivialDifference);
}
