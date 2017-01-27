package async.async;

import play.Logger;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import async.functionalwork.FunctionalMaster;

public class Asyncleton {
	
	public static final int DEFAULT_NUM_WORKERS = 5;

	private static final Asyncleton instance = new Asyncleton();
	
	
	private ActorSystem mainSystem;
	
	private ActorRef mainListener;
	private ActorRef mainMaster;
	
	private boolean initialized;
	
	protected Asyncleton(){
		initialize();
//		for(Entry<WorkType, RegistryEntry> entry: WorkerRegistry.getInstance().getRegistry().entrySet()){
//			ActorRef master = mainSystem.actorOf(Props.create(GenericMaster.class, entry.getValue().getNumWorkers(), mainListener, entry.getValue().getClazz()));
//			masters.put(entry.getKey(), master);
//		}
	}
	
	public ActorRef getGenericMaster(int numWorkers, Class<? extends UntypedActor> clazz) {
		return mainSystem.actorOf(Props.create(GenericMaster.class, numWorkers, mainListener, clazz));
	}
	
	public ActorRef getFunctionalMaster(int numWorkers, boolean needsJpa) {
		return mainSystem.actorOf(Props.create(FunctionalMaster.class, numWorkers,needsJpa)); 
	}
	
	private void initialize() {
		if(!initialized) {
			
			Logger.info("Starting up main async system");
			mainSystem = ActorSystem.create("mainSystem");
			mainListener = mainSystem.actorOf(Props.create(MainListener.class), "mainListener");
			Logger.info("Main async system ready for jobs");
			 
		}
	}
	
	public void restart() {
		mainSystem.shutdown();
		initialized = false;
		initialize();
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
