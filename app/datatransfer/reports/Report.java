package datatransfer.reports;

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
	
	public Report(String name) {
		this.setName(name);
	}
	
	public Report() {}
	
	public ReportRow getReportRow(String key){
		return reportRows.get(key);
	}

	public long getReportId() {
		return reportId;
	}

	public Report setReportId(long reportId) {
		this.reportId = reportId;
		return this;
	}

	public Map<String, ReportRow> getReportRows() {
		return reportRows;
	}
	
	public ReportRow addReportRow(String key, ReportRow reportRow) {
		return this.reportRows.put(key, reportRow);
	}

	public Report setReportRows(Map<String, ReportRow> reportRows) {
		this.reportRows.clear();
		this.reportRows.putAll(reportRows);
		return this;
	}

	public Set<String> getColumnLabels() {
		return columnLabels;
	}

	public Report setColumnLabels(Set<String> columnLabels) {
		this.columnLabels.clear();
		this.columnLabels.addAll(columnLabels);
		return this;
	}

	public Report addColumnLabel(String columnLabel) {
		this.columnLabels.add(columnLabel);
		return this;
	}

	public String getName() {
		return name;
	}

	public Report setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isAppendDate() {
		return appendDate;
	}

	public Report setAppendDate(boolean appendDate) {
		this.appendDate = appendDate;
		return this;
	}

	public String getKeyColumn() {
		return keyColumn;
	}

	public Report setKeyColumn(String keyColumn) {
		this.keyColumn = keyColumn;
		return this;
	}
	
	public Report acquireColumnNamesFromRows() {
		for(ReportRow reportRow : reportRows.values()){
			for(String columnLabel : reportRow.getCells().keySet()){
				addColumnLabel(columnLabel);
			}
		}
		return this;
	}
	
}
