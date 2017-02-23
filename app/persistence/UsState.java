package persistence;

import javax.persistence.Entity;
import javax.persistence.Id;

//To reload all states, run method in GeneralDAO
@Entity	
public class UsState {
	@Id
	private String stateCode;
	private String stateName;
	private String projectCode;
	
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public String getStateCode() {
		return stateCode;
	}
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	
	
}
