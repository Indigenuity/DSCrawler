package async.tools;

import java.util.HashSet;
import java.util.Set;

import async.registration.ContextItem;
import async.work.WorkType;
import persistence.tasks.Task;

public class SiteUpdateTool extends Tool { 
	


	protected final static Set<ContextItem> requiredContextItems = new HashSet<ContextItem>();
	static{
		ContextItem item = new ContextItem("urlCheckId", String.class, false);
		requiredContextItems.add(item);
		item = new ContextItem("siteId", String.class, false);
		requiredContextItems.add(item);
	}
	
	protected final static Set<ContextItem> resultContextItems = new HashSet<ContextItem>();
	
	protected final static Set<WorkType> abilities = new HashSet<WorkType>();
	static{
		abilities.add(WorkType.SITE_UPDATE);
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
		throw new UnsupportedOperationException("sigh, need to update this");
//		try{
//			Long urlCheckId = Long.parseLong(task.getContextItem("urlCheckId"));
//			Long siteId = Long.parseLong(task.getContextItem("siteId"));
//			
//			JPA.withTransaction( () -> {
//				Site site = JPA.em().find(Site.class, siteId);
//				UrlCheck urlCheck = JPA.em().find(UrlCheck.class, urlCheckId);
//				
//				
//				if(urlCheck.getStatusCode() == 200) {
//					if(DSFormatter.equals(urlCheck.getResolvedSeed(), site.getHomepage())){
//	//					System.out.println("no redirect");
//						site.setRedirectResolveDate(urlCheck.getCheckDate());
//						urlCheck.setAccepted(true);
//						urlCheck.setNoChange(true);
//						task.setWorkStatus(WorkStatus.WORK_COMPLETED);
//						
//					}
//					else if(UrlSniffer.isGenericRedirect(urlCheck.getResolvedSeed(), site.getHomepage())){
//	//					System.out.println("generic change");
//						site.setHomepage(urlCheck.getResolvedSeed());
//						site.setRedirectResolveDate(urlCheck.getCheckDate());
//						urlCheck.setAccepted(true);
//						task.setWorkStatus(WorkStatus.WORK_COMPLETED);
//					}
//				}
//				if(task.getWorkStatus() != WorkStatus.WORK_COMPLETED){
//	//				System.out.println("work needs review");
//					task.setWorkStatus(WorkStatus.NEEDS_REVIEW);				
//				}
//			});
//			
//		}
//		catch(Exception e) {
//			Logger.error("Error in Site Update Worker: " + e);
//			e.printStackTrace();
//			task.setWorkStatus(WorkStatus.NEEDS_REVIEW);
//		}
//		return task;
	}
	
	
//	public static SiteUpdateWorkResult updateSite(SiteUpdateWorkOrder workOrder) {
//		SiteUpdateWorkResult workResult = new SiteUpdateWorkResult();
//		result.setSiteId(order.getSiteId());
//		result.setUuid(order.getUuid());
//	}
	

}