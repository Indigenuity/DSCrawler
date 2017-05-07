package pods;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dao.GeneralDAO;
import datatransfer.CSVImporter;
import datatransfer.reports.Report;
import datatransfer.reports.ReportRow;
import global.Global;
import play.db.jpa.JPA;
import utilities.DSFormatter;

public class PodsLoader {
	
	private static final Pattern CANADA_POSTAL = Pattern.compile("([a-zA-Z0-9]{3})([a-zA-Z0-9]{3})");
	
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
		
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		JPA.em().clear();
		
		System.out.println("Creating Primary Area Codes");
		createPrimaryAreaCodes();
	}
	
	private static void clearTable() {
		String query = "delete from PodZip pz";
		JPA.em().createQuery(query).executeUpdate();
	}
	
	public static void createPrimaryAreaCodes() {
		List<PodZip> podZips = GeneralDAO.getAll(PodZip.class);
		System.out.println("zips : " + podZips.size());
		Map<String, Set<PodZip>> canadaPrimaries = new HashMap<String, Set<PodZip>>();
		int count =0;
		for(PodZip podZip : podZips){
			Matcher matcher = CANADA_POSTAL.matcher(podZip.getPostalCode());
			if(matcher.find()){
				String primary = matcher.group(1);
				Set<PodZip> secondaries = canadaPrimaries.get(primary);
				if(secondaries == null){
					secondaries = new HashSet<PodZip>();
					canadaPrimaries.put(primary, secondaries);
				}
				secondaries.add(podZip);
			}
			count++;
			if(count != 0 && count %50000 == 0){
				System.out.println("count : " + count);
			}
		}
		System.out.println("canadaPrimaries : " + canadaPrimaries.entrySet().size());
		int crossPodPostals = 0;
		for(Entry<String, Set<PodZip>> entry : canadaPrimaries.entrySet()){
			if(entry.getValue().size() == 1){
				System.out.println("primary : " + entry.getKey());
				PodZip primaryPodZip = new PodZip();
				PodZip secondaryPodZip = (PodZip)entry.getValue().toArray()[0];
				
				primaryPodZip.setFranchisePod(secondaryPodZip.getFranchisePod());
				primaryPodZip.setIndyPod(secondaryPodZip.getIndyPod());
				primaryPodZip.setPostalCode(entry.getKey());
				primaryPodZip.setPrimaryAreaCode(secondaryPodZip.getPrimaryAreaCode());
				JPA.em().persist(primaryPodZip);
				
//				crossPodPostals++;
				
			}
		}
		System.out.println("crossPodPostals : " + crossPodPostals);
	}

}
