package controllers;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import datadefinitions.WebProvider;
import datatransfer.Cleaner;
import global.Global.HomepageAction;
import persistence.CrawlSet;
import persistence.Dealer;
import persistence.PlacesDealer;
import persistence.Site;
import persistence.SiteCrawl;
import persistence.Temp;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import utilities.DSFormatter;

public class DataEditor extends Controller{
	
	
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
			Temp temp = (Temp) entity;
			System.out.println("homepage : " + temp.getSuggestedUrl());
//			JPA.em().merge(site);
		}
		else {
			System.out.println("null entity");
		}
		
		
		
		return ok();
	}
	
	@Transactional
	public static Result combineOnDomain(long siteId){
		Site primary = JPA.em().find(Site.class, siteId);
		System.out.println("found site to favor in combining : " + primary);
		Cleaner.combineOnDomain(primary);
		return ok();
	}

	public static Result inferWebProvider(long siteCrawlId, int webProviderId) {
		
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
	public static Result createCrawlSet(){
		Logger.info("Creating Crawl Set");
			System.out.println("Creating crawl set");
			CrawlSet crawlSet = new CrawlSet();
			crawlSet.setStartDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
			String query = "select s.siteId from Site s where s.franchise = true and s.siteId not in (select siteId from sites_with_duplicate_domains)";
			List<Site> sites = JPA.em().createNativeQuery(query).getResultList();
			crawlSet.setSites(sites);
			crawlSet.setNeedMobile(sites);
			crawlSet.setNeedRedirectResolve(sites);
			crawlSet.setUncrawled(sites);
			crawlSet.setName("Franchise Sites");
			JPA.em().persist(crawlSet);
		return created("Crawl Set Created");
	}
	
	@Transactional
	public static Result deleteCrawlSet(long crawlSetId) {
		try{
			CrawlSet crawlSet = JPA.em().find(CrawlSet.class, crawlSetId);
			crawlSet.getUncrawled().clear();
			crawlSet.getCompletedCrawls().clear();
			JPA.em().remove(crawlSet);
		} catch(Exception e) {
			return internalServerError("Error deleting Crawl Set " + crawlSetId + " : " + e);
		}
		
		return ok("Crawl Set Deleted");
	}
	
	@Transactional
	public static Result hideFromMatt(long siteId) {
		System.out.println("Hiding from Matt : " + siteId);
		Site site = JPA.em().find(Site.class, siteId);
		if(site == null) {
			Logger.error("Could not find site to hide from Matt");
		}
		else {
			site.setShowToMatt(false);
		}
		return ok();
	}
	
	@Transactional
	public static Result addGroupUrl(long siteId, String url) {
		System.out.println("Adding group url (site " + siteId + ") : " + url);
		Site site = JPA.em().find(Site.class, siteId);
		if(site == null) {
			Logger.error("Could not find site to mark as crawler protected");
		}else {
			site.addGroupUrl(url);
		}
		return ok();
	}
	
	@Transactional
	public static Result removeExtraCrawls() {
		String query = "from Site s where SIZE(s.crawls) > 1";
		List<Site> sites = JPA.em().createQuery(query).getResultList();
		System.out.println("Found sites with extra crawls : " + sites.size());
		
		SiteCrawl tempCrawl;
		for(Site site : sites) {
//			JPA.em().detach(site);
			tempCrawl = null;
			for(int i = 0; i < site.getCrawls().size(); i++) {
				SiteCrawl siteCrawl = site.getCrawls().get(i);
				if(siteCrawl.getNumRetrievedFiles() > 0){
					if(tempCrawl != null && siteCrawl.getNumRetrievedFiles() > tempCrawl.getNumRetrievedFiles()){
						tempCrawl = siteCrawl;
					}
					else if(tempCrawl == null) {
						tempCrawl = siteCrawl;
					}
				}
				else if(tempCrawl == null && i == site.getCrawls().size() - 1) {
					tempCrawl = siteCrawl;
				}
			}
			System.out.println("temp crawl : " + tempCrawl);
			site.getCrawls().clear();
			site.addCrawl(tempCrawl);
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
				site.setDomain(DSFormatter.getDomain(site.getHomepage()));
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
		List<PlacesDealer> dealers = JPA.em().createQuery(query).getResultList();
		int count = 0;
		for(PlacesDealer dealer : dealers) {
			try{
				System.out.println(count++ + " : " + dealer.getWebsite());
				dealer.setDomain(DSFormatter.getDomain(dealer.getWebsite()));
			} catch(Exception e) {
				System.out.println("error : " + e);
			}
		}
		return ok();
	}
	
	@Transactional
	public static Result makeSite(String url, long dealerId) {
		URL validUrl = null;
		try{
			validUrl = new URL(url);
			Dealer dealer = JPA.em().find(Dealer.class, dealerId);
			if(dealer.getMainSite() == null) {
				Site site = new Site();
				site.setHomepage(url);
				dealer.setMainSite(site);
			}
		}catch(MalformedURLException e) {
			Logger.error("Invalid url received in makeSite.  Failed to create site.");
			return Results.badRequest("Invalid url.  Couldn't create site");
		}catch(Exception e) {
			Logger.error("Error in makeSite : " + e);
			return Results.internalServerError("Error while creating site : " + e);
		}
		
		
		
		return ok("Site Created");
	}
	
	@Transactional
	public static Result setWebProvider(long siteCrawlId, int webProviderId) {
		SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
		System.out.println("webprovider : " + WebProvider.getTypeFromId(webProviderId));
		siteCrawl.setInferredWebProvider(WebProvider.getTypeFromId(webProviderId));
		return ok();
	}
	
	@Transactional
	public static Result setHomepage() {
		DynamicForm requestData = Form.form().bindFromRequest();
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, Long.parseLong(requestData.get("crawlSetId")));
		Site site = JPA.em().find(Site.class, Long.parseLong(requestData.get("siteId")));
		String manualHomepage = requestData.get("manualHomepage");
		boolean isNotable = true;
		
		Logger.info("Setting Homepage for Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ", isNotable : " + isNotable + ")");
		Logger.info("manualHomepage: " + manualHomepage);
		System.out.println("Setting Homepage for Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ", isNotable : " + isNotable + ")");
		System.out.println("manualHomepage: " + manualHomepage);
		
		String decodedUrl = DSFormatter.decode(manualHomepage);
		
		if(StringUtils.isEmpty(decodedUrl)) {
			return badRequest("Can't set homepage to null");
		}
		
		site.addRedirectUrl(site.getHomepage());
		site.setHomepage(decodedUrl);
		site.setHomepageNeedsReview(false);
		site.setReviewLater(false);
		site.setReviewReason(null);
		site.setSuggestedHomepage(null);
		
		return ok();
	}
	
	@Transactional
	public static Result acceptSuggested() {
		DynamicForm requestData = Form.form().bindFromRequest();
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, Long.parseLong(requestData.get("crawlSetId")));
		Site site = JPA.em().find(Site.class, Long.parseLong(requestData.get("siteId")));
		boolean isNotable = Boolean.parseBoolean(requestData.get("isNotable"));
		
		Logger.info("Accepting suggested for Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ", isNotable : " + isNotable + ")");
		System.out.println("Accepting suggested for Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ", isNotable : " + isNotable + ")");
		
		site.addRedirectUrl(site.getHomepage());
		site.setHomepage(site.getSuggestedHomepage());
		site.setSuggestedHomepage(null);
		site.setNotableChange(isNotable);
		site.setQueryStringApproved(true);
		site.setHompageValidUrlConfirmed(true);
		site.setHomepageNeedsReview(false);
		site.setReviewLater(false);
		site.setReviewReason(null);
		crawlSet.getNeedRedirectResolve().remove(site);
		
		return ok();
	}
	
	@Transactional
	public static Result ignoreSuggested() {
		DynamicForm requestData = Form.form().bindFromRequest();
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, Long.parseLong(requestData.get("crawlSetId")));
		Site site = JPA.em().find(Site.class, Long.parseLong(requestData.get("siteId")));
		
		Logger.info("Ignoring suggested for Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ")");
		System.out.println("Ignoring suggested for Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ")");
		
		site.setHomepageNeedsReview(false);
		site.setSuggestedHomepage(null);
		site.setReviewReason(null);
		crawlSet.getNeedRedirectResolve().remove(site);
		
		return ok();
	}
	
	@Transactional
	public static Result markMaybeDefunct() {
		DynamicForm requestData = Form.form().bindFromRequest();
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, Long.parseLong(requestData.get("crawlSetId")));
		Site site = JPA.em().find(Site.class, Long.parseLong(requestData.get("siteId")));
		
		Logger.info("Marking Site " + site.getSiteId() + " for closing. (CrawlSet : " + crawlSet.getCrawlSetId() + ")");
		System.out.println("Marking Site " + site.getSiteId() + " for closing. (CrawlSet : " + crawlSet.getCrawlSetId() + ")");
		
		site.setMaybeDefunct(true);
		site.setHomepageNeedsReview(false);
		site.setReviewLater(false);
		crawlSet.getNeedRedirectResolve().remove(site);
		
		return ok();
	}
	
	@Transactional
	public static Result reviewLater() {
		System.out.println("review later");
		DynamicForm requestData = Form.form().bindFromRequest();
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, Long.parseLong(requestData.get("crawlSetId")));
		Site site = JPA.em().find(Site.class, Long.parseLong(requestData.get("siteId")));
		
		Logger.info("Marking to review later for Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ")");
		System.out.println("Marking to review later for Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ")");
		
		site.setReviewLater(true);
		site.setHomepageNeedsReview(false);
		crawlSet.getNeedRedirectResolve().remove(site);
		
		return ok();
	}
	
	@Transactional
	public static Result markCrawlProtected() {
		DynamicForm requestData = Form.form().bindFromRequest();
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, Long.parseLong(requestData.get("crawlSetId")));
		Site site = JPA.em().find(Site.class, Long.parseLong(requestData.get("siteId")));
		
		Logger.info("Marking as crawl protected on Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ")");
		System.out.println("Marking as crawl protected on Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ")");
		
		site.setCrawlerProtected(true);
		site.setReviewLater(false);
		site.setHomepageNeedsReview(false);
		crawlSet.getUncrawled().remove(site);
		crawlSet.getNeedMobile().remove(site);
		
		return ok();
	}
	
	@Transactional
	public static Result approveSmallCrawl() {
		DynamicForm requestData = Form.form().bindFromRequest();
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, Long.parseLong(requestData.get("crawlSetId")));
		Site site = JPA.em().find(Site.class, Long.parseLong(requestData.get("siteId")));
		SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, Long.parseLong(requestData.get("siteCrawlId")));
		
		Logger.info("Approving small crawl on Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ")");
		System.out.println("Approving small crawl on Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ")");
		
		siteCrawl.setSmallCrawlApproved(true);
		
		return ok();
	}
	
	@Transactional
	public static Result markGroupSite() {
		DynamicForm requestData = Form.form().bindFromRequest();
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, Long.parseLong(requestData.get("crawlSetId")));
		Site site = JPA.em().find(Site.class, Long.parseLong(requestData.get("siteId")));
		
		Logger.info("Marking as group Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ")");
		System.out.println("Marking as group Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ")");
		
		site.setGroupSite(true);
		return ok();
	}
	
	@Transactional
	public static Result markRecrawl() {
		DynamicForm requestData = Form.form().bindFromRequest();
		CrawlSet crawlSet = JPA.em().find(CrawlSet.class, Long.parseLong(requestData.get("crawlSetId")));
		Site site = JPA.em().find(Site.class, Long.parseLong(requestData.get("siteId")));
		SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, Long.parseLong(requestData.get("siteCrawlId")));
		
		Logger.info("Marking for recrawl Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ")");
		System.out.println("Marking for recrawl Site " + site.getSiteId() + ". (CrawlSet : " + crawlSet.getCrawlSetId() + ")");
		
		crawlSet.getCompletedCrawls().remove(siteCrawl);
		crawlSet.getUncrawled().add(site);
		
		return ok();
	}
	
	@Transactional
	public static Result markNonStandalone(String domain) {
//		Site site = JPA.em().find(Site.class, siteId);
//		
//		Logger.info("Marking Site " + site.getSiteId() + " as not standalone.");
//		System.out.println("Marking Site " + site.getSiteId() + " as not standalone.");
//		
//		site.setStandaloneSite(false);
//		
		return ok();
	}
	
	@Transactional
	public static Result confirmHomepage(long siteId, String action){
		
		
		Site site  = JPA.em().find(Site.class, siteId);
		System.out.println(action + " (" + site.getHomepage() + ") Suggested : " + site.getSuggestedHomepage());
		if(action.equals("IGNORE")) {
			site.setSuggestedHomepage(null);
			site.setHomepageNeedsReview(false);
			site.setRedirectResolveDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
		}
		else if(action.equals("MARK_FOR_CLOSING")) {
			site.setMaybeDefunct(true);
			site.setHomepageNeedsReview(false);
			site.setRedirectResolveDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
		}
		else if(action.equals("MARK_FOR_REVIEW")) {
			site.setHomepageNeedsReview(false);
			site.setReviewLater(true);
			
		}
		else if(action.equals("ACCEPT")) {
			site.getRedirectUrls().add(site.getHomepage());
			site.setHomepage(site.getSuggestedHomepage());
			site.setSuggestedHomepage(null);
			site.setHomepageNeedsReview(false);
			site.setQueryStringApproved(true);
			site.setHompageValidUrlConfirmed(true);
			site.setRedirectResolveDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
		}
		else if(action.equals("ACCEPT_AND_MARK_CHANGE")) {
			site.getRedirectUrls().add(site.getHomepage());
			site.setHomepage(site.getSuggestedHomepage());
			site.setSuggestedHomepage(null);
			site.setNotableChange(true);
			site.setQueryStringApproved(true);
			site.setHomepageNeedsReview(false);
			site.setHompageValidUrlConfirmed(true);
			site.setRedirectResolveDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
		}
		else if(action.equals("NEW_SITE")) {
			site.setNotableChange(true);
			site.setReviewLater(true);
			site.setHomepageNeedsReview(false);
		}
		return ok();
	}
}
