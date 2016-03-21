package async.async;

import play.Logger;
import play.db.jpa.JPA;


import akka.actor.UntypedActor;

public class MainListener extends UntypedActor {

	@Override
	public void onReceive(Object work) throws Exception {
		try {
			if(work instanceof String){
				JPA.withTransaction( () -> {
					Thread.sleep(3000);
					System.out.println("clearning");
					JPA.em().clear();
					System.gc();
					
				});
				
			}
//			System.out.println("Main Listener Got result : " + work);
			
			
		} catch (Throwable e) {
			Logger.error("Error while saving SiteInformation or SiteSummary in MainListener : " + e);
			System.out.println("Error in mainlistener : " + e);
			e.printStackTrace(); 
		} 
	}

}