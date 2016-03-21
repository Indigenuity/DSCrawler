package async.async;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import agarbagefolder.WorkSet;
import akka.actor.ActorRef;
import async.monitoring.AsyncMonitor;
import async.work.WorkItem;

public class WaitingRoom {
	
	private final Map<WorkItem, WorkSet> workSets = Collections.synchronizedMap(new HashMap<WorkItem, WorkSet>());
	private final Map<Long, ActorRef> customers = Collections.synchronizedMap(new HashMap<Long, ActorRef>());
	
	private String name;
	private Long uuid = UUID.randomUUID().getLeastSignificantBits();
	
	public WaitingRoom() {
		this.name = "Unnamed waiting room " + uuid;
	}
	
	public WaitingRoom(String name) {
		this.name = name;
	}
	
	//Functionally act like adding to a set
	public  boolean add(Long uuid, ActorRef customer){
//		System.out.println("Customer entered waiting room : " + uuid);  
		synchronized (customers){
			if(customers.containsKey(uuid)) {
				return false;
			}
			AsyncMonitor.instance().addWip(this.name, uuid);
			customers.put(uuid, customer);
			return true;
		}
	}
	
	public ActorRef remove(Long uuid){
//		System.out.println("Customer leaving waiting room : " + uuid);
		synchronized(customers) {
			AsyncMonitor.instance().finishWip(this.name, uuid);
			return customers.remove(uuid);
		}
	}

	//Functionally act like adding to a set
	public boolean add(WorkItem workItem, WorkSet workSet){
//		System.out.println("workset entered waiting room : " + workSet.getUuid());
		synchronized (workSets){
			if(workSets.containsKey(workItem)) {
				return false;
			}
			workSets.put(workItem, workSet);
			return true;
		}
	}
	
	public WorkSet remove(WorkItem workItem) {
//		System.out.println("workset leaving waiting room : " + workItem.getUuid());
		return workSets.remove(workItem);
	}
	
	public  boolean contains(ActorRef customer) {
		return customers.containsKey(customer);
	}
	
	public  int size() {
		return customers.size();
	}
	
	public  void clear() {
		customers.clear();
	}
	
}
