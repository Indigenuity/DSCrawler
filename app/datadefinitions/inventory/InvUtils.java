package datadefinitions.inventory;

import java.net.URI;

import org.jsoup.nodes.Document;

public class InvUtils {

	public static InvType detectInvType(Document doc, URI uri){
		for(InvType invType : InvType.values()){
			if(invType.getTool().isOfThisType(doc, uri)){
				return invType;
			}
		}
		return null;
	}
	
	public static InvType detectRootInvType(Document doc, URI uri){
		for(InvType invType : InvType.values()){
			if(invType.getTool().isGeneralRoot(doc, uri)
					|| invType.getTool().isNewRoot(doc, uri)
					|| invType.getTool().isUsedRoot(doc, uri)){
				return invType;
			}
		}
		return null;
	}
	
	public static InvType detectInvType(URI uri){
		for(InvType invType : InvType.values()){
			if(invType.getTool().isOfThisType(uri)){
				return invType;
			}
		}
		return null;
	}
	
	public static InvType detectRootInvType(URI uri){
		for(InvType invType : InvType.values()){
			if(invType.getTool().isGeneralRoot(uri)
					|| invType.getTool().isNewRoot(uri)
					|| invType.getTool().isUsedRoot(uri)){
				return invType;
			}
		}
		return null;
	}
	
}
