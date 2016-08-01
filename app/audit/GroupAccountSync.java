package audit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import persistence.Dealer;
import persistence.GroupAccount;

@Entity
@Audited(withModifiedFlag=true)
public class GroupAccountSync {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long salesforceSyncId;
	
	
	private String notes;
	private LocalDateTime timeCreated = LocalDateTime.now();
	
	
	private Boolean deleteNotPresent = false;
	private String sourceFile;

	@OneToMany
	@NotAudited
	private List<Dealer> unchangedDealers = new ArrayList<Dealer>();
	@OneToMany
	@NotAudited
	private List<Dealer> addedDealers = new ArrayList<Dealer>();
	@OneToMany
	@NotAudited
	private List<Dealer> modifiedDealers = new ArrayList<Dealer>();
	@OneToMany
	@NotAudited
	private List<Dealer> notPresentDealers = new ArrayList<Dealer>();
	@OneToMany
	@NotAudited
	private List<Dealer> deletedDealers = new ArrayList<Dealer>();
	
	@OneToMany
	@NotAudited
	private List<GroupAccount> unchangedGroupAccounts = new ArrayList<GroupAccount>();
	@OneToMany
	@NotAudited
	private List<GroupAccount> addedGroupAccounts = new ArrayList<GroupAccount>();
	@OneToMany
	@NotAudited
	private List<GroupAccount> modifiedGroupAccounts = new ArrayList<GroupAccount>();
	@OneToMany
	@NotAudited
	private List<GroupAccount> notPresentGroupAccounts = new ArrayList<GroupAccount>();
	@OneToMany
	@NotAudited
	private List<GroupAccount> deletedGroupAccounts = new ArrayList<GroupAccount>();
	
	
	public long getSalesforceSyncId() {
		return salesforceSyncId;
	}
	public void setSalesforceSyncId(long salesforceSyncId) {
		this.salesforceSyncId = salesforceSyncId;
	}
	public Boolean getDeleteNotPresent() {
		return deleteNotPresent;
	}
	public void setDeleteNotPresent(Boolean deleteNotPresent) {
		this.deleteNotPresent = deleteNotPresent;
	}
	public String getSourceFile() {
		return sourceFile;
	}
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
	public List<Dealer> getUnchangedDealers() {
		return unchangedDealers;
	}
	public void setUnchangedDealers(List<Dealer> unchangedDealers) {
		this.unchangedDealers = unchangedDealers;
	}
	public List<Dealer> getAddedDealers() {
		return addedDealers;
	}
	public void setAddedDealers(List<Dealer> addedDealers) {
		this.addedDealers = addedDealers;
	}
	public List<Dealer> getModifiedDealers() {
		return modifiedDealers;
	}
	public void setModifiedDealers(List<Dealer> modifiedDealers) {
		this.modifiedDealers = modifiedDealers;
	}
	public List<Dealer> getNotPresentDealers() {
		return notPresentDealers;
	}
	public void setNotPresentDealers(List<Dealer> notPresentDealers) {
		this.notPresentDealers = notPresentDealers;
	}
	public List<Dealer> getDeletedDealers() {
		return deletedDealers;
	}
	public void setDeletedDealers(List<Dealer> deletedDealers) {
		this.deletedDealers = deletedDealers;
	}
	public List<GroupAccount> getUnchangedGroupAccounts() {
		return unchangedGroupAccounts;
	}
	public void setUnchangedGroupAccounts(List<GroupAccount> unchangedGroupAccounts) {
		this.unchangedGroupAccounts = unchangedGroupAccounts;
	}
	public List<GroupAccount> getAddedGroupAccounts() {
		return addedGroupAccounts;
	}
	public void setAddedGroupAccounts(List<GroupAccount> addedGroupAccounts) {
		this.addedGroupAccounts = addedGroupAccounts;
	}
	public List<GroupAccount> getModifiedGroupAccounts() {
		return modifiedGroupAccounts;
	}
	public void setModifiedGroupAccounts(List<GroupAccount> modifiedGroupAccounts) {
		this.modifiedGroupAccounts = modifiedGroupAccounts;
	}
	public List<GroupAccount> getNotPresentGroupAccounts() {
		return notPresentGroupAccounts;
	}
	public void setNotPresentGroupAccounts(List<GroupAccount> notPresentGroupAccounts) {
		this.notPresentGroupAccounts = notPresentGroupAccounts;
	}
	public List<GroupAccount> getDeletedGroupAccounts() {
		return deletedGroupAccounts;
	}
	public void setDeletedGroupAccounts(List<GroupAccount> deletedGroupAccounts) {
		this.deletedGroupAccounts = deletedGroupAccounts;
	}
	
	
}
