package agarbagefolder;

import async.monitoring.AsyncMonitor.WorkInProgress;
import persistence.CrawlSet;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.SiteInformationOld;
import persistence.SiteSummary;

public class SiteWork {
	
	public static final int NO_WORK = 0;
	public static final int DO_WORK = 1;
	public static final int WORK_IN_PROGRESS = 2;		
	public static final int WORK_COMPLETED = 3;
	
	public static final int SMALL_CRAWL = 0;
	public static final int NORMAL_CRAWL = 1;
	

	private SiteInformationOld siteInfo;
	private SiteSummary siteSummary;
	private SiteCrawl siteCrawl;
	private Site site;
	private CrawlSet crawlSet;
	
	private long crawlSetId = -1;
	private long siteId = -1;
	private long siteCrawlId = -1;
	

	private int urlWork = NO_WORK;
	private int redirectResolveWork = NO_WORK;
	private int crawlWork = NO_WORK;
	private int docAnalysisWork = NO_WORK;
	private int metaAnalysisWork = NO_WORK;
	private int amalgamationWork = NO_WORK;
	private int textAnalysisWork = NO_WORK;
	private int matchesWork = NO_WORK;
	private int stringExtractionWork = NO_WORK;
	private int staffExtractionWork = NO_WORK;
	private int summaryWork = NO_WORK;
	private int backupWork = NO_WORK;
	private int restoreWork = NO_WORK;
	
	private int allWorkNeeded = NO_WORK;
	
	private int customWork = NO_WORK;

	private int crawlType = NORMAL_CRAWL;
	
	public SiteWork(SiteInformationOld siteInfo){
		this.siteInfo = siteInfo;
	}
	
	public SiteWork(SiteSummary siteSummary) {
		this.siteSummary = siteSummary;
	}
	
	public SiteWork() {
		
	}
	
	public void clearWork() {
		this.urlWork = NO_WORK;
		this.redirectResolveWork = NO_WORK;
		this.crawlWork = NO_WORK;
		this.docAnalysisWork = NO_WORK;
		this.amalgamationWork = NO_WORK;
		this.textAnalysisWork = NO_WORK;
		this.matchesWork = NO_WORK;
		this.stringExtractionWork = NO_WORK;
		this.staffExtractionWork = NO_WORK;
		this.summaryWork = NO_WORK;
		this.backupWork = NO_WORK;
		this.restoreWork = NO_WORK;
	}
	
	public SiteInformationOld getSiteInfo() {
		return siteInfo;
	}

	public void setSiteInfo(SiteInformationOld siteInfo) {
		this.siteInfo = siteInfo;
	}

	public SiteSummary getSiteSummary() {
		return siteSummary;
	}

	public void setSiteSummary(SiteSummary siteSummary) {
		this.siteSummary = siteSummary;
	}

	public int getUrlWork() {
		return urlWork;
	}

	public void setUrlWork(int urlWork) {
		this.urlWork = urlWork;
	}

	public int getRedirectResolveWork() {
		return redirectResolveWork;
	}

	public void setRedirectResolveWork(int redirectResolveWork) {
		this.redirectResolveWork = redirectResolveWork;
	}

	public int getCrawlWork() {
		return crawlWork;
	}

	public void setCrawlWork(int crawlWork) {
		this.crawlWork = crawlWork;
	}

	public int getMatchesWork() {
		return matchesWork;
	}

	public void setMatchesWork(int matchesWork) {
		this.matchesWork = matchesWork;
	}

	public int getStringExtractionWork() {
		return stringExtractionWork;
	}

	public void setStringExtractionWork(int stringExtractionWork) {
		this.stringExtractionWork = stringExtractionWork;
	}

	public int getStaffExtractionWork() {
		return staffExtractionWork;
	}

	public void setStaffExtractionWork(int staffExtractionWork) {
		this.staffExtractionWork = staffExtractionWork;
	}

	public int getSummaryWork() {
		return summaryWork;
	}

	public void setSummaryWork(int summaryWork) {
		this.summaryWork = summaryWork;
	}

	public int getAllWorkNeeded() {
		return allWorkNeeded;
	}

	public void setAllWorkNeeded(int allWorkNeeded) {
		this.allWorkNeeded = allWorkNeeded;
	}

	public int getCustomWork() {
		return customWork;
	}

	public void setCustomWork(int customWork) {
		this.customWork = customWork;
	}

	public SiteCrawl getSiteCrawl() {
		return siteCrawl;
	}

	public void setSiteCrawl(SiteCrawl siteCrawl) {
		this.siteCrawl = siteCrawl;
	}

	public int getDocAnalysisWork() {
		return docAnalysisWork;
	}

	public void setDocAnalysisWork(int docAnalysisWork) {
		this.docAnalysisWork = docAnalysisWork;
	}

	public int getAmalgamationWork() {
		return amalgamationWork;
	}

	public void setAmalgamationWork(int amalgamationWork) {
		this.amalgamationWork = amalgamationWork;
	}

	public int getTextAnalysisWork() {
		return textAnalysisWork;
	}

	public void setTextAnalysisWork(int textAnalysisWork) {
		this.textAnalysisWork = textAnalysisWork;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public int getCrawlType() {
		return crawlType;
	}

	public void setCrawlType(int crawlType) {
		this.crawlType = crawlType;
	}

	public CrawlSet getCrawlSet() {
		return crawlSet;
	}

	public void setCrawlSet(CrawlSet crawlSet) {
		this.crawlSet = crawlSet;
	}

	public int getMetaAnalysisWork() {
		return metaAnalysisWork;
	}

	public void setMetaAnalysisWork(int metaAnalysisWork) {
		this.metaAnalysisWork = metaAnalysisWork;
	}

	public long getCrawlSetId() {
		return crawlSetId;
	}

	public void setCrawlSetId(long crawlSetId) {
		this.crawlSetId = crawlSetId;
	}

	public long getSiteId() {
		return siteId;
	}

	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	public long getSiteCrawlId() {
		return siteCrawlId;
	}

	public void setSiteCrawlId(long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}

	public int getBackupWork() {
		return backupWork;
	}

	public void setBackupWork(int backupWork) {
		this.backupWork = backupWork;
	}

	public int getRestoreWork() {
		return restoreWork;
	}

	public void setRestoreWork(int restoreWork) {
		this.restoreWork = restoreWork;
	}

	
	
	
}
