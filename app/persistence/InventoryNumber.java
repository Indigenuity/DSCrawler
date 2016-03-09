package persistence;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import datadefinitions.newdefinitions.InventoryType;

@Entity
public class InventoryNumber {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long inventoryNumberId;
	
	@Enumerated(EnumType.STRING)
	private InventoryType inventoryType;
	private Integer count;
	
	
	public long getInventoryNumberId() {
		return inventoryNumberId;
	}
	public void setInventoryNumberId(long inventoryNumberId) {
		this.inventoryNumberId = inventoryNumberId;
	}
	public InventoryType getInventoryType() {
		return inventoryType;
	}
	public void setInventoryType(InventoryType inventoryType) {
		this.inventoryType = inventoryType;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
	@Override 
	public String toString(){
		return count + "";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((count == null) ? 0 : count.hashCode());
		result = prime * result + ((inventoryType == null) ? 0 : inventoryType.hashCode());
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
		InventoryNumber other = (InventoryNumber) obj;
		if (inventoryNumberId == other.inventoryNumberId)
			return true;
		
		if (count == null) {
			if (other.count != null)
				return false;
		} else if (!count.equals(other.count))
			return false;
		if (inventoryType != other.inventoryType)
			return false;
		return true;
	}
	
	
}
