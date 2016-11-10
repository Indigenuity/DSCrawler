package crawling.discovery.execution;

import java.util.LinkedList;

import crawling.discovery.planning.PrimaryResourcePlan;
import newwork.WorkResult;

public class FetchQueue<T> {


	protected PrimaryResourcePlan<T> plan;
	
	protected LinkedList<ResourceRequest<T>> queue = new LinkedList<ResourceRequest<T>>();
	protected LinkedList<Long> inProgress = new LinkedList<Long>();
	
	
	public synchronized boolean add(ResourceRequest<T> request){
		return queue.add(request);
	}
	
	public synchronized FetchWorkOrder<T> next(){
		ResourceRequest<T> request = queue.pop();
		if(request != null){
			FetchWorkOrder<T> workOrder = new FetchWorkOrder<T>(this.plan, request); 
			inProgress.add(workOrder.getUuid());
			return workOrder;
		}
		return null;
	}
	
	public synchronized boolean finish(WorkResult workResult) {
		return inProgress.remove(workResult.getUuid());
	}
	
	public synchronized boolean queueIsEmpty(){
		return queue.isEmpty();
	}
	
	public synchronized boolean inProgressIsEmpty(){
		return inProgress.isEmpty();
	}
	
	public synchronized boolean isIdle(){
		return queue.isEmpty() && inProgress.isEmpty();
	}
}
