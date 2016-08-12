package places;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PlacesPage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long placesPageId;
	
	@Column(nullable=false)
	private String placesId;
	
	@Column(nullable=true)
	private String formattedAddress; 
	@Column(nullable=true)
	private String country;
	@Column(nullable=true)
	private String formattedPhoneNumber; 
	@Column(nullable=true)
	private double longitude;
	@Column(nullable=true)
	private double latitude;
	@Column(nullable=true, columnDefinition="TEXT")
	private String iconUrl;
	@Column(nullable=true)
	private String internationalPhoneNumber;
	@Column(nullable=true)
	private String name;
	@Column(nullable=true, columnDefinition="TEXT")
	private String openHours;
	@Column(nullable=true)
	private boolean permanentlyClosed;
	@Column(nullable=true, columnDefinition="TEXT")
	private String photos;
	@Column(nullable=true)
	private String priceLevel;
	@Column(nullable=true)
	private double rating;
	@Column(nullable=true)
	private double ratingCount;
	@Column(nullable=true)
	private String types;
	@Column(nullable=true, columnDefinition="TEXT")
	private String googleUrl;
	@Column(nullable=true)
	private int utcOffset;
	@Column(nullable=true)
	private String vicinity;
	@Column(nullable=true, columnDefinition="TEXT")
	private String website;
	
	private String shortCountry;
	
	@Column(nullable=true, columnDefinition="TEXT")
	private String domain;
	
	
	public void setId(long id) {
		this.placesPageId = id;
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
	public String getWebsite() {
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
		return placesPageId;
	}
	public void setPlacesPageId(long placesPageId) {
		this.placesPageId = placesPageId;
	}
	public String getShortCountry() {
		return shortCountry;
	}
	public void setShortCountry(String shortCountry) {
		this.shortCountry = shortCountry;
	}

	
}
