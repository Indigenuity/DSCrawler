package crawling.nydmv;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.envers.Audited;

@Entity
@Audited(withModifiedFlag=true)
public class County {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long nyCountyId;
	
	private String name;
	private String value;
	
	private Date dateCrawled;
	
	
	public County() {}
	public County(String name) {
		this.name = name;
	}

	public long getNyCountyId() {
		return nyCountyId;
	}

	public void setNyCountyId(long nyCountyId) {
		this.nyCountyId = nyCountyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getDateCrawled() {
		return dateCrawled;
	}

	public void setDateCrawled(Date dateCrawled) {
		this.dateCrawled = dateCrawled;
	}
	
	
	
	
}
