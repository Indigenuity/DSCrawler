package datatransfer;


import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import datatransfer.reports.Report;
import datatransfer.reports.ReportRow;
import places.CanadaPostal;
import play.db.jpa.JPA;

public class CSVImporter {
	
	public static Report importReport(String filename) throws IOException {
		List<Map<String, String>> records = recordsFromCSV(filename);
		String keyColumn = records.get(0).keySet().toArray()[0].toString();
		return reportFromRecords(records, keyColumn);
	}
	
	public static Report importReportWithKey(String filename, String keyColumn) throws IOException {
		List<Map<String, String>> records = recordsFromCSV(filename);
		return reportFromRecords(records, keyColumn);
	}
	
	public static List<Map<String, String>> recordsFromCSV(String filename) throws IOException {
		Reader in = new FileReader(filename);
		List<CSVRecord> csvRecords = CSVFormat.EXCEL.withHeader().parse(in).getRecords();
		List<Map<String, String>> records = new ArrayList<Map<String, String>>();
//		int count = 0;
		for(CSVRecord csvRecord : csvRecords) {
			records.add(csvRecord.toMap());
//			if(count++ > 10){
//				break;
//			}
		}
		in.close();
		return records;
	}
	
	public static Report reportFromRecords(List<Map<String, String>> records, String keyColumn) {
		Report report = new Report();
		report.setColumnLabels(records.get(0).keySet());
		report.setKeyColumn(keyColumn);
		for(int i = 1; i < records.size(); i++){ 	//Skip header
			ReportRow reportRow = new ReportRow();
			Map<String, String> record = records.get(i);
			for(String columnLabel : report.getColumnLabels()){
				reportRow.putCell(columnLabel, record.get(columnLabel));
			}
			report.addReportRow(record.get(report.getKeyColumn()), reportRow);
		}
		
		return report;
	}
	
	
	public static void importCanadaPostals() throws IOException {
		Reader in = new FileReader("C:\\Workspace\\DSStorage\\source/ca_postal_codes.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		
		CanadaPostal row;
		for(CSVRecord record : records) {
			row = new CanadaPostal();
			
			row.latitude = Double.valueOf(record.get("Latitude"));
			row.longitude= Double.valueOf(record.get("Longitude"));
			row.code = record.get("Postal Code");
			row.name = record.get("Place Name");
			row.province = record.get("Province");
			JPA.em().persist(row);
		}
	}
	
}
