package persistence;

import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class SFEntry {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long sfEntryId;
	
	
	private String accountId;
	private String accountAuto;
	private String accountType;
	private String name;
	private String phone;
	private String street;
	private String city;
	private String state;
	private String postal;
	private String country;
	private String brandAffiliations;
	private String accountLevel;
	private String parentAccount;
	private String parentAccountId;
	
	private Date importDate;
	private boolean permanent;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String website;
	
	@ManyToOne
	private Site mainSite;

	public long getSfEntryId() {
		return sfEntryId;
	}

	public void setSfEntryId(long sfEntryId) {
		this.sfEntryId = sfEntryId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountAuto() {
		return accountAuto;
	}

	public void setAccountAuto(String accountAuto) {
		this.accountAuto = accountAuto;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Site getMainSite() {
		return mainSite;
	}

	public void setMainSite(Site mainSite) {
		this.mainSite = mainSite;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccountLevel() {
		return accountLevel;
	}

	public void setAccountLevel(String accountLevel) {
		this.accountLevel = accountLevel;
	}

	public String getParentAccount() {
		return parentAccount;
	}

	public void setParentAccount(String parentAccount) {
		this.parentAccount = parentAccount;
	}

	public String getParentAccountId() {
		return parentAccountId;
	}

	public void setParentAccountId(String parentAccountId) {
		this.parentAccountId = parentAccountId;
	}

	public Date getImportDate() {
		return importDate;
	}

	public void setImportDate(Date importDate) {
		this.importDate = importDate;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getBrandAffiliations() {
		return brandAffiliations;
	}

	public void setBrandAffiliations(String brandAffiliations) {
		this.brandAffiliations = brandAffiliations;
	}

	public boolean isPermanent() {
		return permanent;
	}

	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getPostal() {
		return postal;
	}

	public void setPostal(String postal) {
		this.postal = postal;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}	
	
	

}
