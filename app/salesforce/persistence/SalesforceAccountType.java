package salesforce.persistence;

public enum SalesforceAccountType {

	GROUP, DEALER, SITE, OEM, REGIONAL, DEALERSHIP, SEGMENT, UNKNOWN;
	
	public static SalesforceAccountType getBySalesforceValue(String salesforceValue) {
		if("Dealership".equals(salesforceValue)){
			return DEALER;
		} else if("Segment".equals(salesforceValue)){
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
