package audit.sync;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
/**********************
 * 
 * The Sync class is intended as a bookmark of sorts for the auditing system.
 * A new Sync entity should be saved whenever you want to mark a particular revision to go back and see what changes
 * happened in a mass synchronization.  There are hundreds or thousands of changes that can happen in a single revision.
 * 
 * Looking up the revision number in the Sync audit table should reveal the revision number for all the changes made in the 
 * mass synchronization that happened alongside the Sync object creation.
 *
 */
@Entity
@Audited(withModifiedFlag=true)
public class Sync {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long syncId;
	
	private String notes;
	@Enumerated(EnumType.STRING)
	private SyncType syncType;
	private Date timeCreated = Calendar.getInstance().getTime();
	 
	
	public Sync(){}
	public Sync(SyncType syncType){
		this.syncType = syncType;
	}
	
	public long getSyncId() {
		return syncId;
	}
	public void setSyncId(long syncId) {
		this.syncId = syncId;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public SyncType getSyncType() {
		return syncType;
	}
	public void setSyncType(SyncType syncType) {
		this.syncType = syncType;
	}
	public Date getTimeCreated() {
		return timeCreated;
	}
	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
	
	
}
