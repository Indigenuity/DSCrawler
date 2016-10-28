package crawling.nydmv;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;

import persistence.salesforce.SalesforceAccount;
import places.PlacesDealer;

@Entity
@Audited(withModifiedFlag=true)
public class NYDealer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long nyDealerId;
	
	private String facilityNumber;
	private String facilityName;
	private String street;
	private String city;
	private String zip;
	private String county;
	
	private String standardStreet;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private SalesforceAccount sfAccount;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private PlacesDealer placesDealer;
	
	private String placesId;
	
	private String linkStatus;
	
	
	public long getNyDealerId() {
		return nyDealerId;
	}
	public void setNyDealerId(long nyDealerId) {
		this.nyDealerId = nyDealerId;
	}
	public String getFacilityNumber() {
		return facilityNumber;
	}
	public void setFacilityNumber(String facilityNumber) {
		this.facilityNumber = facilityNumber;
	}
	public String getFacilityName() {
		return facilityName;
	}
	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
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
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getStandardStreet() {
		return standardStreet;
	}
	public void setStandardStreet(String standardStreet) {
		this.standardStreet = standardStreet;
	}
	public SalesforceAccount getSfAccount() {
		return sfAccount;
	}
	public void setSfAccount(SalesforceAccount sfAccount) {
		this.sfAccount = sfAccount;
	}
	public PlacesDealer getPlacesDealer() {
		return placesDealer;
	}
	public void setPlacesDealer(PlacesDealer placesDealer) {
		this.placesDealer = placesDealer;
	}
	public String getLinkStatus() {
		return linkStatus;
	}
	public void setLinkStatus(String linkStatus) {
		this.linkStatus = linkStatus;
	}
	public String getPlacesId() {
		return placesId;
	}
	public void setPlacesId(String placesId) {
		this.placesId = placesId;
	}
	
	
	
}
