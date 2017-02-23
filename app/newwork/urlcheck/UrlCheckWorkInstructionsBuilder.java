package newwork.urlcheck;

import persistence.UrlCheck;
import sites.UrlChecker;

import java.util.concurrent.Callable;

import newwork.TerminalWorkInstructionsBuilder;

public class UrlCheckWorkInstructionsBuilder implements TerminalWorkInstructionsBuilder<String, UrlCheck>{

	public static UrlCheckWorkInstructionsBuilder getInstance() {
		return new UrlCheckWorkInstructionsBuilder();
	}
	@Override
	public Callable<UrlCheck> build(String seed) {
		Callable<UrlCheck> instructions = () -> UrlChecker.checkUrl(seed);
		return instructions;
	}

	
}
