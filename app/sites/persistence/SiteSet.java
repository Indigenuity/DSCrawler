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

@Entity
public class SiteSet {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long siteSetId;
	
	@OneToMany(fetch=FetchType.LAZY)
	private Set<Site> sites = new HashSet<Site>();
	
}
