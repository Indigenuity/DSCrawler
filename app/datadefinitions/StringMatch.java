package datadefinitions;

import java.util.Set;


public interface StringMatch {
	//If only Java supported static abstract methods
	public String getDescription();
	public String getDefinition();
	public String getNotes();
	public Set<StringMatch> getOffsetMatches();
	
}
