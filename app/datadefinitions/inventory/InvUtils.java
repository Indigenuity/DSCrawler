package datadefinitions.inventory;

import java.net.URI;

import org.jsoup.nodes.Document;

public class InvUtils {

	public static InvType getInvType(Document doc, URI uri){
		for(InvType invType : InvType.values()){
			if(invType.getTool().matchesType(doc, uri)){
				return invType;
			}
		}
		return null;
	}
}
