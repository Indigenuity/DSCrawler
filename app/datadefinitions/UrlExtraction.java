package datadefinitions;

import java.util.HashSet;
import java.util.Set;

public enum UrlExtraction implements StringMatch{
	
	FACEBOOK		("Facebook URL", "facebook.com", ""),
  	GOOGLE_PLUS		("Google+ URL", "plus.google.com", ""),
  	TWITTER			("Twitter URL", "twitter.com", ""),
  	YOUTUBE			("YouTube URL", "youtube.com", ""),
  	FLICKER			("Flickr URL", "flickr.com", ""),
  	INSTAGRAM		("Instagram URL", "instagram.com", ""),
  	YELP			("Yelp URL", "yelp.com", ""),
  	LINKED_IN		("LinkedIn URL", "linkedin.com", ""),
  	PINTEREST		("Pinterest URL", "pinterest.com", ""),
  	FOURSQUARE		("Foursquare URL", "foursquare.com", ""),
  	TUMBLR			("Tumblr URL", "tumblr.com", "");
	
	
	
	
	private String description;
	private String definition;
	private String notes;
	public final Set<StringMatch> offsetMatches = new HashSet<StringMatch>();
	
	private UrlExtraction(String description, String definition, String notes){
		this.description = description;
		this.definition = definition;
		this.notes = notes;
	}
	
	private UrlExtraction(String description, String definition, String notes, Set<StringMatch> offsetMatches){
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
}
