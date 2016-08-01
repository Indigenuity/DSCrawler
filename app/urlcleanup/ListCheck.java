package urlcleanup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import datatransfer.reports.Report;
import persistence.UrlCheck;

@Entity
public class ListCheck {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long listCheckId;
	
	@OneToOne(cascade=CascadeType.ALL)
	private ListCheckConfig config;
	
	@OneToOne(cascade=CascadeType.ALL)
	private Report report;
	
	@OneToMany(cascade=CascadeType.ALL)
	private Map<String, UrlCheck> urlChecks = new HashMap<String, UrlCheck>();

	public long getListCheckId() {
		return listCheckId;
	}

	public void setListCheckId(long listCheckId) {
		this.listCheckId = listCheckId;
	}

	public ListCheckConfig getConfig() {
		return config;
	}

	public void setConfig(ListCheckConfig config) {
		this.config = config;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public Map<String, UrlCheck> getUrlChecks() {
		return urlChecks;
	}

}
