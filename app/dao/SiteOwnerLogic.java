package dao;

import persistence.Site;
import sites.utilities.SiteLogic;
import urlcleanup.SiteOwner;

public class SiteOwnerLogic {

	public static void assignUnresolvedSiteThreadsafe(SiteOwner owner){
		if(owner.getWebsiteString() == null){
			owner.assignUnresolvedSite(null);
			return;
		}
//		System.out.println("Assigning unresolved site for site : " + owner.getWebsiteString());
		owner.assignUnresolvedSite(SitesDAO.getOrNewThreadsafe(owner.getWebsiteString()));
	}
	
	public static void assignUnresolvedSite(SiteOwner owner){
		if(owner.getWebsiteString() == null){
			owner.assignUnresolvedSite(null);
			return;
		}
		owner.assignUnresolvedSite(SitesDAO.getOrNew(owner.getWebsiteString()));
	}
	
	public static void forwardSite(SiteOwner owner) {
		try{
			Site redirectEndpoint = SiteLogic.getRedirectEndpoint(owner.getUnresolvedSite(), true);
			owner.assignResolvedSite(redirectEndpoint);
		} catch(StackOverflowError e) {
			System.out.println("caught stackoverflow error while forwarding site.  Unresolved site : " + owner.getUnresolvedSite().getSiteId());
		}
		
	}
	
	public static void refreshRedirectPath(SiteOwner owner){
		owner.assignResolvedSite(SiteLogic.refreshRedirectPath(owner.getUnresolvedSite(), false));
	}
	
	public static void forceRefreshRedirectPath(SiteOwner owner){
		owner.assignResolvedSite(SiteLogic.refreshRedirectPath(owner.getUnresolvedSite(), true));
	}
	
	
}
