package datadefinitions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


public enum StringExtraction implements StringMatch{
	
	EMAIL_ADDRESS 			(1, "EMAIL_ADDRESS", "[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})", ""),
//	GOOGLE_ANALYTICS_CODE 	(2, "GOOGLE_ANALYTICS_CODE", "\\bUA-\\d{4,10}-\\d{1,4}\\b", "");
	STATE_FULL				(3, "Full State Name", "(?i)\\b(Alabama|Alaska|Arizona|Arkansas|California|Colorado|Connecticut|Delaware|Florida|Georgia|Hawaii|Idaho|Illinois|Indiana|Iowa|Kansas|Kentucky|Louisiana|Maine|Maryland|Massachusetts|Michigan|Minnesota|Mississippi|Missouri|Montana|Nebraska|Nevada|Hampshire|Jersey|New-Mexico|York|Ohio|Oklahoma|Oregon|Pennsylvania|Rhode-Island|Carolina|Dakota|Tennessee|Texas|Utah|Vermont|Virginia|Washington|WestVirginia|Wisconsin|Wyoming)\\b", ""),
	STATE_ABBR				(4, "Abbreviated State Name", "(?i)\\b(AL|AK|AZ|AR|CA|CO|CT|DE|FL|GA|HI|ID|IL|IN|IA|KS|KY|LA|ME|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|VT|VA|WA|WV|WI|WY)\\b", ""),
	MAKE					(5, "Matches makes of most common OEM", "(?i)\\b(acura|audi|aston|bentley|bmw|cadillac|chrysler|chevrolet|chevy|dodge|ferrari|fiat|ford|gm|gmc|honda|hummer|hyundai|infiniti|jeep|jaguar|kia|lamborghini|landrover|lexus|lincoln|mercury|lotus|maserati|mazda|mclaren|mercedes|mini|mitsubishi|nissan|porsche|rollsroyce|scion|smart|subaru|toyota|volvo|volkswagen|vw)\\b", ""),
	CITY					(6, "Grabs city from addresses", ",([^,]+)[^a-zA-Z]+" + STATE_ABBR.getDefinition(), "Only works on formatted addresses with a comma before and after a city, followed by a state abbreviation");
	
	
	
	private int id;
	private String description;
	private String definition;
	private String notes;
	public final Set<StringMatch> offsetMatches = new HashSet<StringMatch>();
	private Pattern pattern;
	
	private final static Map<Integer, StringExtraction> enumIds = new HashMap<Integer, StringExtraction>();
	static {
		for(StringExtraction sm : StringExtraction.values()){
			enumIds.put(sm.getId(), sm);
		}
	}
	
	public static StringExtraction getTypeFromId(Integer id) {
		return enumIds.get(id);
	}
	
	private StringExtraction(int id, String description, String definition, String notes){
		this.id = id;
		this.description = description;
		this.definition = definition;
		this.notes = notes;
		this.pattern = Pattern.compile(definition);
	}
	
	private StringExtraction(int id, String description, String definition, String notes, Set<StringMatch> offsetMatches){
		this.id = id;
		this.description = description;
		this.definition = definition;
		this.notes = notes;
		this.pattern = Pattern.compile(definition);
		this.offsetMatches.addAll(offsetMatches);
	}
	
	public StringExtraction getType(Integer id) {
		return getTypeFromId(id); 
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public String getDefinition() {
		return this.definition;
	}
	
	public String getNotes() { 
		return this.notes;
	}
	
	public Pattern getPattern() {
		return this.pattern;
	}
	public Set<StringMatch> getOffsetMatches(){
		return this.offsetMatches;
	}
}
