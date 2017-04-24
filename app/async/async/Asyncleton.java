package async.async;

import play.Logger;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import async.functionalwork.ConsumerWorkOrder;
import async.functionalwork.FunctionWorkOrder;
import async.functionalwork.FunctionalWorker;
import async.functionalwork.JpaFunctionalWorker;
import async.functionalwork.MonotypeMaster;
import crawling.CrawlMaster;

public class Asyncleton {
	
	public static final int DEFAULT_NUM_WORKERS = 5;    

	private static final Asyncleton instance = new Asyncleton();
	
	
	private ActorSystem mainSystem;
	
	private ActorRef mainListener;
	private ActorRef mainMaster;
	private ActorRef crawlMaster;
	
	private boolean initialized;
	
	protected Asyncleton(){
		initialize();
	} 
	
	public ActorRef getCrawlMaster() {
		return crawlMaster;
	}
	
	public ActorRef getMonotypeMaster(int numWorkers, Class<?> clazz) {
		return mainSystem.actorOf(Props.create(MonotypeMaster.class, numWorkers, clazz).withDispatcher("akka.master-dispatcher")); 
	}
	
	public <T> void runConsumerMaster(int numWorkers, Consumer<T> consumer, Stream<T> inputs, boolean needsJpa){
		Class<?> clazz = needsJpa ? JpaFunctionalWorker.class : FunctionalWorker.class;
		ActorRef functionalMaster = getMonotypeMaster(numWorkers, clazz);
		
		inputs.forEach((input) -> {
			ConsumerWorkOrder<T> workOrder = new ConsumerWorkOrder<T>(consumer, input);
			functionalMaster.tell(workOrder, ActorRef.noSender());
		});
	}
	
	public <T, U> void runFunctionMaster(int numWorkers, Function<T, U> consumer, Stream<T> inputs, boolean needsJpa){
		Class<?> clazz = needsJpa ? JpaFunctionalWorker.class : FunctionalWorker.class;
		ActorRef functionalMaster = getMonotypeMaster(numWorkers, clazz);
		inputs.forEach((input) -> {
			FunctionWorkOrder<T, U> workOrder = new FunctionWorkOrder<T, U>(consumer, input);
			functionalMaster.tell(workOrder, ActorRef.noSender());
		});
	}
	
	private void initialize() {
		if(!initialized) {
			
			Logger.info("Starting up main async system");
			mainSystem = ActorSystem.create("mainSystem");
			mainListener = mainSystem.actorOf(Props.create(MainListener.class), "mainListener");
			crawlMaster = mainSystem.actorOf(Props.create(CrawlMaster.class).withDispatcher("akka.master-dispatcher"));
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
