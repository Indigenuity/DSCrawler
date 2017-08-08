package async.callables;

public interface Callback<V> {

	default void onFailure(Throwable t){
	}
	
	default void onSuccess(){
	}
	
	default void onSuccess(V result){
	}
}
