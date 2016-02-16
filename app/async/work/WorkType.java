package async.work;

import async.MainWorker;
import async.amalgamation.AmalgamationWorker;
import async.datatransfer.DataTransferWorker;
import async.docanalysis.DocAnalysisWorker;
import async.metaanalysis.MetaAnalysisWorker;
import async.sniffer.SnifferWorker;
import async.textanalysis.TextAnalysisWorker;
import async.work.crawling.CrawlingWorker;
import async.work.googleplaces.GooglePlacesWorker;
import async.work.infofetch.InfoFetchWorker;
import async.work.siteupdate.SiteUpdateWorker;
import async.work.urlresolve.UrlResolveWorker;
import async.workers.MobileAnalysisWorker;
import async.workers.MobileWorker;

 
//The types of work to be done by workers in this program.
//ORDER MATTERS. 
//Higher tasks may need to be completed before lower tasks are possible to complete
public enum WorkType {
	NO_WORK						,
	RESTORE						(DataTransferWorker.class), 
	URL							, 
	REDIRECT_RESOLVE			(UrlResolveWorker.class), 
	SITE_UPDATE					(SiteUpdateWorker.class),
	CRAWL						(CrawlingWorker.class, 15), 
	SMALL_CRAWL					(),
	MOBILE_TEST					(MobileWorker.class),
	MOBILE_ANALYSIS				(MobileAnalysisWorker.class),
	AMALGAMATION				(AmalgamationWorker.class),
	DOC_ANALYSIS				(DocAnalysisWorker.class),
	STRING_EXTRACTION			, 
	STAFF_EXTRACTION			,
	TEXT_ANALYSIS				(TextAnalysisWorker.class), 
	MATCHES						, 
	SUMMARY						, 
	META_ANALYSIS				(MetaAnalysisWorker.class),
	PLACES_PAGE_FETCH			(GooglePlacesWorker.class),
	CUSTOM						, 
	BACKUP						(DataTransferWorker.class),
	INFO_FETCH					(InfoFetchWorker.class, 20);

	private static final int DEFAULT_NUM_WORKERS = 5;
	private Class<?> defaultWorker;
	private int numWorkers;
	
	private WorkType(Class<?> defaultWorker){
		this.defaultWorker = defaultWorker;
		this.numWorkers = DEFAULT_NUM_WORKERS;
	}
	
	private WorkType(Class<?> defaultWorker, int numWorkers){
		this.defaultWorker = defaultWorker;
		this.numWorkers = numWorkers;
	}
	
	private WorkType() {
		this.defaultWorker = null;
	}
	
	public Class<?> getDefaultWorker(){
		return this.defaultWorker;
	}
	
	public int getNumWorker() {
		return this.numWorkers;
	}
}
