package audit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class ListMatcher {
	
	
	public static <T, U, R> ListMatchResult<T, U, R> compareLists(List<T> first, List<U> second, BiFunction<T,U,Boolean> equalityFunction,
			BiFunction<T, U, R> distanceFunction){
		ListMatchResult<T, U, R> result = new ListMatchResult<T, U, R>();
		result.setFirst(first);
		result.setSecond(second);
		
		int count = 0;
		for(T t : first) {
			List<U> matches = new ArrayList<U>();
			List<Distance<U, R>> distances = new ArrayList<Distance<U,R>>();
			result.getMatches().put(t, matches);
			result.getDistances().put(t, distances);
			boolean hasMatch = false;
			for(U u : second) {
				if(equalityFunction.apply(t, u)){
					matches.add(u);
					hasMatch = true;
				} else {
					R distance = distanceFunction.apply(t, u);
					
					if(distance != null) {
						distances.add(new Distance<U, R>(u, distance));
						hasMatch = true;
					}
				}
				
			}
			if(!hasMatch) {
				result.getNoMatchFirst().add(t);
			}
			if(count++ %500 == 0){
				System.out.println("count : " + count);
			}
		}
		return result;
	}
	

}
