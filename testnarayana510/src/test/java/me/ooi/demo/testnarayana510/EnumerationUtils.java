package me.ooi.demo.testnarayana510;

import java.util.Enumeration;
import java.util.function.Consumer;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class EnumerationUtils {
	
	public static <T> void forEach(Enumeration<T> e, Consumer<? super T> c) {
		while (e.hasMoreElements()) {
			c.accept(e.nextElement());
		}
	}
	
}
