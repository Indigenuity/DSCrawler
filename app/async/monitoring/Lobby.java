package async.monitoring;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class Lobby {

	private static final LinkedHashSet<WaitingRoom> waitingRooms = new LinkedHashSet<WaitingRoom>();
	
	public static LinkedHashSet<WaitingRoomSummary> getRoomSummaries(){
		synchronized(waitingRooms){
			return waitingRooms.stream()
					.map(WaitingRoomSummary::new)
					.collect(Collectors.toCollection(LinkedHashSet::new));
		}
	}
	
	public static boolean add(WaitingRoom room){
		return waitingRooms.add(room);
	}
	
	public static boolean remove(WaitingRoom room){
		return waitingRooms.remove(room);
	}
}
