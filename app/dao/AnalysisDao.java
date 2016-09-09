package dao;

import java.util.List;

import analysis.SiteCrawlAnalysis;
import datadefinitions.GeneralMatch;
import persistence.SiteCrawl;
import play.db.jpa.JPA;

public class AnalysisDao {

	public static SiteCrawlAnalysis getOrNew(SiteCrawl siteCrawl) {
		String queryString = "from SiteCrawlAnalysis sca where sca.siteCrawl = :siteCrawl";
		List<SiteCrawlAnalysis> existing = JPA.em().createQuery(queryString, SiteCrawlAnalysis.class)
				.setParameter("siteCrawl", siteCrawl).getResultList();
		
		if(existing.size() > 0){
			return existing.get(0);
		}
		
		SiteCrawlAnalysis newGuy = new SiteCrawlAnalysis(siteCrawl);
		JPA.em().persist(newGuy);
		return newGuy;
	}
	
	public static Long getCountGeneralMatch(GeneralMatch generalMatch) {
		String queryString = "select count(sca) from SiteCrawlAnalysis sca join sca.generalMatches gm where gm = :generalMatch";
		Long count = (Long) JPA.em().createQuery(queryString).setParameter("generalMatch", generalMatch).getSingleResult();
		return count;
	}
}
