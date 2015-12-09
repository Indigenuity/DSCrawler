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
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import utilities.DSFormatter;

@Entity
public class Dealer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long dealerId;
	
	@Enumerated(EnumType.STRING)
	private Datasource datasource;
	
	@Column(nullable = true, columnDefinition="varchar(300)")
	private String dealerName;
	
	@Column(nullable = true, columnDefinition="varchar(15)")
	private String niada;
	@Column(nullable = true, columnDefinition="varchar(15)")
	private String capdb;
	@Column(nullable = true, columnDefinition="varchar(255)")
	private String placesId;
	@Column(nullable = true, columnDefinition="varchar(255)")
	private String sfId;
	
	@Column(nullable = false, columnDefinition="boolean default true")
	private boolean franchise;
	
	//Mistakenly created as onetoone instead of manytoone
	@OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JoinColumn(name="DEALER_MAIN_SITE_ID")
	private Site mainSite;
	
	@Column(columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.EAGER)
	private List<String> previousUrls = new ArrayList<String>();
	
	@Column(nullable = true, columnDefinition="varchar(255)")
	private String address;
	
	@Column(nullable = true, columnDefinition="varchar(255)")
	private String street;
	
	@Column(nullable = true, columnDefinition="varchar(255)")
	private String city;
	
	@Column(nullable = true, columnDefinition="varchar(255)")
	private String state;
	
	@Column(nullable = true, columnDefinition="varchar(255)")
	private String zip;
	
	@Column(nullable = true, columnDefinition="varchar(255)")
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

	public boolean isFranchise() {
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

	public List<String> getPreviousUrls() {
		return previousUrls;
	}

	public void setPreviousUrls(List<String> previousUrls) {
		this.previousUrls.clear();
		for(String url : previousUrls){
			this.previousUrls.add(DSFormatter.truncate(url, 4000));
		}
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



	public enum Datasource {
		ManualEntry, NIADA, CapDB, GooglePlacesAPI, OEM, Special_Project, SalesForce
	}
	
}
