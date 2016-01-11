package persistence;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class UrlCheck {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long urlCheckId;
	
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String seed;
	
	private String seedHost;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String resolvedHost;
	
	private String resolvedDomain;
	
	private Date checkDate;
	
	private int statusCode;

	private boolean unchanged;
	private boolean genericChange;
	private boolean error;
	private boolean pathApproved;
	private boolean queryApproved;
	private boolean statusApproved;
	private boolean domainApproved;
	private boolean domainChanged;
	
	private boolean accepted;

}
