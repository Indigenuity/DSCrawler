package crawling.projects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.envers.Audited;

import utilities.DSFormatter;

@Entity
@Audited(withModifiedFlag=true)
public class BasicDealer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long basicDealerId;
	
	private String name;
	private String street;
	private String city;
	private String state;
	private String country;
	private String postal;
	private String phone;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String website;
	
	private String stdName;
	private String stdStreet; 
	private String stdCity;
	private String stdState;
	private String stdCountry;
	private String stdPhone;
	private String stdPostal;
	
	
	private String identifier;
	private String projectIdentifier;
	
	private Long foreignIdentifier;
	private String foreignIdentifierString;
	private String foreignType;
	
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String custom1;
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String custom2;
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String custom3;
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String custom4;
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String custom5;
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String custom6;
	
	private Boolean outdated = false;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = DSFormatter.truncate(street, 255);
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
		this.state = DSFormatter.truncate(state, 255);
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
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public long getBasicDealerId() {
		return basicDealerId;
	}
	public void setBasicDealerId(long basicDealerId) {
		this.basicDealerId = basicDealerId;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getStdName() {
		return stdName;
	}
	public void setStdName(String stdName) {
		this.stdName = stdName;
	}
	public String getStdStreet() {
		return stdStreet;
	}
	public void setStdStreet(String stdStreet) {
		this.stdStreet = DSFormatter.truncate(stdStreet, 255);
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
		this.stdState = DSFormatter.truncate(stdState, 255);
	}
	public String getStdPhone() {
		return stdPhone;
	}
	public void setStdPhone(String stdPhone) {
		this.stdPhone = stdPhone;
	}
	public String getProjectIdentifier() {
		return projectIdentifier;
	}
	public void setProjectIdentifier(String projectIdentifier) {
		this.projectIdentifier = projectIdentifier;
	}
	public Long getForeignIdentifier() {
		return foreignIdentifier;
	}
	public void setForeignIdentifier(Long foreignIdentifier) {
		this.foreignIdentifier = foreignIdentifier;
	}
	public String getForeignType() {
		return foreignType;
	}
	public void setForeignType(String foreignType) {
		this.foreignType = foreignType;
	}
	public String getCustom1() {
		return custom1;
	}
	public void setCustom1(String custom1) {
		this.custom1 = DSFormatter.truncate(custom1, 255);
	}
	public String getCustom2() {
		return custom2;
	}
	public void setCustom2(String custom2) {
		this.custom2 = DSFormatter.truncate(custom2, 255);
	}
	public String getCustom3() {
		return custom3;
	}
	public void setCustom3(String custom3) {
		this.custom3 = DSFormatter.truncate(custom3, 255);
	}
	public String getCustom4() {
		return custom4;
	}
	public void setCustom4(String custom4) {
		this.custom4 = DSFormatter.truncate(custom4, 255);
	}
	public String getCustom5() {
		return custom5;
	}
	public void setCustom5(String custom5) {
		this.custom5 = DSFormatter.truncate(custom5, 255);
	}
	public String getCustom6() {
		return custom6;
	}
	public void setCustom6(String custom6) {
		this.custom6 = DSFormatter.truncate(custom6, 255);
	}
	public String getForeignIdentifierString() {
		return foreignIdentifierString;
	}
	public void setForeignIdentifierString(String foreignIdentifierString) {
		this.foreignIdentifierString = foreignIdentifierString;
	}
	public Boolean getOutdated() {
		return outdated;
	}
	public void setOutdated(Boolean outdated) {
		this.outdated = outdated;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
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
	
	
}
