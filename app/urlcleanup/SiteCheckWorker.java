package urlcleanup;

import java.util.function.Consumer;

import akka.actor.UntypedActor;
import async.work.Order;
import async.work.Result;
import dao.SitesDAO;
import persistence.Site;
import persistence.Site.SiteStatus;
import persistence.UrlCheck;
import play.db.jpa.JPA;
import utilities.UrlSniffer;

public class SiteCheckWorker extends UntypedActor {

	public class SiteCheck implements Consumer<Site>{
	
		@Override
		public void accept(Site site) {
			System.out.println("checking site : " + site.getHomepage());
			UrlCheck urlCheck = UrlSniffer.checkUrl(site.getHomepage());
			JPA.withTransaction( () -> {
				JPA.em().persist(urlCheck);
				site.setUrlCheck(urlCheck);
				
				if(urlCheck.isError()) {
					SitesDAO.markError(site);
				} else if(urlCheck.getStatusCode() >= 400){
					SitesDAO.markDefunct(site);
				} else if(urlCheck.isNoChange()){
					if(urlCheck.isAllApproved()){
						SitesDAO.approve(site);
					} else if(site.getSiteStatus() != SiteStatus.APPROVED){
						SitesDAO.review(site);
					}
				} else {
					if(urlCheck.isAllApproved()){
						SitesDAO.acceptRedirect(site, urlCheck.getResolvedSeed());
					} else if (urlCheck.isDomainApproved()){
						SitesDAO.reviewRedirect(site, urlCheck.getResolvedSeed());
					} else {
						SitesDAO.markDefunct(site);
					}
				}
				JPA.em().merge(site);
			});
		}
	}
	

	@Override
	public void onReceive(Object message) throws Exception {
		@SuppressWarnings("unchecked")
		Order<Site> order = (Order<Site>)message;
		Result result = new Result(order.getUuid());
		try{
			(new SiteCheck()).accept(order.getSubject());
		} catch(Exception e) {
			result.setMessage(e.getClass().getSimpleName() + " in SiteCheckWorker : " + e.getMessage());
			result.setFailure(true);
		} 
		getSender().tell(result, getSelf());
		
	}

}
