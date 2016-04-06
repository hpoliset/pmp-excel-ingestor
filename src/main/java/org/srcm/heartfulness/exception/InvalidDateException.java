/**
 * 
 */
package org.srcm.heartfulness.exception;

/**
 * @author Koustav Dutta
 *
 */
public class InvalidDateException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public InvalidDateException(String message) {
		super(message);
	}

	public InvalidDateException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidDateException(Throwable cause) {
		super(cause);
	}

	protected InvalidDateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	

}
