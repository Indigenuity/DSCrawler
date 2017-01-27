package crawling.nydmv;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;

import akka.actor.ActorRef;
import async.async.Asyncleton;
import audit.Distance;
import audit.ListMatchResult;
import audit.ListMatcher;
import datatransfer.CSVGenerator;
import datatransfer.reports.Report;
import datatransfer.reports.ReportRow;
import play.db.jpa.JPA;
import salesforce.persistence.SalesforceAccount;
import tyrex.services.UUID;

public class NyControl {
	
	
	public static void runLinks() {
		String queryString = "from NYDealer ny where ny.linkStatus = 'No Link'";
		List<NYDealer> nyDealers= JPA.em().createQuery(queryString, NYDealer.class).setMaxResults(13000).getResultList();
		
		ActorRef master = Asyncleton.getInstance().getGenericMaster(5, NyLinkWorker.class);
		for(NYDealer nyDealer : nyDealers) {
			NyLinkWorkOrder workOrder = new NyLinkWorkOrder(nyDealer.getNyDealerId());
			master.tell(workOrder, ActorRef.noSender());
		}
		
		
	}

	public static void matchNyDealers() throws IOException {
		System.out.println("starting comparison");
		String queryString = "from SalesforceAccount sa";
		List<SalesforceAccount> sfAccounts = JPA.em().createQuery(queryString, SalesforceAccount.class).getResultList();
		System.out.println("sfAccounts : " + sfAccounts.size());
		
		queryString = "from NYDealer nyd";
		List<NYDealer> nyDealers= JPA.em().createQuery(queryString, NYDealer.class).getResultList();
		System.out.println("nyDealers : " + nyDealers.size());
		
		BiFunction<NYDealer, SalesforceAccount, Double> distanceFunction = (sfAccount, nyDealer) -> {
			if(sfAccount.getStandardStreet() == null || nyDealer.getStandardStreet() == null){
				return null;
			}
			
			Double distance = StringUtils.getJaroWinklerDistance(sfAccount.getStandardStreet(), nyDealer.getStandardStreet());
			if(distance > .9){
				return distance;
			}
			return null;
		};
		
		BiFunction<NYDealer, SalesforceAccount, Boolean> equalityFunction = (sfAccount, nyDealer) -> {
			if(sfAccount.getStandardStreet() == null || nyDealer.getStandardStreet() == null){
				return false;
			}
			return StringUtils.equals(sfAccount.getStandardStreet(), nyDealer.getStandardStreet());
		};
		
		ListMatchResult<NYDealer, SalesforceAccount, Double> listMatch = ListMatcher.compareLists(nyDealers, sfAccounts, equalityFunction, distanceFunction);
		Report report = new Report("Salesforce matches with NY dealers");
		
		for(Entry<NYDealer, List<Distance<SalesforceAccount, Double>>> entry: listMatch.getDistances().entrySet()){
			System.out.println("nyDealer: " +  entry.getKey());
			for(Distance<SalesforceAccount, Double> distance : entry.getValue()){
				System.out.println("\t sfAccount : " + distance.getItem() + " : " + distance.getDistance());
				ReportRow reportRow = new ReportRow();
				reportRow.putCell("sfId", distance.getItem().getSalesforceId());
				reportRow.putCell("sfAccountId", distance.getItem().getSalesforceAccountId() + "");
				reportRow.putCell("sfName", distance.getItem().getName());
				reportRow.putCell("sfStreet", distance.getItem().getStandardStreet());
				reportRow.putCell("nyDealerId", entry.getKey().getNyDealerId() + "");
				reportRow.putCell("nyDealerName", entry.getKey().getFacilityName());
				reportRow.putCell("nyDealerStreet", entry.getKey().getStandardStreet());
				reportRow.putCell("matchType", "JaroWinklerDistance");
				reportRow.putCell("distance", distance.getDistance() + "");
				report.addReportRow(UUID.create(), reportRow);
			}
		}
		
		for(Entry<NYDealer, List<SalesforceAccount>> entry: listMatch.getMatches().entrySet()){
			NYDealer entryDealer = entry.getKey();
			List<SalesforceAccount> entryAccounts = entry.getValue();
			if(entryAccounts.size() == 1) {
				entryDealer.setLinkStatus("Single SalesforceAccount Match");
				entryDealer.setSfAccount(entryAccounts.get(0));
			} else if(entryAccounts.size() > 1){
				entryDealer.setLinkStatus("Multiple SalesforceAccount Matches");
			} else {
				entryDealer.setLinkStatus("No SalesforceAccount Matches");
			}
			for(SalesforceAccount sfAccount: entry.getValue()){
				ReportRow reportRow = new ReportRow();
				reportRow.putCell("sfId", sfAccount.getSalesforceId());
				reportRow.putCell("sfAccountId", sfAccount.getSalesforceAccountId() + "");
				reportRow.putCell("sfName", sfAccount.getName());
				reportRow.putCell("sfStreet", sfAccount.getStandardStreet());
				reportRow.putCell("nyDealerId", entry.getKey().getNyDealerId() + "");
				reportRow.putCell("nyDealerName", entry.getKey().getFacilityName());
				reportRow.putCell("nyDealerStreet", entry.getKey().getStandardStreet());
				reportRow.putCell("matchType", "exact");
				report.addReportRow(UUID.create(), reportRow);
			}
		}
		
		for(NYDealer nyDealer : listMatch.getNoMatchFirst()) {
			nyDealer.setLinkStatus("No SalesforceAccount Matches");
			ReportRow reportRow = new ReportRow();
			reportRow.putCell("nyDealerId", nyDealer.getNyDealerId() + "");
			reportRow.putCell("nyDealerName", nyDealer.getFacilityName());
			reportRow.putCell("nyDealerStreet", nyDealer.getStandardStreet());
			reportRow.putCell("matchType", "no match");
			report.addReportRow(UUID.create(), reportRow);
		}
		
		CSVGenerator.printReport(report);	
	}
}
