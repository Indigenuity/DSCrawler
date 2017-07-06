package dao;

import persistence.Site;
import sites.SiteLogic;
import urlcleanup.SiteOwner;

public class SiteOwnerLogic {

	public static void assignUnresolvedSiteThreadsafe(SiteOwner owner){
		if(owner.getWebsiteString() == null){
			owner.setUnresolvedSite(null);
			return;
		}
//		System.out.println("Assigning unresolved site for site : " + owner.getWebsiteString());
		owner.setUnresolvedSite(SitesDAO.getOrNewThreadsafe(owner.getWebsiteString()));
	}
	
	public static void assignUnresolvedSite(SiteOwner owner){
		if(owner.getWebsiteString() == null){
			owner.setUnresolvedSite(null);
			return;
		}
		owner.setUnresolvedSite(SitesDAO.getOrNew(owner.getWebsiteString()));
	}
	
	public static void forwardSite(SiteOwner owner) {
		try{
			Site redirectEndpoint = SiteLogic.getRedirectEndpoint(owner.getUnresolvedSite(), true);
			owner.setResolvedSite(redirectEndpoint);
		} catch(StackOverflowError e) {
			System.out.println("caught stackoverflow error while forwarding site.  Unresolved site : " + owner.getUnresolvedSite().getSiteId());
		}
		
	}
	
	public static void refreshRedirectPath(SiteOwner owner){
		owner.setResolvedSite(SiteLogic.refreshRedirectPath(owner.getUnresolvedSite(), false));
	}
	
	public static void forceRefreshRedirectPath(SiteOwner owner){
		owner.setResolvedSite(SiteLogic.refreshRedirectPath(owner.getUnresolvedSite(), true));
	}
}
