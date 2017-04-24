package crawling.discovery.execution;

import java.util.UUID;

public class PlanId {

	private final long uuid = UUID.randomUUID().getLeastSignificantBits();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (uuid ^ (uuid >>> 32));
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
		PlanId other = (PlanId) obj;
		if (uuid != other.uuid)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PlanReference [uuid=" + uuid + "]";
	}
	
	
}
