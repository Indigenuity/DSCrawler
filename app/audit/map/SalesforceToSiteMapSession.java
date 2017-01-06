package audit.map;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import audit.sync.JpaPersistenceContext;
import audit.sync.Sync;
import audit.sync.SyncType;
import dao.SitesDAO;
import persistence.Site;
import persistence.salesforce.SalesforceAccount;
import play.db.jpa.JPA;

public class SalesforceToSiteMapSession extends SingleMapSession<SalesforceAccount, Site> {

	private Collection<SalesforceAccount> accounts;
	private Iterator<SalesforceAccount> accountsIterator;
	
	
	public SalesforceToSiteMapSession(Collection<SalesforceAccount> accounts){
		super(SalesforceAccount.class, Site.class);
		this.accounts = accounts;
		this.accountsIterator = this.accounts.iterator();
		this.persistenceContext = new JpaPersistenceContext(JPA.em());
		this.keySupplier = () -> accountsIterator.hasNext() ? accountsIterator.next() : null;
		this.valueFetcher = (account) -> SitesDAO.getFirst("homepage", account.getSalesforceWebsite(), 0);
		this.assigner = (account, site) -> account.setSite(site);
		this.valueCreator = (account) -> {
			if(StringUtils.isEmpty(account.getSalesforceWebsite())){
				return null;
			}
			return new Site(account.getSalesforceWebsite());
		};
		this.sync.setSyncType(SyncType.ASSIGN_SITELESS);
	}
	
	@Override
	protected void preCommit(){
		persistenceContext.insert(sync);
	}
}
