package datatransfer.reports;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import scaffolding.Scaffolder;

public class ReportFactory {
	
	//Relies on hashcodes being unique
	public static Report fromGenericSet(Set<?> items) {
		Report report = new Report();
		report.setReportRows(items.stream().collect(Collectors.toMap( (item) -> item.hashCode() + "", ReportFactory::fromObject)));
		return report;
	}

	public static Report fromEntityCollection(Collection<?> items) {
		Report report = new Report();
		report.setReportRows(items.stream().collect(Collectors.toMap((item) -> {return Scaffolder.getId(item) + "";}, ReportFactory::fromObject)));
		return report;
	}
	
	public static ReportRow fromObject(Object item){
		Map<String, Object> fields = Scaffolder.getSimpleFields(item);
		ReportRow reportRow = new ReportRow();
		for(Entry<String, Object> entry : fields.entrySet()){
			reportRow.putCell(entry.getKey(), entry.getValue() + "");
		}
		return reportRow;
	}
	
	public static <K> Report fromGenericKeyMap(Map<K, ReportRow> reportRows){
		Report report = new Report();
		for(Entry<K, ReportRow> entry : reportRows.entrySet()){
			report.addReportRow(entry.getKey() + "", entry.getValue());
		}
		return report;
	}
	
	public static Report contrastReports(Report firstReport, Report secondReport) {
		Report report = new Report();
				
		for(Entry<String, ReportRow> entry : firstReport.getReportRows().entrySet()){
			ReportRow firstRow = entry.getValue();
			ReportRow secondRow = secondReport.getReportRows().get(entry.getKey());
			
			ReportRow newFirst = new ReportRow();
			ReportRow newSecond = new ReportRow();
			for(Entry<String, String> cellEntry : firstRow.getCells().entrySet()){
				String firstCell = cellEntry.getValue();
				String secondCell = secondRow.getCell(cellEntry.getKey());
				
				//Keep the key value in the row
				if(StringUtils.equals(cellEntry.getValue(), entry.getKey())){
					newFirst.putCell(cellEntry.getKey(), firstCell  + "old");
					newSecond.putCell(cellEntry.getKey(), secondCell + "new");
				}
				
				//Copy rows with only differing values.  Rows with no differing values will never be copied.
				if(!StringUtils.equals(firstCell, secondCell)){
					newFirst.putCell(cellEntry.getKey(), firstCell);
					newSecond.putCell(cellEntry.getKey(), secondCell);
					report.addReportRow(entry.getKey() + "old", newFirst);
					report.addReportRow(entry.getKey() + "new", newSecond);
				} 
			}
		}
		return report;
	}
}
