package places;

import java.util.Date;

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

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import datadefinitions.RecordType;
import persistence.Site;
import urlcleanup.SiteOwner;

@Entity
@Table(indexes = {@Index(name = "placesId_index",  columnList="placesId", unique = false)})
@Audited(withModifiedFlag=true)
public class PlacesDealer implements SiteOwner {

	public enum PlacesType {
		FRANCHISE, INDEPENDENT, NOT_DEALER
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long placesDealerId;
	
	@Column(unique=true)
	private String placesId;
	
	@Enumerated(value=EnumType.STRING)
	private PlacesType actualType;
	
	private String formattedAddress;
	private String street;
	private String city;
	private String postal;
	private String province;
	private String country;
	private String formattedPhoneNumber; 
	private Double longitude;
	private Double latitude;
	private String internationalPhoneNumber;
	private String name;
	private Boolean permanentlyClosed;
	private String priceLevel;
	private Double rating;
	private Double ratingCount;
	private String types;
	private Integer utcOffset;
	private String vicinity;
	private String shortCountry;
	
	private String stdStreet;
	private String stdCity;
	private String stdProvince;
	private String stdCountry;
	private String stdPostal;
	private String stdPhone;
	
	
	@Column(nullable=true, columnDefinition="TEXT")
	private String iconUrl;
	@Column(nullable=true, columnDefinition="TEXT")
	private String openHours;
	@Column(nullable=true, columnDefinition="TEXT")
	private String photos;
	@Column(nullable=true, columnDefinition="TEXT")
	private String googleUrl;
	@Column(nullable=true, columnDefinition="TEXT")
	private String website;
	
	@Column(nullable=true, columnDefinition="TEXT")
	private String domain;
	
	
	private Date detailFetchDate;
	private String placesStatus;
	@Column(nullable = true, columnDefinition="varchar(500)")
	private String errorMessage;
	
	@Enumerated(value=EnumType.STRING)
	private RecordType recordType = RecordType.UNCLASSIFIED;
	
	
	@ManyToOne
	private PlacesDealer forwardsTo;
	@ManyToOne
	private Site site;
	@ManyToOne
	private Site unresolvedSite;
	
	private String salesforceMatchString;
	
	
	public void setId(long id) {
		this.placesDealerId = id;
	}
	public String getPlacesId() {
		return placesId;
	}
	public void setPlacesId(String placesId) {
		this.placesId = placesId;
	}
	public String getFormattedAddress() {
		return formattedAddress;
	}
	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}
	public String getFormattedPhoneNumber() {
		return formattedPhoneNumber;
	}
	public void setFormattedPhoneNumber(String formattedPhoneNumber) {
		this.formattedPhoneNumber = formattedPhoneNumber;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getInternationalPhoneNumber() {
		return internationalPhoneNumber;
	}
	public void setInternationalPhoneNumber(String internationalPhoneNumber) {
		this.internationalPhoneNumber = internationalPhoneNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOpenHours() {
		return openHours;
	}
	public void setOpenHours(String openHours) {
		this.openHours = openHours;
	}
	public boolean isPermanentlyClosed() {
		return permanentlyClosed;
	}
	public void setPermanentlyClosed(boolean permanentlyClosed) {
		this.permanentlyClosed = permanentlyClosed;
	}
	public String getPhotos() {
		return photos;
	}
	public void setPhotos(String photos) {
		this.photos = photos;
	}
	public String getPriceLevel() {
		return priceLevel;
	}
	public void setPriceLevel(String priceLevel) {
		this.priceLevel = priceLevel;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	public String getGoogleUrl() {
		return googleUrl;
	}
	public void setGoogleUrl(String googleUrl) {
		this.googleUrl = googleUrl;
	}
	public int getUtcOffset() {
		return utcOffset;
	}
	public void setUtcOffset(int utcOffset) {
		this.utcOffset = utcOffset;
	}
	public String getVicinity() {
		return vicinity;
	}
	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}
	public String getWebsiteString() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public double getRatingCount() { 
		return ratingCount;
	}
	public void setRatingCount(double ratingCount) {
		this.ratingCount = ratingCount;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public long getPlacesPageId() {
		return placesDealerId;
	}
	public void setPlacesPageId(long placesPageId) {
		this.placesDealerId = placesPageId;
	}
	public String getShortCountry() {
		return shortCountry;
	}
	public void setShortCountry(String shortCountry) {
		this.shortCountry = shortCountry;
	}
	public Date getDetailFetchDate() {
		return detailFetchDate;
	}
	public void setDetailFetchDate(Date detailFetchDate) {
		this.detailFetchDate = detailFetchDate;
	}
	public long getPlacesDealerId() {
		return placesDealerId;
	}
	public void setPlacesDealerId(long placesDealerId) {
		this.placesDealerId = placesDealerId;
	}
	public PlacesType getActualType() {
		return actualType;
	}
	public void setActualType(PlacesType actualType) {
		this.actualType = actualType;
	}
	public String getPlacesStatus() {
		return placesStatus;
	}
	public void setPlacesStatus(String placesStatus) {
		this.placesStatus = placesStatus;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = StringUtils.abbreviate(errorMessage, 500);
	}
	public PlacesDealer getForwardsTo() {
		return forwardsTo;
	}
	public void setForwardsTo(PlacesDealer forwardsTo) {
		this.forwardsTo = forwardsTo;
	}
	public Site getSite() {
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
		if(this.unresolvedSite == null){
			this.unresolvedSite = this.site;
		}
	}
	public Site setUnresolvedSite(Site site) {
		this.unresolvedSite = site;
		if(this.site == null) {
			this.site = this.unresolvedSite;
		}
		return this.unresolvedSite;
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
	public String getPostal() {
		return postal;
	}
	public void setPostal(String postal) {
		this.postal = postal;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public Boolean getPermanentlyClosed() {
		return permanentlyClosed;
	}
	public void setPermanentlyClosed(Boolean permanentlyClosed) {
		this.permanentlyClosed = permanentlyClosed;
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
	public String getStdProvince() {
		return stdProvince;
	}
	public void setStdProvince(String stdProvince) {
		this.stdProvince = stdProvince;
	}
	public String getStdCountry() {
		return stdCountry;
	}
	public void setStdCountry(String stdCountry) {
		this.stdCountry = stdCountry;
	}
	public String getStdPostal() {
		return stdPostal;
	}
	public void setStdPostal(String stdPostal) {
		this.stdPostal = stdPostal;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	public void setRatingCount(Double ratingCount) {
		this.ratingCount = ratingCount;
	}
	public void setUtcOffset(Integer utcOffset) {
		this.utcOffset = utcOffset;
	}
	
	public String getStdPhone() {
		return stdPhone;
	}
	public void setStdPhone(String stdPhone) {
		this.stdPhone = stdPhone;
	}
	@Override
	public Site getUnresolvedSite() {
		return this.unresolvedSite;
	}
	@Override
	public Site getResolvedSite() {
		return this.site;
	}
	@Override
	public Site setResolvedSite(Site site) {
		setSite(site);
		return this.site;
	}
	public String getSalesforceMatchString() {
		return salesforceMatchString;
	}
	public void setSalesforceMatchString(String salesforceMatch) {
		this.salesforceMatchString = salesforceMatch;
	}
	public String getWebsite() {
		return website;
	}
	public RecordType getRecordType() {
		return recordType;
	}
	public void setRecordType(RecordType recordType) {
		this.recordType = recordType;
	}
	
}
