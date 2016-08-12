package places;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity	
public class ZipLocation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long zipLocationId;
	
	public double latitude;
	public double longitude;
	public String zip;
	public String city;
	public String state;
	public String zipType;
	@Column(nullable = true)
	public boolean decomissioned;
}
