package newwork.urlcheck;

import java.util.concurrent.Callable;

import newwork.TerminalWorkOrder;
import persistence.UrlCheck;

public class UrlCheckWorkOrder extends TerminalWorkOrder<UrlCheck>{

	protected String seed;
	
	public UrlCheckWorkOrder(String seed) {
		super(UrlCheck.class);
		this.config = new UrlCheckConfig();
		this.seed = seed;
	}
	
	public UrlCheckWorkOrder(String seed, UrlCheckConfig config) {
		super(UrlCheck.class);
		this.config = new UrlCheckConfig();
		this.seed = seed;
	}

	@Override
	public Callable<UrlCheck> getInstructions() {
		return UrlCheckWorkInstructionsBuilder.getInstance().build(seed);
	}

	@Override
	protected void initProperties() {
		// TODO Auto-generated method stub
		
	}

}
