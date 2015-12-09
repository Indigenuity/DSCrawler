package datadefinitions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum UrlExtraction implements StringMatch{
	
	FACEBOOK		(1, "Facebook URL", "facebook.com", ""),
  	GOOGLE_PLUS		(2, "Google+ URL", "plus.google.com", ""),
  	TWITTER			(3, "Twitter URL", "twitter.com", ""),
  	YOUTUBE			(4, "YouTube URL", "youtube.com", ""),
  	FLICKER			(5, "Flickr URL", "flickr.com", ""),
  	INSTAGRAM		(6, "Instagram URL", "instagram.com", ""),
  	YELP			(7, "Yelp URL", "yelp.com", ""),
  	LINKED_IN		(8, "LinkedIn URL", "linkedin.com", ""),
  	PINTEREST		(9, "Pinterest URL", "pinterest.com", ""),
  	FOURSQUARE		(10, "Foursquare URL", "foursquare.com", ""),
  	TUMBLR			(11, "Tumblr URL", "tumblr.com", "");
	
	
	
	
	private int id;
	private String description;
	private String definition;
	private String notes;
	public final Set<StringMatch> offsetMatches = new HashSet<StringMatch>();
	
	private final static Map<Integer, UrlExtraction> enumIds = new HashMap<Integer, UrlExtraction>();
	static {
		for(UrlExtraction sm : UrlExtraction.values()){
			enumIds.put(sm.getId(), sm);
		}
	}
	
	public static UrlExtraction getTypeFromId(Integer id) {
		return enumIds.get(id);
	}
	
	private UrlExtraction(int id, String description, String definition, String notes){
		this.id = id;
		this.description = description;
		this.definition = definition;
		this.notes = notes;
	}
	
	private UrlExtraction(int id, String description, String definition, String notes, Set<StringMatch> offsetMatches){
		this.id = id;
		this.description = description;
		this.definition = definition;
		this.notes = notes;
		this.offsetMatches.addAll(offsetMatches);
	}
	
	public UrlExtraction getType(Integer id) {
		return getTypeFromId(id); 
	}
	
	public int getId() {
		return this.id;
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
}
