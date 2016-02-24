package async.tools;

import java.util.HashSet;
import java.util.Set;

import crawling.DealerCrawlController;
import datadefinitions.NoCrawlDomain;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.tasks.Task;
import play.Logger;
import play.db.jpa.JPA;
import async.registration.ContextItem;
import async.work.WorkStatus;
import async.work.WorkType;

//For sending sites to crawl to individual threads
public class SiteCrawlTool extends Tool { 
	


	protected final static Set<ContextItem> requiredContextItems = new HashSet<ContextItem>();
	static{
		ContextItem item = new ContextItem("siteId", String.class, false);
		requiredContextItems.add(item);
	}
	
	protected final static Set<ContextItem> resultContextItems = new HashSet<ContextItem>();
	static{
		ContextItem item = new ContextItem("siteCrawlId", String.class, false);
		resultContextItems.add(item);
	}
	
	protected final static Set<WorkType> abilities = new HashSet<WorkType>();
	static{
		abilities.add(WorkType.SITE_CRAWL);
	}
	
	@Override
	public  Set<WorkType> getAbilities() {
		return abilities;
	}
	
	@Override
	public Set<ContextItem> getRequiredItems(WorkType workType) {
		return requiredContextItems;
	}


	@Override
	public Set<ContextItem> getResultItems(WorkType workType) {
		return resultContextItems;
	}	
	
	@Override
	protected Task safeDoTask(Task task) {
		System.out.println("SiteCrawlTool processing task : " + task);
		try{
			Long siteId = Long.parseLong(task.getContextItem("siteId"));
			
			Site[] siteTemp = new Site[1];
			JPA.withTransaction( () -> {
				siteTemp[0]= JPA.em().find(Site.class, siteId);
			});
			Site site = siteTemp[0];
			boolean shouldCrawl = true;
			for(NoCrawlDomain domain : NoCrawlDomain.values()) {
				if(site.getHomepage().contains(domain.definition))
					shouldCrawl = false;
			}
			
			if(shouldCrawl){
				final SiteCrawl siteCrawl = DealerCrawlController.crawlSite(site.getHomepage());
				
				JPA.withTransaction(() -> {
					siteCrawl.setSite(site);
					JPA.em().persist(siteCrawl);
				});
				task.addContextItem("siteCrawlId",  siteCrawl.getSiteCrawlId() + "");
				task.setWorkStatus(WorkStatus.WORK_COMPLETED);
			}
			else {
				task.setWorkStatus(WorkStatus.NEEDS_REVIEW);
				task.setNote("Site domain is in the NoCrawlDomains");
			}
			
		}
		catch(Exception e) {
//			System.out.println("Error while crawling : " + siteInfo.getSiteInformationId() + " " + e);
//			Logger.error("Error while crawling : " + siteInfo.getSiteInformationId() + " " + e);
			Logger.error("error while crawling : " + e);
			e.printStackTrace();
			task.setWorkStatus(WorkStatus.NEEDS_REVIEW);
			task.setNote("Exception : " + e.getMessage());
		}
		
		return task;
	}
	

}
