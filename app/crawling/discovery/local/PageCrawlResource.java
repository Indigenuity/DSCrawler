package crawling.discovery.local;

import org.jsoup.nodes.Document;

import crawling.discovery.entities.FlushableResource;
import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.execution.PlanId;
import crawling.discovery.planning.PreResource;
import persistence.PageCrawl;
import play.db.jpa.JPA;
import sites.utilities.PageCrawlLogic;

public class PageCrawlResource extends FlushableResource {
	
	protected Long pageCrawlId;
	
	public PageCrawlResource(Object source, PageCrawl value, Resource parent, ResourceId resourceId, PlanId planId) {
		super(source, value, parent, resourceId, planId);
		pageCrawlId = value.getPageCrawlId();
	}
	
	public PageCrawlResource(PreResource preResource, Resource parent, ResourceId resourceId, PlanId planId){
		super(preResource, parent, resourceId, planId);
		this.pageCrawlId = (Long)preResource.getValue();
	}
	
	@Override
	public Object produceValue() {
		try {
			return tryProduce(); 	//If there is already an entity manager, don't make a new one
		} catch (Exception e) {
			try {
				return JPA.withTransaction(() ->{
					return tryProduce();
				});
			} catch (Throwable e1) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private Object tryProduce(){
		PageCrawl pageCrawl =JPA.em().find(PageCrawl.class, pageCrawlId);
		return pageCrawl;
	}
	
	@Override
	public void flush(){
		super.flush();
	}
	
	public PageCrawl getPageCrawl() {
		return (PageCrawl)getValue();
	}

}
