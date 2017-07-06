package crawling.discovery.control;

import crawling.discovery.entities.FlushableResource;
import crawling.discovery.entities.Resource;
import newwork.WorkStatus;

public class CrawlUtil {

	public static void flush(Resource resource){
		if(resource instanceof FlushableResource){
			((FlushableResource)resource).flush();
		}
	}
	
	public static boolean needDiscoveryWork(Resource resource){
		return resource.getDiscoveryStatus() == WorkStatus.UNASSIGNED;
	}
	
	public static boolean needFetchWork(Resource resource){
		return resource.getFetchStatus() == WorkStatus.UNASSIGNED;
	}
	
	public static boolean readyForDiscovery(Resource resource) {
		return resource.getFetchStatus() == WorkStatus.COMPLETE 
				|| resource.getFetchStatus() == WorkStatus.PRECOMPLETED;
		
	}
}
