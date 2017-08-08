package async.callables;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import async.functionalwork.FunctionWorkOrder;
import async.monitoring.WaitingRoom;
import newwork.TypedWorkResult;

public class CallbackExecutorWrapper {
	
	
	public static <R> Callable<R> addCallback(Callable<R> callable, Callback<R> callback){
		return () -> {
			R result;
			try{
				result = callable.call(); 
			} catch(Throwable t){
				callback.onFailure(t);
				throw t;
			}
			callback.onSuccess(result);
			return result;
		};
	}
	
	public static Runnable addCallback(Runnable runnable, Callback<?> callback){
		return () -> {
			try{
				runnable.run();
			} catch(Throwable t){
				callback.onFailure(t);
				throw t;
			}
			callback.onSuccess();
		};
	}
	
	public static <R> Future<R> submitWithCallback(ExecutorService executor, Callable<R> callable, Callback<R> callback){
		return executor.submit(addCallback(callable, callback));
	}
	
	public static Future<?> submitWithCallback(ExecutorService executor, Runnable runnable, Callback<?> callback){
		return executor.submit(addCallback(runnable, callback));
	} 
	
	public static <R> Future<R> submitWithCallback(ExecutorService executor, Runnable runnable, Callback<R> callback, R result){
		return executor.submit(addCallback(runnable, callback), result);
	} 
	
}
