package async.async;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import akka.actor.ActorRef;
import async.monitoring.AsyncMonitor;

public class WaitingRoom {
	
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

	public  boolean contains(ActorRef customer) {
		return customers.containsKey(customer);
	}
	
	public  int size() {
		return customers.size();
	}
	
	public  void clear() {
		customers.clear();
	}
	
	public boolean isEmpty(){
		return size() < 1;
	}
	
}
