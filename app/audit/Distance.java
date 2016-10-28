package audit;

public class Distance<U, R> {

	private U item;
	private R distance;
	
	
	public Distance(U item, R distance) {
		this.item = item;
		this.distance = distance;
	}
	
	
	public U getItem() {
		return item;
	}
	public void setItem(U item) {
		this.item = item;
	}
	public R getDistance() {
		return distance;
	}
	public void setDistance(R distance) {
		this.distance = distance;
	}
	
	
}
