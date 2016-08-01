package persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import persistence.salesforce.SalesforceAccount;

@Entity
@Table(indexes = {@Index(name = "name_index",  columnList="name", unique = false),
        @Index(name = "salesforceId_index", columnList="salesforceId",     unique = false)})
@Audited(withModifiedFlag=true)
public class GroupAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long groupAccountId;
	
	private String name;
	
	/************************************  Salesforce Fields ************************************/
	
	private String salesforceId;
	private String accountType;
	private String parentAccountName;
	private String parentAccountSalesforceId;
	@Column(columnDefinition = "varchar(4000)")
	private String salesforceWebsite;
	
	private Boolean franchise = false;
	
	
	public long getGroupAccountId() {
		return groupAccountId;
	}
	public void setGroupAccountId(long groupAccountId) {
		this.groupAccountId = groupAccountId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSalesforceId() {
		return salesforceId;
	}
	public void setSalesforceId(String salesforceId) {
		this.salesforceId = salesforceId;
	}
	public String getSalesforceWebsite() {
		return salesforceWebsite;
	}
	public void setSalesforceWebsite(String salesforceWebsite) {
		this.salesforceWebsite = salesforceWebsite;
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
	public Boolean getFranchise() {
		return franchise;
	}
	public void setFranchise(Boolean franchise) {
		this.franchise = franchise;
	}
	public Boolean isFranchise() {
		return franchise;
	}
	
	
}
