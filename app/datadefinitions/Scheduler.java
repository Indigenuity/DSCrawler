package datadefinitions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public enum Scheduler implements StringMatch{
	COBALT_SCHEDULER						(1, "Cobalt Scheduler", "service.xw.gm.com", ""),
    XTIME_SCHEDULER							(2, "XTime Scheduler", "consumer.xtime.com", ""),
    OTHER_XTIME								(3, "Any XTime Product", "xtime.com", ""),
    AUTO_APPOINTMENTS_SCHEDULER				(4, "Auto Appointments Scheduler", "autoappointments.com", ""),	//Likely Dominion
    TIME_HIGHWAY_SCHEDULER					(5, "Time Highway Scheduler", "timehighway.com", ""),
    VIN_SOLUTION_SCHEDULER					(6, "Vin Solutions Scheduler", "SAMSPanel", ""),	//Should get new grabber
    MY_VEHICLE_SITE_SCHEDULER				(7, "MyVehicleSite.com Scheduler(AutoLoop)", "myvehiclesite.com/appt", ""),	//AutoLoop?
    TOTAL_CUSTOMER_CONNECT_SCHEDULER		(8, "Total Customer Connect Scheduler", "totalcustomerconnect.com", ""),
    DEALER_CONNECTION_SCHEDULER				(9, "Dealer Connection Scheduler", "dealerconnection.com/service-appointment", ""),	//A bit dubious
    ADP_SCHEDULER							(10, "ADP Scheduler", "adpserviceedge.com/appt", ""),
    ADP_SCHEDULER_BACKUP					(11, "Any ADP Service Product", "adpserviceedge.com", ""),
    ADP_OLD_SCHEDULER						(12, "Older ADP Scheduler", "adponlineservice.com", ""),
    ADP_OLD_SCHEDULER_ALTERNATIVE			(13, "Other ADP Scheduler", "dealerinventoryonline.com", ""),
    SHOPWATCH_SCHEDULER						(14, "Shopwatch Scheduler", "sdilink.net", ""),	//Service Dynamics
    ACUITY_SCHEDULER						(15, "Acuity Scheduler", "acuityscheduling.com", ""),
    CIMA_SYSTEMS							(16, "CIMA Scheduler", "cimasystems.biz", ""),	//link from site to this url
    CIMA_SYSTEMS_SECONDARY					(17, "Other CIMA Scheulder", "cimasystems.net", ""),	//scheduler has link to here
    UDC_REVOLUTION							(18, "UDC Revolution Scheduler", "udcnet.com", ""),
    DEALER_SOCKET							(19, "Dealer Socket Scheduler", "my.dealersocket.com", ""),
    DEALER_FX_SCHEDULER						(20, "Dealer FX Scheduler", "dealer-fx.com", ""),
    SERVICE_BOOK_PRO_SCHEDULER				(21, "Service Book Pro Scheduler", "servicebookpro.com", ""), 	//Very similar to adpserviceedge
    AD_WORKZ_SCHEDULER						(22, "Ad Workz Scheduler", "adworkz.com", ""),
    CAR_RESEARCH_SCHEDULER					(23, "Car Research", "car-research.com", ""),
    DRIVERSIDE_SCHEDULER					(24, "Driverside Scheduler", "driverside.com", ""),
    DEALERMINE_SCHEDULER					(25, "Dealermine Scheduler", "dealermine.net", ""),
    PBS_SYSTEMS_SCHEDULER					(26, "PBS Systems Scheduler", "pbssystems.com", ""),
    LEAD_RESULT_SCHEDULER					(27, "Any Lead Result Product", "leadresult.com", ""),
    SCHEDULE_WEB_PRO_SCHEDULER				(28, "Schedule Web Pro Scheduler", "schedulemyservice.com", ""),
    REYNOLDS_SCHEDULER						(29, "General Reynolds Product", "reyrey.net", ""),
	AUTO_SKED								(30, "Auto Sked Scheduler", "autosked.com", ""),
	APPOINTMENT_PLUS						(31, "Appointment Plus Scheduler", "appointment-plus.com", ""),
	CNET_SERVICE							(32, "CNet Service Scheduler", "cnetservice.net", ""),
	NAKED_LIME_SCHEDULER					(33, "Naked Lime Scheduler", "websites.dealer.nakedlime.com", "URL is too general"),
	NAKED_LIME_SECOND_SCHEDULER				(34, "Any Naked Lime Product", "site.nlmkt.com", "Maybe not naked lime?"),
	UNIVERSAL_COMPUTER_CONSULTING			(35, "Universal Computer Consulting Scheduler", "svcresv.htt", "Looks Generic"),
	DRIVE_360								(36, "Drive 360 Scheduler", "drive360crm.com/Secure/ScheduleService.aspx", ""),
	DRIVER_CONNECT							(37, "Driver Connect Scheduler", "driver-connect.com/ScheduleService", ""),
//	DRIVER_SIDE								(38, "Driver Side", "driverside.com", ""),
	KAAR_MA									(39, "Kaar-ma Scheduler", "kaar-ma.com", ""),
	LEAD_RESULT								(40, "Lead Result Scheduler", "backend.leadresult.com", "Maybe MotorWebs"),
	REYNOLDS_SECONDARY						(41, "Reynolds Scheduler", "websites.dealer.reyrey.net", "a bit iffy"),
	XTIME_MAINTENANCE_MAP					(42, "XTime Maintenance Map", "xtime.com/xt/map", "");
	

	public final int id;
	public final String description;
	public final String definition;
	public final String notes;
	public final Set<StringMatch> offsetMatches = new HashSet<StringMatch>();
	
	private final static Map<Integer, Scheduler> enumIds = new HashMap<Integer, Scheduler>();
	static {
		for(Scheduler sm : Scheduler.values()){
			enumIds.put(sm.getId(), sm);
		}
	}
	
	public static Scheduler getTypeFromId(Integer id) {
		return enumIds.get(id);
	}
	
	private Scheduler(int id, String description, String definition, String notes){
		this.id = id;
		this.description = description;
		this.definition = definition;
		this.notes = notes;
	}
	
	private Scheduler(int id, String description, String definition, String notes, Set<StringMatch> offsetMatches){
		this.id = id;
		this.description = description;
		this.definition = definition;
		this.notes = notes;
		this.offsetMatches.addAll(offsetMatches);
	}
	
	public Scheduler getType(Integer id) {
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
	public Set<StringMatch> getOffsetMatches(){
		return this.offsetMatches;
	}
}
