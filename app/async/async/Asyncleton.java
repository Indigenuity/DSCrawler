package async.async;

import play.Logger;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;
import async.functionalwork.ConsumerWorkOrder;
import async.functionalwork.FunctionWorkOrder;
import async.functionalwork.FunctionalWorker;
import async.functionalwork.JpaFunctionalWorker;
import async.functionalwork.MonotypeMaster;
import async.functionalwork.ResultAccumulator;
import crawling.CrawlMaster;
import datatransfer.reports.Report;
import newwork.TerminateWhenFinished;

public class Asyncleton {
	
	public static final int DEFAULT_NUM_WORKERS = 25; 
	public static final int NUM_CRAWLER_WORKERS = 25;

	private static final Asyncleton instance = new Asyncleton();
	
	
	private ActorSystem mainSystem;
	
	private ActorRef mainListener;
	private ActorRef mainMaster;
	
	private Map<Class<?>, ActorRef> oneShotMasters = new HashMap<Class<?>, ActorRef>();
	
	private boolean initialized;
	
	protected Asyncleton(){
		initialize();
	} 
	
	public void sendMaxWorkerConfig(Class<?> clazz, MaxWorkerConfig config){
		queueOneShotMessage(clazz, config, ActorRef.noSender());
	}
	
	public void queueOneShotMessage(Class<?> clazz, Object message){
		queueOneShotMessage(clazz, message, ActorRef.noSender());
	}
	
	public void queueOneShotMessage(Class<?> clazz, Object message, ActorRef sender){
		ActorRef master = getOneShotMaster(clazz);
		master.tell(message, sender);
	}
	
	public ActorRef getOneShotMaster(Class<?> clazz){
		ActorRef master = oneShotMasters.get(clazz);
		if(master == null){
			master = generateOneShotMaster(clazz);
			oneShotMasters.put(clazz, master);
		}
		return master;
	}
	
	private ActorRef generateOneShotMaster(Class<?> clazz){
		return mainSystem.actorOf(Props.create(TypedOneShotMaster.class, DEFAULT_NUM_WORKERS, clazz).withDispatcher("akka.master-dispatcher"));
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
	
	public <T, R> Future<Object> runResultAccumulator(Function<T, R> function, Function<List<R>, ?> cumulativeConsumer, Stream<T> inputs, int numWorkers, boolean needsJpa) throws Exception{
		Class<?> clazz = needsJpa ? JpaFunctionalWorker.class : FunctionalWorker.class;
		ActorRef accumulator = mainSystem.actorOf(Props.create(ResultAccumulator.class, numWorkers, clazz, cumulativeConsumer));
		inputs.forEach((input) -> {
			FunctionWorkOrder<T, R> workOrder = new FunctionWorkOrder<T, R>(function, input);
			accumulator.tell(workOrder, ActorRef.noSender());
		});
		Timeout timeout = new Timeout(FiniteDuration.create(1, TimeUnit.DAYS));
		return Patterns.ask(accumulator, new TerminateWhenFinished(), timeout);
	}
	
	private void initialize() {
		if(!initialized) {
			
			Logger.info("Starting up main async system");
			mainSystem = ActorSystem.create("mainSystem");
			mainListener = mainSystem.actorOf(Props.create(MainListener.class), "mainListener");
//			crawlMaster = mainSystem.actorOf(Props.create(CrawlMaster.class, NUM_CRAWLER_WORKERS).withDispatcher("akka.master-dispatcher"));
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
