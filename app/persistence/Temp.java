package persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import utilities.DSFormatter;

@Entity
public class Temp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long tempId;
	
	private String sfId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Site site;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String givenUrl;

	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String intermediateUrl;

	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String standardizedUrl;
	
	private String domain;
	
	private String name;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String suggestedUrl;
	
	private String suggestedSource;
	
	private String problem;
	
	private String nextStep;
	
	private String siteSource;
	
	private int projectId;
	
	private String state;
	
	private Long infoFetchId;
	
	private String accountLevel;
	private String parentAccount;
	private String phone;
	private String street;
	

	public long getTempId() {
		return tempId;
	}

	public void setTempId(long tempId) {
		this.tempId = tempId;
	}

	public String getSfId() {
		return sfId;
	}

	public void setSfId(String sfId) {
		this.sfId = sfId;
	}

	public String getGivenUrl() {
		return givenUrl;
	}

	public void setGivenUrl(String givenUrl) {
		this.givenUrl = DSFormatter.truncate(givenUrl, 4000);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStandardizedUrl() {
		return standardizedUrl;
	}

	public void setStandardizedUrl(String standardizedUrl) {
		this.standardizedUrl = DSFormatter.truncate(standardizedUrl, 4000);
	}

	public String getIntermediateUrl() {
		return intermediateUrl;
	}

	public void setIntermediateUrl(String intermediateUrl) {
		this.intermediateUrl = DSFormatter.truncate(intermediateUrl, 4000);
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getSuggestedUrl() {
		return suggestedUrl;
	}

	public void setSuggestedUrl(String suggestedUrl) {
		this.suggestedUrl = DSFormatter.truncate(suggestedUrl, 4000);
	}

	public String getSuggestedSource() {
		return suggestedSource;
	}

	public void setSuggestedSource(String suggestedSource) {
		this.suggestedSource = suggestedSource;
	}

	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public String getNextStep() {
		return nextStep;
	}

	public void setNextStep(String nextStep) {
		this.nextStep = nextStep;
	}

	public String getSiteSource() {
		return siteSource;
	}

	public void setSiteSource(String siteSource) {
		this.siteSource = siteSource;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Long getInfoFetchId() {
		return infoFetchId;
	}

	public void setInfoFetchId(Long infoFetchId) {
		this.infoFetchId = infoFetchId;
	}

	
	
	
}
