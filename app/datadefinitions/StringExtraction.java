package datadefinitions;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


public enum StringExtraction implements StringMatch{
	
	EMAIL_ADDRESS 			("EMAIL_ADDRESS", "[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})", ""),
//	GOOGLE_ANALYTICS_CODE 	("GOOGLE_ANALYTICS_CODE", "\\bUA-\\d{4,10}-\\d{1,4}\\b", "");
	STATE_FULL				("Full State Name", "(?i)\\b(Alabama|Alaska|Arizona|Arkansas|California|Colorado|Connecticut|Delaware|Florida|Georgia|Hawaii|Idaho|Illinois|Indiana|Iowa|Kansas|Kentucky|Louisiana|Maine|Maryland|Massachusetts|Michigan|Minnesota|Mississippi|Missouri|Montana|Nebraska|Nevada|Hampshire|Jersey|New-Mexico|York|Ohio|Oklahoma|Oregon|Pennsylvania|Rhode-Island|Carolina|Dakota|Tennessee|Texas|Utah|Vermont|Virginia|Washington|WestVirginia|Wisconsin|Wyoming)\\b", ""),
	STATE_ABBR				("Abbreviated State Name", "(?i)\\b(AL|AK|AZ|AR|CA|CO|CT|DE|FL|GA|HI|ID|IL|IN|IA|KS|KY|LA|ME|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|VT|VA|WA|WV|WI|WY)\\b", ""),
	MAKE					("Matches makes of most common OEM", "(?i)\\b(acura|audi|aston|bentley|bmw|cadillac|chrysler|chevrolet|chevy|dodge|ferrari|fiat|ford|gm|gmc|honda|hummer|hyundai|infiniti|jeep|jaguar|kia|lamborghini|landrover|lexus|lincoln|mercury|lotus|maserati|mazda|mclaren|mercedes|mini|mitsubishi|nissan|porsche|rollsroyce|scion|smart|subaru|toyota|volvo|volkswagen|vw)\\b", ""),
	CITY					("Grabs city from addresses", ",([^,]+)[^a-zA-Z]+" + STATE_ABBR.getDefinition(), "Only works on formatted addresses with a comma before and after a city, followed by a state abbreviation"),
	POPULOUS_US_CITIES		("Contains names of most populous cities in the US", "(?i)\\b(New[ -]York|Los[ -]Angeles|Chicago|Houston|Philadelphia|Phoenix|San[ -]Antonio|San[ -]Diego|Dallas|San[ -]Jose|Austin|Jacksonville|San[ -]Francisco|Indianapolis|Columbus|Fort[ -]Worth|Charlotte|Seattle|Denver|El[ -]Paso|Detroit|Washington|Boston|Memphis|Nashville|Portland|Oklahoma[ -]City|Las[ -]Vegas|Baltimore|Louisville|Milwaukee|Albuquerque|Tucson|Fresno|Sacramento|Kansas[ -]City|Long[ -]Beach|Mesa|Atlanta|Colorado[ -]Springs|Virginia[ -]Beach|Raleigh|Omaha|Miami|Oakland|Minneapolis|Tulsa|Wichita|New[ -]Orleans|Arlington|Cleveland|Bakersfield|Tampa|Aurora|Honolulu|Anaheim|Santa[ -]Ana|Corpus[ -]Christi|Riverside|St[ -]Louis|Lexington|Stockton|Pittsburgh|Saint[ -]Paul|Anchorage|Cincinnati|Henderson|Greensboro|Plano|Newark|Toledo|Orlando|Chula[ -]Vista|Jersey[ -]City|Chandler|Fort[ -]Wayne|Buffalo|Durham|St[ -]Petersburg|Irvine|Laredo|Lubbock|Madison|Gilbert|Norfolk|Reno|Winston–Salem|Glendale|Hialeah|Garland|Scottsdale|Irving|Chesapeake|North[ -]Las[ -]Vegas|Fremont|Baton[ -]Rouge|Richmond|Boise|San[ -]Bernardino|Spokane|Birmingham|Modesto|Des[ -]Moines|Rochester|Fontana|Oxnard|Moreno[ -]Valley|Fayetteville|Huntington[ -]Beach|Yonkers|Glendale|Aurora|Montgomery|Columbus|Amarillo|Little[ -]Rock|Akron|Shreveport|Augusta|Grand[ -]Rapids|Salt[ -]Lake[ -]City|Huntsville|Tallahassee|Grand[ -]Prairie|Overland[ -]Park|Knoxville|Worcester|Brownsville|Newport[ -]News|Santa[ -]Clarita|Port[ -]St[ -]Lucie|Providence|Fort[ -]Lauderdale|Chattanooga|Tempe|Oceanside|Garden[ -]Grove|Rancho[ -]Cucamonga|Cape[ -]Coral|Santa[ -]Rosa|Vancouver|Sioux[ -]Falls|Peoria|Ontario|Jackson|Elk[ -]Grove|Springfield|Pembroke[ -]Pines|Salem|Corona|Eugene|McKinney|Fort[ -]Collins|Lancaster|Cary|Palmdale|Hayward|Salinas|Frisco|Springfield|Pasadena|Macon|Alexandria|Pomona|Lakewood|Sunnyvale|Escondido|Kansas[ -]City|Hollywood|Clarksville|Torrance|Rockford|Joliet|Paterson|Bridgeport|Naperville|Savannah|Mesquite|Syracuse|Pasadena|Fullerton|Killeen|Dayton|McAllen|Bellevue|Miramar|Hampton|West[ -]Valley[ -]City|Warren|Olathe|Columbia|Thornton|Carrollton|Midland|Charleston|Waco|Sterling[ -]Heights|Denton|Cedar[ -]Rapids|New[ -]Haven|Roseville|Gainesville|Visalia|Coral[ -]Springs|Thousand[ -]Oaks|Elizabeth|Stamford|Concord|Lafayette|Topeka|Kent|Simi[ -]Valley|Santa[ -]Clara|Murfreesboro|Hartford|Athens|Victorville|Abilene|Vallejo|Berkeley|Norman|Allentown|Evansville|Columbia|Odessa|Fargo|Beaumont|Ann[ -]Arbor|El[ -]Monte|Springfield|Round[ -]Rock|Wilmington|Arvada|Provo|Peoria|Lansing|Downey|Carlsbad|Costa[ -]Mesa|Miami[ -]Gardens|Westminster|Clearwater|Fairfield|Rochester|Elgin|Temecula|West[ -]Jordan|Inglewood|Richardson|Lowell|Gresham|Antioch|Cambridge|High[ -]Point|Billings|Manchester|Murrieta|Richmond|Ventura|Pueblo|Pearland|Waterbury|West[ -]Covina|North[ -]Charleston|Everett|College[ -]Station|Palm[ -]Bay|Pompano[ -]Beach|Boulder|Norwalk|West[ -]Palm[ -]Beach|Broken[ -]Arrow|Daly[ -]City|Sandy[ -]Springs|Burbank|Green[ -]Bay|Santa[ -]Maria|Wichita[ -]Falls|Lakeland|Clovis|Lewisville|El[ -]Cajon|San[ -]Mateo|Rialto|Edison|Davenport|Hillsboro|Woodbridge|Las[ -]Cruces|South[ -]Bend|Vista|Greeley|Davie|San[ -]Angelo|Jurupa[ -]Valley|Renton)\\b", ""),
	POPULOUS_CANADA_CITIES	("Contains names of most populous cities in Canada", "(?i)\\b(Toronto|Montreal|Calgary|Ottawa|Edmonton|Mississauga|Winnipeg|Vancouver|Brampton|Hamilton|Quebec[ -]City|Surrey|Laval|Halifax|London|Markham|Vaughan|Gatineau|Longueuil|Burnaby|Saskatoon|Kitchener|Windsor|Regina|Richmond|Richmond[ -]Hill|Oakville|Burlington|Greater[ -]Sudbury|Sherbrooke|Oshawa|Saguenay|Lévis|Barrie|Abbotsford|St[ -]Catharines|Trois-Rivières|Cambridge|Coquitlam|Kingston|Whitby|Guelph|Kelowna|Saanich|Ajax|Thunder[ -]Bay|Terrebonne|St[ -]John's|Langley|Chatham-Kent|Delta|Waterloo|Cape[ -]Breton|Brantford|Strathcona[ -]County|Saint-Jean-sur-Richelieu|Red[ -]Deer|Pickering|Kamloops|Clarington|North[ -]Vancouver|Milton|Nanaimo|Lethbridge|Niagara[ -]Falls|Repentigny|Victoria|Newmarket|Brossard|Peterborough|Chilliwack|Maple[ -]Ridge|Sault[ -]Ste[ -]Marie|Kawartha[ -]Lakes|Sarnia|Prince[ -]George|Drummondville|Saint[ -]John|Moncton|Saint-Jérôme|New[ -]Westminster|Wood[ -]Buffalo|Granby|Norfolk[ -]County|St[ -]Albert|Medicine[ -]Hat|Caledon|Halton[ -]Hills|Port[ -]Coquitlam|Fredericton|Grande[ -]Prairie|North[ -]Bay|Blainville|Saint-Hyacinthe|Aurora|Welland|Shawinigan|Dollard-des-Ormeaux|Belleville|North[ -]Vancouver)\\b", ""),
	US_ADDRESS				("Matches city names with no commas", "(?i)([^,]+), ([^,]+), ([a-zA-Z]+) ([^,]+)", ""),
	HOST					("Matches domains, not full URLs", "[-A-Za-z0-9]+(\\.[-A-Za-z0-9-]+)*(\\.[A-Za-z]{2,4})(:[0-9]+)*", ""),
	VIN						("North American vin numbers", "\\b([A-Za-z0-9]{3})([A-Za-z0-9]{5})([A-Za-z0-9]{1})([A-Za-z0-9]{1})([A-Za-z0-9]{1})([A-Za-z0-9]{1}[0-9]{5})\\b", ""),
	MONEY_STRING			("Money strings of at least 4 digits to only get values at or above $1000", 	"\\$(([0-9,]{4,})(\\.[0-9]{2})?)", "Don't use this for money on French sites, since they use commas instead of decimal points"),
	
	
	;
	
	private String description;
	private String definition;
	private String notes;
	public final Set<StringMatch> offsetMatches = new HashSet<StringMatch>();
	private Pattern pattern;
	
	
	private StringExtraction(String description, String definition, String notes){
		this.description = description;
		this.definition = definition;
		this.notes = notes;
		this.pattern = Pattern.compile(definition);
	}
	
	private StringExtraction(String description, String definition, String notes, Set<StringMatch> offsetMatches){
		this.description = description;
		this.definition = definition;
		this.notes = notes;
		this.pattern = Pattern.compile(definition);
		this.offsetMatches.addAll(offsetMatches);
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
