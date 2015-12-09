package persistence.initializers;

import org.hibernate.Hibernate;

import persistence.SiteCrawl;
import play.Logger;
import play.db.jpa.JPA;

public class Initializer {

	public static void siteCrawl(SiteCrawl siteCrawl) {
		try {
			JPA.withTransaction(new play.libs.F.Function0<Long>() {
				public Long apply() throws Throwable {
					
					Hibernate.initialize(siteCrawl);
					return 42L;
				}
			});
		} catch (Throwable e) {
			Logger.error("Thrown error in initializer : " + e);
		}
	}
}
