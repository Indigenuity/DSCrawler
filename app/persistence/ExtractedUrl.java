package persistence;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.commons.lang.builder.HashCodeBuilder;

import persistence.converters.UrlExtractionConverter;
import utilities.DSFormatter;
import datadefinitions.UrlExtraction;

@Entity
public class ExtractedUrl {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long extractedUrlId;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String value = "";
	
	@Column
	@Convert(converter = UrlExtractionConverter.class)
	private UrlExtraction urlType; 
	
	public ExtractedUrl(String value, UrlExtraction urlType) {
		this.value = value;
		this.urlType = urlType;
	}
	
	private ExtractedUrl() {
		
	}
	
	public void validation() {
		if(DSFormatter.needsTruncation(this.value, 1000)) {
			System.out.println("truncating : " + this.value.length());
			this.value = DSFormatter.truncate(this.value, 1000);
		}
	}

	public long getExtractedUrlId() {
		return extractedUrlId;
	}

	public void setExtractedUrlId(long extractedUrlId) {
		this.extractedUrlId = extractedUrlId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = DSFormatter.truncate(value, 4000);
	}

	public UrlExtraction getUrlType() {
		return urlType;
	}

	public void setUrlType(UrlExtraction urlType) {
		this.urlType = urlType;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		if(!(obj instanceof ExtractedUrl)){
			return false;
		}
		ExtractedUrl casted = (ExtractedUrl) obj;
		if(casted.getUrlType() != this.getUrlType()) {
			return false;
		}
		if(!casted.getValue().equals(this.getValue())){
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(7, 3).
				append(value).
				append(urlType).
				toHashCode();
	}
	 
	
}
