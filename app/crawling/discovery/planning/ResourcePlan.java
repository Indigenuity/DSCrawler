package crawling.discovery.planning;

import java.util.UUID;

import crawling.discovery.entities.Resource;

public class ResourcePlan<S, R extends Resource<?>> {

	protected final Long uuid = UUID.randomUUID().getLeastSignificantBits();
	
	protected Class<ResourceHandler<S, R>> handlerClazz;
	
	public ResourcePlan(Class<ResourceHandler<S, R>> handlerClazz) {
		this.handlerClazz = handlerClazz;
	}
	
	public ResourcePlan<S, R> copy(){
		return new ResourcePlan<S, R>(handlerClazz);
	}

	public Long getUuid() {
		return uuid;
	}
	
	
}
