package audit.sync;

import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import audit.AuditDao;
import datatransfer.CSVGenerator;
import datatransfer.reports.Report;
import datatransfer.reports.ReportFactory;
import datatransfer.reports.ReportRow;
import play.db.jpa.JPA;

public class SyncControl {

	public static <T> void generateAllReports(Class<T> clazz, Sync sync) throws IOException {
		Integer revisionNumber = AuditDao.getRevisionOfSync(sync);
		List<T> added = AuditDao.getInsertedAtRevision(clazz, revisionNumber, 20, 0);
		List<T> deleted = AuditDao.getDeletedAtRevision(clazz, revisionNumber, 20, 0);
		List<T> modified = AuditDao.getUpdatedAtRevision(clazz, revisionNumber, 20, 0);
		
		Report addedReport = ReportFactory.fromEntityCollection(added);
		addedReport.setName(sync.getSyncType() + " Added at sync (" + sync.getSyncId() + ")");
		Report deletedReport = ReportFactory.fromEntityCollection(deleted);
		deletedReport.setName(sync.getSyncType() + " Deleted at sync (" + sync.getSyncId() + ")");
		Report modifiedReport = ReportFactory.fromEntityCollection(modified);
		modifiedReport.setName(sync.getSyncType() + " Modified at sync (" + sync.getSyncId() + ")");
		
		Report summaryReport = new Report();
		summaryReport.setName("Summary Report for sync (" + sync.getSyncId() + ")");
		ReportRow reportRow = new ReportRow();
		summaryReport.addReportRow("", reportRow);
		reportRow.putCell("Total Added", added.size() + "");
		reportRow.putCell("Total Deleted", deleted.size() + "");
		reportRow.putCell("Total Modified", modified.size() + "");
		
		CSVGenerator.printReport(addedReport);
		CSVGenerator.printReport(deletedReport);
		CSVGenerator.printReport(modifiedReport);
	}
	
	
	
}
