package datadefinitions.newdefinitions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import datadefinitions.StringMatch;

public enum TestMatch implements StringMatch{
	
	BMW_GROUP					("Probably BMW's OEM pages", "VIP-PAGE GENERATOR"),
	CONVERTUS					("potential web provider in Canada", "convertus"),
	STRATHCOM					("potential web provider in canada", "strathcom"),
	VIN_SOLUTIONS				( "Vin Solutions", "VinSolutions"),
	AUTO_TRADER					("Canada web provider", "autotrader.ca"),
	AUTO_HEBDO					("Maybe related to autotrader", "autoHebdo"),
	TRADER_WEB					("Not sure what this is", "traderweb.ca"),
	EVOLIO						("Evolio General match", "evolio"),
	E_DEALER					("Edealer general match", "edealer"),
	TRADER						("potential web provider in canada", "trader");
	
	
	public final String description;
	public final String definition;
	public final String notes;
	public final Set<StringMatch> offsetMatches = new HashSet<StringMatch>();
	
	 
	private TestMatch(String description, String definition){
		this.description = description;
		this.definition = definition;
		this.notes = "";
	}
	
	private TestMatch(String description, String definition, String notes){
		this.description = description;
		this.definition = definition;
		this.notes = notes;
	}
	
	private TestMatch(String description, String definition, String notes, Set<StringMatch> offsetMatches){
		this.description = description;
		this.definition = definition;
		this.notes = notes;
		this.offsetMatches.addAll(offsetMatches);
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
	public Set<StringMatch> getOffsetMatches(){
		return this.offsetMatches;
	}
	
	private static final Set<TestMatch> currentMatches = new HashSet<TestMatch>();
	private static final Set<TestMatch> previousMatches = new HashSet<TestMatch>();
	static {
		//*************  potential Canada web providers
		currentMatches.add(CONVERTUS);
		currentMatches.add(STRATHCOM);
		currentMatches.add(TRADER);
	}
	 
	 
	public static Set<TestMatch> getCurrentMatches() {
//		return Collections.unmodifiableSet(currentMatches);
		return new HashSet<TestMatch>(Arrays.asList(TestMatch.values()));
	}

	public static Set<TestMatch> getPreviousMatches() {
		return Collections.unmodifiableSet(previousMatches);
	}
	 
	 
}
