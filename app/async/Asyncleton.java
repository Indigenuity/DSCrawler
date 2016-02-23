package async;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import play.Logger;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import async.amalgamation.AmalgamationMaster;
import async.amalgamation.AmalgamationWorker;
import async.analysis.*;
import async.crawling.CrawlingListener;
import async.crawling.CrawlingMaster;
import async.datatransfer.DataTransferMaster;
import async.docanalysis.DocAnalysisMaster;
import async.docanalysis.DocAnalysisWorker;
import async.metaanalysis.MetaAnalysisMaster;
import async.metaanalysis.MetaAnalysisWorker;
import async.monitoring.MonitoringListener;
import async.registration.RegistryEntry;
import async.registration.ContextItem;
import async.registration.WorkerRegistry;
import async.sniffer.SnifferListener;
import async.sniffer.SnifferMaster;
import async.textanalysis.TextAnalysisMaster;
import async.textanalysis.TextAnalysisWorker;
import async.tools.UrlResolveTool;
import async.work.SiteImportWorker;
import async.work.UniqueMaster;
import async.work.WorkType;
import async.work.crawling.CrawlingWorker;
import async.work.infofetch.InfoFetchWorker;
import async.work.siteupdate.SiteUpdateWorker;
import persistence.tasks.Task;

public class Asyncleton {
	
	public static final int DEFAULT_NUM_WORKERS = 5;

	private static final Asyncleton instance = new Asyncleton();
	
	private final Map<WorkType, ActorRef> masters = Collections.synchronizedMap(new HashMap<WorkType, ActorRef>());
	
	private ActorSystem mainSystem;
	
	private ActorRef mainListener;
	private ActorRef mainMaster;
	
	private boolean initialized;
	
	protected Asyncleton(){
		initialize();
		for(Entry<WorkType, RegistryEntry> entry: WorkerRegistry.getInstance().getRegistry().entrySet()){
			ActorRef master = mainSystem.actorOf(Props.create(GenericMaster.class, entry.getValue().getNumWorkers(), mainListener, entry.getValue().getClazz()));
			masters.put(entry.getKey(), master);
		}
	}
	
	public ActorRef getMaster(WorkType workType) {
		return masters.get(workType);
	}
	
	public void doTask(Task task, ActorRef sender, boolean checkRequiredContextItems) {
		RegistryEntry entry = WorkerRegistry.getInstance().getRegistrant(task.getWorkType());
		
		if(checkRequiredContextItems) {
			for(ContextItem item : entry.getRequiredContextItems()){
				String name = item.getName();
				boolean nullable  = item.isNullable();
				if(task.getContextItem(name) == null && !nullable){
					throw new IllegalArgumentException("Cannot perform " + entry.getWorkType() + " task without required context item " + name);
				}
			}
		}
		
		masters.get(entry.getWorkType()).tell(task, sender);
	}
	
	
	
	private void initialize() {
		if(!initialized) {
			
			Logger.info("Starting up main async system");
			mainSystem = ActorSystem.create("mainSystem");
			mainListener = mainSystem.actorOf(Props.create(MainListener.class), "mainListener");
			mainMaster = mainSystem.actorOf(Props.create(MainMaster.class, 60, mainListener));
			Logger.info("Main async system ready for jobs");
			
			 
			RegistryEntry entry = new RegistryEntry();
			
			
			tool = new UrlResolveTool();
			
			entry.setClazz(UrlResolveTool.class);
			entry.setWorkType(WorkType.REDIRECT_RESOLVE);
			ContextItem item = new ContextItem("seed", String.class, false);
			entry.addRequiredContextItem(item);
			item = new ContextItem("urlCheckId", Long.class, false);
			entry.addResultContextItem(item);
			WorkerRegistry.getInstance().register(entry.getWorkType(), entry);
			
			entry = new RegistryEntry();
			entry.setClazz(InfoFetchWorker.class);
			entry.setWorkType(WorkType.INFO_FETCH);
			item = new ContextItem("infoFetchId", Long.class, false);
			entry.addRequiredContextItem(item);
			WorkerRegistry.getInstance().register(entry.getWorkType(), entry);
			
			entry = new RegistryEntry();
			entry.setClazz(SiteImportWorker.class);
			entry.setWorkType(WorkType.SITE_IMPORT);
			item = new ContextItem("urlCheckId", Long.class, false);
			entry.addRequiredContextItem(item);
			item = new ContextItem("franchise", Boolean.class, false);
			entry.addRequiredContextItem(item);
			item = new ContextItem("siteId", Long.class, false);
			entry.addResultContextItem(item);
			WorkerRegistry.getInstance().register(entry.getWorkType(), entry);
			
			entry = new RegistryEntry();
			entry.setClazz(SiteUpdateWorker.class);
			entry.setWorkType(WorkType.SITE_UPDATE);
			item = new ContextItem("siteId", Long.class, false);
			entry.addRequiredContextItem(item);
			item = new ContextItem("urlCheckId", Long.class, false);
			entry.addRequiredContextItem(item);
			WorkerRegistry.getInstance().register(entry.getWorkType(), entry);
			
			entry = new RegistryEntry();
			entry.setClazz(CrawlingWorker.class);
			entry.setWorkType(WorkType.SITE_CRAWL);
			item = new ContextItem("siteId", Long.class, false);
			entry.addRequiredContextItem(item);
			item = new ContextItem("siteCrawlId", Long.class, false);
			entry.addResultContextItem(item);
			WorkerRegistry.getInstance().register(entry.getWorkType(), entry);
			
			entry = new RegistryEntry();
			entry.setClazz(AmalgamationWorker.class);
			entry.setWorkType(WorkType.AMALGAMATION);
			item = new ContextItem("siteCrawlId", Long.class, false);
			entry.addRequiredContextItem(item);
			WorkerRegistry.getInstance().register(entry.getWorkType(), entry);
			
			entry = new RegistryEntry();
			entry.setClazz(TextAnalysisWorker.class);
			entry.setWorkType(WorkType.TEXT_ANALYSIS);
			item = new ContextItem("siteCrawlId", Long.class, false);
			entry.addRequiredContextItem(item);
			WorkerRegistry.getInstance().register(entry.getWorkType(), entry);
			
			entry = new RegistryEntry();
			entry.setClazz(DocAnalysisWorker.class);
			entry.setWorkType(WorkType.DOC_ANALYSIS);
			item = new ContextItem("siteCrawlId", Long.class, false);
			entry.addRequiredContextItem(item);
			WorkerRegistry.getInstance().register(entry.getWorkType(), entry);
			
			entry = new RegistryEntry();
			entry.setClazz(MetaAnalysisWorker.class);
			entry.setWorkType(WorkType.META_ANALYSIS);
			item = new ContextItem("siteCrawlId", Long.class, false);
			entry.addRequiredContextItem(item);
			WorkerRegistry.getInstance().register(entry.getWorkType(), entry);
		}
	}

	public static Asyncleton instance() { 
		return instance;
	}

	public ActorSystem getMainSystem() {
		return mainSystem;
	}

	public ActorRef getMainListener() {
		return mainListener;
	}

	public ActorRef getMainMaster() {
		return mainMaster;
	}
	
	public boolean isInitialized() {
		return initialized;
	}

	public static Asyncleton getInstance() {
		return instance;
	}

}
