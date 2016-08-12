package scaffolding;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.*;

import persistence.Site;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scaffolder {
	
	//Gets all primitives, wrappers and Strings, including any field marked with @Id or @Primary
	public static Map<String, Object> getSimpleFields(Object entity) {
		try{
			Map<String, Object> simples = new HashMap<String, Object>();
			Field[] fields = entity.getClass().getDeclaredFields();
			for(Field field : fields) {
				if(isSimple(field)){
					simples.put(field.getName(), BeanUtils.getProperty(entity, field.getName()));
				}
			}
			return simples;
		} catch(Exception e) {
			throw new IllegalStateException("Got Exception : " + e.getClass().getSimpleName() + " : " + e.getMessage());
		}
	}

	public static Map<String, Object> getBasicFields(Object entity) {
		try{
			Map<String, Object> basics = new HashMap<String, Object>();
			Field[] fields = entity.getClass().getDeclaredFields();
			for(Field field : fields) {
				if(isBasic(field)){
					basics.put(field.getName(), BeanUtils.getProperty(entity, field.getName()));
				}
			}
			return basics;
		} catch(Exception e) {
			throw new IllegalStateException("Got Exception : " + e.getClass().getSimpleName() + " : " + e.getMessage());
		}
	}
	
	public static Map<String, Object> getNonBasicFields(Object entity) {
		try{
			Map<String, Object> nonBasics = new HashMap<String, Object>();
			Field[] fields = entity.getClass().getDeclaredFields();
			for(Field field : fields) {
				if(isBasic(field)){
					nonBasics.put(field.getName(), BeanUtils.getProperty(entity, field.getName()));
				}
			}
			return nonBasics;
		} catch(Exception e) {
			throw new IllegalStateException("Got Exception : " + e.getClass().getSimpleName() + " : " + e.getMessage());
		}
	}
	
	public static Map<String, Long> getIdField(Object entity) {
		try{
			Field[] fields = entity.getClass().getDeclaredFields();
			Map<String, Long> idField = new HashMap<String, Long>();
			for(Field field : fields) {
				if(field.isAnnotationPresent(javax.persistence.Id.class)){
					idField.put(field.getName(), Long.parseLong(BeanUtils.getProperty(entity, field.getName())));
					return idField;
				}
			}
			return null;
		} catch(Exception e) {
			throw new IllegalStateException("Got Exception : " + e.getClass().getSimpleName() + " : " + e.getMessage());
		}
	}
	
	public static long getId(Object entity) {
		try{
			Field[] fields = entity.getClass().getDeclaredFields();
			for(Field field : fields) {
				if(field.isAnnotationPresent(javax.persistence.Id.class)){
					return Long.parseLong(BeanUtils.getProperty(entity, field.getName()));
				}
			}
			return -1;
		} catch(Exception e) {
			throw new IllegalStateException("Got Exception : " + e.getClass().getSimpleName() + " : " + e.getMessage());
		}
	}
	
	
	/* 
	 * A field is simple if:
	 * 		it is a primitive, wrapper class, or a string
	 */
	public static boolean isSimple(Field field) {
		if(field.getType().isPrimitive() || String.class.isAssignableFrom(field.getType()) || Boolean.class.isAssignableFrom(field.getType())) {
			return true;
		}
		return false;
	}
	
	/* 
	 * A field is basic if:
	 * 		it is a primitive, wrapper class, or a string
	 * 		it is not the Id for an Entity class
	 */
	public static boolean isBasic(Field field) {
		if(isSimple(field) && !field.isAnnotationPresent(javax.persistence.Id.class)) {
			return true;
		}
		return false;
	}
}
