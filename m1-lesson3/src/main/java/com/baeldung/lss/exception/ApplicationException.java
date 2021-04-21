package com.baeldung.lss.exception;

public class ApplicationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ApplicationException(String string) {
		super(string);
	}

}
