package controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.metamodel.ManagedType;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import audit.sync.SalesforceControl;
import datatransfer.Cleaner;
import persistence.Site;
import persistence.SiteCrawl;
import places.PlacesPage;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import utilities.DSFormatter;

public class DataEditor extends Controller{
	
	
	
	//TODO DELETE THIS
	@Transactional
	public static Result markSignificantDifferences() {
//		SalesforceControl.markSignificantDifferences();
		return ok("Salesforce Accounts marked with significant differences successfully");
	}
	
	
	@Transactional
	public static Result editEntity() throws IllegalAccessException, InvocationTargetException {
		DynamicForm data = Form.form().bindFromRequest();
		String entityClass = "class " + data.get("entityClass");
		long entityId = Long.parseLong(data.get("entityId"));
		Object entity = null;
		//Only accept JPA Entity classes
		for(ManagedType<?> type : JPA.em().getMetamodel().getManagedTypes()) {
//			System.out.println("type : " + type.getJavaType());
//			System.out.println("entityClass : " + entityClass);
			if(StringUtils.equals(type.getJavaType().toString(), entityClass)){
				entity = JPA.em().find(type.getJavaType(), entityId);
				System.out.println("entity : " + entity);
				System.out.println("Found entity : " + entityId);
//				return ok(views.html.scaffolding.viewEntity.render(entity));
			}
		}
		if(entity != null){
			for(Entry<String, String> entry : data.data().entrySet()){
				BeanUtils.setProperty(entity, entry.getKey(), entry.getValue());
	//			System.out.println("key : " + entry.getKey());
	//			System.out.println("value : " + entry.getValue());
			}
//			JPA.em().merge(site);
		}
		else {
			System.out.println("null entity");
		}
		
		
		
		return ok();
	}
	
	@Transactional
	public static Result deDupHomepages() {
		int numCleaned = Cleaner.cleanDuplicateHomepages();
		
		return  ok(views.html.dashboard.render("Combined sites for " + numCleaned + " Sites"));
	}
	
	@Transactional 
	public static Result combineFavoring(String domain, long siteId) {
		
		return ok();
	}
	
	
	@Transactional
	public static Result removeExtraCrawls() {
		String query = "from Site s where SIZE(s.crawls) > 1";
		List<Site> sites = JPA.em().createQuery(query).getResultList();
		System.out.println("Found sites with extra crawls : " + sites.size());
		
		SiteCrawl tempCrawl;
		for(Site site : sites) {
////			JPA.em().detach(site);
//			tempCrawl = null;
//			for(int i = 0; i < site.getCrawls().size(); i++) {
//				SiteCrawl siteCrawl = site.getCrawls().get(i);
//				if(siteCrawl.getNumRetrievedFiles() > 0){
//					if(tempCrawl != null && siteCrawl.getNumRetrievedFiles() > tempCrawl.getNumRetrievedFiles()){
//						tempCrawl = siteCrawl;
//					}
//					else if(tempCrawl == null) {
//						tempCrawl = siteCrawl;
//					}
//				}
//				else if(tempCrawl == null && i == site.getCrawls().size() - 1) {
//					tempCrawl = siteCrawl;
//				}
//			}
//			System.out.println("temp crawl : " + tempCrawl);
//			site.getCrawls().clear();
//			site.addCrawl(tempCrawl);
		}
		
		return ok();
	}
	
	@Transactional
	public static Result fillStandardizedFormat() {
		String query = "from Site s where redirectResolveDate is not null ";
		int chunkSize = 500;
		int offset = 0;
		List<Site> sites = JPA.em().createQuery(query).getResultList();
		int count = 0;
		for(Site site : sites) {
			try{
				System.out.println(count++ + " : " + site.getHomepage());
//				site.setStandardizedHomepage(DSFormatter.standardizeUrl(site.getHomepage()));
//				site.setDomain(DSFormatter.getDomain(site.getHomepage()));
			} catch(Exception e) {
				System.out.println("error : " + e);
			}
		}
		return ok();
	}
	
	@Transactional
	public static Result fillPlacesDomain() {
		String query = "from PlacesDealer pd where pd.website is not null and pd.domain is null";
		int chunkSize = 500;
		int offset = 0;
		List<PlacesPage> dealers = JPA.em().createQuery(query).getResultList();
		int count = 0;
		for(PlacesPage dealer : dealers) {
			try{
				System.out.println(count++ + " : " + dealer.getWebsite());
				dealer.setDomain(DSFormatter.getDomain(dealer.getWebsite()));
			} catch(Exception e) {
				System.out.println("error : " + e);
			}
		}
		return ok();
	}
}
