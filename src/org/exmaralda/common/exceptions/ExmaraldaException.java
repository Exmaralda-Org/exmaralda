/**
 * 
 */
package org.exmaralda.common.exceptions;

/**
 * @author woerner
 *
 */
public abstract class ExmaraldaException extends Exception {

	/**
	 * 
	 */
	public ExmaraldaException() {
	}

	/**
	 * @param message
	 */
	public ExmaraldaException(String message) {
		super(message);

	}

	/**
	 * @param cause
	 */
	public ExmaraldaException(Throwable cause) {
		super(cause);

	}

	/**
	 * @param message
	 * @param cause
	 */
	public ExmaraldaException(String message, Throwable cause) {
		super(message, cause);

	}

}
