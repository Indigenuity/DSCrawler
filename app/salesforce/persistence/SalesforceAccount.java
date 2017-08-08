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
        @Index(name = "salesforceId_index", columnList="salesforceId", unique = false)})
//@Audited(withModifiedFlag=true)
public class SalesforceAccount implements SiteOwner{

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private long salesforceAccountId;
	
	/************************** Basics ********************************/
	
	private Boolean outdated = false;
	private Date lastUpdated = new Date();
	private Date lastModified = null;
	private Date created = null; 
	
	
	private String name;
	@Column(columnDefinition = "varchar(100)")
	private String alias;
	private String salesforceId;
	private String parentAccountName;
	private String parentAccountSalesforceId;
	@Column(columnDefinition = "varchar(100)")
	private String groupOrganization;
	
	@Column(columnDefinition = "varchar(500)")
	private String brandAffiliation;
	@Column(columnDefinition = "varchar(10)")
	private String primaryArea;
	@Column(columnDefinition = "varchar(10)")
	private String capDbRating;

	@Enumerated(EnumType.STRING)
	private DealershipType dealershipType;
	@Enumerated(EnumType.STRING)
	private SalesforceAccountType accountType;
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "varchar(20)")
	private SalesforceCustomerType customerType;
	
	
	@Column(columnDefinition = "varchar(4000)")
	private String salesforceWebsite;
	private String phone;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String country;
	
	private String stdStreet;
	private String stdCity;
	private String stdState;
	private String stdCountry;
	private String stdPhone;
	private String stdPostal;
	
	
	//Denotes if there is a significant difference between the salesforceWebsite and the homepage of the assigned site.
	private Boolean trivialDifference = false;
	private Boolean siteMismatch = false;
	private Boolean franchise;
	
	
	
	
	
	/*********************** Product IDs *******************************/
	@Column(columnDefinition = "varchar(10)")
	private String eclNum;
	@Column(columnDefinition = "varchar(10)")
	private String aaxNum;
	@Column(columnDefinition = "varchar(10)")
	private String inventoryId;
	@Column(columnDefinition = "varchar(10)")
	private String invGroupId;
	@Column(columnDefinition = "varchar(10)")
	private String dealerTrackId;
	@Column(columnDefinition = "varchar(10)")
	private String aaxGroupId;
	@Column(columnDefinition = "varchar(10)")
	private String aaxCompanyId;
	@Column(columnDefinition = "varchar(10)")
	private String crmSiteId;
	@Column(columnDefinition = "varchar(10)")
	private String socketId;
	@Column(columnDefinition = "varchar(10)")
	private String fusionId;
	@Column(columnDefinition = "varchar(10)")
	private String portalPayId;
	@Column(columnDefinition = "varchar(10)")
	private String fexIntitutionalId;
	@Column(columnDefinition = "varchar(20)")
	private String legacyFexId;
	@Column(columnDefinition = "varchar(20)")
	private String legacyAutoStarId;
	
	/************************* MRR ************************************/
	
	private Float totalMrr;
	private Float totalAtRiskMrr;
	private Float crmMrr;
	private Float crmAtRiskMrr;
	private Float deskingMrr;
	private Float deskingAtRiskMrr;
	private Float inventoryMrr;
	private Float inventoryAtRiskMrr;
	private Float idmsMrr;
	private Float revenueRadarMrr;
	private Float revenueAtRiskRadarMrr;
	private Float websiteDgmMrr;
	private Float websiteAtRiskDgmMrr;
	
	
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

	public String getFullLocation(){
		return getStreet() + " " + getCity() + ", " + getState() + " " + getZip() + ", " + getCountry();
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
	
	public String getPrimaryArea() {
		return primaryArea;
	}

	public void setPrimaryArea(String primaryArea) {
		this.primaryArea = DSFormatter.truncate(primaryArea, 10);
	}

	public String getEclNum() {
		return eclNum;
	}

	public void setEclNum(String eclNum) {
		this.eclNum = eclNum;
	}

	public String getAaxNum() {
		return aaxNum;
	}

	public void setAaxNum(String aaxNum) {
		this.aaxNum = aaxNum;
	}

	public String getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(String inventoryId) {
		this.inventoryId = inventoryId;
	}

	public String getInvGroupId() {
		return invGroupId;
	}

	public void setInvGroupId(String invGroupId) {
		this.invGroupId = invGroupId;
	}

	public String getDealerTrackId() {
		return dealerTrackId;
	}

	public void setDealerTrackId(String dealerTrackId) {
		this.dealerTrackId = dealerTrackId;
	}

	public String getAaxGroupId() {
		return aaxGroupId;
	}

	public void setAaxGroupId(String aaxGroupId) {
		this.aaxGroupId = aaxGroupId;
	}

	public String getAaxCompanyId() {
		return aaxCompanyId;
	}

	public void setAaxCompanyId(String aaxCompanyId) {
		this.aaxCompanyId = aaxCompanyId;
	}

	public String getCrmSiteId() {
		return crmSiteId;
	}

	public void setCrmSiteId(String crmSiteId) {
		this.crmSiteId = crmSiteId;
	}

	public String getSocketId() {
		return socketId;
	}

	public void setSocketId(String socketId) {
		this.socketId = socketId;
	}

	public String getFusionId() {
		return fusionId;
	}

	public void setFusionId(String fusionId) {
		this.fusionId = fusionId;
	}

	public String getPortalPayId() {
		return portalPayId;
	}

	public void setPortalPayId(String portalPayId) {
		this.portalPayId = portalPayId;
	}

	public String getFexIntitutionalId() {
		return fexIntitutionalId;
	}

	public void setFexIntitutionalId(String fexIntitutionalId) {
		this.fexIntitutionalId = DSFormatter.truncate(fexIntitutionalId, 10);
	}

	public String getLegacyFexId() {
		return legacyFexId;
	}

	public void setLegacyFexId(String legacyFexId) {
		this.legacyFexId = legacyFexId;
	}

	public String getLegacyAutoStarId() {
		return legacyAutoStarId;
	}

	public void setLegacyAutoStarId(String legacyAutoStarId) {
		this.legacyAutoStarId = legacyAutoStarId;
	}

	public String getCapDbRating() {
		return capDbRating;
	}

	public void setCapDbRating(String capDbRating) {
		this.capDbRating = capDbRating;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = DSFormatter.truncate(alias, 100);
	}

	public SalesforceCustomerType getCustomerType() {
		return customerType;
	}

	public void setCustomerType(SalesforceCustomerType customerType) {
		this.customerType = customerType;
	}

	public Float getTotalMrr() {
		return totalMrr;
	}

	public void setTotalMrr(Float totalMrr) {
		this.totalMrr = totalMrr;
	}

	public Float getCrmMrr() {
		return crmMrr;
	}

	public void setCrmMrr(Float crmMrr) {
		this.crmMrr = crmMrr;
	}

	public Float getDeskingMrr() {
		return deskingMrr;
	}

	public void setDeskingMrr(Float deskingMrr) {
		this.deskingMrr = deskingMrr;
	}

	public Float getInventoryMrr() {
		return inventoryMrr;
	}

	public void setInventoryMrr(Float inventoryMrr) {
		this.inventoryMrr = inventoryMrr;
	}

	public Float getIdmsMrr() {
		return idmsMrr;
	}

	public void setIdmsMrr(Float idmsMrr) {
		this.idmsMrr = idmsMrr;
	}

	public Float getRevenueRadarMrr() {
		return revenueRadarMrr;
	}

	public void setRevenueRadarMrr(Float revenueRadarMrr) {
		this.revenueRadarMrr = revenueRadarMrr;
	}

	public Float getWebsiteDgmMrr() {
		return websiteDgmMrr;
	}

	public void setWebsiteDgmMrr(Float websiteDgmMrr) {
		this.websiteDgmMrr = websiteDgmMrr;
	}

	public Float getTotalAtRiskMrr() {
		return totalAtRiskMrr;
	}

	public void setTotalAtRiskMrr(Float totalAtRiskMrr) {
		this.totalAtRiskMrr = totalAtRiskMrr;
	}

	public Float getCrmAtRiskMrr() {
		return crmAtRiskMrr;
	}

	public void setCrmAtRiskMrr(Float crmAtRiskMrr) {
		this.crmAtRiskMrr = crmAtRiskMrr;
	}

	public Float getDeskingAtRiskMrr() {
		return deskingAtRiskMrr;
	}

	public void setDeskingAtRiskMrr(Float deskingAtRiskMrr) {
		this.deskingAtRiskMrr = deskingAtRiskMrr;
	}

	public Float getInventoryAtRiskMrr() {
		return inventoryAtRiskMrr;
	}

	public void setInventoryAtRiskMrr(Float inventoryAtRiskMrr) {
		this.inventoryAtRiskMrr = inventoryAtRiskMrr;
	}

	public Float getRevenueAtRiskRadarMrr() {
		return revenueAtRiskRadarMrr;
	}

	public void setRevenueAtRiskRadarMrr(Float revenueAtRiskRadarMrr) {
		this.revenueAtRiskRadarMrr = revenueAtRiskRadarMrr;
	}

	public Float getWebsiteAtRiskDgmMrr() {
		return websiteAtRiskDgmMrr;
	}

	public void setWebsiteAtRiskDgmMrr(Float websiteAtRiskDgmMrr) {
		this.websiteAtRiskDgmMrr = websiteAtRiskDgmMrr;
	}

	public String getGroupOrganization() {
		return groupOrganization;
	}

	public void setGroupOrganization(String groupOrganization) {
		this.groupOrganization = groupOrganization;
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

	@Override
	public Boolean isTrivialDifference() {
		return trivialDifference;
	}

	@Override
	public void setTrivialDifference(boolean trivialDifference) {
		this.trivialDifference = trivialDifference;
	}
}
