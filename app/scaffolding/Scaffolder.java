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

public static Map<String, Object> getBasicFields(Object entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> basics = new HashMap<String, Object>();
		Field[] fields = entity.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(isBasic(field)){
				basics.put(field.getName(), BeanUtils.getProperty(entity, field.getName()));
			}
		}
		return basics;
	}
	
	public static Map<String, Object> getNonBasicFields(Object entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> nonBasics = new HashMap<String, Object>();
		Field[] fields = entity.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(isBasic(field)){
				nonBasics.put(field.getName(), BeanUtils.getProperty(entity, field.getName()));
			}
		}
		return nonBasics;
	}
	
	public static Map<String, Long> getIdField(Object entity) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Field[] fields = entity.getClass().getDeclaredFields();
		Map<String, Long> idField = new HashMap<String, Long>();
		for(Field field : fields) {
			if(field.isAnnotationPresent(javax.persistence.Id.class)){
				idField.put(field.getName(), Long.parseLong(BeanUtils.getProperty(entity, field.getName())));
				return idField;
			}
		}
		return null;
	}
	
	public static long getId(Object entity) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Field[] fields = entity.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(javax.persistence.Id.class)){
				return Long.parseLong(BeanUtils.getProperty(entity, field.getName()));
			}
		}
		return -1;
	}

	/* 
	 * A field is basic if:
	 * 		it is a primitive or a string
	 * 		it is not the Id for an Entity class
	 */
	public static boolean isBasic(Field field) {
		if((field.getType().isPrimitive() || String.class.isAssignableFrom(field.getType())) && !field.isAnnotationPresent(javax.persistence.Id.class)) {
			return true;
		}
		return false;
	}
}
