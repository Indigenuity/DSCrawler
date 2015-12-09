package persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import persistence.converters.GeneralMatchConverter;
import persistence.converters.SchedulerConverter;
import persistence.converters.WebProviderConverter;
import datadefinitions.GeneralMatch;
import datadefinitions.Scheduler;
import datadefinitions.WebProvider;
import utilities.DSFormatter;


public class PageInformation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long pageInformationId;
	
	@Column(nullable = true, columnDefinition="varchar(1000)")
	protected String path;
	@Column(nullable = true, columnDefinition="varchar(1000)")
	protected String fileLocation;
	@Column(nullable = false, columnDefinition="boolean default false")
	protected boolean largeFile; 
	
	@Convert(converter = WebProviderConverter.class)
	@ElementCollection(fetch=FetchType.EAGER)
	protected List<WebProvider> webProviders = new ArrayList<WebProvider>();
	
	@Convert(converter = SchedulerConverter.class)
	@ElementCollection(fetch=FetchType.EAGER)
	protected List<Scheduler> schedulers = new ArrayList<Scheduler>();
	
	@Convert(converter = GeneralMatchConverter.class)
	@ElementCollection(fetch=FetchType.EAGER)
	protected List<GeneralMatch> generalMatches = new ArrayList<GeneralMatch>();
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
	protected List<ExtractedString> extractedStrings = new ArrayList<ExtractedString>();
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
	protected List<ExtractedUrl> extractedUrls = new ArrayList<ExtractedUrl>();
	
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval=true)
	protected List<Staff> allStaff = new ArrayList<Staff>();
	
	@Transient
	protected ArrayList<String> links;
	
	@PreUpdate
	@PrePersist
	public void validation() {
		System.out.println("Calling page validation");
		this.path = DSFormatter.truncate(this.path, 1000);
		this.fileLocation = DSFormatter.truncate(this.fileLocation, 1000);
		for(ExtractedUrl item : extractedUrls) {
			item.validation();
		}
		for(ExtractedString item : extractedStrings) {
			item.validation();
		}
	}
	
	@PostUpdate
	@PostPersist
	public void post() {
		
//		System.out.println("postupdate : " + ++testing);
//		System.out.println("linked in : " + this.exStrings.LINKED_IN);
			
	}
	
	public PageInformation() {
		initFields("none");
	}
	public PageInformation(String path) {
		initFields(path);
	}
	
	private void initFields(String path) {
		this.setId(0);
		this.setLargeFile(false);
		this.setLinks(new ArrayList<String>());
		this.setPath(path);
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if(path != null && DSFormatter.needsTruncation(path, 1000)){
			path = DSFormatter.truncate(path, 1000);
		}
		this.path = path;
	}
	
	public void addLink(String link){
		this.links.add(link);
	}
	
	public ArrayList<String> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<String> links) {
		this.links = links;
	}
	
	public long getId() {
		return pageInformationId;
	}

	public void setId(long id) {
		this.pageInformationId = id;
	}
	public boolean isLargeFile(){
		return this.largeFile;
	}
	public void setLargeFile(boolean largeFile) {
		this.largeFile = largeFile;
	}

	public String getFileLocation() {
		return fileLocation;
	}
	public void setFileLocation(String fileLocation) {
		if(fileLocation != null && DSFormatter.needsTruncation(fileLocation, 1000)){
			fileLocation = DSFormatter.truncate(fileLocation, 1000);
		}
		this.fileLocation = fileLocation;
	}
	
	public List<Staff> getAllStaff() {
		return allStaff;
	}
	public void setAllStaff(List<Staff> allStaff) {
		this.allStaff.clear();
		this.allStaff.addAll(allStaff);
	}
	
	public List<WebProvider> getWebProviders() {
		return webProviders;
	}

	public void setWebProviders(List<WebProvider> webProviders) {
		this.webProviders = webProviders;
	}
	

	public List<Scheduler> getSchedulers() {
		return schedulers;
	}

	public void setSchedulers(List<Scheduler> schedulers) {
		this.schedulers = schedulers;
	}

	public void setGeneralMatches(List<GeneralMatch> generalMatches) {
		this.generalMatches = generalMatches;
	}
	
	public List<GeneralMatch> getGeneralMatches() {
		return this.generalMatches;
	}
	
	

	public List<ExtractedString> getExtractedStrings() {
		return extractedStrings;
	}

	public void setExtractedStrings(List<ExtractedString> extractedStrings) {
		this.extractedStrings.clear();
		this.extractedStrings.addAll(extractedStrings);
	}

	public List<ExtractedUrl> getExtractedUrls() {
		return extractedUrls;
	}

	public void setExtractedUrls(List<ExtractedUrl> extractedUrls) {
		this.extractedUrls.clear();
		this.extractedUrls.addAll(extractedUrls);
	}

	public String stringify() {
		StringBuilder sb = new StringBuilder();
		sb.append("Page Information: \n");
		sb.append("\tPath: ");
		sb.append(this.path);
		sb.append("\nIs a large file : ");
		sb.append(this.largeFile);
		sb.append("\nWeb Provider Information:\n");
		sb.append("\n");
		return sb.toString();
	}
}
