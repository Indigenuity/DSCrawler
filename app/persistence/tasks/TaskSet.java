package persistence.tasks;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

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
	
	
}
