package utilities;

import java.util.ArrayList;

public class Tim {

	private static long start = 0;
	private static long last = 0;
	
	private static ArrayList<Long> times = new ArrayList<Long>();
	
	public static void start() {
		start = System.currentTimeMillis();
		last = start;
		System.out.println("Start: " + start);
	}
	
	public static void intermediate() {
		intermediate("");
	}
	
	public static void intermediate(String message) {
		long now = System.currentTimeMillis();
		long difference = now - last;
		System.out.println("intermediate time : " + difference + " (" + message + ")");
		times.add(difference);
		last = now;
	}
	
	public static void end() {
		long now = System.currentTimeMillis();
		long difference = now - last;
		times.add(difference);
		System.out.println("Ending times : ");
		int index = 1;
		for(Long item : times) {
			System.out.println("Leg " + index++ + ": " + item);
		}
		System.out.println("Total : " + (now - start));
		times.clear();
	}
	
	
	
}
