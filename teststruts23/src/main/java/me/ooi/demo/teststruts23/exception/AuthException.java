package me.ooi.demo.teststruts23.exception;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class AuthException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AuthException(String message) {
		super(message);
    }
	
	public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
	
    public AuthException(Throwable cause) {
        super(cause);
    }
}
