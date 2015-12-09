package persistence;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class SiteInformation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long siteId;
	
	
	private String domain;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String homepage;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String standardizedHomepage;
	
	@Column(nullable = false, columnDefinition="boolean default true")
	private boolean standaloneSite = true;	//If this site is the only one on the domain, as opposed to PAACO sites

	
}
