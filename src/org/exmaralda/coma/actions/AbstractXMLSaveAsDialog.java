/*
 * Created on 17.06.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.io.File;

import javax.swing.JFileChooser;

/**
 * coma2/org.sfb538.coma2.fileActions/AbstractXMLSaveAsDialog.java
 * 
 * @author woerner
 * 
 */
public class AbstractXMLSaveAsDialog extends JFileChooser {
	String extension = "coma";

	/**
	 * 
	 */
	public AbstractXMLSaveAsDialog() {
		super();
	}

	/**
	 * @param arg0
	 */
	public AbstractXMLSaveAsDialog(String ext) {
		super();
		extension = ext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JFileChooser#getSelectedFile()
	 */
	@Override
	public File getSelectedFile() {
		File sgSF = super.getSelectedFile();
		if (sgSF != null) {
			if (sgSF.getName().endsWith(extension)) {
				return sgSF;
			} else {
				sgSF = new File(sgSF.getAbsolutePath() + "." + extension);
				return sgSF;
			}
		} else {
			return null;
		}
	}
}
