/*
 * Created on 22.06.2004 by woerner
 */
package org.exmaralda.coma.actions;

import javax.swing.JFileChooser;

import org.exmaralda.coma.filters.ComaOpenFilter;

/**
 * coma2/org.sfb538.coma2.fileActions/CFileChooser.java
 * @author woerner
 * 
 */
public class CFileChooser extends JFileChooser {

	/**
	 * 
	 */
	public CFileChooser() {
		super();
	}


	public CFileChooser(String fileExtension, boolean loadSave, String title) {
		super();
		this.addChoosableFileFilter(new ComaOpenFilter());

	}
}
