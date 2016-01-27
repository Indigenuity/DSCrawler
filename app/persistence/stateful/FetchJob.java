package persistence.stateful;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import async.work.infofetch.InfoFetch;

@Entity
public class FetchJob {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long fetchJobId;
	
	private String name;
	
	@OneToMany(mappedBy="fetchJob")
	private Set<InfoFetch> fetches = new HashSet<InfoFetch>();
	
	@Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable=false, updatable=false)
	private Date dateStarted;


	public long getFetchJobId() {
		return fetchJobId;
	}

	public void setFetchJobId(long fetchJobId) {
		this.fetchJobId = fetchJobId;
	}

	public Set<InfoFetch> getFetches() {
		return fetches;
	}

	public void setFetches(Set<InfoFetch> fetches) {
		this.fetches.clear();
		this.fetches.addAll(fetches);
	}
	
	public void addFetch(InfoFetch fetch) {
		this.fetches.add(fetch);
	}

	public Date getDateStarted() {
		return dateStarted;
	}

	public void setDateStarted(Date dateStarted) {
		this.dateStarted = dateStarted;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
}
