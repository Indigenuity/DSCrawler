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
import async.sniffer.SnifferListener;
import async.sniffer.SnifferMaster;
import async.textanalysis.TextAnalysisMaster;
import async.work.WorkType;
import async.work.WorkerRegistry;

public class Asyncleton {
	
	public static final int DEFAULT_NUM_WORKERS = 5;

	private static final Asyncleton instance = new Asyncleton();
	
	private final Map<WorkType, ActorRef> masters = Collections.synchronizedMap(new HashMap<WorkType, ActorRef>());
	
	private ActorSystem mainSystem;
	private ActorSystem redirectResolveSystem;
	private ActorSystem crawlSystem;
	private ActorSystem docAnalysisSystem;
	private ActorSystem metaAnalysisSystem;
	private ActorSystem amalgamationSystem;
	private ActorSystem textAnalysisSystem;
	private ActorSystem inferenceSystem;
	private ActorSystem dataTransferSystem;
	
	private ActorSystem matchingsystem;
	private ActorSystem stringExtractionSystem;
	private ActorSystem staffExtractionSystem;
	private ActorSystem summarySystem;
	
	private ActorRef mainListener;
	private ActorRef mainMaster;
	
	private ActorRef crawlingMaster;
	private ActorRef docAnalysisMaster;
	private ActorRef metaAnalysisMaster;
	private ActorRef amalgamationMaster;
	private ActorRef textAnalysisMaster;
	private ActorRef inferenceMaster;
	private ActorRef dataTransferMaster;
	
	
	private ActorRef redirectResolveMaster;
	
	private ActorRef matchingMaster;
	private ActorRef stringExtractionMaster;
	private ActorRef staffExtractionMaster;
	private ActorRef summaryMaster;
	
	private boolean initialized;
	
	protected Asyncleton(){
		initialize();
		for(Entry<WorkType, Class<?>> entry: WorkerRegistry.getInstance().getRegistry().entrySet()){
			if(entry.getValue() == MainWorker.class){
				masters.put(entry.getKey(), mainMaster);
			}
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
			redirectResolveSystem = ActorSystem.create("redirectResolveSystem");
			crawlSystem = ActorSystem.create("crawlSystem");
			docAnalysisSystem = ActorSystem.create("docAnalysisSystem");
			metaAnalysisSystem = ActorSystem.create("metaAnalysisSystem");
			amalgamationSystem = ActorSystem.create("amalgamationSystem");
			textAnalysisSystem = ActorSystem.create("textAnalysisSystem");
			inferenceSystem = ActorSystem.create("inferenceSystem");
			dataTransferSystem = ActorSystem.create("dataTransferSystem");
			
			
			matchingsystem = ActorSystem.create("matchingSystem");
			stringExtractionSystem = ActorSystem.create("stringExtractionSystem");
			staffExtractionSystem = ActorSystem.create("staffExtractionSystem");
			summarySystem = ActorSystem.create("summarySystem");
			
			mainListener = mainSystem.actorOf(Props.create(MainListener.class), "mainListener");
			mainMaster = mainSystem.actorOf(Props.create(MainMaster.class, 60, mainListener));
			Logger.info("Main async system ready for jobs");
			
			 
			redirectResolveMaster = redirectResolveSystem.actorOf(Props.create(SnifferMaster.class, 5));
			crawlingMaster = crawlSystem.actorOf(Props.create(CrawlingMaster.class, 22));
			docAnalysisMaster = docAnalysisSystem.actorOf(Props.create(DocAnalysisMaster.class, 22));
			metaAnalysisMaster = metaAnalysisSystem.actorOf(Props.create(MetaAnalysisMaster.class, 22));
			amalgamationMaster = amalgamationSystem.actorOf(Props.create(AmalgamationMaster.class, 22));
			textAnalysisMaster = textAnalysisSystem.actorOf(Props.create(TextAnalysisMaster.class, 22));
//			inferenceMaster = inferenceSystem.actorOf(Props.create(InferenceMaster.class, 22));
			dataTransferMaster = dataTransferSystem.actorOf(Props.create(DataTransferMaster.class, 22));
			
			matchingMaster = matchingsystem.actorOf(Props.create(MatchingMaster.class, 20));
			stringExtractionMaster = stringExtractionSystem.actorOf(Props.create(StringExtractionMaster.class, 10));
			staffExtractionMaster = staffExtractionSystem.actorOf(Props.create(StaffExtractionMaster.class, 10));
			summaryMaster = summarySystem.actorOf(Props.create(SummaryMaster.class, 10));
		}
	}

	public static Asyncleton instance() { 
		return instance;
	}

	public ActorRef getCrawlingMaster() {
		return crawlingMaster;
	}

	public ActorRef getRedirectResolveMaster() {
		return redirectResolveMaster;
	}

	public ActorRef getMatchingMaster() {
		return matchingMaster;
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
	
	public ActorRef getStringExtractionMaster() {
		return stringExtractionMaster;
	}

	public ActorRef getStaffExtractionMaster() {
		return staffExtractionMaster;
	}

	public ActorRef getSummaryMaster() {
		return summaryMaster;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public static Asyncleton getInstance() {
		return instance;
	}

	public ActorSystem getRedirectResolveSystem() {
		return redirectResolveSystem;
	}

	public ActorSystem getCrawlSystem() {
		return crawlSystem;
	}

	public ActorSystem getDocAnalysisSystem() {
		return docAnalysisSystem;
	}

	public ActorSystem getAmalgamationSystem() {
		return amalgamationSystem;
	}

	public ActorSystem getTextAnalysisSystem() {
		return textAnalysisSystem;
	}

	public ActorSystem getInferenceSystem() {
		return inferenceSystem;
	}

	public ActorSystem getMatchingsystem() {
		return matchingsystem;
	}

	public ActorSystem getStringExtractionSystem() {
		return stringExtractionSystem;
	}

	public ActorSystem getStaffExtractionSystem() {
		return staffExtractionSystem;
	}

	public ActorSystem getSummarySystem() {
		return summarySystem;
	}

	public ActorRef getDocAnalysisMaster() {
		return docAnalysisMaster;
	}
	
	public ActorRef getMetaAnalysisMaster() {
		return metaAnalysisMaster;
	}

	public ActorRef getAmalgamationMaster() {
		return amalgamationMaster;
	}

	public ActorRef getTextAnalysisMaster() {
		return textAnalysisMaster;
	}

	public ActorRef getInferenceMaster() {
		return inferenceMaster;
	}
	
	public ActorRef getDataTransferMaster() {
		return dataTransferMaster;
	}
	
	
	
	
	
}
