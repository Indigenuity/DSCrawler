package pods;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@Entity
@Table(indexes = {@Index(name = "areaCode_index",  columnList="areaCode", unique = false),
		@Index(name = "primaryAreaCode_index",  columnList="primaryAreaCode", unique = false),
		@Index(name = "indyPod_index",  columnList="indyPod", unique = false),
		@Index(name = "franchisePod_index",  columnList="franchisePod", unique = false)})
@Audited(withModifiedFlag=true)
public class PodZip {
	
	@Id
	private String postalCode;
	private String provinceName;
	private String areaCode;
	private String primaryAreaCode;
	private String indyPod;
	private String franchisePod;
	
	
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getIndyPod() {
		return indyPod;
	}
	public void setIndyPod(String indyPod) {
		this.indyPod = indyPod;
	}
	public String getFranchisePod() {
		return franchisePod;
	}
	public void setFranchisePod(String franchisePod) {
		this.franchisePod = franchisePod;
	}
	public String getPrimaryAreaCode() {
		return primaryAreaCode;
	}
	public void setPrimaryAreaCode(String primaryAreaCode) {
		this.primaryAreaCode = primaryAreaCode;
	}
	
	

}
