package persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CanadaPostal {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long zipLocationId;
	
	public double latitude;
	public double longitude;
	public String code;
	public String name;
	public String province;

}
