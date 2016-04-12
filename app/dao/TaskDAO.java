package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import async.work.WorkStatus;
import async.work.WorkType;
import persistence.Site;
import persistence.tasks.Task;
import persistence.tasks.TaskSet;
import play.db.jpa.JPA;

public class TaskDAO {
	
	public static List<WorkType> getWorkTypes(Long taskSetId) {
		String query = "select distinct t.workType from TaskSet ts join ts.tasks t where ts.taskSetId = :taskSetId";
		TypedQuery<WorkType> q = JPA.em().createQuery(query, WorkType.class);
		q.setParameter("taskSetId", taskSetId);
		List<WorkType> workTypes = q.getResultList();
		return workTypes;
	}
	
	public static List<WorkType> getTaskSetSubtaskWorkTypes(Long taskSetId) {
		String query = "select distinct st.workType from TaskSet ts join ts.tasks t join t.subtasks st where ts.taskSetId = :taskSetId";
		TypedQuery<WorkType> q = JPA.em().createQuery(query, WorkType.class);
		q.setParameter("taskSetId", taskSetId);
		List<WorkType> workTypes = q.getResultList();
		return workTypes;
	}
	
	public static List<WorkType> getSubtaskWorkTypes(Long taskId) {
		String query = "select distinct st.workType from Task t join t.subtasks st where t.taskId = :taskId";
		TypedQuery<WorkType> q = JPA.em().createQuery(query, WorkType.class);
		q.setParameter("taskId", taskId);
		List<WorkType> workTypes = q.getResultList();
		return workTypes;
	}

	public static Integer countWorkStatus(Long taskSetId, WorkStatus workStatus) {
		String query = "select count(t) from TaskSet ts join ts.tasks t where ts.taskSetId = :taskSetId and t.workStatus = :workStatus";
		Query q = JPA.em().createQuery(query);
		q.setParameter("taskSetId", taskSetId);
		q.setParameter("workStatus", workStatus);
		Integer value = Integer.parseInt(q.getSingleResult() + "");
		
		return value;
	}
	
	public static Integer countWorkStatusSubtasks(Long taskSetId, WorkStatus workStatus) {
		String query = "select count(s) from TaskSet ts join ts.tasks t join t.subtasks s where ts.taskSetId = :taskSetId and s.workStatus = :workStatus";
		Query q = JPA.em().createQuery(query);
		q.setParameter("taskSetId", taskSetId);
		q.setParameter("workStatus", workStatus);
		Integer value = Integer.parseInt(q.getSingleResult() + "");
		
		return value;
	}
	
	public static Integer countWorkStatusByWorkType(Long taskSetId, WorkStatus workStatus, WorkType workType) {
		String query = "select count(t) from TaskSet ts join ts.tasks t where ts.taskSetId = :taskSetId and t.workStatus = :workStatus and t.workType = :workType";
		Query q = JPA.em().createQuery(query);
		q.setParameter("taskSetId", taskSetId);
		q.setParameter("workStatus", workStatus);
		q.setParameter("workType", workType);
		Integer value = Integer.parseInt(q.getSingleResult() + "");
		
		return value;
	}
	
	public static Integer countWorkStatusSubtasksByWorkType(Long taskSetId, WorkStatus workStatus, WorkType workType) {
		String query = "select count(s) from TaskSet ts join ts.tasks t join t.subtasks s where ts.taskSetId = :taskSetId and s.workStatus = :workStatus and s.workType = :workType";
		Query q = JPA.em().createQuery(query);
		q.setParameter("taskSetId", taskSetId);
		q.setParameter("workStatus", workStatus);
		q.setParameter("workType", workType);
		Integer value = Integer.parseInt(q.getSingleResult() + "");
		
		return value;
	}
	
	public static Integer countWorkType(Long taskSetId, WorkType workType) {
		String query = "select count(t) from TaskSet ts join ts.tasks t where ts.taskSetId = :taskSetId and t.workType = :workType";
		Query q = JPA.em().createQuery(query);
		q.setParameter("taskSetId", taskSetId);
		q.setParameter("workType", workType);
		Integer value = Integer.parseInt(q.getSingleResult() + "");
		
		return value;
	}
	
	public static Integer countWorkTypeSubtasks(Long taskSetId, WorkType workType) {
		String query = "select count(s) from TaskSet ts join ts.tasks t join t.subtasks s where ts.taskSetId = :taskSetId and s.workType = :workType";
		Query q = JPA.em().createQuery(query);
		q.setParameter("taskSetId", taskSetId);
		q.setParameter("workType", workType);
		Integer value = Integer.parseInt(q.getSingleResult() + "");
		
		return value;
	}
	
	public static long getCount(Long taskSetId, String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getCount(taskSetId, parameters);
	}
	public static long getCount(Long taskSetId, Map<String, Object> parameters) {
		String query = "select count(t) from TaskSet ts join ts.tasks t where ts.taskSetId = :taskSetId";
		
		for(String key : parameters.keySet()) {
			query += " and t." + key + " = :" + key;
		}
		
		parameters.put("taskSetId", taskSetId);
		TypedQuery<Long> q = JPA.em().createQuery(query, Long.class);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		return q.getSingleResult();
	}
	public static List<Task> getList(Long taskSetId, String valueName, Object value, int count, int offset){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getList(taskSetId, parameters, count, offset);
	}
	public static List<Task> getList(Long taskSetId, Map<String, Object> parameters, int count, int offset){
		String query = "select t from TaskSet ts join ts.tasks t where ts.taskSetId = :taskSetId";
		for(String key : parameters.keySet()) {
			query += " and t." + key + " = :" + key;
		}

		parameters.put("taskSetId", taskSetId);
		TypedQuery<Task> q = JPA.em().createQuery(query, Task.class);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		q.setFirstResult(offset);
		q.setMaxResults(count);
		return q.getResultList();
	}
	
	
	public static Task getFirst(Long taskSetId, String valueName, Object value, int offset){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getFirst(taskSetId, parameters, offset);
	}
	public static Task getFirst(Long taskSetId, Map<String, Object> parameters, int offset){
		String query = "select t from TaskSet ts join ts.tasks t where ts.taskSetId = :taskSetId";
		for(String key : parameters.keySet()) {
			query += " and t." + key + " = :" + key;
		}
		parameters.put("taskSetId", taskSetId);
		TypedQuery<Task> q = JPA.em().createQuery(query, Task.class);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		q.setFirstResult(offset);
		q.setMaxResults(1);
		List<Task> results = q.getResultList();
		if(results.size() > 0){
			return results.get(0);
		}
		return null;
	}
	
	
}
