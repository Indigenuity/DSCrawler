package persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CapEntry {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long capEntryId;
	
	public String lead_no;
	public String dealershipName;
	public boolean franchise;
	public String assignedTo;
	public String niada;
	public String address;
	public String city;
	public String state;
	public String zip;
	public String country;
	public String phone;
	public String website;
	public String email;
	public String firstName;
	public String lastName;
	public String role;
	public String first1;
	public String last1;
	public String role1;
	public String first2;
	public String last2;
	public String role2;
	public String first3;
	public String last3;
	public String role3;
	public String rating;
	public String leadStatus;
	public int inventory;
	public String webProvider;
	public String hasScheduler;
	public String schedulerProvider;
	public boolean audi;
	public boolean bmw;
	public boolean hyundai;
	public boolean infiniti;
	public boolean jaguar;
	public boolean jeep;
	public boolean landRover;
	public boolean lexus;
	public boolean lincoln;
	public boolean mercedes;
	public boolean mini;
	public boolean porsche;
	public boolean toyota;
	public boolean vw;
	public boolean volvo;
	public boolean acura;
	public boolean chevy;
	public boolean chrysler;
	public boolean dodge;
	public boolean fiat;
	public boolean ford;
	public boolean gmc;
	public boolean hummer;
	public boolean kia;
	public boolean mazda;
	public boolean nissan;
	public boolean scion;
	public boolean smart;
	public boolean subaru;
	public boolean buick;
	public boolean cadillac;
	public boolean honda;
	public boolean mitsubishi;
	public boolean bentley;
	public boolean lotus;
	public boolean maserati;
	public boolean astonMartin;
	public boolean ferrari;
	public boolean mclaren;
	public boolean lamborghini;
	public boolean rollsRoyce;
	public String singlePoint;
	public String groupName;
	public String numSalesPeople;
	public String hasBDC;
	public String salesServiceBDC;
	public String carsSold;
	public String hasCRM;
	public String CRM;
	public String salesServiceCRM;
	public String serviceSoftware;
	public String currentCRM;
	public String hasDMS;
	public String DMS;
	public String useDataMining;
	public String dataMiningToold;
	public String currentCRMSatisfaction;
	public String timeLeftCRM;
	public String inMarketCRM;
	public String areaCode;
	
	
	public boolean noStaffPage;
	public boolean retail;
	public boolean wholesale;
	public boolean leasing;
	public boolean partsDept;
	public boolean rental;
	public boolean serviceDept;
	public boolean bhph;
	public String serviceInternal;
	public boolean deliveryVehicles;
	public boolean accessibilityVehicles;
	public String howFinance;
	public String whichFinance;
	public String relatedFinance;
	public String adSpend;
	public String howAdvertise;
	public String averageDown;
	public String vehicleAge;
	public String retailAverage;
	public String monthSales;
	public String characterizeInventory;
	public String yearsInOperation;
	public String singleMulti;
	public String numLocations;
	public String numEmployees;
	
	public String reasonNotInterested;
	
	

}
