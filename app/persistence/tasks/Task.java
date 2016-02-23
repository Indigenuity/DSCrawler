package persistence.tasks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.lang.StringUtils;

import async.work.WorkStatus;
import async.work.WorkType;

@Entity
public class Task {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long taskId;

	@ElementCollection
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private Map<String, String> contextItems = new HashMap<String, String>();
	
	@Enumerated(EnumType.STRING)
	private WorkStatus workStatus = WorkStatus.DO_WORK;
	@Enumerated(EnumType.STRING)
	private WorkType workType;
	
	private String note;
	
	@ManyToOne(cascade=CascadeType.DETACH)
	@JoinTable(name="taskSet_task", 
			joinColumns={@JoinColumn(name="taskId")},
		    inverseJoinColumns={@JoinColumn(name="taskSetId")})
	private TaskSet taskSet;
	
	@ManyToOne(cascade=CascadeType.DETACH)
	@JoinTable(name="task_subtask", 
			joinColumns={@JoinColumn(name="subtaskId")},
		    inverseJoinColumns={@JoinColumn(name="superTaskId")})
	private Task supertask;
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinTable(name="task_subtask", 
			joinColumns={@JoinColumn(name="supertaskId")},
		    inverseJoinColumns={@JoinColumn(name="subtaskId")})
	private Set<Task> subtasks = new HashSet<Task>();
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinTable(name="task_prereqTask", 
			joinColumns={@JoinColumn(name="taskId")},
		    inverseJoinColumns={@JoinColumn(name="prereqTaskId")})
	private Set<Task> prerequisites = new HashSet<Task>();
	
	private Boolean serialTask = true;
	
	public boolean prereqsSatisfied() {
		for(Task prereq : prerequisites){
			if(prereq.getWorkStatus() != WorkStatus.WORK_COMPLETED){
				return false;
			}
		}
		return true;
	}
	
	public synchronized Map<String, String> getContextItems() {
		Map<String, String> returned = new HashMap<String, String>();
		returned.putAll(contextItems);
		return returned;
	}
	public synchronized String addContextItem(String name, String item) {
		if(StringUtils.length(item) > 4000){
			throw new IllegalArgumentException("Can't have context item longer than 4000 characters");
		}
		return contextItems.put(name, item);
	}
	public synchronized String removeContextItems(String name) {
		return contextItems.remove(name);
	}
	public synchronized String getContextItem(String name) {
		return contextItems.get(name);
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public WorkStatus getWorkStatus() {
		return workStatus;
	}
	public void setWorkStatus(WorkStatus workStatus) {
		this.workStatus = workStatus;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Task getSupertask() {
		return supertask;
	}
	public void setSupertask(Task supertask) {
		this.supertask = supertask;
	}
	public Set<Task> getSubtasks() {
		return subtasks;
	}
	public boolean addSubtask(Task subtask) {
		return this.subtasks.add(subtask);
	}
	public Set<Task> getPrerequisites() {
		return prerequisites;
	}
	public boolean addPrerequisite(Task prereqTask) {
		return this.prerequisites.add(prereqTask);
	}
	public WorkType getWorkType() {
		return workType;
	}
	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}

	public Boolean isSerialTask() {
		return serialTask;
	}

	public void setSerialTask(Boolean serialTask) {
		this.serialTask = serialTask;
	}
	
	
}
