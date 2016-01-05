package async.work;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorkSet {

	@SuppressWarnings("rawtypes")
	private Map<WorkType, WorkItem> workItems = new HashMap<WorkType, WorkItem>();
	
	private Long id = new Long(0);
	private Long uuid = UUID.randomUUID().getLeastSignificantBits();
	private Long siteId = new Long(0);
	private Long siteCrawlId = new Long(0);
	private Long crawlSetId = new Long(0);
	private Long mobileCrawlId = new Long(0);
	private String idType = "Site";
	
	public WorkSet(Long id) {
		this.id = id;
	}
	
	public WorkSet() {
	}
	
	public void addWorkItem(WorkItem workItem) {
		workItem.setWorkSet(this);
		workItem.setSiteCrawlId(this.siteCrawlId);
		workItem.setSiteId(this.siteId);
		workItem.setCrawlSetId(this.crawlSetId);
		workItem.setMobileCrawlId(this.mobileCrawlId);
		workItems.put(workItem.getWorkType(), workItem);
	}
	
	public void removeWorkItem(WorkType workType) {
		workItems.remove(workType);
	}
	
	public WorkItem getWorkItem(WorkType workType) {
		return workItems.get(workType);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public Map<WorkType, WorkItem> getWorkItems() {
		return workItems;
	}
	
	//Relies on the order of items in WorkType to get the order done properly
	public WorkItem getNextWorkItem() {
		WorkItem next = null;
		for(WorkType workType : WorkType.values()) {
			next = workItems.get(workType);
			if(next != null && next.getWorkStatus() == WorkStatus.DO_WORK){
				return next;
			}
		}
		return null;
	}

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Long getSiteCrawlId() {
		return siteCrawlId;
	}

	public void setSiteCrawlId(Long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}

	public Long getCrawlSetId() {
		return crawlSetId;
	}

	public void setCrawlSetId(Long crawlSetId) {
		this.crawlSetId = crawlSetId;
	}

	public long getUuid() {
		return uuid;
	}

	public Long getMobileCrawlId() {
		return mobileCrawlId;
	}

	public void setMobileCrawlId(Long mobileCrawlId) {
		this.mobileCrawlId = mobileCrawlId;
	}
	

}
