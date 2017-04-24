package crawling.discovery.local;

import crawling.discovery.entities.FlushableResource;
import crawling.discovery.entities.Resource;
import crawling.discovery.entities.ResourceId;
import crawling.discovery.execution.PlanId;
import persistence.PageCrawl;
import play.db.jpa.JPA;

public class PageCrawlResource extends FlushableResource{

	public PageCrawlResource(Object source, PageCrawl value, Resource parent, ResourceId resourceId, PlanId planId) {
		super(source, value, parent, resourceId, planId);
		this.setKey(value.getPageCrawlId());
		this.setValue(value);
	}
	
	public PageCrawlResource(Object source, PageCrawl value, Resource parent){
		super(source, value, parent);
		this.setKey(value.getPageCrawlId());
		this.setValue(value);
	}

	@Override
	public Object produceValue(Object key) {
		try {
			return JPA.em().find(PageCrawl.class, key);		//If there is already an entity manager, don't make a new one
		} catch (Exception e) {
			try {
				return JPA.withTransaction(() ->{
					PageCrawl pageCrawl =JPA.em().find(PageCrawl.class, (Long)key); 
					return pageCrawl;
				});
			} catch (Throwable e1) {
				throw new RuntimeException(e);
			}
		}
	}

}
