package controllers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.TemporalType;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import analysis.AnalysisControl;
import analysis.AnalysisDao;
import analysis.PageCrawlAnalysis;
import analysis.SiteCrawlAnalysis;
import async.async.Asyncleton;
import async.functionalwork.JpaFunctionalBuilder;
import dao.GeneralDAO;
import dao.SitesDAO;
import datatransfer.CSVGenerator;
import datatransfer.ReportGenerator;
import datatransfer.reports.Report;
import global.Global;
import persistence.PageCrawl;
import persistence.Site;
import persistence.Site.RedirectType;
import persistence.Site.SiteStatus;
import persistence.SiteCrawl;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import reporting.StatsBuilder;
import sites.crawling.SiteCrawlLogic;
import sites.persistence.SiteSet;
import sites.utilities.SiteLogic;
import sites.utilities.SiteSetDao;
import sites.utilities.SiteSetLogic;

public class SitesController extends Controller {
	
	final static Logger.ALogger dsLogger = Logger.of("ds");

	@Transactional 
    public static Result sitesDashboard() {
    	return ok(views.html.sites.sitesDashboard.render());
    }
	
	@Transactional
	public static Result sitesDashboardStats() {
		return ok(views.html.viewstats.viewStats.render(StatsBuilder.sitesDashboard()));
	}
	
	@Transactional
	public static Result viewSiteCrawlAnalysis(long siteCrawlAnalysisId){
		SiteCrawlAnalysis analysis = JPA.em().find(SiteCrawlAnalysis.class, siteCrawlAnalysisId);
		if(analysis == null){
			return badRequest("No SiteCrawlAnalysis with id : " + siteCrawlAnalysisId);
		}
		return ok(views.html.sites.analysis.siteCrawlAnalysis.render(analysis));
	}
	
	@Transactional
	public static Result recentSiteCrawls(){
		String queryString = "from SiteCrawl sc order by sc.crawlDate desc";
		List<SiteCrawl> siteCrawls = JPA.em().createQuery(queryString, SiteCrawl.class).setMaxResults(10).getResultList();
		return ok(views.html.sites.snippets.recentSiteCrawls.render(siteCrawls));
	}
	
	@Transactional
	public static Result siteCrawlList(Long siteId){
		String queryString = "select sc from Site s join s.crawls sc";
		List<SiteCrawl> siteCrawls = JPA.em().createQuery(queryString, SiteCrawl.class).setMaxResults(10).getResultList();
		return ok(views.html.sites.snippets.recentSiteCrawls.render(siteCrawls));
	}
	
	@Transactional
	public static Result viewPageCrawlAnalysis(long pageCrawlAnalysisId){
		PageCrawlAnalysis analysis = JPA.em().find(PageCrawlAnalysis.class, pageCrawlAnalysisId);
		if(analysis == null){
			return badRequest("No PageCrawlAnalysis with id : " + pageCrawlAnalysisId);
		}
		return ok(views.html.sites.analysis.pageCrawlAnalysis.render(analysis));
	}
	
	@Transactional
	public static Result viewSite(long siteId){
		Site site = JPA.em().find(Site.class, siteId);
		return ok(views.html.sites.site.render(site));
	}
	
	@Transactional
	public static Result viewSiteCrawl(long siteCrawlId){
		SiteCrawl siteCrawl = JPA.em().find(SiteCrawl.class, siteCrawlId);
		return ok(views.html.sites.siteCrawl.render(siteCrawl));
	}
	
	@Transactional
	public static Result viewPageCrawl(long pageCrawlId){
		PageCrawl pageCrawl = JPA.em().find(PageCrawl.class, pageCrawlId);
		return ok(views.html.sites.pageCrawl.render(pageCrawl));
	}
	
