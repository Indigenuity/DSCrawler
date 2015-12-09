package async;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import async.work.WorkItem;
import async.work.WorkSet;

public class WaitingRoom {
	
	private static final Map<WorkItem, WorkSet> workSets = Collections.synchronizedMap(new HashMap<WorkItem, WorkSet>());

	//Functionally act like adding to a set
	public static boolean add(WorkItem workItem, WorkSet workSet){
		System.out.println("workset entered waiting room : " + workSet.getUuid());
		synchronized (workSets){
			if(workSets.containsKey(workItem)) {
				return false;
			}
			workSets.put(workItem, workSet);
			return true;
		}
	}
	
	public static WorkSet remove(WorkItem workItem) {
		System.out.println("workset leaving waiting room : " + workItem.getUuid());
		return workSets.remove(workItem);
	}
	
	public static boolean contains(WorkItem workItem) {
		return workSets.containsKey(workItem);
	}
	
	public static int size() {
		return workSets.size();
	}
	
	public static void clear() {
		workSets.clear();
	}
	
}
