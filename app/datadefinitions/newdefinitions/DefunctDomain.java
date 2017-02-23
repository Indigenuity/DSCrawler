package datadefinitions.newdefinitions;

import java.util.regex.Pattern;

import datadefinitions.StringMatch;

public enum DefunctDomain implements StringMatch {
	
	// .*//b makes sure the domain only matches on these exact domains.  E.g. .*\\bhonda.com.* won't match http://www.arrowheadhonda.com 
	
	CHEVRON				("chevronwithtechron.com"),
	CHEVY_DEALER		("chevydealerlocator.com"),
	DRIVE_TIME			("drivetime.com"),
	HONDA				("honda.com"),
	CLICK_MOTIVE		("clickmotive.com"),
	DEALER_CAR_SEARCH	("dealercarsearch.com"),
	GM_DEALER_LOCATOR	("gmdealerlocator.com"),
	HUGE_DOMAINS		("hugedomains.com"),
	GM					("gm.com");
	
	public final String definition;
	private DefunctDomain(String definition) {
		this.definition = definition;
	}
	public String getDefinition() {
		return definition;
	}
	@Override
	public String getDescription() {
		return "This signifies an expired domain name";
	}
	@Override
	public String getNotes() {
		return "This signifies an expired domain name";
	}
	
	
}