	@Transactional
	public static Result reAnalyzeSites(){
		List<Long> siteIds = GeneralDAO.getAllIds(Site.class);
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind((site) -> {
					SiteLogic.analyzeUrlStructure(site);
				}, Site.class), 
				siteIds.stream(), 
				true);
		return ok("Queued " + siteIds.size() + " for re-analysis");
	}
	
	@Transactional
	public static Result noInventoryCrawls(){
		String queryString = "from SiteCrawl sc where (sc.newInventoryRoot is null or sc.usedInventoryRoot is null) and sc.crawlDate > :staleDate";
		List<SiteCrawl> siteCrawls = JPA.em().createQuery(queryString, SiteCrawl.class)
				.setMaxResults(100)
				.setParameter("staleDate",  Global.getStaleDate(), TemporalType.DATE)
				.getResultList();
		
		return ok(views.html.sites.noInventoryCrawls.render(siteCrawls));
	}
	
	@Transactional
	public static Result reviewSites(){
		List<Site> sites = SitesDAO.getList("siteStatus", SiteStatus.NEEDS_REVIEW, 20, 0);
		return ok(views.html.sites.reviewSites.render(sites));
	}

	@Transactional
	public static Result siteReviewSubmit(){
		DynamicForm data = Form.form().bindFromRequest();
    	Long siteId = Long.parseLong(data.get("siteId"));
    	Site site = JPA.em().find(Site.class, siteId);
		String manualRedirect = data.get("manualRedirect");
		String action = data.get("actionType");

		if(action.equals("Approve Redirect")){
//			SiteLogic.acceptUrlCheck(site, false);
		} else if(action.equals("Approve Shared Site")) {
//			SiteLogic.acceptUrlCheck(site, true);
		} else if(action.equals("Mark Defunct")){
//			SiteLogic.markDefunct(site);
		} else if(action.equals("Other Issue")){
//			SiteLogic.markError(site);
		}  else if(action.equals("Disapprove")){
//			SiteLogic.disapprove(site);
		} else if(action.equals("Redirect and Disapprove")){
//			Site endpoint = SiteLogic.manuallyRedirect(site, manualRedirect);
//			SiteLogic.disapprove(endpoint);
		} else if(action.equals("Inference Redirect")){
			Site newSite = SitesDAO.getOrNewThreadsafe(manualRedirect);
			SiteLogic.redirect(site, newSite, RedirectType.INFERENCE);
		} else if(action.equals("Path Paring")){
			SiteLogic.parePath(site);
		} else if(action.equals("Attempt Path Paring")){
			SiteLogic.attemptParePath(site);
		} else if(action.equals("Approve Path")){
//			SiteLogic.approvePath(site);
		} else if(action.equals("Unapprove Site")){
//			SiteLogic.unapproveSite(site);
		} else {
			System.out.println("oops.  Action not understood");
		}
		return ok();
	}
	
	@Transactional
	public static Result reviewNotStandardPaths(){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("notStandardHomepagePath", true);
		parameters.put("approvedHomepagePath", false);
		parameters.put("uncrawlableDomain", false);
		parameters.put("defunctPath", false);
		parameters.put("defunctDomain", false);
		parameters.put("unapproved", false);
		parameters.put("httpError", false);
		parameters.put("redirects", false);
		List<Site> sites = SitesDAO.getList(parameters, 50, 0);
//		System.out.println("sites : " + sites.size());
		return ok(views.html.sites.reviewSites.render(sites));
	}
	
	@Transactional
	public static Result reviewNotStandardQueries(){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("notStandardQuery", true);
		parameters.put("approvedQuery", false);
		parameters.put("uncrawlableDomain", false);
		parameters.put("defunctPath", false);
		parameters.put("defunctDomain", false);
		parameters.put("unapproved", false);
		parameters.put("httpError", false);
		parameters.put("redirects", false);
		List<Site> sites = SitesDAO.getList(parameters, 50, 0);
//		System.out.println("sites : " + sites.size());
		return ok(views.html.sites.reviewSites.render(sites));
	}
	
