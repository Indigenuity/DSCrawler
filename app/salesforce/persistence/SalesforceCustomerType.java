package salesforce.persistence;

public enum SalesforceCustomerType {
	CUSTOMER, PROSPECT, FORMER_CUSTOMER, OEM, CHANNEL_PARTNER, UNKNOWN;
	
	public static SalesforceCustomerType getBySalesforceValue(String salesforceValue) {
		if("Customer".equals(salesforceValue)){
			return CUSTOMER;
		} else if("Prospect".equals(salesforceValue)){
			return PROSPECT;
		} else if("Former Customer".equals(salesforceValue)){
				return FORMER_CUSTOMER;
		} else if("OEM".equals(salesforceValue)){
				return OEM;
		} else if("Channel Partner".equals(salesforceValue)){
				return CHANNEL_PARTNER;
		} 
	
		return UNKNOWN;
	}
}
