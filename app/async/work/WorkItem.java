package async.work;

import java.util.UUID;

public class WorkItem {

	private WorkType workType;
	private WorkStatus workStatus = WorkStatus.NO_WORK;
	private WorkSet workSet;
	private Long uuid = UUID.randomUUID().getLeastSignificantBits();
	private Long id;
	private Long siteId;
	private Long siteCrawlId;
	private Long crawlSetId;
	
	public WorkItem(WorkType workType, WorkStatus workStatus) {
		this.workType = workType;
		this.workStatus = workStatus;
	}
	
	public WorkItem(WorkType workType) {
		this.workType = workType;
		this.workStatus = WorkStatus.DO_WORK;
	}

	public WorkType getWorkType() {
		return workType;
	}

	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}

	public WorkStatus getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(WorkStatus workStatus) {
		this.workStatus = workStatus;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public WorkSet getWorkSet() {
		return workSet;
	}

	public void setWorkSet(WorkSet workSet) {
		this.workSet = workSet;
	}

	public Long getUuid() {
		return uuid;
	}

	
	
}
