package salesforce.persistence;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import persistence.Site;
import urlcleanup.SiteOwner;
import utilities.DSFormatter;

@Entity
@Table(indexes = {@Index(name = "name_index",  columnList="name", unique = false),
		@Index(name = "std_street_index",  columnList="stdStreet", unique = false),
		@Index(name = "std_phone_index",  columnList="stdPhone", unique = false),
		@Index(name = "std_country_index",  columnList="stdCountry", unique = false),
		@Index(name = "std_state_index",  columnList="stdState", unique = false),
		@Index(name = "std_postal_index",  columnList="stdPostal", unique = false),
        @Index(name = "salesforceId_index", columnList="salesforceId",     unique = false)})
@Audited(withModifiedFlag=true)
public class SalesforceAccount implements SiteOwner{

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private long salesforceAccountId;
	
	/************************** Basics ********************************/
	
	private Boolean outdated = false;
	private Date lastUpdated = new Date();
	
	private String name;
	private String salesforceId;
	private String parentAccountName;
	private String parentAccountSalesforceId;
	@Column(columnDefinition = "varchar(4000)")
	private String salesforceWebsite;
	private Boolean franchise;

	@Enumerated(EnumType.STRING)
	private DealershipType dealershipType;
	@Enumerated(EnumType.STRING)
	private SalesforceAccountType accountType;
	
	private String phone;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String country;
	@Column(columnDefinition = "varchar(500)")
	private String brandAffiliation;
	private String customerStatus;
	
	//Denotes if there is a significant difference between the salesforceWebsite and the homepage of the assigned site.
	private Boolean significantDifference = false;
	
	private Boolean siteMismatch = false;
	
	
	private String stdStreet;
	private String stdCity;
	private String stdState;
	private String stdCountry;
	private String stdPhone;
	private String stdPostal;
	
	
	/************************ Relationships ***************************/
	
	@ManyToOne
	private Site site;
	
	@ManyToOne
	private Site unresolvedSite;

	public long getSalesforceAccountId() {
		return salesforceAccountId;
	}

	public SalesforceAccount setSalesforceAccountId(long salesforceAccountId) {
		this.salesforceAccountId = salesforceAccountId;
		return this;
	}

	public String getName() {
		return name;
	}

	public SalesforceAccount setName(String name) {
		this.name = name;
		return this;
	}

	public String getSalesforceId() {
		return salesforceId;
	}

	public SalesforceAccount setSalesforceId(String salesforceId) {
		this.salesforceId = salesforceId;
		return this;
	}

	public String getParentAccountName() {
		return parentAccountName;
	}

	public SalesforceAccount setParentAccountName(String parentAccountName) {
		this.parentAccountName = parentAccountName;
		return this;
	}

	public String getParentAccountSalesforceId() {
		return parentAccountSalesforceId;
	}

	public SalesforceAccount setParentAccountSalesforceId(String parentAccountSalesforceId) {
		this.parentAccountSalesforceId = parentAccountSalesforceId;
		return this;
	}

	public String getSalesforceWebsite() {
		return salesforceWebsite;
	}

	public SalesforceAccount setSalesforceWebsite(String salesforceWebsite) {
		this.salesforceWebsite = salesforceWebsite;
		return this;
	}

	public Boolean getFranchise() {
		return franchise;
	}

	public SalesforceAccount setFranchise(Boolean franchise) {
		this.franchise = franchise;
		return this;
	}

	public SalesforceAccountType getAccountType() {
		return accountType;
	}

	public SalesforceAccount setAccountType(SalesforceAccountType accountType) {
		this.accountType = accountType;

		return this;
	}

	public Site getSite() {
		return site;
	}

	public SalesforceAccount setSite(Site site) {
		this.site = site;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getBrandAffiliation() {
		return brandAffiliation;
	}

	public void setBrandAffiliation(String brandAffiliation) {
//		System.out.println("setting brand affiliation : " + brandAffiliation);
		this.brandAffiliation = DSFormatter.truncate(brandAffiliation, 255);
	}

	public String getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(String customerStatus) {
		this.customerStatus = customerStatus;
	}
	
	public String getFullLocation(){
		return getStreet() + " " + getCity() + ", " + getState() + " " + getZip() + ", " + getCountry();
	}

	public Boolean getSignificantDifference() {
		return significantDifference;
	}

	public void setSignificantDifference(Boolean significantDifference) {
		this.significantDifference = significantDifference;
	}

	public DealershipType getDealershipType() {
		return dealershipType;
	}

	public void setDealershipType(DealershipType dealershipType) {
		this.dealershipType = dealershipType;
	}

	public String getStdPostal() {
		return stdPostal;
	}

	public void setStdPostal(String stdPostal) {
		this.stdPostal = stdPostal;
	}

	public String getStdStreet() {
		return stdStreet;
	}

	public void setStdStreet(String stdStreet) {
		this.stdStreet = stdStreet;
	}

	public String getStdCity() {
		return stdCity;
	}

	public void setStdCity(String stdCity) {
		this.stdCity = stdCity;
	}

	public String getStdState() {
		return stdState;
	}

	public void setStdState(String stdState) {
		this.stdState = stdState;
	}

	public String getStdCountry() {
		return stdCountry;
	}

	public void setStdCountry(String stdCountry) {
		this.stdCountry = stdCountry;
	}

	public String getStdPhone() {
		return stdPhone;
	}

	public void setStdPhone(String stdPhone) {
		this.stdPhone = stdPhone;
	}

	public Boolean getOutdated() {
		return outdated;
	}

	public void setOutdated(Boolean outdated) {
		this.outdated = outdated;
	}

	public Boolean getSiteMismatch() {
		return siteMismatch;
	}

	public void setSiteMismatch(Boolean siteMismatch) {
		this.siteMismatch = siteMismatch;
	}

	@Override
	public String getWebsiteString() {
		return salesforceWebsite;
	}
	@Override
	public Site getUnresolvedSite() {
		return unresolvedSite; 
	}

	@Override
	public Site setUnresolvedSite(Site site) {
		this.unresolvedSite = site;
		return unresolvedSite;
	}

	@Override
	public Site getResolvedSite() {
		return this.getSite();
	}

	@Override
	public Site setResolvedSite(Site site) {
		this.setSite(site);
		return this.getSite();
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
}
