package datadefinitions.newdefinitions;

import datadefinitions.StringMatch;

public enum SharedDomain implements StringMatch {
	_509_AUTOS					("509autos.com"),
	A_MILE_OF_CARS				("amileofcars.com"),
	ABILITY_CENTER				("abilitycenter.com"),
	AUDI_CA						("audi.ca"),
	AUTO_SHOPPER				("autoshopper.com"),
	AUTO_TRADER					("autotrader.com"),
	AUTO_TRADER_CA				("autotrader.ca"),
	BILLION_AUTO				("billionauto.com"),
	BIRD_NOW					("birdnow.com"),
	BRENENGEN					("brenengen.com"),
	CAMPERS_INN					("campersinn.com"),
	CAR_OUTLET					("caroutlet.com"),
	CARS_ONLINE_FREE			("carsonlinefree.com"),
	CONTINENTAL_AUTO_GROUP		("continentalautogroup.com"),
	DRIVE_REINEKE				("drivereineke.com"),
	FINCH						("seefinchfirst.com"),
	FRASER_WAY					("fraserway.com"),
	HEFFNER						("heffner.ca"),
	HONDRU_AUTO					("hondruauto.com"),
	JOHN_BEAR					("johnbear.com"),
	JOHNSON_RV					("johnsonrv.com"),
	KIJIJI						("kijiji.ca"),
	MASERATI					("dealers.maserati.com"),
	MEMBERS_AUTO_CHOICE			("membersautochoice.com"),
	NEW_ROADS					("newroads.ca"),
	PERFORMANCE_MOBILITY		("performancemobility.com"),
	RENT_A_WRECK				("rentawreck.com"),
	SAWICKI_MOTORS				("sawickimotors.com"),
	THRIFTY_CAR_SALES			("thriftycarsales.com"),
	VEHICLES_WHEELS_CA			("vehicles.wheels.ca"),
	WHEELS_CA					("wheels.ca"),
	
	;
	
	public final String definition;
	private SharedDomain(String definition) {
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