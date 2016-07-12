package newwork.urlcheck;

import java.util.Properties;

import global.Global;

public class UrlCheckConfig extends Properties {

	private static final long serialVersionUID = -1390185986162274472L;
	
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
	
	private static final Properties urlCheckDefaults = new Properties();
	static{
		urlCheckDefaults.setProperty("inputType", InputType.DATABASE.name());
		urlCheckDefaults.setProperty("inputFilename", "");
		urlCheckDefaults.setProperty("useProxy", Global.useProxy() + "");
		urlCheckDefaults.setProperty("proxyUrl", Global.getProxyUrl());
		urlCheckDefaults.setProperty("proxyPort", Global.getProxyPort() + "");
	}
	
	
	public UrlCheckConfig(){
		super(urlCheckDefaults);
	}

	

	public String getInputType() {
		return getProperty("inputType");
	}

	public void setInputType(String inputType) {
		setProperty("inputType", inputType);
	}

	public String getInputFilename() {
		return getProperty("inputFilename");
	}

	public void setInputFilename(String inputFilename) {
		setProperty("inputFilename", inputFilename);
	}

	public String getUseProxy() {
		return getProperty("useProxy");
	}

	public void setUseProxy(String useProxy) {
		setProperty("useProxy", useProxy);
	}

	public String getProxyUrl() {
		return getProperty("proxyUrl");
	}

	public void setProxyUrl(String proxyUrl) {
		setProperty("proxyUrl", proxyUrl);
	}

	public String getProxyPort() {
		return getProperty("proxyPort");
	}

	public void setProxyPort(String proxyPort) {
		setProperty("proxyPort", proxyPort);
	}

}
