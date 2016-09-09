package places;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CanadaPostal implements PostalLocation{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long zipLocationId;
	
	public Double latitude;
	public Double longitude;
	public String code;
	public String name;
	public String province;
	
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
		return this.code;
	}

}
