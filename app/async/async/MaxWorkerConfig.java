package async.async;

public class MaxWorkerConfig {

	private final int maxWorkers;
	
	public MaxWorkerConfig(int maxWorkers){
		this.maxWorkers = maxWorkers;
	}

	public int getMaxWorkers() {
		return maxWorkers;
	}
}
