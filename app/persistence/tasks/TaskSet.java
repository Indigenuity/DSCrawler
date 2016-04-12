package persistence.tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.OneToMany;

import async.work.WorkType;
import dao.TaskDAO;

@Entity
public class TaskSet {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long taskSetId;
	
	private String name;
	
	private Date dateCreated;

	@OneToMany(fetch=FetchType.LAZY)
	@JoinTable(name="taskSet_task", 
			joinColumns={@JoinColumn(name="taskSetId")},
		    inverseJoinColumns={@JoinColumn(name="taskId")})
	private Set<Task> tasks = new HashSet<Task>();
	
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<WorkType> workTypes = new HashSet<WorkType>();

	public TaskSet() {
		this.name = null;
		this.dateCreated = Calendar.getInstance().getTime();
	}
	
	public Long getTaskSetId() {
		return taskSetId;
	}

	public void setTaskSetId(Long taskSetId) {
		this.taskSetId = taskSetId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Set<Task> getTasks() {
		Set<Task> returned = new HashSet<Task>();
		returned.addAll(this.tasks);
		return returned;
	}

	public void setTasks(Set<Task> tasks) {
		this.tasks.clear();
		for(Task task : tasks){
			addTask(task);
		}
	}
	
	public boolean addTask(Task task){
		if(this.tasks.add(task)){
			this.workTypes.add(task.getWorkType());
			return true;
		}
		return false;
	}
	
	public Set<WorkType> getWorkTypes() {
		Set<WorkType> returned = new HashSet<WorkType>();
		returned.addAll(workTypes);
		return returned;
	}
	
	public void refreshWorkTypes() {
		this.workTypes.clear();
		this.workTypes.addAll(TaskDAO.getWorkTypes(this.taskSetId));
	}

}
