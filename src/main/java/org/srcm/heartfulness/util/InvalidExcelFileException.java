package org.srcm.heartfulness.util;

/**
 * Created by vsonnathi on 11/17/15.
 */

public class InvalidExcelFileException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidExcelFileException(String message) {
		super(message);
	}

	public InvalidExcelFileException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidExcelFileException(Throwable cause) {
		super(cause);
	}

	protected InvalidExcelFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
