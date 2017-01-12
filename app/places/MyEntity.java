package places;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import datadefinitions.GeneralMatch;
import play.db.jpa.JPA;

@Entity
public class MyEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long myEntityId;
	
	@Enumerated(EnumType.STRING)
	public Condition myCondition = Condition.BAD;
	
	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch=FetchType.LAZY)
	public Set<Condition> conditions = new HashSet<Condition>();
	
	public enum Condition {
		GOOD, FAIR, BAD, SOL
	}
	
	public static void doSomeThings(){
		MyEntity bob = new MyEntity();
		bob.conditions.add(Condition.BAD);
		JPA.em().persist(bob);
		
		String queryString = "from MyEntity me where :condition member of me.conditions";
		List<MyEntity> things = JPA.em().createQuery(queryString, MyEntity.class).setParameter("condition", Condition.BAD).getResultList();
		System.out.println("things : " + things);
	}
}
