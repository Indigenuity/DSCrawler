package persistence.salesforce;

public enum DealershipType {
	FRANCHISE, INDEPENDENT, POWER_SPORTS, COMMERCIAL_TRUCK, PENTANA, RV, UNKNOWN;
	
	public static DealershipType getBySalesforceValue(String salesforceValue) {
		if("Franchise".equals(salesforceValue)){
			return FRANCHISE;
		} else if("Independent".equals(salesforceValue)){
			return INDEPENDENT;
		} else if("Commercial Truck".equals(salesforceValue)){
				return COMMERCIAL_TRUCK;
		} else if("Pentana".equals(salesforceValue)){
				return PENTANA;
		} else if("RV".equals(salesforceValue)){
				return RV;
		} else if("Power Sports".equals(salesforceValue)){
				return POWER_SPORTS;
		}
	
		return UNKNOWN;
	}
}
