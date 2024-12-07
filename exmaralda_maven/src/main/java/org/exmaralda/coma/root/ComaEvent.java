package org.exmaralda.coma.root;

import java.util.EventObject;

public class ComaEvent extends EventObject {
	/**
	 * @param arg0
	 */
	String message;

	public ComaEvent(Object o, String message) {
		super(o);
		this.message = message;
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		return message;
	}
}
