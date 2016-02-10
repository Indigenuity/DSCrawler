package dao;

import java.util.List;

import javax.persistence.Query;

import async.work.infofetch.InfoFetch;
import play.db.jpa.JPA;

public class InfoFetchDAO {
	
	
	public static List<InfoFetch> getNeedReview(Long fetchJobId, int count, int offset){
		String query = "select info from FetchJob fj join fj.fetches info where fetchJobId = :fetchJobId "
				+ " and (info.urlCheck.workStatus = 'NEED_REVIEW' or info.siteUpdate.workStatus = 'NEED_REVIEW' or "
				+ "info.siteCrawl.workStatus = 'NEED_REVIEW' or info.amalgamation.workStatus = 'NEED_REVIEW' or "
				+ "info.textAnalysis.workStatus = 'NEED_REVIEW' or info.docAnalysis.workStatus = 'NEED_REVIEW' or "
				+ "info.metaAnalysis.workStatus = 'NEED_REVIEW' or info.placesPageFetch.workStatus = 'NEED_REVIEW')";
		Query q = JPA.em().createQuery(query, InfoFetch.class).setFirstResult(offset).setMaxResults(count);
		q.setParameter("fetchJobId", fetchJobId);
		List<InfoFetch> fetches = q.getResultList();
		return fetches;
	}
	
	public static List<InfoFetch> getNeedReview(Long fetchJobId, String subtaskName, int count, int offset){
		String query = "select info from FetchJob fj join fj.fetches info where fetchJobId = :fetchJobId "
				+ " and (info." + subtaskName + ".workStatus = 'NEEDS_REVIEW')";
		Query q = JPA.em().createQuery(query, InfoFetch.class).setFirstResult(offset).setMaxResults(count);
		q.setParameter("fetchJobId", fetchJobId);
		List<InfoFetch> fetches = q.getResultList();
		return fetches;
	}

}
