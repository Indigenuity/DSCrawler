package crawling.discovery.html;

import crawling.discovery.entities.FlushableResource;
import crawling.discovery.entities.ResourceId;
import persistence.PageCrawl;
import play.db.jpa.JPA;

public class PageCrawlResource extends FlushableResource{

	public PageCrawlResource(Object value, ResourceId resourceId) {
		super(value, resourceId);
	}

	@Override
	public Object produceValue(Object key) {
		try {
			return JPA.withTransaction(() ->{
				return JPA.em().find(PageCrawl.class, (Long)key);
			});
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

}
