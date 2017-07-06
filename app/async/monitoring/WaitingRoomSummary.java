package async.monitoring;

import async.monitoring.WaitingRoom.WaitingRoomStatus;

public class WaitingRoomSummary {

	private final String name;
	private final int size;
	private final int totalAdded;
	private final int totalFinished;
	private final int backOrderCount;
	private final WaitingRoomStatus roomStatus;
	
	public WaitingRoomSummary(WaitingRoom waitingRoom){
		this.name = waitingRoom.getName();
		this.size = waitingRoom.size();
		this.totalAdded = waitingRoom.getTotalAdded();
		this.totalFinished = waitingRoom.getTotalFinished();
		this.backOrderCount = waitingRoom.backOrderCount();
		this.roomStatus = waitingRoom.getRoomStatus();
	}
	
	public int getSize() {
		return size;
	}
	public int getTotalAdded() {
		return totalAdded;
	}
	public int getTotalFinished() {
		return totalFinished;
	}
	public int getBackOrderCount() {
		return backOrderCount;
	}
	public WaitingRoomStatus getRoomStatus() {
		return roomStatus;
	}
	public String getName() {
		return name;
	}
	
}
