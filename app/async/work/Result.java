package async.work;

public class Result {
	protected final Long uuid;
	protected Boolean failure = false;
	protected String message;
	
	public Result(Long uuid) {
		super();
		this.uuid = uuid;
	}

	public Long getUuid() {
		return uuid;
	}

	public Boolean getFailure() {
		return failure;
	}

	public void setFailure(Boolean failure) {
		this.failure = failure;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
