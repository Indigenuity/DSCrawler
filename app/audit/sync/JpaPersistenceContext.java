package audit.sync;

import javax.persistence.EntityManager;

public class JpaPersistenceContext implements PersistenceContext{

	private EntityManager em;
	
	private Boolean deleteOutdate = false;
	
	public JpaPersistenceContext(EntityManager em) {
		this.em = em;
	}
	
	public JpaPersistenceContext(EntityManager em, Boolean deleteOutdate) {
		this.em = em;
		this.deleteOutdate = deleteOutdate;
	}
	
	@Override
	public <T> T insert(T record) {
//		System.out.println("inserting : " + record);
//		return record;
//		System.out.println("inserting : "+ record.getClass().getSimpleName());
		return em.merge(record);
	}

	@Override
	public <T> T update(T record) {
//		System.out.println("updating : " + record);
//		return record;
		return em.merge(record);
	}

	@Override
	public <T> T outdate(T record) {
//		System.out.println("outdating : " + record);
//		return record;
		if(deleteOutdate){
			em.remove(record);
		}
		return record;
	}

	@Override
	public <K, T> T fetch(K key, Class<T> clazz) {
		return em.find(clazz, key);
	}

	@Override
	public void flush() {
//		System.out.println("flushing");
		em.flush();
		em.clear();
	}

	@Override
	public void commit() {
		System.out.println("Committing");
		em.getTransaction().commit();
		em.getTransaction().begin();
	}
	
	

}
