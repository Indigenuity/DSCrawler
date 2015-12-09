package analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import datadefinitions.OEM;
import datadefinitions.StringExtraction;

public class AnalysisUtils {

	public static boolean matchesPattern(Pattern pattern, String original) {
		if(pattern == null || original == null)
			return false;
		Matcher matcher = pattern.matcher(original);
		if(matcher.find()) {
			return true;
		}
		return false;
	}
	
	public static boolean containsMake(String original) {
		if(StringUtils.isEmpty(original))
			return false;
		
		for(OEM oem : OEM.values()){
			if(StringUtils.contains(original, oem.definition)){
				return true;
			}
		}
		
		return false;
	}
}
