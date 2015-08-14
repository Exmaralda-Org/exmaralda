/**
 *
 */
package org.exmaralda.teide.ui;

import java.io.File;
import java.util.Vector;

/**
 * @author woerner
 *
 */
public class TeideData {
	File rootDir;

	Vector<File> files;

	public TeideData() {
		rootDir = null;
		files = new Vector<File>();
	}
}
