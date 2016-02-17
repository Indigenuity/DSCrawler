package datadefinitions;

public enum InvalidDomain {
	CHEVRON			("chevronwithtechron.com"),
	DRIVE_TIME		("drivetime.com"),
	HONDA			("honda.com"),
	GM				("gm.com");
	
	public final String definition;
	private InvalidDomain(String definition) {
		this.definition = definition;
	}
}
