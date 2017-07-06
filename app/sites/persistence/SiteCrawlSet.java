package sites.persistence;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import persistence.Site;
import persistence.SiteCrawl;

@Entity
public class SiteCrawlSet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long siteCrawlSetId;
	
	@OneToMany(fetch=FetchType.LAZY)
	private Set<SiteCrawl> siteCrawls = new HashSet<SiteCrawl>();
}
