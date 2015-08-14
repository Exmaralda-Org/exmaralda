/*
 * Created on 02.03.2005 by woerner
 */
package org.exmaralda.tagger;

import java.util.EventListener;

/**
 * Ludger/z2tagger/TaggerEventListener.java
 * @author woerner
 * 
 */
public interface TaggerEventListener extends EventListener {
	public void TaggerEventHappened(TaggerEvent t);
}
