package datadefinitions;

import java.util.HashSet;
import java.util.Set;


public enum Scheduler implements StringMatch{
	COBALT_SCHEDULER						("Cobalt Scheduler", "service.xw.gm.com", ""),
    XTIME_SCHEDULER							("XTime Scheduler", "consumer.xtime.com", ""),
    OTHER_XTIME								("Any XTime Product", "xtime.com", ""),
    AUTO_APPOINTMENTS_SCHEDULER				("Auto Appointments Scheduler", "autoappointments.com", ""),	//Likely Dominion
    TIME_HIGHWAY_SCHEDULER					("Time Highway Scheduler", "timehighway.com", ""),
    VIN_SOLUTION_SCHEDULER					("Vin Solutions Scheduler", "SAMSPanel", ""),	//Should get new grabber
    MY_VEHICLE_SITE_SCHEDULER				("MyVehicleSite.com Scheduler(AutoLoop)", "myvehiclesite.com/appt", ""),	//AutoLoop?
    TOTAL_CUSTOMER_CONNECT_SCHEDULER		("Total Customer Connect Scheduler", "totalcustomerconnect.com", ""),
    DEALER_CONNECTION_SCHEDULER				("Dealer Connection Scheduler", "dealerconnection.com/service-appointment", ""),	//A bit dubious
    ADP_SCHEDULER							("ADP Scheduler", "adpserviceedge.com/appt", ""),
    ADP_SCHEDULER_BACKUP					("Any ADP Service Product", "adpserviceedge.com", ""),
    ADP_OLD_SCHEDULER						("Older ADP Scheduler", "adponlineservice.com", ""),
    ADP_OLD_SCHEDULER_ALTERNATIVE			("Other ADP Scheduler", "dealerinventoryonline.com", ""),
    SHOPWATCH_SCHEDULER						("Shopwatch Scheduler", "sdilink.net", ""),	//Service Dynamics
    ACUITY_SCHEDULER						("Acuity Scheduler", "acuityscheduling.com", ""),
    CIMA_SYSTEMS							("CIMA Scheduler", "cimasystems.biz", ""),	//link from site to this url
    CIMA_SYSTEMS_SECONDARY					("Other CIMA Scheulder", "cimasystems.net", ""),	//scheduler has link to here
    UDC_REVOLUTION							("UDC Revolution Scheduler", "udcnet.com", ""),
    DEALER_SOCKET							("Dealer Socket Scheduler", "my.dealersocket.com", ""),
    DEALER_FX_SCHEDULER						("Dealer FX Scheduler", "dealer-fx.com", ""),
    SERVICE_BOOK_PRO_SCHEDULER				("Service Book Pro Scheduler", "servicebookpro.com", ""), 	//Very similar to adpserviceedge
    AD_WORKZ_SCHEDULER						("Ad Workz Scheduler", "adworkz.com", ""),
    CAR_RESEARCH_SCHEDULER					("Car Research", "car-research.com", ""),
    DRIVERSIDE_SCHEDULER					("Driverside Scheduler", "driverside.com", ""),
    DEALERMINE_SCHEDULER					("Dealermine Scheduler", "dealermine.net", ""),
    PBS_SYSTEMS_SCHEDULER					("PBS Systems Scheduler", "pbssystems.com", ""),
    LEAD_RESULT_SCHEDULER					("Any Lead Result Product", "leadresult.com", ""),
    SCHEDULE_WEB_PRO_SCHEDULER				("Schedule Web Pro Scheduler", "schedulemyservice.com", ""),
    REYNOLDS_SCHEDULER						("General Reynolds Product", "reyrey.net", ""),
	AUTO_SKED								("Auto Sked Scheduler", "autosked.com", ""),
	APPOINTMENT_PLUS						("Appointment Plus Scheduler", "appointment-plus.com", ""),
	CNET_SERVICE							("CNet Service Scheduler", "cnetservice.net", ""),
	NAKED_LIME_SCHEDULER					("Naked Lime Scheduler", "websites.dealer.nakedlime.com", "URL is too general"),
	NAKED_LIME_SECOND_SCHEDULER				("Any Naked Lime Product", "site.nlmkt.com", "Maybe not naked lime?"),
	UNIVERSAL_COMPUTER_CONSULTING			("Universal Computer Consulting Scheduler", "svcresv.htt", "Looks Generic"),
	DRIVE_360								("Drive 360 Scheduler", "drive360crm.com/Secure/ScheduleService.aspx", ""),
	DRIVER_CONNECT							("Driver Connect Scheduler", "driver-connect.com/ScheduleService", ""),
//	DRIVER_SIDE								("Driver Side", "driverside.com", ""),
	KAAR_MA									("Kaar-ma Scheduler", "kaar-ma.com", ""),
	LEAD_RESULT								("Lead Result Scheduler", "backend.leadresult.com", "Maybe MotorWebs"),
	REYNOLDS_SECONDARY						("Reynolds Scheduler", "websites.dealer.reyrey.net", "a bit iffy"),
	XTIME_MAINTENANCE_MAP					("XTime Maintenance Map", "xtime.com/xt/map", "");
	

	public final String description;
	public final String definition;
	public final String notes;
	public final Set<StringMatch> offsetMatches = new HashSet<StringMatch>();
	
	private Scheduler(String description, String definition, String notes){
		this.description = description;
		this.definition = definition;
		this.notes = notes;
	}
	
	private Scheduler(String description, String definition, String notes, Set<StringMatch> offsetMatches){
		this.description = description;
		this.definition = definition;
		this.notes = notes;
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
	public Set<StringMatch> getOffsetMatches(){
		return this.offsetMatches;
	}
}
