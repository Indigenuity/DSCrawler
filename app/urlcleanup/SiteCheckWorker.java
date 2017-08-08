package urlcleanup;

import java.util.function.Consumer;

import akka.actor.UntypedActor;
import async.work.Order;
import async.work.Result;
import persistence.Site;
import persistence.Site.SiteStatus;
import persistence.UrlCheck;
import play.db.jpa.JPA;
import sites.UrlChecker;
import sites.utilities.SiteLogic;

public class SiteCheckWorker extends UntypedActor {

	public class SiteCheck implements Consumer<Site>{
	
		@Override
		public void accept(Site site) {
			System.out.println("checking site : " + site.getHomepage());
			UrlCheck urlCheck = UrlChecker.checkUrl(site.getHomepage());
			JPA.withTransaction( () -> {
				JPA.em().persist(urlCheck);
				site.setUrlCheck(urlCheck);
				SiteLogic.applyHttpCheck(site);
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
