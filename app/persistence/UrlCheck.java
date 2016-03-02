package persistence;

import java.sql.Date;

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
	private String seedHost;

	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String resolvedSeed;
	private String resolvedHost;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String errorMessage;
	
	private Date checkDate;
	
	private int statusCode;

	private boolean unchanged;
	private boolean genericChange;
	private boolean error;
	private boolean pathApproved;
	private boolean queryApproved;
	private boolean statusApproved;
	private boolean domainApproved;
	private boolean domainChanged;
	private boolean valid = true;
	private boolean languagePath;
	private boolean languageQuery;
	
	private boolean noChange;
	private boolean accepted;
	
	public UrlCheck(){}
	public UrlCheck(String seed) {
		this.seed = seed;
	}
	public boolean isAllApproved(){
		return !this.isError() 
				&& this.isValid()
				&& this.isStatusApproved()
				&& (this.isNoChange() || this.isGenericChange())
				&& this.isPathApproved()
				&& this.isDomainApproved()
				&& this.isQueryApproved();
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
	public String getSeedHost() {
		return seedHost;
	}
	public void setSeedHost(String seedHost) {
		this.seedHost = seedHost;
	}
	public String getResolvedHost() {
		return resolvedHost;
	}
	public void setResolvedHost(String resolvedHost) {
		this.resolvedHost = resolvedHost;
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
	public boolean isGenericChange() {
		return genericChange;
	}
	public void setGenericChange(boolean genericChange) {
		this.genericChange = genericChange;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public boolean isPathApproved() {
		return pathApproved;
	}
	public void setPathApproved(boolean pathApproved) {
		this.pathApproved = pathApproved;
	}
	public boolean isQueryApproved() {
		return queryApproved;
	}
	public void setQueryApproved(boolean queryApproved) {
		this.queryApproved = queryApproved;
	}
	public boolean isStatusApproved() {
		return statusApproved;
	}
	public void setStatusApproved(boolean statusApproved) {
		this.statusApproved = statusApproved;
	}
	public boolean isDomainApproved() {
		return domainApproved;
	}
	public void setDomainApproved(boolean domainApproved) {
		this.domainApproved = domainApproved;
	}
	public boolean isDomainChanged() {
		return domainChanged;
	}
	public void setDomainChanged(boolean domainChanged) {
		this.domainChanged = domainChanged;
	}
	public boolean isAccepted() {
		return accepted;
	}
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
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
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public boolean isLanguagePath() {
		return languagePath;
	}
	public void setLanguagePath(boolean languagePath) {
		this.languagePath = languagePath;
	}
	public boolean isLanguageQuery() {
		return languageQuery;
	}
	public void setLanguageQuery(boolean languageQuery) {
		this.languageQuery = languageQuery;
	}
	

}
