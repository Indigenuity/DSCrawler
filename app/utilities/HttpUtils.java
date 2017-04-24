package utilities;

public class HttpUtils {
	public static boolean isRedirect(int statusCode){
		return statusCode >= 300 && statusCode < 400;
	}
	
	public static boolean isSuccessful(int statusCode){
		return statusCode >= 200 && statusCode < 300;
	}
	
	public static boolean isError(int statusCode){
		return statusCode >= 400;
	}
	
	public static boolean isUnsuccessful(int statusCode){
		return statusCode >= 300;
	}
}
