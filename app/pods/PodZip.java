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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((franchisePod == null) ? 0 : franchisePod.hashCode());
		result = prime * result + ((indyPod == null) ? 0 : indyPod.hashCode());
		result = prime * result + ((primaryAreaCode == null) ? 0 : primaryAreaCode.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PodZip other = (PodZip) obj;
		if (franchisePod == null) {
			if (other.franchisePod != null)
				return false;
		} else if (!franchisePod.equals(other.franchisePod))
			return false;
		if (indyPod == null) {
			if (other.indyPod != null)
				return false;
		} else if (!indyPod.equals(other.indyPod))
			return false;
		if (primaryAreaCode == null) {
			if (other.primaryAreaCode != null)
				return false;
		} else if (!primaryAreaCode.equals(other.primaryAreaCode))
			return false;
		return true;
	}
	
	
	
	

}
