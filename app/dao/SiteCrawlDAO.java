package dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

import persistence.Site;
import persistence.SiteCrawl;
import play.db.jpa.JPA;

public class SiteCrawlDAO {
	
//	private static final List<Field> siteCrawlBooleans = new ArrayList<Field>();
//	static {
//		Field[] allFields = SiteCrawl.class.getDeclaredFields();
//		for(Field f : allFields) {
//			if(f.getType() == boolean.class) {
//				siteCrawlBooleans.add(f);
//			}
//		}
//	}
	
	
	public static Integer getCount(long crawlSetId) {
		String query = "select count(*) from CrawlSet cs join cs.completedCrawls sc where cs.crawlSetId = " + crawlSetId;
		return GeneralDAO.getSingleInt(query, false);
	}
	
	public static List<SiteCrawl> getAll(long crawlSetId, int count, int offset) {
		String query = "select sc from CrawlSet cs join cs.completedCrawls sc where cs.crawlSetId = " + crawlSetId;
		return getList(query, false, count, offset);
	}
	
	public static Integer getFilesMovedCount(long crawlSetId) {
		String query = "select count(*) from CrawlSet cs join cs.completedCrawls sc where sc.filesMoved = true and cs.crawlSetId = " + crawlSetId;
		return GeneralDAO.getSingleInt(query, false);
	}
	
	public static List<SiteCrawl> getFilesMoved(long crawlSetId, int count, int offset) {
		String query = "select sc from CrawlSet cs join cs.completedCrawls sc where sc.filesMoved = true and cs.crawlSetId = " + crawlSetId;
		return getList(query, false, count, offset);
	}
	
	public static Integer getFilesDeletedCount(long crawlSetId) {
		String query = "select count(*) from CrawlSet cs join cs.completedCrawls sc where sc.filesDeleted = true and cs.crawlSetId = " + crawlSetId;
		return GeneralDAO.getSingleInt(query, false);
	}
	
	public static List<SiteCrawl> getFilesDeleted(long crawlSetId, int count, int offset) {
		String query = "select sc from CrawlSet cs join cs.completedCrawls sc where sc.filesDeleted = true and cs.crawlSetId = " + crawlSetId;
		return getList(query, false, count, offset);
	}
	
	public static Integer getCrawlingDoneCount(long crawlSetId) {
		String query = "select count(*) from CrawlSet cs join cs.completedCrawls sc where sc.crawlingDone = true and cs.crawlSetId = " + crawlSetId;
		return GeneralDAO.getSingleInt(query, false);
	}
	
	public static List<SiteCrawl> getCrawlingDone(long crawlSetId, int count, int offset) {
		String query = "select sc from CrawlSet cs join cs.completedCrawls sc where sc.crawlingDone = true and cs.crawlSetId = " + crawlSetId;
		return getList(query, false, count, offset);
	}
	
	public static Integer getReviewLaterCount(long crawlSetId) {
		String query = "select count(*) from CrawlSet cs join cs.completedCrawls sc where sc.reviewLater = true and cs.crawlSetId = " + crawlSetId;
		return GeneralDAO.getSingleInt(query, false);
	}
	
	public static List<SiteCrawl> getReviewLater(long crawlSetId, int count, int offset) {
		String query = "select sc from CrawlSet cs join cs.completedCrawls sc where sc.reviewLater = true and cs.crawlSetId = " + crawlSetId;
		return getList(query, false, count, offset);
	}
	
	public static Integer getMaxPagesReachedCount(long crawlSetId) {
		String query = "select count(*) from CrawlSet cs join cs.completedCrawls sc where sc.maxPagesReached = true and cs.crawlSetId = " + crawlSetId;
		return GeneralDAO.getSingleInt(query, false);
	}
	
	public static List<SiteCrawl> getMaxPagesReached(long crawlSetId, int count, int offset) {
		String query = "select sc from CrawlSet cs join cs.completedCrawls sc where sc.maxPagesReached = true and cs.crawlSetId = " + crawlSetId;
		return getList(query, false, count, offset);
	}
	
