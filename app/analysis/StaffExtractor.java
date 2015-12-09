package analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import datadefinitions.WebProvider;
import persistence.Staff;

public class StaffExtractor {
	
	
	public static Set<Staff> extractStaff(Document doc, Collection<WebProvider> webProviders){
		Set<Staff> allStaff = new HashSet<Staff>();
		allStaff.addAll(extractDealerCom(doc));
		allStaff.addAll(extractCobalt(doc));
		allStaff.addAll(extractVinSolutions(doc));
		allStaff.addAll(extractEmployee(doc));
		allStaff.addAll(extractDealerCarSearch(doc));
		allStaff.addAll(extractNakedLimePrimary(doc));
		allStaff.addAll(extractNakedLimeSecondary(doc));
		
//		System.out.println("allstaff after extract staff : " + allStaff.size());
//		for(Staff staff : allStaff){
//			System.out.println("staff : " + staff.cell + " " + staff.email + " " + staff.fn + " " + staff.name + " " + staff.other + " " + staff.phone + " " + staff.title);
//		}
		return allStaff;
	}
	public static Set<Staff> extractStaff(String text, Collection<WebProvider> webProviders) {
		
		Document doc = Jsoup.parse(text);
		return extractStaff(doc, webProviders);
	}
	
	//Cobalt
	public static List<Staff> extractCobalt(Document doc) { 
		List<Staff> allStaff = new ArrayList<Staff>();
		
		Elements employees = doc.select("div[itemprop=\"employee\"]");
//		if(employees.size() > 0)
//			System.out.println("employees : " + employees.size());
		for(Element employee : employees) {
			Staff staff = new Staff();
			Elements temps = employee.select(".contact-name");
			if(temps.size() > 0) {
				staff.setFn(temps.get(0).text());
//				System.out.println("fn : " + staff.fn);
			}
			temps = employee.select(".contact-title");
			if(temps.size() > 0) {
				staff.setTitle(temps.get(0).text());
//				System.out.println("title : " + staff.title);
			}
			temps = employee.select(".contact-email");
			if(temps.size() > 0) {
				staff.setEmail(temps.get(0).text());
//				System.out.println("email : " + staff.email);
			}
			temps = employee.select(".contact-cell");
			if(temps.size() > 0) {
				staff.setCell(temps.get(0).text());
			}
			allStaff.add(staff);
		}
		
		return allStaff;
	}
	
	//Dealer.com
	public static List<Staff> extractDealerCom(Document doc) {
		List<Staff> allStaff = new ArrayList<Staff>();
//		System.out.println("extracting");
		Elements vcards = doc.select(".vcard");
		for(Element vcard : vcards) {
			Staff staff = new Staff();
			Elements temps = vcard.select(".fn");
			if(temps.size() > 0){
				staff.setFn(temps.get(0).text());
//				System.out.println("fn : " + staff.fn);
			}
			temps = vcard.select(".title");
			if(temps.size() > 0){
				staff.setTitle(temps.get(0).text());
//				System.out.println("title : " + staff.title);
			}
			temps = vcard.select(".phone");
			if(temps.size() > 0){
				staff.setPhone(temps.get(0).text());
//				System.out.println("phone : " + staff.phone);
			}
			temps = vcard.select(".email");
			if(temps.size() > 0){
				staff.setEmail(temps.get(0).text());
//				System.out.println("email : " + staff.email);
			}
			allStaff.add(staff);
		}
//		System.out.println("now size : " + allStaff.size());
		return allStaff;
	}
	
	//Vinsolutions
	public static List<Staff> extractVinSolutions(Document doc) {
		List<Staff> allStaff = new ArrayList<Staff>();
		
		Elements employees = doc.select(".tabPanelStaff");
//		if(employees.size() > 0)
//			System.out.println("employees : " + employees.size());
		for(Element employee : employees) {
			Staff staff = new Staff();
			Elements temps = employee.select(".name");
			if(temps.size() > 0) {
				staff.setFn(temps.get(0).text());
//				System.out.println("fn : " + staff.fn);
			}
			temps = employee.select(".position");
			if(temps.size() > 0) {
				staff.setTitle(temps.get(0).text());
//				System.out.println("title : " + staff.title);
			}
			temps = employee.select(".email");
			if(temps.size() > 0) {
				staff.setEmail(temps.get(0).text());
//				System.out.println("email : " + staff.email);
			}
			temps = employees.select(".phone");
			if(temps.size() > 0){
				staff.setPhone(temps.get(0).text());
//				System.out.println("phone : " + staff.phone);
			}
		}
		
		return allStaff;
	}
	
