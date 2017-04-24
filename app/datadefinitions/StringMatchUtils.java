package datadefinitions;

public class StringMatchUtils {
	public static boolean equalsAny(StringMatch[] matches, String text) {
		for(StringMatch match : matches) {
			if(text.equals(match.getDefinition())){
				return true;
			}
			
		}
		return false;
	}
	
	public static boolean containsAny(StringMatch[] matches, String text) {
		for(StringMatch match : matches) {
			if(text.contains(match.getDefinition())){
				return true;
			}
		}
		return false;
	}
	
	public static boolean matchesAny(StringMatch[] matches, String text) {
		for(StringMatch match : matches) {
			if(text.matches(match.getDefinition())){
				return true;
			}
		}
		return false;
	}
}
