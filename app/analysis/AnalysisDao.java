package analysis;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import datadefinitions.GeneralMatch;
import datadefinitions.newdefinitions.LinkTextMatch;
import global.Global;
import persistence.SiteCrawl;
import play.db.jpa.JPA;

public class AnalysisDao {
	
	private static final Object newAnalysisMutex = new Object();

	//Must synchronize for multiple threads trying to create the SiteCrawlAnalysis object.  We still don't want multiple
	//threads accessing the same analysis, but we can at least stop multiple analyses from being created
	public static SiteCrawlAnalysis getOrNew(SiteCrawl siteCrawl) {
		synchronized(newAnalysisMutex){
			String queryString = "from SiteCrawlAnalysis sca where sca.siteCrawl = :siteCrawl";
			List<SiteCrawlAnalysis> existing = JPA.em().createQuery(queryString, SiteCrawlAnalysis.class)
					.setParameter("siteCrawl", siteCrawl).getResultList();
			
			if(existing.size() > 0){
				return existing.get(0);
			}
			
			SiteCrawlAnalysis newGuy = new SiteCrawlAnalysis(siteCrawl);
			return JPA.em().merge(newGuy);
		}
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
	
	public static SiteCrawlAnalysis get(Long siteCrawlId) {
		String queryString = "select sca from SiteCrawlAnalysis sca join sca.siteCrawl sc where sc.siteCrawlId = :siteCrawlId";
		List<SiteCrawlAnalysis> existing = JPA.em().createQuery(queryString, SiteCrawlAnalysis.class)
				.setParameter("siteCrawlId", siteCrawlId).getResultList();
		
		if(existing.size() > 0){
			return existing.get(0);
		}
		return null;
	}
	
	public static boolean hasFreshAnalysis(Long siteCrawlId){
		String queryString = "select sca.siteCrawlAnalysisId from SiteCrawlAnalysis sca join sca.siteCrawl sc where sc.siteCrawlId = :siteCrawlId and sca.analysisDate > :staleDate";
		List<Long> ids = JPA.em().createQuery(queryString, Long.class)
				.setParameter("siteCrawlId", siteCrawlId)
				.setParameter("staleDate", Global.getStaleDate()).getResultList();
		return ids.size() > 0;
	}
	
	public static boolean hasFreshInventoryAnalysis(Long siteCrawlId){
		String queryString = "select sca.siteCrawlAnalysisId from SiteCrawlAnalysis sca join sca.siteCrawl sc where sc.siteCrawlId = :siteCrawlId and sca.analysisDate > :staleDate and size(sca.vehicles) > 0";
		List<Long> ids = JPA.em().createQuery(queryString, Long.class)
				.setParameter("siteCrawlId", siteCrawlId)
				.setParameter("staleDate", Global.getStaleDate()).getResultList();
		return ids.size() > 0;
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
