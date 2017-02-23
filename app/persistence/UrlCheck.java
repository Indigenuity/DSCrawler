package persistence;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import utilities.DSFormatter;

@Entity
public class UrlCheck {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long urlCheckId;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String seed;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String resolvedSeed;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String errorMessage;
	
	private Date checkDate = new Date();
	private int statusCode;
	private boolean noChange;

	//Refers to errors related to program execution, not HTTP errors
	private boolean error;
	private boolean statusApproved;
	
	public UrlCheck(){}
	public UrlCheck(String seed) {
		this.seed = seed;
	}
	
	public long getUrlCheckId() {
		return urlCheckId;
	}
	public void setUrlCheckId(long urlCheckId) {
		this.urlCheckId = urlCheckId;
	}
	public String getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		this.seed = DSFormatter.truncate(seed, 4000);
	}
	public Date getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public boolean isStatusApproved() {
		return statusApproved;
	}
	public void setStatusApproved(boolean statusApproved) {
		this.statusApproved = statusApproved;
	}
	public String getResolvedSeed() {
		return resolvedSeed;
	}
	public void setResolvedSeed(String resolvedSeed) {
		this.resolvedSeed = DSFormatter.truncate(resolvedSeed, 4000);
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = DSFormatter.truncate(errorMessage, 4000);
	}
	public boolean isNoChange() {
		return noChange;
	}
	public void setNoChange(boolean noChange) {
		this.noChange = noChange;
	}
}
