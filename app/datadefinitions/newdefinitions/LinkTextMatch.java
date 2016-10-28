package datadefinitions.newdefinitions;

import java.util.HashSet;
import java.util.Set;

import datadefinitions.StringMatch;

public enum LinkTextMatch {
		
		
	//***General Credit Application Matches 		
	
	CREDIT_APPLICATION						("Credit application indicator", "credit application"),
	CREDIT_APPLICATION2						("Credit application indicator", "credit-application"),
	CREDIT_APPLICATION3						("Credit application indicator", "credit_application"),
	CREDIT_APPLICATION4						("Credit application indicator", "credit app"),
	CREDIT_APPLICATION5						("Credit application indicator", "credit-app"),
	CREDIT_APPLICATION6						("Credit application indicator", "credit_app"),
	CREDIT_APPLICATION7						("Credit application indicator", "finance application"),
	CREDIT_APPLICATION8						("Credit application indicator", "finance-application"),
	CREDIT_APPLICATION9						("Credit application indicator", "finance_application"),
	CREDIT_APPLICATION10					("Credit application indicator", "finance app"),
	CREDIT_APPLICATION11					("Credit application indicator", "finance-app"),
	CREDIT_APPLICATION12					("Credit application indicator", "finance_app"),
	CREDIT_APPLICATION13					("Credit application indicator", "apply for credit"),
	CREDIT_APPLICATION14					("Credit application indicator", "get approved"),
	CREDIT_APPLICATION15					("Credit application indicator", "apply now"),
	CREDIT_APPLICATION16					("Credit application indicator", "preapproved"),
	CREDIT_APPLICATION17					("Credit application indicator", "pre-approved");
	
	public final String description;
	public final String definition;
	public final String notes;
	public final Set<StringMatch> offsetMatches = new HashSet<StringMatch>();
	
	private LinkTextMatch(String description, String definition, String notes){
		this.description = description;
		this.definition = definition;
		this.notes = notes;
	}
	
	private LinkTextMatch(String description, String definition){
		this.description = description;
		this.definition = definition;
		this.notes = "";
	}
	
	
	public String getDescription(){
		return this.description;
	}
	
	public String getDefinition() {
		return this.definition;
	}
	
	public String getNotes() { 
		return this.notes;
	}
}
