package me.ooi.demo.testquartz186_cluster_spring3;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

/**
 * @author jun.zhao
 */
public class TestUtils {
	
	private static final String DATE_TIME_PATTERN_STRING = "yyyy-MM-dd HH:mm:ss";
	private static final String TIME_PATTERN_STRING = "HH:mm:ss:SSS";
	private static final String DATE_PATTERN_STRING = "yyyy-MM-dd";
	
	private static ZoneId defaultZoneId = ZoneId.systemDefault();

	public static Date date(String dateStr) {
		if( DATE_PATTERN_STRING.length() == dateStr.length() ) {
			return Date.from(LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_PATTERN_STRING))
					.atStartOfDay(defaultZoneId).toInstant());
		}else if( DATE_TIME_PATTERN_STRING.length() == dateStr.length() ) {
			return Date.from(LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_STRING))
					.atZone(defaultZoneId).toInstant());
		}else {
			throw new RuntimeException("unsupported date");
		}
	}
	
	public static String timeFormat(Date date) {
		return DateTimeFormatter.ofPattern(TIME_PATTERN_STRING).format(date.toInstant().atZone(defaultZoneId).toLocalDateTime());
	}
	
	public static boolean timeEq(Date d1, Date d2) {
		return DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_STRING).format(d1.toInstant().atZone(defaultZoneId).toLocalDateTime())
				.equals(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_STRING).format(d2.toInstant().atZone(defaultZoneId).toLocalDateTime()));
	}
	
	public static boolean dayEq(Date d1, Date d2) {
		return DateTimeFormatter.ofPattern(DATE_PATTERN_STRING).format(d1.toInstant().atZone(defaultZoneId).toLocalDate())
				.equals(DateTimeFormatter.ofPattern(DATE_PATTERN_STRING).format(d2.toInstant().atZone(defaultZoneId).toLocalDate()));
	}
	
	public static Date minusMinutes(int minutes) {
		return new Date(new Date().getTime()-TimeUnit.MINUTES.toMillis(minutes));
	}
	
	public static void sleep(long millis) {
		try { Thread.sleep(millis); } catch (InterruptedException e) { }
	}
	
	public static void println(String msg) {
		System.out.println(timeFormat(new Date())+"\t"+msg);
	}
	
	public static class SizedStack<T> extends Stack<T> {
		private static final long serialVersionUID = 1L;
		
		private int maxSize;

	    public SizedStack(int size) {
	        super();
	        this.maxSize = size;
	    }

	    @Override
	    public T push(T object) {
	        //If the stack is too big, remove elements until it's the right size.
	        while (this.size() >= maxSize) {
	            this.remove(0);
	        }
	        return super.push(object);
	    }
	}

}