	public static List<Staff> extractEmployee(Document doc) {
		List<Staff> allStaff = new ArrayList<Staff>();
		
		Elements employees = doc.select(".employee");
//		if(employees.size() > 0)
//			System.out.println("employees : " + employees.size());
		for(Element employee : employees) {
			Staff staff = new Staff();
			Elements temps = employee.select(".employee_name");
			if(temps.size() > 0) {
				staff.setFn(temps.get(0).text());
//				System.out.println("fn : " + staff.fn);
			}
			temps = employee.select(".employee_title");
			if(temps.size() > 0) {
				staff.setTitle(temps.get(0).text());
//				System.out.println("title : " + staff.title);
			}
			temps = employee.select(".employee_email");
			if(temps.size() > 0) {
				staff.setEmail(temps.get(0).text());
//				System.out.println("email : " + staff.email);
			}
			//Sometimes these aren't phone numbers
			temps = employees.select(".employee_comments");
			if(temps.size() > 0){
				String phone = temps.get(0).text();
				if(phone != null){
					phone = phone.substring(0, Math.min(phone.length(), 250));
				}
				staff.setPhone(phone);
//				System.out.println("phone : " + staff.phone);
			}
		}
		
		return allStaff;
	}
	
	public static List<Staff> extractDealerCarSearch(Document doc) {
		List<Staff> allStaff = new ArrayList<Staff>();
		
		Elements employees = doc.select(".Staff_Text");
//		if(employees.size() > 0)
//			System.out.println("employees : " + employees.size());
		for(Element employee : employees) {
			Staff staff = new Staff();
			Elements temps = employee.select(".Staff_Name");
			if(temps.size() > 0) {
				staff.setFn(temps.get(0).text());
//				System.out.println("fn : " + staff.fn);
			}
			temps = employee.select(".Staff_Title");
			if(temps.size() > 0) {
				staff.setTitle(temps.get(0).text());
//				System.out.println("title : " + staff.title);
			}
			//No individual selector
			temps = employee.select(".Staff_Phone_Email");
			if(temps.size() > 0) {
				staff.setEmail(temps.get(0).text());
//				System.out.println("email : " + staff.email);
			}
			temps = employees.select(".Staff_Phone");
			if(temps.size() > 0){
				staff.setPhone(temps.get(0).text());
//				System.out.println("phone : " + staff.phone);
			}
		}
		
		return allStaff;
	}

	public static List<Staff> extractNakedLimePrimary(Document doc) {
		List<Staff> allStaff = new ArrayList<Staff>();
		
		Elements employees = doc.select(".staff");
//		if(employees.size() > 0)
//			System.out.println("employees : " + employees.size());
		for(Element employee : employees) {
			Staff staff = new Staff();
			Elements temps = employee.select(".staffName, .nametext");
			if(temps.size() > 0) {
				staff.setFn(temps.get(0).text());
//				System.out.println("fn : " + staff.fn);
			}
			temps = employee.select(".staffJobTitle, .position");
			if(temps.size() > 0) {
				staff.setTitle(temps.get(0).text());
//				System.out.println("title : " + staff.title);
			}
			temps = employee.select(".staffEmail");
			if(temps.size() > 0) {
				staff.setEmail(temps.get(0).text());
//				System.out.println("email : " + staff.email);
			}
			temps = employees.select(".staffPhone");
			if(temps.size() > 0){
				staff.setPhone(temps.get(0).text());
//				System.out.println("phone : " + staff.phone);
			}
		}
		
		return allStaff;
	}
	
	public static List<Staff> extractNakedLimeSecondary(Document doc) {
		List<Staff> allStaff = new ArrayList<Staff>();
		
		//Several different versions that use different ids that end in the same thing
		Elements employees = doc.select("table[id$=StaffDataList] td");
//		if(employees.size() > 0)
//			System.out.println("employees : " + employees.size());
		for(Element employee : employees) {
			Staff staff = new Staff();
			Elements temps = employee.select("h2");
			if(temps.size() > 0) {
				staff.setFn(temps.get(0).text());
//				System.out.println("fn : " + staff.fn);
			}
			temps = employee.select("span");
			if(temps.size() > 0) {
				staff.setTitle(temps.get(0).text());
//				System.out.println("title : " + staff.title);
			}
			//No tags to match for other info, but it IS there, so just grab everything
			staff.setOther(employee.text().substring(0, Math.min(250, employee.text().length())));
		}
		
		return allStaff;
	}
	
	public static List<Staff> extractDealerInspirePrimary(Document doc) {
		List<Staff> allStaff = new ArrayList<Staff>();
		
		Elements employees = doc.select("ul.staff li");
//		if(employees.size() > 0)
//			System.out.println("employees : " + employees.size());
		for(Element employee : employees) {
			Staff staff = new Staff();
			Elements temps = employee.select("h3");
			if(temps.size() > 0) {
				staff.setFn(temps.get(0).text());
//				System.out.println("fn : " + staff.fn);
			}
			temps = employee.select("h4");
			if(temps.size() > 0) {
				staff.setTitle(temps.get(0).text());
//				System.out.println("title : " + staff.title);
			}
			temps = employee.select("a");
			if(temps.size() > 0) {
				staff.setEmail(temps.get(0).attr("href"));
//				System.out.println("email : " + staff.email);
			}
			temps = employees.select("div.staffhone");
			if(temps.size() > 0){
				staff.setPhone(temps.get(0).text());
//				System.out.println("phone : " + staff.phone);
			}
		}
		
		return allStaff;
	}
	
}
