package async.registration;

import java.util.ArrayList;
import java.util.List;

import async.tools.Tool;
import async.work.WorkType;

public class RegistryEntry {
	
	public final static Integer DEFAULT_NUM_WORKERS = 5;

	private Class<? extends Tool> clazz;
	private List<ContextItem> requiredContextItems = new ArrayList<ContextItem>();
	private List<ContextItem> resultContextItems = new ArrayList<ContextItem>();
	private WorkType workType;
	private Integer numWorkers = DEFAULT_NUM_WORKERS;
	
	public Class<? extends Tool> getClazz() {
		return clazz;
	}
	public void setClazz(Class<? extends Tool> clazz) {
		this.clazz = clazz;
	}
	public List<ContextItem> getRequiredContextItems() {
		List<ContextItem> copy = new ArrayList<ContextItem>();
		copy.addAll(requiredContextItems);
		return copy;
	}
	public void addRequiredContextItem(ContextItem item) {
		this.requiredContextItems.add(item);
	}
	public WorkType getWorkType() {
		return workType;
	}
	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}
	public Integer getNumWorkers() {
		return numWorkers;
	}
	public void setNumWorkers(Integer numWorkers) {
		this.numWorkers = numWorkers;
	}
	
	public List<ContextItem> getResultContextItems() {
		List<ContextItem> copy = new ArrayList<ContextItem>();
		copy.addAll(resultContextItems);
		return copy;
	}
	public void addResultContextItem(ContextItem item) {
		this.resultContextItems.add(item);
	}
	
}
