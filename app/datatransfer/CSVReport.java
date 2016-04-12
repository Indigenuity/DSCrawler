package datatransfer;

import java.util.ArrayList;
import java.util.List;

public class CSVReport {

	private List<String[]> rows = new ArrayList<String[]>();
	private String[] headerValues;
	private String name = "Unnamed Report";
	private boolean appendDate = true;
	
	public CSVReport(){}
	
	public CSVReport(String name){
		this.name = name;
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

	public List<String[]> getRows() {
		return rows;
	}

	public void setRows(List<String[]> rows) {
		this.rows = rows;
	}
	
	public void addRow(String[] row){
		this.rows.add(row);
	}
	public void addRow(List<String> row) {
		rows.add((String[])row.toArray(new String[row.size()]));
	}

	public String[] getHeaderValues() {
		return headerValues;
	}

	public void setHeaderValues(String[] headerValues) {
		if(this.headerValues != null){
			return;
		}
		this.headerValues = headerValues;
		this.rows.add(this.headerValues);
	}
	public void setHeaderValues(List<String> headerValues) {
		if(this.headerValues != null){
			return;
		}
		this.headerValues = (String[])headerValues.toArray(new String[headerValues.size()]);
		this.rows.add(this.headerValues);
	}
}
