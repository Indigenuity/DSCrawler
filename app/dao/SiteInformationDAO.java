package dao;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;

import analysis.SiteAnalyzer;
import analysis.SiteSummarizer;
import persistence.PageInformation;
import persistence.SiteInformationOld;
import persistence.SiteSummary;
import play.Logger;
import play.db.DB;
import utilities.DSFormatter;

public class SiteInformationDAO {
	
	//Urls given as raw are often bad urls.  This will be the first step in getting the right url
	public static void reviewUrls(Connection connection) throws SQLException {
		Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		String query = "select SITE_ID, GIVEN_URL, INTERMEDIATE_URL, REDIRECT_URL, SITE_URL, URL_REQUIRES_REVIEW from SiteInformation";
		ResultSet rs = statement.executeQuery(query);
		
		String given;
		String intermediate;
		while(rs.next()) {
			given = rs.getString("GIVEN_URL");
			System.out.println("Given : " + given);
			
			//Take care of empty urls
			if(given == null || given.equals("")) {
				intermediate = "";
			}
			//Take care of urls without http://
			else {
				intermediate = DSFormatter.toHttp(given);
			}
			//Take off query strings
			intermediate = DSFormatter.removeQueryString(intermediate);
			
			//Mark URLs with more than the domain for review, unmark others
			rs.updateBoolean("URL_REQUIRES_REVIEW", DSFormatter.isApprovedUrl(intermediate));
			
			rs.updateString("INTERMEDIATE_URL", intermediate);
			rs.updateRow();
		}
		rs.close();
		statement.close(); 
	}
	
}
