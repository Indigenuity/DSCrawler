package persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import utilities.DSFormatter;

@Entity
public class Temp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long tempId;
	
	private String sfId;
	
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

	
	
	
}
