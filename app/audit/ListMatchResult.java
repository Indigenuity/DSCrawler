package audit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMatchResult<T, U, R> {

	
	private List<T> first;
	private List<U> second;
	
	private List<T> noMatchFirst = new ArrayList<T>();
	private List<U> noMatchSecond = new ArrayList<U>();
	
	private Map<T, List<U>> matches = new HashMap<T, List<U>>();
	private Map<T, List<Distance<U, R>>> distances = new HashMap<T, List<Distance<U, R>>>();

	public List<T> getFirst() {
		return first;
	}

	public void setFirst(List<T> first) {
		this.first = first;
	}

	public List<U> getSecond() {
		return second;
	}

	public void setSecond(List<U> second) {
		this.second = second;
	}

	public Map<T, List<U>> getMatches() {
		return matches;
	}

	public void setMatches(Map<T, List<U>> matches) {
		this.matches = matches;
	}

	public Map<T, List<Distance<U, R>>> getDistances() {
		return distances;
	}

	public void setDistances(Map<T, List<Distance<U, R>>> distances) {
		this.distances = distances;
	}

	/**
	 * @return the noMatchFirst
	 */
	public List<T> getNoMatchFirst() {
		return noMatchFirst;
	}

	/**
	 * @param noMatchFirst the noMatchFirst to set
	 */
	public void setNoMatchFirst(List<T> noMatchFirst) {
		this.noMatchFirst = noMatchFirst;
	}

	/**
	 * @return the noMatchSecond
	 */
	public List<U> getNoMatchSecond() {
		return noMatchSecond;
	}

	/**
	 * @param noMatchSecond the noMatchSecond to set
	 */
	public void setNoMatchSecond(List<U> noMatchSecond) {
		this.noMatchSecond = noMatchSecond;
	}

	
	
	
}

