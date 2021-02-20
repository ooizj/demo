package me.ooi.demo.testquartz186_spring3;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jun.zhao
 */
public class ClassUtils {
	
	/**
	 * 查找类中有某注解的方法
	 * @param clzz
	 * @param annotation
	 * @return
	 */
	public static List<Method> getDeclaredMethodsByAnnotation(Class<?> clzz, Class<? extends Annotation> annotation){
		List<Method> ret = new ArrayList<Method>(1);
		for (Method method : clzz.getDeclaredMethods()) {
			if( method.isAnnotationPresent(annotation) ) {
				ret.add(method);
			}
		}
		return ret;
	}
	
}
