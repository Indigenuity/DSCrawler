package datadefinitions;

import java.util.regex.Pattern;

public enum OEM {

	ACURA			("acura"),
	AUDI			("audi"),
	ASTON_MARTIN	("aston"),
	BENTLEY			("bentley"),
	BMW				("bmw"),
	BUICK			("buick"),
	CADILLAC		("cadillac"),
	CHRYSLER		("chrysler"),
	CHEVROLET		("chevrolet"),
	CHEVY			("chevy"),
	CJD				("cjd"),
	DODGE			("dodge"),
	FERRARI			("ferrari"),
	FIAT			("fiat"),
	FORD			("ford"),
	GM				("gm"),
	GMC				("gmc"),
	HONDA			("honda"),
	HUMMER			("hummer"),
	HYUNDAI			("hyundai"),
	INFINITI		("infiniti"),
	JEEP			("jeep"),
	JAGUAR			("jaguar"),
	KIA				("kia"),
	LAMBORGHINI		("lamborghini"),
	LAND_ROVER		("landrover"),
	LEXUS			("lexus"),
	LINCOLN			("lincoln"),
	MERCURY			("mercury"),
	LOTUS			("lotus"),
	MASERATI		("maserati"),
	MAZDA			("mazda"),
	MCLAREN			("mclaren"),
	MERCEDES		("mercedes"),
	MINI			("mini"),
	MITSUBISHI		("mitsubishi"),
	NISSAN			("nissan"),
	PORSCHE			("porsche"),
	ROLLS_ROYCE		("rollsroyce"),
	SCION			("scion"),
	SMART			("smart"),
	SUBARU			("subaru"),
	TOYOTA			("toyota"),
	VOLVO			("volvo"),
	VOLKSWAGEN		("volkswagen"),
	VW				("vw");
	
	
	private final String definition;
	private final Pattern pattern; 
	
	private OEM(String definition) {
		this.definition = definition;
		pattern = Pattern.compile("(?i)\\b" + definition + "\\b");
	}

	public String getDefinition() {
		return definition;
	}

	public Pattern getPattern() {
		return pattern;
	}
	
	
	
	
}
