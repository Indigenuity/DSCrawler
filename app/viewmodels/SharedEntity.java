package viewmodels;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import persistence.*;
public class SharedEntity {
	
	private String url = null;
	private Dealer dealer = null;
	private Site site = null;
	private SiteCrawl siteCrawl = null;
	private Temp temp = null;
	
	private Set<Dealer> dealers = new HashSet<Dealer>();
	private Set<Site> sites = new HashSet<Site>();
	private Set<SiteCrawl> siteCrawls = new HashSet<SiteCrawl>();
	private Set<Temp> temps = new HashSet<Temp>();
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Set<Dealer> getDealers() {
		return dealers;
	}
	public void setDealers(Collection<Dealer> dealers) {
		this.dealers.clear();
		this.dealers.addAll(dealers);
	}
	public Set<Site> getSites() {
		return sites;
	}
	public void setSites(Collection<Site> sites) {
		this.sites.clear();
		this.sites.addAll(sites);
	}
	public Set<SiteCrawl> getSiteCrawls() {
		return siteCrawls;
	}
	public void setSiteCrawls(Collection<SiteCrawl> siteCrawls) {
		this.siteCrawls.clear();
		this.siteCrawls.addAll(siteCrawls);
	}
	public Dealer getDealer() {
		return dealer;
	}
	public void setDealer(Dealer dealer) {
		this.dealer = dealer;
	}
	public Site getSite() {
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
	}
	public SiteCrawl getSiteCrawl() {
		return siteCrawl;
	}
	public void setSiteCrawl(SiteCrawl siteCrawl) {
		this.siteCrawl = siteCrawl;
	}
	public void setTemp(Temp temp) {
		this.temp = temp;
	}
	public Temp getTemp(){
		return this.temp;
	}
	public Set<Temp> getTemps() {
		return temps;
	}
	public void setTemps(Set<Temp> temps) {
		this.temps.clear();
		this.temps.addAll(temps);
	}
	
}
