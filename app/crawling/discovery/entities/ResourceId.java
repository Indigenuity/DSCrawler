package crawling.discovery.entities;

public abstract class ResourceId {
	public abstract boolean equals(Object other);
	public abstract int hashCode();
	public abstract String toString();
}
