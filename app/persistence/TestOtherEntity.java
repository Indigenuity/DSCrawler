package persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.envers.Audited;

@Entity
@Audited(withModifiedFlag=true)
public class TestOtherEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long testOtherEntityId;
	
	private String otherString = "The Other String";

	public long getTestOtherEntityId() {
		return testOtherEntityId;
	}

	public void setTestOtherEntityId(long testOtherEntityId) {
		this.testOtherEntityId = testOtherEntityId;
	}

	public String getOtherString() {
		return otherString;
	}

	public void setOtherString(String otherString) {
		this.otherString = otherString;
	}
	
	

}
