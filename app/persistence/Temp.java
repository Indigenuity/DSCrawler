package persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
	
	private String name;
	
	

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
		this.givenUrl = givenUrl;
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
		this.standardizedUrl = standardizedUrl;
	}

	public String getIntermediateUrl() {
		return intermediateUrl;
	}

	public void setIntermediateUrl(String intermediateUrl) {
		this.intermediateUrl = intermediateUrl;
	}
	
	
	
}
