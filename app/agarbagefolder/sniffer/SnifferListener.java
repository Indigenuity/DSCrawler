package agarbagefolder.sniffer;

import akka.actor.UntypedActor;

public class SnifferListener extends UntypedActor {

	@Override
	public void onReceive(Object arg0) throws Exception {
		
		System.out.println("Finished sniffing all the sites!");
		this.getContext().system().shutdown();
	}
}
