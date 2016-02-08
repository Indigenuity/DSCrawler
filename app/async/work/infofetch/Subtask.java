package async.work.infofetch;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import async.work.WorkStatus;

@Embeddable
public class Subtask {

	@Enumerated(EnumType.STRING)
	public WorkStatus workStatus = WorkStatus.NO_WORK;
	public String note;
	
}
