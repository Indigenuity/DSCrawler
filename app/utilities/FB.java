package utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.db.DB;

public class FB {
	
	public static final String NOT_A_PAGE = "Not a Page";
	
	public static final String SHARER = "sharer.php";
	public static final String FACEBOOK_LEGAL = "legal/terms";
	public static final String FACEBOOK_PRINCIPLES = "principles.php";
	public static final String FACEBOOK_DIRECTORY = "directory/";
	public static final String LOGIN = "login/";
	public static final String PHP = ".php";
	
	public static final Pattern SIMPLE_URL = Pattern.compile("(?<=facebook.com/)[^/]+$");
	
	public static final String PAGES = "pages/";
	
	public static String getIdentifier(String url) {
		String identifier = NOT_A_PAGE;
//		url = url.toLowerCase();
		if(url.contains(SHARER) || url.contains(FACEBOOK_LEGAL) || url.contains(FACEBOOK_PRINCIPLES) ||
				url.contains(FACEBOOK_DIRECTORY) || url.contains(LOGIN) || url.contains(PHP)){
			return NOT_A_PAGE;
		}
		
		url = UrlUtils.removeQueryString(url);
		
		
		int lastIndex = url.lastIndexOf("/");
//		System.out.println("last index : " + lastIndex);
		if(url.contains(PAGES)){
			identifier = DSFormatter.getLastSegment(url);
		}
		else{
			Matcher matcher = SIMPLE_URL.matcher(url);
			if(matcher.find()){
				identifier = matcher.group(0);
			}
		}
		identifier = identifier.trim();
		if(DSFormatter.isEmpty(identifier)){
			return NOT_A_PAGE;
		}
		return identifier;
	}
	
	public static boolean isAlreadyPresent(String givenUrl) throws SQLException   {
//		System.out.println("checking given url  : " + givenUrl);
		Connection connection = DB.getConnection();
		String query = "select fbPageId from FBPage where givenUrl = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, givenUrl);
		ResultSet rs = statement.executeQuery();
		
		boolean present = false;
		if(rs.first()){
			present = true;
		}
		
		rs.close();
		connection.close();
		return present;
	}
}
