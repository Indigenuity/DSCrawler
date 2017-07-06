package crawling.projects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import persistence.Site;
import salesforce.persistence.SalesforceAccount;
import urlcleanup.SiteOwner;
import utilities.DSFormatter;

@Entity
@Audited(withModifiedFlag=true)
public class BasicDealer implements SiteOwner {

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
	
	@ManyToOne
	private Site site;
	@ManyToOne
	private Site unresolvedSite;
	
	@ManyToOne
	@NotAudited
	private SalesforceAccount salesforceAccount;
	
	@NotAudited
	@ManyToMany(fetch=FetchType.LAZY)
	private Set<SalesforceAccount> possibleMatches = new HashSet<SalesforceAccount>();
	
	private String stdName;
	private String stdStreet; 
	private String stdCity;
	private String stdState;
	private String stdCountry;
	private String stdPhone;
	private String stdPostal;
	
	
	private String identifier;
	private String projectIdentifier;
	private String salesforceMatchString;
	
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((custom1 == null) ? 0 : custom1.hashCode());
		result = prime * result + ((custom2 == null) ? 0 : custom2.hashCode());
		result = prime * result + ((custom3 == null) ? 0 : custom3.hashCode());
		result = prime * result + ((custom4 == null) ? 0 : custom4.hashCode());
		result = prime * result + ((custom5 == null) ? 0 : custom5.hashCode());
		result = prime * result + ((custom6 == null) ? 0 : custom6.hashCode());
		result = prime * result + ((foreignIdentifier == null) ? 0 : foreignIdentifier.hashCode());
		result = prime * result + ((foreignIdentifierString == null) ? 0 : foreignIdentifierString.hashCode());
		result = prime * result + ((foreignType == null) ? 0 : foreignType.hashCode());
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((outdated == null) ? 0 : outdated.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((postal == null) ? 0 : postal.hashCode());
		result = prime * result + ((projectIdentifier == null) ? 0 : projectIdentifier.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((stdCity == null) ? 0 : stdCity.hashCode());
		result = prime * result + ((stdCountry == null) ? 0 : stdCountry.hashCode());
		result = prime * result + ((stdName == null) ? 0 : stdName.hashCode());
		result = prime * result + ((stdPhone == null) ? 0 : stdPhone.hashCode());
		result = prime * result + ((stdPostal == null) ? 0 : stdPostal.hashCode());
		result = prime * result + ((stdState == null) ? 0 : stdState.hashCode());
		result = prime * result + ((stdStreet == null) ? 0 : stdStreet.hashCode());
		result = prime * result + ((street == null) ? 0 : street.hashCode());
		result = prime * result + ((website == null) ? 0 : website.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicDealer other = (BasicDealer) obj;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (custom1 == null) {
			if (other.custom1 != null)
				return false;
		} else if (!custom1.equals(other.custom1))
			return false;
		if (custom2 == null) {
			if (other.custom2 != null)
				return false;
		} else if (!custom2.equals(other.custom2))
			return false;
		if (custom3 == null) {
			if (other.custom3 != null)
				return false;
		} else if (!custom3.equals(other.custom3))
			return false;
		if (custom4 == null) {
			if (other.custom4 != null)
				return false;
		} else if (!custom4.equals(other.custom4))
			return false;
		if (custom5 == null) {
			if (other.custom5 != null)
				return false;
		} else if (!custom5.equals(other.custom5))
			return false;
		if (custom6 == null) {
			if (other.custom6 != null)
				return false;
		} else if (!custom6.equals(other.custom6))
			return false;
		if (foreignIdentifier == null) {
			if (other.foreignIdentifier != null)
				return false;
		} else if (!foreignIdentifier.equals(other.foreignIdentifier))
			return false;
		if (foreignIdentifierString == null) {
			if (other.foreignIdentifierString != null)
				return false;
		} else if (!foreignIdentifierString.equals(other.foreignIdentifierString))
			return false;
		if (foreignType == null) {
			if (other.foreignType != null)
				return false;
		} else if (!foreignType.equals(other.foreignType))
			return false;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (outdated == null) {
			if (other.outdated != null)
				return false;
		} else if (!outdated.equals(other.outdated))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (postal == null) {
			if (other.postal != null)
				return false;
		} else if (!postal.equals(other.postal))
			return false;
		if (projectIdentifier == null) {
			if (other.projectIdentifier != null)
				return false;
		} else if (!projectIdentifier.equals(other.projectIdentifier))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (stdCity == null) {
			if (other.stdCity != null)
				return false;
		} else if (!stdCity.equals(other.stdCity))
			return false;
		if (stdCountry == null) {
			if (other.stdCountry != null)
				return false;
		} else if (!stdCountry.equals(other.stdCountry))
			return false;
		if (stdName == null) {
			if (other.stdName != null)
				return false;
		} else if (!stdName.equals(other.stdName))
			return false;
		if (stdPhone == null) {
			if (other.stdPhone != null)
				return false;
		} else if (!stdPhone.equals(other.stdPhone))
			return false;
		if (stdPostal == null) {
			if (other.stdPostal != null)
				return false;
		} else if (!stdPostal.equals(other.stdPostal))
			return false;
		if (stdState == null) {
			if (other.stdState != null)
				return false;
		} else if (!stdState.equals(other.stdState))
			return false;
		if (stdStreet == null) {
			if (other.stdStreet != null)
				return false;
		} else if (!stdStreet.equals(other.stdStreet))
			return false;
		if (street == null) {
			if (other.street != null)
				return false;
		} else if (!street.equals(other.street))
			return false;
		if (website == null) {
			if (other.website != null)
				return false;
		} else if (!website.equals(other.website))
			return false;
		return true;
	}
	@Override
	public String getWebsiteString() {
		return website;
	}
	@Override
	public Site getUnresolvedSite() {
		return unresolvedSite;
	}
	@Override
	public Site setUnresolvedSite(Site unresolvedSite) {
		this.unresolvedSite = unresolvedSite;
		return this.unresolvedSite;
	}
	@Override
	public Site getResolvedSite() {
		return getSite();
	}
	@Override
	public Site setResolvedSite(Site site) {
		setSite(site);
		return getSite();
	}
	public Site getSite() {
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
	}
	public String getSalesforceMatchString() {
		return salesforceMatchString;
	}
	public void setSalesforceMatchString(String salesforceMatchString) {
		this.salesforceMatchString = salesforceMatchString;
	}
	public SalesforceAccount getSalesforceAccount() {
		return salesforceAccount;
	}
	public void setSalesforceAccount(SalesforceAccount salesforceAccount) {
		this.salesforceAccount = salesforceAccount;
	}
	public Set<SalesforceAccount> getPossibleMatches() {
		return possibleMatches;
	}
	public void setPossibleMatches(Set<SalesforceAccount> possibleMatches) {
		this.possibleMatches.clear();
		this.possibleMatches.addAll(possibleMatches);
	}
	public void addPossibleMatches(Collection<SalesforceAccount> possibleMatches) {
		this.possibleMatches.addAll(possibleMatches);
	}
	
	
	
}
