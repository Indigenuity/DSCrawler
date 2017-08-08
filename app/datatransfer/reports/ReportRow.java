package datatransfer.reports;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import utilities.DSFormatter;

@Entity
public class ReportRow {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long reportRowId;
	
	@ElementCollection
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private Map<String, String> cells = new HashMap<String, String>();

	public long getReportRowId() {
		return reportRowId;
	}

	public void setReportRowId(long reportRowId) {
		this.reportRowId = reportRowId;
	}

	public Map<String, String> getCells() {
		return cells;
	}
	
	public String getCell(String key) {
		return cells.get(key);
	}
	
	public String putCell(String key, Object value) {
		return cells.put(key, DSFormatter.truncate(value + "", 4000));
	}
	
	public void putCells(Map<String, ?> cells){
		for(Entry<String, ?> entry : cells.entrySet()){
			putCell(entry.getKey(), entry.getValue());
		}
	}

}
