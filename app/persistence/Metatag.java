package persistence;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;

import utilities.DSFormatter;

@Entity
public class Metatag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long metatagId;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String raw;
	
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String name;
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String content;
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String httpEquiv;
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String scheme;
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String itemprop;
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String property;
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String charset;
	
	public long getMetatagId() {
		return metatagId;
	}
	public void setMetatagId(long metatagId) {
		this.metatagId = metatagId;
	}
	public String getRaw() {
		return raw;
	}
	public void setRaw(String raw) {
		this.raw = DSFormatter.truncate(raw, 4000);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = DSFormatter.truncate(name, 1000);
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = DSFormatter.truncate(content, 1000);
	}
	public String getHttpEquiv() {
		return httpEquiv;
	}
	public void setHttpEquiv(String httpEquiv) {
		this.httpEquiv = DSFormatter.truncate(httpEquiv, 1000);
	}
	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = DSFormatter.truncate(scheme, 1000);
	}
	public String getItemprop() {
		return itemprop;
	}
	public void setItemprop(String itemprop) {
		this.itemprop = DSFormatter.truncate(itemprop, 1000);
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = DSFormatter.truncate(property, 1000);
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = DSFormatter.truncate(charset, 1000);
	}
	
	//Must both have content to be equals
	public static boolean contentEquals(Metatag first, Metatag second) {
		if(first == null || second == null){
			return false;
		}
		if(first.getContent() == null || second.getContent() == null){
			return false;
		}
		return StringUtils.equals(first.getContent(), second.getContent());
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((charset == null) ? 0 : charset.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result
				+ ((httpEquiv == null) ? 0 : httpEquiv.hashCode());
		result = prime * result
				+ ((itemprop == null) ? 0 : itemprop.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((property == null) ? 0 : property.hashCode());
		result = prime * result + ((raw == null) ? 0 : raw.hashCode());
		result = prime * result + ((scheme == null) ? 0 : scheme.hashCode());
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
		Metatag other = (Metatag) obj;
		
		if (metatagId > 0 && (metatagId == other.metatagId))
			return true;
		
		
		if (charset == null) {
			if (other.charset != null)
				return false;
		} else if (!charset.equals(other.charset))
			return false;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (httpEquiv == null) {
			if (other.httpEquiv != null)
				return false;
		} else if (!httpEquiv.equals(other.httpEquiv))
			return false;
		if (itemprop == null) {
			if (other.itemprop != null)
				return false;
		} else if (!itemprop.equals(other.itemprop))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		if (raw == null) {
			if (other.raw != null)
				return false;
		} else if (!raw.equals(other.raw))
			return false;
		if (scheme == null) {
			if (other.scheme != null)
				return false;
		} else if (!scheme.equals(other.scheme))
			return false;
		return true;
	}
	
	
	
}
