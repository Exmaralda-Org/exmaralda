/**
 * 
 */
package org.exmaralda.teide.ui;

import java.io.File;
import java.io.FileFilter;

/**
 * @author woerner
 *
 */
public class FF implements FileFilter {

	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File pathname) {
		return false;
	}

}
