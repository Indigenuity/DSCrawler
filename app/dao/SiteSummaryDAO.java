package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import persistence.SiteSummary;

public class SiteSummaryDAO {

	protected static final SiteSummaryDAO instance = new SiteSummaryDAO();
	
	private List<String[]> CSVRows;
	
	protected SiteSummaryDAO () {
		
	}
	
	public void addCSVRow(SiteSummary summary) {
		
	}
	
	public void startNewCSV(){
		CSVRows = new ArrayList<String[]>();
	}
	
	public static SiteSummaryDAO instance(){ 
		return instance;
	}
	
	public static void refreshSchema(Connection connection) throws SQLException {
		System.out.println("Refreshing schema for SiteSummary");
		System.out.println("Creating table if necessary");
		//Create table
		Statement createStatement = connection.createStatement();
		String query = "create table if not exists SiteSummary(SUMMARY_ID bigint auto_increment primary key)";
		createStatement.executeUpdate(query);
		
		//Fill columns
		System.out.println("Adding columns if necessary");
		query = "{call AddColumnUnlessExists(?,?,?)}";
		CallableStatement statement = connection.prepareCall(query);
		
		statement.setString(1, "SiteSummary");
		statement.setString(2, "SITE_ID");
		statement.setString(3, "bigint not null");
		statement.executeUpdate();
		statement.setString(2, "SITE_NAME");
		statement.setString(3, "varchar(255)");
		statement.executeUpdate();
		statement.setString(2, "SITE_URL");
		statement.setString(3, "varchar(300) not null");
		statement.executeUpdate();
		statement.setString(2, "NIADA_ID");
		statement.setString(3, "varchar(15)");
		statement.executeUpdate();
		statement.setString(2, "CAPDB_ID");
		statement.setString(3, "varchar(15)");
		statement.executeUpdate();
		statement.setString(2, "GIVEN_ADDRESS");
		statement.setString(3, "varchar(100)");
		statement.executeUpdate();
		statement.setString(2, "GIVEN_URL");
		statement.setString(3, "varchar(300)");
		statement.executeUpdate();
		statement.setString(2, "INTERMEDIATE_URL");
		statement.setString(3, "varchar(300)");
		statement.executeUpdate();
		statement.setString(2, "CRAWL_FROM_GIVEN_URL");
		statement.setString(3, "boolean default false");
		statement.executeUpdate();
		statement.setString(2, "CRAWL_DATE");
		statement.setString(3, "datetime");
		statement.executeUpdate();
		statement.setString(2, "GIVEN_EMAIL");
		statement.setString(3, "varchar(100)");
		statement.executeUpdate();
		statement.setString(2, "REDIRECT_URL");
		statement.setString(3, "varchar(300)");
		statement.executeUpdate();
		statement.setString(2, "URL_REQUIRES_REVIEW");
		statement.setString(3, "boolean default false");
		statement.executeUpdate();
		statement.setString(2, "FRANCHISE");
		statement.setString(3, "boolean default false");
		statement.executeUpdate();
		statement.setString(2, "CRAWL_STORAGE_FOLDER");
		statement.setString(3, "varchar(300)");
		statement.executeUpdate();
		
		System.out.println("Adding constraints if necessary");
		//Set Constraints
		statement.close();
		query = "{call AddConstraintUnlessExists(?,?,?,?)}";
		statement = connection.prepareCall(query);
		statement.setString(1, "SiteSummary");
		statement.setString(2, "SUMMARY_SITE_ID_REFERENCE");
		statement.setString(3, "FOREIGN KEY");
		statement.setString(4, "(SITE_ID) references SiteInformation(SITE_ID)");
		statement.executeUpdate();
		statement.close();
	}
}
