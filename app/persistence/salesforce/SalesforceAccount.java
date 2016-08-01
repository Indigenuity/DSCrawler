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
	private SalesforceAccountType accountType;
	
	
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
	
	
}