	public static Integer getAmalgamationDoneCount(long crawlSetId) {
		String query = "select count(*) from CrawlSet cs join cs.completedCrawls sc where sc.amalgamationDone = true and cs.crawlSetId = " + crawlSetId;
		return GeneralDAO.getSingleInt(query, false);
	}
	
	public static List<SiteCrawl> getAmalgamationDone(long crawlSetId, int count, int offset) {
		String query = "select sc from CrawlSet cs join cs.completedCrawls sc where sc.amalgamationDone = true and cs.crawlSetId = " + crawlSetId;
		return getList(query, false, count, offset);
	}
	
	public static Integer getDocAnalysisDoneCount(long crawlSetId) {
		String query = "select count(*) from CrawlSet cs join cs.completedCrawls sc where sc.docAnalysisDone = true and cs.crawlSetId = " + crawlSetId;
		return GeneralDAO.getSingleInt(query, false);
	}
	
	public static List<SiteCrawl> getDocAnalysisDone(long crawlSetId, int count, int offset) {
		String query = "select sc from CrawlSet cs join cs.completedCrawls sc where sc.docAnalysisDone = true and cs.crawlSetId = " + crawlSetId;
		return getList(query, false, count, offset);
	}
	
	public static Integer getTextAnalysisDoneCount(long crawlSetId) {
		String query = "select count(*) from CrawlSet cs join cs.completedCrawls sc where sc.textAnalysisDone = true and cs.crawlSetId = " + crawlSetId;
		return GeneralDAO.getSingleInt(query, false);
	}
	
	public static List<SiteCrawl> getTextAnalysisDone(long crawlSetId, int count, int offset) {
		String query = "select sc from CrawlSet cs join cs.completedCrawls sc where sc.textAnalysisDone = true and cs.crawlSetId = " + crawlSetId;
		return getList(query, false, count, offset);
	}
	
	public static Integer getMetaAnalysisDoneCount(long crawlSetId) {
		String query = "select count(*) from CrawlSet cs join cs.completedCrawls sc where sc.metaAnalysisDone = true and cs.crawlSetId = " + crawlSetId;
		return GeneralDAO.getSingleInt(query, false);
	}
	
	public static List<SiteCrawl> getMetaAnalysisDone(long crawlSetId, int count, int offset) {
		String query = "select sc from CrawlSet cs join cs.completedCrawls sc where sc.metaAnalysisDone = true and cs.crawlSetId = " + crawlSetId;
		return getList(query, false, count, offset);
	}
	
	public static Integer getInferencesDoneCount(long crawlSetId) {
		String query = "select count(*) from CrawlSet cs join cs.completedCrawls sc where sc.inferencesDone = true and cs.crawlSetId = " + crawlSetId;
		return GeneralDAO.getSingleInt(query, false);
	}
	
	public static List<SiteCrawl> getInferencesDone(long crawlSetId, int count, int offset) {
		String query = "select sc from CrawlSet cs join cs.completedCrawls sc where sc.inferencesDone = true and cs.crawlSetId = " + crawlSetId;
		return getList(query, false, count, offset);
	}
	
	public static Integer getMaybeDuplicateCount(long crawlSetId) {
		String query = "select count(*) from CrawlSet cs join cs.completedCrawls sc where sc.maybeDuplicate = true and cs.crawlSetId = " + crawlSetId;
		return GeneralDAO.getSingleInt(query, false);
	}
	
	public static List<SiteCrawl> getMaybeDuplicate(long crawlSetId, int count, int offset) {
		String query = "select sc from CrawlSet cs join cs.completedCrawls sc where sc.maybeDuplicate = true and cs.crawlSetId = " + crawlSetId;
		return getList(query, false, count, offset);
	}
	

	
	public static List<SiteCrawl> getList(String query, boolean isNative, int count, int offset) {
		if(isNative) {
			return JPA.em().createNativeQuery(query, SiteCrawl.class).setMaxResults(count).setFirstResult(offset).getResultList();
		}
		return JPA.em().createQuery(query, SiteCrawl.class).setMaxResults(count).setFirstResult(offset).getResultList();
	}
	
}
