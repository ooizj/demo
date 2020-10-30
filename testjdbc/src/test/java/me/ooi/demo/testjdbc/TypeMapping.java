package me.ooi.demo.testjdbc;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jun.zhao
 */
public class TypeMapping {
	
	private static Map<Class<?>, Class<?>> mapping;
	static {
		mapping = new LinkedHashMap<Class<?>, Class<?>>();
		mapping.put(java.sql.Timestamp.class, java.util.Date.class);
	}
	
	public static Class<?> getMapping(Class<?> source) {
		if( !mapping.containsKey(source) ) {
			return source;
		}
		return mapping.get(source);
	}

}
