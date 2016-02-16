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
import async.analysis.*;
import async.crawling.CrawlingListener;
import async.crawling.CrawlingMaster;
import async.datatransfer.DataTransferMaster;
import async.docanalysis.DocAnalysisMaster;
import async.metaanalysis.MetaAnalysisMaster;
import async.monitoring.MonitoringListener;
import async.registration.WorkerRegistry;
import async.sniffer.SnifferListener;
import async.sniffer.SnifferMaster;
import async.textanalysis.TextAnalysisMaster;
import async.work.UniqueMaster;
import async.work.WorkType;
import async.work.infofetch.InfoFetchWorker;

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
		for(Entry<WorkType, Class<?>> entry: WorkerRegistry.getInstance().getRegistry().entrySet()){
			if(entry.getValue() == MainWorker.class){
				masters.put(entry.getKey(), mainMaster);
			}
//			else if(entry.getValue() == InfoFetchWorker.class){
//				ActorRef master = mainSystem.actorOf(Props.create(UniqueMaster.class, mainListener, entry.getValue()));
//				masters.put(entry.getKey(), master);
//			}
			else{
				ActorRef master = mainSystem.actorOf(Props.create(GenericMaster.class, entry.getKey().getNumWorker(), mainListener, entry.getValue()));
				masters.put(entry.getKey(), master);
			}
		}
	}
	
	public ActorRef getMaster(WorkType workType) {
		return masters.get(workType);
	}
	
	private void initialize() {
		if(!initialized) {
			
			Logger.info("Starting up main async system");
			mainSystem = ActorSystem.create("mainSystem");
			mainListener = mainSystem.actorOf(Props.create(MainListener.class), "mainListener");
			mainMaster = mainSystem.actorOf(Props.create(MainMaster.class, 60, mainListener));
			Logger.info("Main async system ready for jobs");
			
			 
			
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
