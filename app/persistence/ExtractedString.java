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

import persistence.converters.StringExtractionConverter;
import scala.reflect.internal.Trees.This;
import utilities.DSFormatter;
import datadefinitions.StringExtraction;

@Entity
public class ExtractedString {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long extractedStringId;
	
	
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String value = "";
	
	@Convert(converter = StringExtractionConverter.class)
	private StringExtraction stringType; 
	
	public ExtractedString(String value, StringExtraction stringType) {
		this.value = value;
		this.stringType = stringType;
	}
	
	public ExtractedString() { 
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		if(!(obj instanceof ExtractedString)){
			return false;
		}
		ExtractedString casted = (ExtractedString) obj;
		if(casted.getStringType() != this.getStringType()) {
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
				append(stringType).
				toHashCode();
	}
	
	public void validation() {
		if(DSFormatter.needsTruncation(this.value, 1000)) {
			this.value = DSFormatter.truncate(this.value, 1000);
		}
	}

	public long getExtractedStringId() {
		return extractedStringId;
	}

	public void setExtractedStringId(long extractedStringId) {
		this.extractedStringId = extractedStringId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = DSFormatter.truncate(value, 1000);
	}

	public StringExtraction getStringType() {
		return stringType;
	}

	public void setStringType(StringExtraction stringType) {
		this.stringType = stringType;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
	
	
}
