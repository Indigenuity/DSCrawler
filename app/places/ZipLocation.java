package places;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity	
public class ZipLocation implements PostalLocation {

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
	
	public Date dateFetched;
	
	@Override
	public double getLatitude() {
		return latitude;
	}
	@Override
	public double getLongitude() {
		return longitude;
	}
	@Override
	public void setDateFetched(Date dateFetched) {
		this.dateFetched = dateFetched;
	}
	@Override
	public Date getDateFetched() {
		return this.dateFetched;
	}
	@Override
	public String getPostalCode() {
		return this.zip;
	}
}
