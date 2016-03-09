package async.work;

import async.tools.AmalgamationTool;
import async.tools.DataTransferWorker;
import async.tools.DocAnalysisTool;
import async.tools.GooglePlacesWorker;
import async.tools.MetaAnalysisTool;
import async.tools.SiteCrawlTool;
import async.tools.SiteImportTool;
import async.tools.SiteUpdateTool;
import async.tools.TextAnalysisTool;
import async.tools.UrlResolveTool;
import async.work.infofetch.InfoFetchWorker;
import async.workers.MobileAnalysisWorker;
import async.workers.MobileWorker;

 
//The types of work to be done by workers in this program.
//ORDER MATTERS. 
//Higher tasks may need to be completed before lower tasks are possible to complete
public enum WorkType {
	NO_WORK						,
	RESTORE						(DataTransferWorker.class), 
	URL							, 
	REDIRECT_RESOLVE			(UrlResolveTool.class), 
	SITE_IMPORT					(SiteImportTool.class),
	SITE_UPDATE					(SiteUpdateTool.class),
	SITE_CRAWL					(SiteCrawlTool.class, 15), 
	SMALL_CRAWL					(),
	MOBILE_TEST					(MobileWorker.class),
	MOBILE_ANALYSIS				(MobileAnalysisWorker.class),
	AMALGAMATION				(AmalgamationTool.class),
	DOC_ANALYSIS				(DocAnalysisTool.class),
	STRING_EXTRACTION			, 
	STAFF_EXTRACTION			,
	TEXT_ANALYSIS				(TextAnalysisTool.class), 
	MATCHES						, 
	SUMMARY						, 
	META_ANALYSIS				(MetaAnalysisTool.class),
	PLACES_PAGE_FETCH			(GooglePlacesWorker.class),
	CUSTOM						, 
	BACKUP						(DataTransferWorker.class),
	INFO_FETCH					(InfoFetchWorker.class, 20),
	TASK						,	
	SUPERTASK					,
	SF_LINK						,
	INVENTORY_COUNT
	;

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
