package agarbagefolder;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import async.work.WorkStatus;

@Embeddable
public class Subtask {

	@Enumerated(EnumType.STRING)
	public WorkStatus workStatus = WorkStatus.NO_WORK;
	public String note;
	
	@Transient
	public boolean active = true;
	
}
