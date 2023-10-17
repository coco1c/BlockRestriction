package com.coco.Exceptions;

public class NonMatchingWorldException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NonMatchingWorldException() {
        super();
    }

    public NonMatchingWorldException(String message) {
        super(message);
    }

    public NonMatchingWorldException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonMatchingWorldException(Throwable cause) {
        super(cause);
    }
}
