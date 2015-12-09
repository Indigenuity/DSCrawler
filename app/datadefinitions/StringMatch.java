package datadefinitions;

import java.util.Set;


public interface StringMatch {
	//If only Java supported static abstract methods
	public StringMatch getType(Integer id);
	public int getId();
	public String getDescription();
	public String getDefinition();
	public String getNotes();
	public Set<StringMatch> getOffsetMatches();
	
}
