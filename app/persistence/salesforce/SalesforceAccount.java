package persistence.salesforce;

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
import utilities.DSFormatter;

@Entity
@Table(indexes = {@Index(name = "name_index",  columnList="name", unique = false),
        @Index(name = "salesforceId_index", columnList="salesforceId",     unique = false)})
@Audited(withModifiedFlag=true)
public class SalesforceAccount {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private long salesforceAccountId;
	
	/************************** Basics ********************************/
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
	
	
	private String standardStreet;
	private String stdStreet;
	private String stdCity;
	private String stdState;
	private String stdCountry;
	private String stdPhone;
	
	
	/************************ Relationships ***************************/
	
	@ManyToOne
	private SalesforceAccount parentAccount;
	
	@ManyToOne
	private Site site;

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

	public SalesforceAccount getParentAccount() {
		return parentAccount;
	}

	public SalesforceAccount setParentAccount(SalesforceAccount parentAccount) {
		this.parentAccount = parentAccount;
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

	public String getStandardStreet() {
		return standardStreet;
	}

	public void setStandardStreet(String standardStreet) {
		this.standardStreet = standardStreet;
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
	
}