//		DynamicForm data = Form.form().bindFromRequest();
//		String[] siteStatusStrings =  request().body().asFormUrlEncoded().get("siteStatuses[]");
		
	
	@Transactional
	public static Result logicalRedirects() {
		Multimap<String, Object> parameters = ArrayListMultimap.create();
		parameters.put("badUrlStructure", true);
		parameters.put("redirects", false);
		List<Long> badUrlIds = GeneralDAO.getKeyListAnd(Site.class, "siteId", parameters);
		
		parameters = ArrayListMultimap.create();
		parameters.put("notStandardQuery", true);
		parameters.put("redirects", false);
		List<Long> badQueryIds = GeneralDAO.getKeyListAnd(Site.class, "siteId", parameters);
		
		badUrlIds.addAll(badQueryIds);
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteLogic::tryRedirectByStructure, Site.class), 
				badUrlIds.stream().distinct(), 
				true);
		return ok("Queued URL validation for " + badUrlIds.size() + " sites");
	}
	
	@Transactional
	public static Result httpRedirects() {
		String queryString = "select s.siteId from Site s join s.urlCheck uc where uc.noChange = false and s.redirects = false";
		List<Long> siteIds = JPA.em().createQuery(queryString, Long.class).getResultList();
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind(SiteLogic::applyHttpCheck, Site.class), 
				siteIds.stream(), 
				true);
		return ok("Queued URL validation for " + siteIds.size() + " sites");
	}
	
	/************************************ Site Sets *****************************************************/
	
	@Transactional
	public static Result siteSetList(){
		List<SiteSet> siteSets = GeneralDAO.getAll(SiteSet.class);
		return ok(views.html.sites.sitesets.siteSetList.render(siteSets));
	}
	
	@Transactional
	public static Result viewSiteSet(long siteSetId){
		SiteSet siteSet =JPA.em().find(SiteSet.class, siteSetId); 
		return ok(views.html.sites.sitesets.siteSet.render(siteSet));
	}
	
	@Transactional
	public static Result siteSetDashboardStats(long siteSetId) {
		return ok(views.html.viewstats.viewStats.render(SiteSetLogic.getDashboard(siteSetId)));
	}
	
	
	@Transactional
	public static Result ensureFreshCrawl(long siteSetId) {
		SiteSet siteSet = JPA.em().find(SiteSet.class, siteSetId);
		Set<Long> siteIds = new HashSet<Long>();
		siteIds.addAll(SiteSetDao.sitesWithStaleCrawls(siteSet.getSiteSetId()));
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind((site) ->{
					SiteCrawlLogic.ensureCrawl(site, true, true, true);
				}, Site.class), 
				siteIds.stream().limit(10000), 
				true);
		return ok("Queued up " + siteIds.size() + " sites for ensuring a fresh crawl");
	}
	
	@Transactional
	public static Result ensureFreshInventoryCrawl(long siteSetId) {
		SiteSet siteSet = JPA.em().find(SiteSet.class, siteSetId);
		Set<Long> siteIds = new HashSet<Long>();
		siteIds.addAll(SiteSetDao.sitesWithStaleCrawls(siteSet.getSiteSetId()));
		siteIds.addAll(SiteSetDao.sitesWithBadInventoryCrawls(siteSet.getSiteSetId()));
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind((site) ->{
					SiteCrawlLogic.ensureCrawl(site, true, true, true);
				}, Site.class), 
				siteIds.stream().limit(5000), 
				true);
		return ok("Queued up " + siteIds.size() + " sites for ensuring a fresh inventory crawl");
	}
	
	@Transactional
	public static Result ensureNoErrorCrawl(long siteSetId) {
		SiteSet siteSet = JPA.em().find(SiteSet.class, siteSetId);
		Set<Long> siteIds = new HashSet<Long>();
		siteIds.addAll(SiteSetDao.sitesWithStaleCrawls(siteSet.getSiteSetId()));
		siteIds.addAll(SiteSetDao.sitesWithErrorCrawls(siteSet.getSiteSetId()));
		siteIds.addAll(SiteSetDao.sitesWithSmallCrawls(siteSet.getSiteSetId()));
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind((site) ->{
					SiteCrawlLogic.ensureCrawl(site, true, true, true);
				}, Site.class), 
				siteIds.stream().limit(5000), 
				true);
		return ok("Queued up " + siteIds.size() + " sites for ensuring a no error crawl");
	}
	
	@Transactional
	public static Result ensureGoodCrawl(long siteSetId) {
		SiteSet siteSet = JPA.em().find(SiteSet.class, siteSetId);
		Set<Long> siteIds = new HashSet<Long>();
		siteIds.addAll(SiteSetDao.sitesWithStaleCrawls(siteSet.getSiteSetId()));
		siteIds.addAll(SiteSetDao.sitesWithErrorCrawls(siteSet.getSiteSetId()));
		siteIds.addAll(SiteSetDao.sitesWithSmallCrawls(siteSet.getSiteSetId()));
		siteIds.addAll(SiteSetDao.sitesWithBadInventoryCrawls(siteSet.getSiteSetId()));
		Asyncleton.getInstance().runConsumerMaster(50, 
				JpaFunctionalBuilder.wrapConsumerInFind((site) ->{
					SiteCrawlLogic.ensureCrawl(site, true, true, true);
				}, Site.class), 
				siteIds.stream().limit(5000), 
				true);
		return ok("Queued up " + siteIds.size() + " sites for ensuring a good crawl");
	}
	
	@Transactional
	public static Result ensureFreshAnalysis(long siteSetId) {
		SiteSet siteSet = JPA.em().find(SiteSet.class, siteSetId);
		Set<Long> siteIds = new HashSet<Long>();
		siteIds.addAll(SiteSetDao.sitesWithFreshCrawls(siteSet.getSiteSetId()));
		Asyncleton.getInstance().runConsumerMaster(5, 
				JpaFunctionalBuilder.wrapConsumerInFind((site) ->{
					SiteCrawl siteCrawl = site.getLastCrawl();
					if(!AnalysisDao.hasFreshAnalysis(siteCrawl.getSiteCrawlId())){
						AnalysisControl.runFullAnalysis(siteCrawl);	
					}
				}, Site.class), 
				siteIds.stream().limit(50), 
				true);
		return ok("Queued up " + siteIds.size() + " sites for ensuring a fresh analysis");
	}
	
	@Transactional
	public static Result runAnalysis(long siteSetId) {
		SiteSet siteSet = JPA.em().find(SiteSet.class, siteSetId);
		Set<Long> siteIds = new HashSet<Long>();
		siteIds.addAll(SiteSetDao.sitesWithFreshCrawls(siteSet.getSiteSetId()));
		Asyncleton.getInstance().runConsumerMaster(5, 
				JpaFunctionalBuilder.wrapConsumerInFind((site) ->{
					SiteCrawl siteCrawl = site.getLastCrawl();
					AnalysisControl.runFullAnalysis(siteCrawl);	
				}, Site.class), 
				siteIds.stream(), 
				true);
		return ok("Queued up " + siteIds.size() + " sites for full analysis");
	}
	
	@Transactional
	public static Result ensureFreshInventoryAnalysis(long siteSetId) {
		SiteSet siteSet = JPA.em().find(SiteSet.class, siteSetId);
		Set<Long> siteIds = new HashSet<Long>();
		siteIds.addAll(SiteSetDao.sitesWithFreshCrawls(siteSet.getSiteSetId()));
		Asyncleton.getInstance().runConsumerMaster(5, 
				JpaFunctionalBuilder.wrapConsumerInFind((site) ->{
					SiteCrawl siteCrawl = site.getLastCrawl();
					if(!AnalysisDao.hasFreshAnalysis(siteCrawl.getSiteCrawlId())){
						AnalysisControl.runInventoryAnalysis(siteCrawl);	
					}
				}, Site.class), 
				siteIds.stream().limit(1), 
				true);
		return ok("Queued up " + siteIds.size() + " sites for ensuring a fresh analysis");
	}
	
	@Transactional
	public static Result runInventoryAnalysis(long siteSetId) {
		SiteSet siteSet = JPA.em().find(SiteSet.class, siteSetId);
		Set<Long> siteIds = new HashSet<Long>();
		siteIds.addAll(SiteSetDao.sitesWithFreshCrawls(siteSet.getSiteSetId()));
		Asyncleton.getInstance().runConsumerMaster(5, 
				JpaFunctionalBuilder.wrapConsumerInFind((site) ->{
					SiteCrawl siteCrawl = site.getLastCrawl();
					AnalysisControl.runInventoryAnalysis(siteCrawl);	
				}, Site.class), 
				siteIds.stream(), 
				true);
		return ok("Queued up " + siteIds.size() + " sites for ensuring a fresh analysis");
	}
	
	@Transactional
	public static Result generateDealerFireReport(long siteSetId) throws Exception {
		Report report = ReportGenerator.generateDealerFireReport(siteSetId);
		File csvReport = CSVGenerator.printReport(report);
		return ok("DealerFire report generated for SiteSet " + siteSetId + " at file location " + csvReport.getAbsolutePath());
	}
	
	
	//refresh last crawl
	//ensure has fresh crawl
	//ensure has fresh inventory crawl
	//ensure has inventory analysis
	//ensure has full analysis
	
}
