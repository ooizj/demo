package me.ooi.demo.testquartz186_spring3.annotation;

/**
 * @author jun.zhao
 */
public class QuartzSetupException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public QuartzSetupException() {
		super() ; 
	}
	
	public QuartzSetupException(String message) {
		super(message) ; 
	}
	
	public QuartzSetupException(Throwable cause) {
		super(cause) ; 
	}
	
	public QuartzSetupException(String message, Throwable cause) {
		super(message, cause) ; 
	}

}
