package async.functionalwork;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import akka.actor.UntypedActor;
import async.async.TypedOneShotMaster;
import datatransfer.reports.ReportRow;
import newwork.WorkResult;
import play.Logger;

public class ResultAccumulator<R> extends MonotypeMaster{
	
	private final List<R> results = new ArrayList<R>();
	private final Function<List<R>, ?> cumulativeFunction;

	public ResultAccumulator(int maxWorkers, Class<?> clazz, Function<List<R>, ?> cumulativeConsumer) {
		super(maxWorkers, clazz);
		this.cumulativeFunction = cumulativeConsumer;
	}

	@Override
	public void processWorkResult(WorkResult workResult) {
		results.add((R) workResult.getResult());
		super.processWorkResult(workResult);
	}
	
	@Override
	public void doShutdown() {
		Logger.debug("ResultAccumulator " + this.hashCode() + " running now on " + results.size() + " results...");
		Object cumulativeResult = this.cumulativeFunction.apply(results);
		this.notifyWhenFinished.tell(cumulativeResult, getSelf());
		Logger.debug("ResultAccumulator " + this.hashCode() + " finished");
		super.doShutdown();
	}
}
