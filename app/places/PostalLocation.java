package places;

import java.util.Date;

public interface PostalLocation {

	public double getLatitude();
	public double getLongitude();
	public void setDateFetched(Date dateFetched);
	public Date getDateFetched();
	public String getPostalCode();
}
