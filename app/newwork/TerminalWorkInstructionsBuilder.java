package newwork;

import java.util.concurrent.Callable;

public interface TerminalWorkInstructionsBuilder<I, O> {

	public Callable<O> build(I input);
}
