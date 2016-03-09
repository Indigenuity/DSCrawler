package dao;

import javax.persistence.Query;

import async.work.WorkStatus;
import async.work.WorkType;
import play.db.jpa.JPA;

public class TaskSetDAO {

	public static void setSubtaskWorkStatus(Long taskSetId, WorkType workType, WorkStatus currentStatus, WorkStatus targetStatus){
		String query = "update taskset ts " +
						"join taskset_task tst on ts.tasksetid = tst.tasksetid " +
						"join task t on t.taskid = tst.taskid " +
						"join task_subtask sub on t.taskid = sub.supertaskId " +
						"join task t2 on sub.subtaskid = t2.taskid " +
						"set t.workStatus = :targetStatus, " +
						"t2.workStatus = :targetStatus " +
						"where ts.tasksetid = :taskSetId " +
						"and t2.worktype = :workType " +
						"and t2.workStatus = :currentStatus";
		
		Query q = JPA.em().createNativeQuery(query);
		
		q.setParameter("taskSetId", taskSetId);
		q.setParameter("targetStatus", targetStatus.name());
		q.setParameter("currentStatus", currentStatus.name());
		q.setParameter("workType", workType.name());
		
		q.executeUpdate();
	}
}
