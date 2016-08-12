package async.work;

import java.util.UUID;

public class Order<T> {

	protected final Long uuid = UUID.randomUUID().getLeastSignificantBits();
	
	
	
	protected final T subject;
	
	public Order(T subject) {
		this.subject = subject;
	}

	public Long getUuid() {
		return uuid;
	}

	public T getSubject() {
		return subject;
	}

	
}
