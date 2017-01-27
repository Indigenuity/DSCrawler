package salesforce.persistence;

public enum SalesforceAccountType {

	GROUP, DEALER, SITE, OEM, REGIONAL, UNKNOWN;
	
	public static SalesforceAccountType getBySalesforceValue(String salesforceValue) {
		if("Franchise".equals(salesforceValue)){
			return DEALER;
		} else if("Site".equals(salesforceValue)){
			return SITE;
		} else if("OEM".equals(salesforceValue)){
				return OEM;
		} else if("Group".equals(salesforceValue)){
				return GROUP;
		} else if("Regional".equals(salesforceValue)){
				return REGIONAL;
		} 
	
		return UNKNOWN;
	}
}
