/*
 * Created on 02.03.2005 by woerner
 */
package org.exmaralda.tagger;

import java.util.EventObject;

/**
 * Ludger/z2tagger/TaggerEvent.java
 * @author woerner
 * 
 */
public class TaggerEvent extends EventObject {
	/**
	 * @param arg0
	 */
	String message;
	public TaggerEvent(Object o, String message) {
		super(o);
		this.message = message;
		// TODO Auto-generated constructor stub
	}
	public String getMessage() {
		return message;
	}
}