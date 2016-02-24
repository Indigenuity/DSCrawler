package async;

import persistence.CrawlSet;
import persistence.PageInformation;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.SiteInformationOld;
import persistence.SiteSummary;
import persistence.Staff;
import play.Logger;
import play.db.jpa.JPA;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import async.work.SiteWork;

public class MainListener extends UntypedActor {

	@Override
	public void onReceive(Object work) throws Exception {
		try {
//			if(work instanceof SiteWork){
//				SiteWork siteWork = (SiteWork) work;
//				SiteCrawl siteCrawl = siteWork.getSiteCrawl();
//				Site site = siteWork.getSite();
//				CrawlSet crawlSet = siteWork.getCrawlSet();
//				JPA.withTransaction( () -> {
//						
////						if(siteWork.getSiteInfo() != null){
////							
////							SiteInformation siteInfo = siteWork.getSiteInfo();
////							System.out.println("Saving siteinformation : " + siteInfo.getSiteInformationId());
////							int count = 0;
////							for(PageInformation pageInfo : siteInfo.getPages()){
////								for(Staff staff : pageInfo.getAllStaff()) {
////									count++;
////								}
////							}
////							System.out.println("numstaff before : " + count);
////						 	siteInfo = JPA.em().merge(siteInfo);
////						 	count = 0;
////							for(PageInformation pageInfo : siteInfo.getPages()){
////								for(Staff staff : pageInfo.getAllStaff()) {
////									count++;
////								}
////							}
////							System.out.println("numstaff after : " + count);
////	//					 	JPA.em().flush();
////	//					 	JPA.em().detach(siteInfo);
////						 	siteWork.setSiteInfo(siteInfo);
//						
//						if(siteCrawl != null){
//							
//							SiteCrawl temp = JPA.em().merge(siteWork.getSiteCrawl());
//							site.setRecrawl(false);
//							siteWork.setSiteCrawl(temp);
//							
//						}
//						else if(site != null) {
//							Site temp = JPA.em().merge(site);
//							siteWork.setSite(temp);
//						}
//						if(crawlSet != null){
//							CrawlSet tempCrawlSet = JPA.em().merge(crawlSet);
////							tempCrawlSet.getUncrawled().remove(site);
//							tempCrawlSet.addCompletedCrawl(siteWork.getSiteCrawl());
//							siteWork.setCrawlSet(tempCrawlSet);
//						}
//						if(siteWork.getSiteSummary() != null) {
//							System.out.println("Saving summary: ");
//							SiteSummary summary = siteWork.getSiteSummary();
//							JPA.em().merge(summary);
//						}
//						
//						JPA.em().getTransaction().commit();
//						JPA.em().getTransaction().begin();
//						JPA.em().clear();
//				});
//				Asyncleton.instance().getMainMaster().tell(siteWork, getSelf());
//			}
//			else {
//				SiteCrawl siteCrawl = (SiteCrawl) work;
//				
//				JPA.withTransaction(new play.libs.F.Function0<Long>() {
//					public Long apply() throws Throwable {
//						Site site = siteCrawl.getSite();
//						System.out.println("site id : " + site.getSiteId());
//						JPA.em().merge(work);
//						 	
//					 	return 42L;
//					}
//				});
//			}
			System.out.println("Main Listener Got result : " + work);
			
			
		} catch (Throwable e) {
			Logger.error("Error while saving SiteInformation or SiteSummary in MainListener : " + e);
			System.out.println("Error in mainlistener : " + e);
			e.printStackTrace(); 
		} 
	}

}