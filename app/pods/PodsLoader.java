package pods;

import java.io.IOException;
import java.util.Map.Entry;

import datatransfer.CSVImporter;
import datatransfer.reports.Report;
import datatransfer.reports.ReportRow;
import global.Global;
import play.db.jpa.JPA;
import utilities.DSFormatter;

public class PodsLoader {
	
	public static void loadFromCsv() throws IOException{
		System.out.println("Clearing out pods table ");
		clearTable();
		System.out.println("Loading new Pods");
		String filename = Global.getInputFolder() + "/Primary Area Field Update Reference File.csv";
		Report podReport = CSVImporter.importReportWithKey(filename, "PostalCode");
		System.out.println("rows : " + podReport.getReportRows().keySet().size());
		int count = 0;
		for(Entry<String, ReportRow> entry: podReport.getReportRows().entrySet()){
			PodZip pz = new PodZip();
			pz.setPostalCode(DSFormatter.standardizeZip(entry.getValue().getCell("PostalCode")));
			pz.setProvinceName(entry.getValue().getCell("ProvinceName"));
			pz.setAreaCode(entry.getValue().getCell("AreaCode"));
			pz.setPrimaryAreaCode(entry.getValue().getCell("Primary Area"));
			pz.setIndyPod(entry.getValue().getCell("Pod - Indy"));
			pz.setFranchisePod(entry.getValue().getCell("Pod - Fran."));
			JPA.em().persist(pz);
			if(count++ %500 == 0){
				System.out.println("count : " + count++);
				JPA.em().getTransaction().commit();
				JPA.em().getTransaction().begin();
				JPA.em().clear();
			}
		}
	}
	
	private static void clearTable() {
		String query = "delete from PodZip pz";
		JPA.em().createQuery(query).executeUpdate();
	}

}
