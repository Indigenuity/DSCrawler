package urlcleanup;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ListCheckConfig {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long listCheckConfigId;
	
	
	public enum InputType {
		SALESFORCE_REPORT	("Website", "Salesforce Unique ID"), 
		DATABASE			("Seed", "SiteId");
		
		public String seedColumnLabel;
		public String keyColumnLabel;
		
		private InputType(String seedColumnLabel, String keyColumnLabel){
			this.seedColumnLabel = seedColumnLabel;
			this.keyColumnLabel = keyColumnLabel;
		}
	}
	
	@Enumerated(EnumType.STRING)
	private InputType inputType;
	private String inputFilename;
	
	private Boolean useProxy = true;
	private String proxyUrl;
	private String proxyPort;
	
	
	public InputType getInputType() {
		return inputType;
	}
	public void setInputType(InputType inputType) {
		this.inputType = inputType;
	}
	public String getInputFilename() {
		return inputFilename;
	}
	public void setInputFilename(String inputFilename) {
		this.inputFilename = inputFilename;
	}
	public Boolean getUseProxy() {
		return useProxy;
	}
	public void setUseProxy(Boolean useProxy) {
		this.useProxy = useProxy;
	}
	public String getProxyUrl() {
		return proxyUrl;
	}
	public void setProxyUrl(String proxyUrl) {
		this.proxyUrl = proxyUrl;
	}
	public String getProxyPort() {
		return proxyPort;
	}
	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}
	
	
}
