package persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import utilities.DSFormatter;

@Entity
public class FBPage {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long fbPageId;
	
	@Column(unique=true)
	private String givenUrl;
	private String fbId;
	@Column(nullable = true, columnDefinition="varchar(2000)")
	private String about;
	private String category;
	@Column(nullable = true, columnDefinition="varchar(2000)")
	private String overview;
	private String contactAddress;
	private String currentLocation;
	@Column(nullable = true, columnDefinition="varchar(2000)")
	private String description;
	private String founded;
	@Column(nullable = true, columnDefinition="varchar(2000)")
	private String generalInfo;
	private String generalManager;
	private String link; 
	@Column(nullable = true, columnDefinition="varchar(2000)")
	private String mission;
	private String name;
	private String nameWithBrand;
	private String parentPageId;
	private String personalInfo;
	private String personalInterests;
	private String phone;
	private String products;
	private String storeNumber;
	private String singleLineAddress;
	private String username;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String website;
	private boolean permanentlyClosed;
	private boolean unclaimed;
	private boolean verified;
	private long likes;
	
	@ElementCollection
	@Column(columnDefinition="varchar(255)")
	private List<String> subCategories = new ArrayList<String>();
	@ElementCollection(fetch=FetchType.EAGER)
	@Column(columnDefinition="varchar(255)")
	private List<String> emails = new ArrayList<String>();
	
	
	private String errorCode;
	private String errorMessage;
	
	private long getFbPageId() {
		return fbPageId;
	}
	private void setFbPageId(long fbPageId) {
		this.fbPageId = fbPageId;
	}
	public List<String> getSubCategories() {
		return subCategories;
	}
	public void setSubCategories(List<String> subCategories) {
		this.subCategories.clear();
		this.subCategories.addAll(subCategories);
	}
	public List<String> getEmails() {
		return emails;
	}
	public void setEmails(List<String> emails) {
		this.emails.clear();
		this.emails.addAll(emails);
	}
	public String getGivenUrl() {
		return givenUrl;
	}
	public void setGivenUrl(String givenUrl) {
		this.givenUrl = givenUrl;
	}
	public String getFbId() {
		return fbId;
	}
	public void setFbId(String fbId) {
		this.fbId = fbId;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = DSFormatter.truncate(about, 2000);
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview =DSFormatter.truncate(overview, 2000); 
	}
	public String getContactAddress() {
		return contactAddress;
	}
	public void setContactAddress(String contactAddress) {
		this.contactAddress = contactAddress;
	}
	public String getCurrentLocation() {
		return currentLocation;
	}
	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description =DSFormatter.truncate(description, 2000); 
	}
	public String getFounded() {
		return founded;
	}
	public void setFounded(String founded) {
		this.founded = founded;
	}
	public String getGeneralInfo() {
		return generalInfo;
	}
	public void setGeneralInfo(String generalInfo) {
		this.generalInfo = DSFormatter.truncate(generalInfo, 2000);
	}
	public String getGeneralManager() {
		return generalManager;
	}
	public void setGeneralManager(String generalManager) {
		this.generalManager = generalManager;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getMission() {
		return mission;
	}
	public void setMission(String mission) {
		this.mission = DSFormatter.truncate(mission, 2000);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNameWithBrand() {
		return nameWithBrand;
	}
	public void setNameWithBrand(String nameWithBrand) {
		this.nameWithBrand = nameWithBrand;
	}
	public String getParentPageId() {
		return parentPageId;
	}
	public void setParentPageId(String parentPageId) {
		this.parentPageId = parentPageId;
	}
	public String getPersonalInfo() {
		return personalInfo;
	}
	public void setPersonalInfo(String personalInfo) {
		this.personalInfo = DSFormatter.truncate(personalInfo, 255);
	}
	public String getPersonalInterests() {
		return personalInterests;
	}
	public void setPersonalInterests(String personalInterests) {
		this.personalInterests = DSFormatter.truncate(personalInterests, 255);
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getProducts() {
		return products;
	}
	public void setProducts(String products) {
		this.products = products;
	}
	public String getStoreNumber() {
		return storeNumber;
	}
	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}
	public String getSingleLineAddress() {
		return singleLineAddress;
	}
	public void setSingleLineAddress(String singleLineAddress) {
		this.singleLineAddress = singleLineAddress;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = DSFormatter.truncate(website, 4000);
	}
	public boolean isPermanentlyClosed() {
		return permanentlyClosed;
	}
	public void setPermanentlyClosed(boolean permanentlyClosed) {
		this.permanentlyClosed = permanentlyClosed;
	}
	public boolean isUnclaimed() {
		return unclaimed;
	}
	public void setUnclaimed(boolean unclaimed) {
		this.unclaimed = unclaimed;
	}
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public long getLikes() {
		return likes;
	}
	public void setLikes(long likes) {
		this.likes = likes;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
