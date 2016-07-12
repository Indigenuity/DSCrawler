package datatransfer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Report {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long reportId;
	
	@OneToMany(cascade=CascadeType.ALL)
	private Map<String, ReportRow> reportRows = new HashMap<String, ReportRow>();
	
	@ElementCollection
	private Set<String> columnLabels = new LinkedHashSet<String>();
	
	private String name = "Unnamed Report";
	private String keyColumn;
	private boolean appendDate = true;

	public long getReportId() {
		return reportId;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
	}

	public Map<String, ReportRow> getReportRows() {
		return reportRows;
	}
	
	public ReportRow addReportRow(String key, ReportRow reportRow) {
		return this.reportRows.put(key, reportRow);
	}

	public Set<String> getColumnLabels() {
		return columnLabels;
	}

	public void setColumnLabels(Set<String> columnLabels) {
		this.columnLabels.clear();
		this.columnLabels.addAll(columnLabels);
	}

	public void addColumnLabel(String columnLabel) {
		this.columnLabels.add(columnLabel);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAppendDate() {
		return appendDate;
	}

	public void setAppendDate(boolean appendDate) {
		this.appendDate = appendDate;
	}

	public String getKeyColumn() {
		return keyColumn;
	}

	public void setKeyColumn(String keyColumn) {
		this.keyColumn = keyColumn;
	}
	
	
}
