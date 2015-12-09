package persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.commons.lang.builder.HashCodeBuilder;

import utilities.DSFormatter;

@Entity
public class Staff {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long staffId;

	protected String name;
	protected String title;
	protected String email;
	protected String phone;
	protected String fn;
	protected String cell;
	protected String other;
	 
	@PreUpdate
	@PrePersist
	public void validation() {
		this.name = DSFormatter.truncate(name, 255);
		this.title = DSFormatter.truncate(title, 255);
		this.email = DSFormatter.truncate(email, 255);
		this.phone = DSFormatter.truncate(phone, 255);
		this.fn = DSFormatter.truncate(fn, 255);
		this.cell = DSFormatter.truncate(cell, 255);
		this.other = DSFormatter.truncate(other, 255);
	}
	
	@Override 
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(!(obj instanceof Staff))
			return false;
		return this.hashCode() == obj.hashCode();
	}
	
	@Override
	public int hashCode() {
		final int prime = 3;
		return new HashCodeBuilder(7, 3).
				append(name).
				append(title).
				append(email).
				append(phone).
				append(fn).
				append(cell).
				append(other).
				toHashCode();
	}

	public long getStaffId() {
		return staffId;
	}

	public void setStaffId(long staffId) {
		this.staffId = staffId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = DSFormatter.truncate(name, 255);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = DSFormatter.truncate(title, 255);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = DSFormatter.truncate(email, 255);
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = DSFormatter.truncate(phone, 255);
	}

	public String getFn() {
		return fn;
	}

	public void setFn(String fn) {
		this.fn = DSFormatter.truncate(fn, 255);
	}

	public String getCell() {
		return cell;
	}

	public void setCell(String cell) {
		this.cell = DSFormatter.truncate(cell, 255);
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = DSFormatter.truncate(other, 255);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(this.getName() != null){
			sb.append(this.getName());
			sb.append(",");
		}
		if(this.getTitle() != null){
			sb.append(this.getTitle());
			sb.append(",");
		}
		if(this.getEmail() != null){
			sb.append(this.getEmail());
			sb.append(",");
		}
		if(this.getPhone() != null){
			sb.append(this.getPhone());
			sb.append(",");
		}
		if(this.getFn() != null){
			sb.append(this.getFn());
			sb.append(",");
		}
		if(this.getCell() != null){
			sb.append(this.getCell());
			sb.append(",");
		}
		if(this.getOther() != null){
			sb.append(this.getOther());
		}
		
		return sb.toString();
	}
	
}
