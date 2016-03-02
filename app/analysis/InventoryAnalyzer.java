package analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import datadefinitions.newdefinitions.InventoryType;
import persistence.InventoryNumber;

public class InventoryAnalyzer {

	public static  Set<InventoryNumber> getInventoryNumbers(String text) {
		Set<InventoryNumber> invNumbers = new HashSet<InventoryNumber>();
		
		for(InventoryType enumElement : InventoryType.values()){
			Matcher matcher = enumElement.getPattern().matcher(text);
//			int count = 0;
	    	while (matcher.find()) {
//	    		System.out.println("found : " + matcher.group(1));
//	    		System.out.println("found count : " + ++count);
	    		InventoryNumber invNumber = new InventoryNumber();
	    		invNumber.setInventoryType(enumElement);
	    		invNumber.setCount(Integer.parseInt(matcher.group(1)));
	    		invNumbers.add(invNumber);
	    	}
		}
		
		return invNumbers;
	}
}
