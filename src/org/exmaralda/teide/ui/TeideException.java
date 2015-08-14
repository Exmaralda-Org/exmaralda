/**
 * 
 */
package org.exmaralda.teide.ui;

/**
 * @author woerner
 *
 */
public class TeideException extends Exception {

	/**
	 * 
	 */
	public TeideException() {
		super();

	}

	/**
	 * @param message
	 * @param cause
	 */
	public TeideException(String message, Throwable cause) {
		super(message, cause);

	}

	/**
	 * @param message
	 */
	public TeideException(String message) {
		super(message);

	}

	/**
	 * @param cause
	 */
	public TeideException(Throwable cause) {
		super(cause);

	}

}
