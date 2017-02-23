package async.monitoring;

import async.monitoring.WaitingRoom.WaitingRoomStatus;

public class WaitingRoomSummary {

	private String name;
	private int size;
	private int totalAdded;
	private int totalFinished;
	private WaitingRoomStatus roomStatus;
	
	public WaitingRoomSummary(WaitingRoom waitingRoom){
		this.name = waitingRoom.getName();
		this.size = waitingRoom.size();
		this.totalAdded = waitingRoom.getTotalAdded();
		this.totalFinished = waitingRoom.getTotalFinished();
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
	public WaitingRoomStatus getRoomStatus() {
		return roomStatus;
	}

	public String getName() {
		return name;
	}
	
}
