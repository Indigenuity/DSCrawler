package audit;

import java.util.List;

import javax.persistence.TypedQuery;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;

import audit.sync.Sync;
import audit.sync.SyncType;
import datatransfer.reports.Report;
import persistence.GroupAccount;
import play.db.jpa.JPA;
import salesforce.persistence.SalesforceAccount;

public class AuditDao {

	public static List<Sync> getSyncsOfType(SyncType syncType, int maxResults, int offset){
		String queryString = "from Sync s where s.syncType = :syncType";
		TypedQuery<Sync> query = JPA.em().createQuery(queryString, Sync.class)
				.setParameter("syncType", syncType)
				.setMaxResults(maxResults)
				.setFirstResult(offset);
		return query.getResultList();
	}
	
	public static Report getGroupAccountSyncReport(Sync sync) {
		
		
		return getGroupAccountsSyncReport(getRevisionOfSync(sync));
	}
	
	public static Integer getRevisionOfSync(Sync sync) {
		AuditReader reader = AuditReaderFactory.get(JPA.em());
		List<Number> revisions = reader.getRevisions(Sync.class, sync.getSyncId());
		Integer revisionNumber = (Integer) revisions.get(0);
		return revisionNumber;
//		Object[] result = (Object[]) reader
//				.createQuery()
//				.forRevisionsOfEntity(Sync.class, false, true).add(AuditEntity.id().eq(sync.getSyncId()))
//				.getSingleResult();
//		
//		
//		
//		System.out.println("result class : " + result.getClass().getSimpleName());
//		if(result == null){
//			return -1;
//		}
//		return Integer.valueOf(result + "");
	}
	
	
	public static <T> Integer countInsertedAtRevision(Class<T> clazz, Integer revisionNumber){
		AuditReader reader = AuditReaderFactory.get(JPA.em());
		return (Integer) reader.createQuery()
			.forEntitiesModifiedAtRevision(clazz, revisionNumber)
			.add(AuditEntity.revisionType().eq(RevisionType.ADD))
			.addProjection(AuditEntity.id().count())
			.getSingleResult();
	}
	
	public static <T> Integer countUpdatedAtRevision(Class<T> clazz, Integer revisionNumber){
		AuditReader reader = AuditReaderFactory.get(JPA.em());
		return (Integer) reader.createQuery()
			.forEntitiesModifiedAtRevision(clazz, revisionNumber)
			.add(AuditEntity.revisionType().eq(RevisionType.MOD))
			.addProjection(AuditEntity.id().count())
			.getSingleResult();
	}
	
	public static <T> Integer countPropertyUpdatedAtRevision(Class<T> clazz, String propertyName, Integer revisionNumber){
		AuditReader reader = AuditReaderFactory.get(JPA.em());
		return (Integer) reader.createQuery()
			.forEntitiesModifiedAtRevision(clazz, revisionNumber)
			.add(AuditEntity.revisionType().eq(RevisionType.MOD))
			.add(AuditEntity.property(propertyName).hasChanged())
			.addProjection(AuditEntity.id().count())
			.getSingleResult();
	}
	
	public static <T> Integer countDeletedAtRevision(Class<T> clazz, Integer revisionNumber){
		AuditReader reader = AuditReaderFactory.get(JPA.em());
		return (Integer) reader.createQuery()
			.forEntitiesModifiedAtRevision(clazz, revisionNumber)
			.add(AuditEntity.revisionType().eq(RevisionType.DEL))
			.addProjection(AuditEntity.id().count())
			.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> getInsertedAtRevision(Class<T> clazz, Integer revisionNumber, Integer count, Integer offset){
		AuditReader reader = AuditReaderFactory.get(JPA.em());
		return (List<T>) reader.createQuery()
			.forEntitiesModifiedAtRevision(clazz, revisionNumber)
			.add(AuditEntity.revisionType().eq(RevisionType.ADD))
			.setMaxResults(count)
			.setFirstResult(offset)
			.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> getUpdatedAtRevision(Class<T> clazz, Integer revisionNumber, Integer count, Integer offset){
		AuditReader reader = AuditReaderFactory.get(JPA.em());
		return (List<T>) reader.createQuery()
			.forEntitiesModifiedAtRevision(clazz, revisionNumber)
			.add(AuditEntity.revisionType().eq(RevisionType.MOD))
			.setMaxResults(count)
			.setFirstResult(offset)
			.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> getPropertyUpdatedAtRevision(Class<T> clazz, String propertyName, Integer revisionNumber, Integer count, Integer offset){
		AuditReader reader = AuditReaderFactory.get(JPA.em());
		return (List<T>) reader.createQuery()
			.forEntitiesModifiedAtRevision(clazz, revisionNumber)
			.add(AuditEntity.revisionType().eq(RevisionType.MOD))
			.add(AuditEntity.property(propertyName).hasChanged())
			.setMaxResults(count)
			.setFirstResult(offset)
			.getResultList();
	}
	
	@SuppressWarnings("unchecked") 
	public static <T> List<T> getDeletedAtRevision(Class<T> clazz, Integer revisionNumber, Integer count, Integer offset){
		AuditReader reader = AuditReaderFactory.get(JPA.em());
		return (List<T>) reader.createQuery()
			.forEntitiesModifiedAtRevision(clazz, revisionNumber)
			.add(AuditEntity.revisionType().eq(RevisionType.DEL))
			.setMaxResults(count)
			.setFirstResult(offset)
			.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public static Report getGroupAccountsSyncReport(Integer revisionNumber){
		AuditReader reader = AuditReaderFactory.get(JPA.em());
		AuditQuery query = reader.createQuery()
				.forEntitiesModifiedAtRevision(GroupAccount.class, revisionNumber);
//		AuditQuery previousQuery = reader.createQuery()
//				.forEntitiesAtRevision(GroupAccount.class, revisionNumber - 1);
		
		List<GroupAccount> groupAccounts = query.getResultList();
//		List<GroupAccount> previousGroupAccounts = previousQuery.getResultList();
		
		groupAccounts.stream().forEach(ga -> System.out.println("Account Name : " + ga.getName()));
		return null;
	}
	
	public static Class<?> getType(Sync sync){
//		SyncType syncType = sync.getSyncType();
//		if(syncType == SyncType.DEALERS){
//			return Dealer.class;
//		} else if(syncType == SyncType.GROUP_ACCOUNTS){
//			return GroupAccount.class;
//		} else if(syncType == SyncType.SALESFORCE_ACCOUNTS){
//			return SalesforceAccount.class;
//		}
//		return null;
		return SalesforceAccount.class;
	}
}
