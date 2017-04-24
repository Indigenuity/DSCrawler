package crawling.discovery.entities;

import java.util.ArrayList;
import java.util.List;

public class PreResource {

	protected final Object source;
	protected final Object value;
	protected final PreResource parent;
	protected final List<PreResource> children = new ArrayList<PreResource>();
	
	public PreResource(Object source, Object value, PreResource parent) {
		super();
		this.source = source;
		this.value = value;
		this.parent = parent;
	}

	public Object getSource() {
		return source;
	}

	public Object getValue() {
		return value;
	}

	public PreResource getParent() {
		return parent;
	}

	public List<PreResource> getChildren() {
		return new ArrayList<PreResource>(children);
	}

	public boolean add(PreResource e) {
		return children.add(e);
	}
}
