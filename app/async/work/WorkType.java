package async.work;

import async.MainWorker;
import async.amalgamation.AmalgamationWorker;
import async.crawling.CrawlingWorker;
import async.datatransfer.DataTransferWorker;
import async.docanalysis.DocAnalysisWorker;
import async.metaanalysis.MetaAnalysisWorker;
import async.sniffer.SnifferWorker;
import async.textanalysis.TextAnalysisWorker;
import async.workers.InfoFetchWorker;
import async.workers.MobileAnalysisWorker;
import async.workers.MobileWorker;
import async.workers.UrlResolveWorker;

 
//The types of work to be done by workers in this program.
//ORDER MATTERS. 
//Higher tasks may need to be completed before lower tasks are possible to complete
public enum WorkType {
	NO_WORK						,
	RESTORE						(DataTransferWorker.class), 
	URL							, 
	REDIRECT_RESOLVE			(UrlResolveWorker.class), 
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
	CUSTOM						, 
	BACKUP						(DataTransferWorker.class),
	INFO_FETCH					(InfoFetchWorker.class);

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
