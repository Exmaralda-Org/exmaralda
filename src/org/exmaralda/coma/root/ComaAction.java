/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.root;

import javax.swing.AbstractAction;

/**
 * coma2/org.sfb538.coma2/ComaActions.java
 * 
 * @author woerner
 */

public abstract class ComaAction extends AbstractAction {

	public Coma coma;

	public ComaData data;

	public ComaAction(String text, javax.swing.ImageIcon icon, Coma c) {
		super(text, icon);
		coma = c;
		data = c.getData();
	}

	public ComaAction(String text, Coma c) {
		super(text);
		coma = c;
	}

}
