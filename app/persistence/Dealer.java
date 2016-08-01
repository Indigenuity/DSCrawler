package persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Index;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import persistence.salesforce.SalesforceAccount;
import utilities.DSFormatter;

@Entity
@Table(indexes = {@Index(name = "dealerName_index",  columnList="dealerName", unique = false),
        @Index(name = "salesforceId_index", columnList="salesforceId",     unique = false)})
@Audited(withModifiedFlag=true)
public class Dealer {
	
	
	/***************************  Identifiers ****************************/
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long dealerId;
	
	@Enumerated(EnumType.STRING)
	private Datasource datasource;
	
	@Column(nullable = true, columnDefinition="varchar(300)")
	private String dealerName;
	
	private String niada;
	private String capdb;
	private String placesId;
	private String sfId;
	
	/************************************  Salesforce Fields ************************************/
	
	private String salesforceId;
	private String parentAccountName;
	private String parentAccountSalesforceId;
	@Column(columnDefinition = "varchar(4000)")
	private String salesforceWebsite;
	private String accountType = "GROUP";
	
	
	@ManyToOne
	private GroupAccount groupAccount;
	
	
	@Column(nullable = false, columnDefinition="boolean default true")
	private boolean franchise;
	
	//Mistakenly created as onetoone instead of manytoone
	@OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JoinColumn(name="DEALER_MAIN_SITE_ID")
	@NotAudited
	private Site mainSite;
	
	
	private String address;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String country;
	private String phone;
	
	
	private boolean sharesDomain = false;
	private boolean sharesHomepage = false;
	private boolean sharesAddress = false;
	
	private boolean homepageSharingApproved = false;
	private boolean addressSharingApproved = false;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean oemDealer = false;
	
	public long getDealerId() {
		return dealerId;
	}

	public void setDealerId(long dealerId) {
		this.dealerId = dealerId;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = DSFormatter.truncate(dealerName, 300);
	}

	public String getNiada() {
		return niada;
	}

	public void setNiada(String niada) {
		this.niada = niada;
	}

	public String getCapdb() {
		return capdb;
	}

	public void setCapdb(String capdb) {
		this.capdb = capdb;
	}

	public Boolean isFranchise() {
		return franchise;
	}

	public void setFranchise(boolean franchise) {
		this.franchise = franchise;
	}

	public Site getMainSite() {
		return mainSite;
	}

	public void setMainSite(Site mainSite) {
		this.mainSite = mainSite;
	}

	public String getPlacesId() {
		return placesId;
	}

	public void setPlacesId(String placesId) {
		this.placesId = placesId;
	}
	
	

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	

	public boolean isSharesDomain() {
		return sharesDomain;
	}

	public void setSharesDomain(boolean sharesDomain) {
		this.sharesDomain = sharesDomain;
	}

	public boolean isSharesHomepage() {
		return sharesHomepage;
	}

	public void setSharesHomepage(boolean sharesHomepage) {
		this.sharesHomepage = sharesHomepage;
	}

	public boolean isSharesAddress() {
		return sharesAddress;
	}

	public void setSharesAddress(boolean sharesAddress) {
		this.sharesAddress = sharesAddress;
	}

	public boolean isHomepageSharingApproved() {
		return homepageSharingApproved;
	}

	public void setHomepageSharingApproved(boolean homepageSharingApproved) {
		this.homepageSharingApproved = homepageSharingApproved;
	}

	public boolean isAddressSharingApproved() {
		return addressSharingApproved;
	}

	public void setAddressSharingApproved(boolean addressSharingApproved) {
		this.addressSharingApproved = addressSharingApproved;
	}

	
	
	
	
	public String getSfId() {
		return sfId;
	}

	public void setSfId(String sfId) {
		this.sfId = sfId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isOemDealer() {
		return oemDealer;
	}

	public void setOemDealer(boolean oemDealer) {
		this.oemDealer = oemDealer;
	}

	public Datasource getDatasource() {
		return datasource;
	}

	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
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



	public String getSalesforceId() {
		return salesforceId;
	}

	public void setSalesforceId(String salesforceId) {
		this.salesforceId = salesforceId;
	}

	public String getParentAccountName() {
		return parentAccountName;
	}

	public void setParentAccountName(String parentAccountName) {
		this.parentAccountName = parentAccountName;
	}

	public String getParentAccountSalesforceId() {
		return parentAccountSalesforceId;
	}

	public void setParentAccountSalesforceId(String parentAccountSalesforceId) {
		this.parentAccountSalesforceId = parentAccountSalesforceId;
	}

	public String getSalesforceWebsite() {
		return salesforceWebsite;
	}

	public void setSalesforceWebsite(String salesforceWebsite) {
		this.salesforceWebsite = salesforceWebsite;
	}



	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public GroupAccount getGroupAccount() {
		return groupAccount;
	}

	public void setGroupAccount(GroupAccount groupAccount) {
		this.groupAccount = groupAccount;
	}



	public enum Datasource {
		ManualEntry, NIADA, CapDB, GooglePlacesAPI, OEM, Special_Project, SalesForce
	}



	public String getName() {
		return getDealerName();
	}


}
