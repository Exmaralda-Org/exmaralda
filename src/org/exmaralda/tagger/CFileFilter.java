/*
 * Created on 22.06.2004 by woerner
 */
package org.exmaralda.tagger;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * coma2/org.sfb538.coma2.filters/CFileFilter.java *  * @author woerner
 */

public class CFileFilter extends FileFilter {
	private String[] fileExtensions;
	private String description = "Files";
	private boolean acceptDirs = true;

	public CFileFilter(String desc, String[] extensions, boolean accDirs) {
		super();
		fileExtensions = extensions;
		acceptDirs = accDirs;
		description = desc;
	}

	public boolean accept(File f) {
		if (f.isDirectory()) {
			if (acceptDirs) {
				return true;
			} else {
				return false;
			}
		} else {
			String filename = f.getName();
			if (filename.indexOf(".") > -1) {
				String fileXtension = filename.substring(filename
						.lastIndexOf(".")+1, filename.length());
				boolean tempResult=false;
				for (int i=0; i<fileExtensions.length; i++) {
					if (fileExtensions[i].equals(fileXtension)) {
						tempResult=true;
					}
				}
				return tempResult;
			} else {
				return false;
			}
		}
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return description;
	}

}