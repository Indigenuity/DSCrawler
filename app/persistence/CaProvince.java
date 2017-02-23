package persistence;

import javax.persistence.Entity;
import javax.persistence.Id;

//To reload all provinces, run method in GeneralDAO
@Entity	
public class CaProvince {
	
	@Id
	private String provinceCode;
	private String provinceName;
	private String projectCode;
	
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getProvinceCode() {
		return provinceCode;
	}
	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	
	
}
