package persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import utilities.DSFormatter;

@Entity
public class ImageTag {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long imageTagId;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String src;
	
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String alt;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String raw;

	public long getMetatagId() {
		return imageTagId;
	}

	public void setMetatagId(long imageTagId) {
		this.imageTagId = imageTagId;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = DSFormatter.truncate(src, 4000);
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = DSFormatter.truncate(alt, 1000);
	}

	public String getRaw() {
		return raw;
	}

	public void setRaw(String raw) {
		this.raw = DSFormatter.truncate(raw, 4000);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alt == null) ? 0 : alt.hashCode());
		result = prime * result + ((raw == null) ? 0 : raw.hashCode());
		result = prime * result + ((src == null) ? 0 : src.hashCode());
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
		ImageTag other = (ImageTag) obj;
		if (alt == null) {
			if (other.alt != null)
				return false;
		} else if (!alt.equals(other.alt))
			return false;
		if (raw == null) {
			if (other.raw != null)
				return false;
		} else if (!raw.equals(other.raw))
			return false;
		if (src == null) {
			if (other.src != null)
				return false;
		} else if (!src.equals(other.src))
			return false;
		return true;
	}
	
	
	
	
	
}
