package com.coco.Exceptions;

public class NonExistentRegion extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NonExistentRegion() {
        super();
    }

    public NonExistentRegion(String message) {
        super(message);
    }

    public NonExistentRegion(String message, Throwable cause) {
        super(message, cause);
    }

    public NonExistentRegion(Throwable cause) {
        super(cause);
    }
}
