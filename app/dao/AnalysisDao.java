package dao;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import analysis.SiteCrawlAnalysis;
import datadefinitions.GeneralMatch;
import datadefinitions.newdefinitions.LinkTextMatch;
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
	
	public static SiteCrawlAnalysis get(SiteCrawl siteCrawl) {
		String queryString = "from SiteCrawlAnalysis sca where sca.siteCrawl = :siteCrawl";
		List<SiteCrawlAnalysis> existing = JPA.em().createQuery(queryString, SiteCrawlAnalysis.class)
				.setParameter("siteCrawl", siteCrawl).getResultList();
		
		if(existing.size() > 0){
			return existing.get(0);
		}
		return null;
	}
	
	public static Long getCombinedCreditAppLinkMatches() {
		List<LinkTextMatch> matches = Arrays.asList(LinkTextMatch.values());
		String queryString = "select count(sc) from SiteCrawlAnalysis sca join sca.siteCrawl sc where sc.crawlDate > '2016-09-08' and exists (from SiteCrawlAnalysis sca2 join sca2.linkTextMatches ltm where ltm in :matches and sca2 = sca)";
		Long count = (Long) JPA.em().createQuery(queryString).setParameter("matches", matches).getSingleResult();
		return count;
	}
	
	public static Long getCombinedCreditAppMatches() {
		Set<GeneralMatch> generalMatches = GeneralMatch.creditAppMatches;
//		String queryString = "select count(sc) from SiteCrawlAnalysis sca join sca.generalMatches gm join sca.siteCrawl sc where sc.crawlDate > '2016-09-08' and gm in :generalMatches";
		String queryString = "select count(sc) from SiteCrawlAnalysis sca join sca.siteCrawl sc where sc.crawlDate > '2016-09-08' and exists (from SiteCrawlAnalysis sca2 join sca2.generalMatches gm where gm in :generalMatches and sca2 = sca)";
		Long count = (Long) JPA.em().createQuery(queryString).setParameter("generalMatches", generalMatches).getSingleResult();
		return count;
	}
	
	public static Long getCombinedGeneralCreditAppMatches() {
		Set<GeneralMatch> generalMatches = GeneralMatch.creditAppGeneralMatches;
		String queryString = "select count(sc) from SiteCrawlAnalysis sca join sca.siteCrawl sc where sc.crawlDate > '2016-09-08' and exists (from SiteCrawlAnalysis sca2 join sca2.generalMatches gm where gm in :generalMatches and sca2 = sca)";
		Long count = (Long) JPA.em().createQuery(queryString).setParameter("generalMatches", generalMatches).getSingleResult();
		return count;
	}
	
	public static Long getCountGeneralMatch(GeneralMatch generalMatch) {
		String queryString = "select count(sca) from SiteCrawlAnalysis sca join sca.generalMatches gm join sca.siteCrawl sc where sc.crawlDate > '2016-09-08' and gm = :generalMatch";
		Long count = (Long) JPA.em().createQuery(queryString).setParameter("generalMatch", generalMatch).getSingleResult();
		return count;
	}
	
	public static Long getCountLinkTextMatch(LinkTextMatch match) {
		String queryString = "select count(sca) from SiteCrawlAnalysis sca join sca.linkTextMatches ltm join sca.siteCrawl sc where sc.crawlDate > '2016-09-08' and ltm = :match";
		Long count = (Long) JPA.em().createQuery(queryString).setParameter("match", match).getSingleResult();
		return count;
	}
}
