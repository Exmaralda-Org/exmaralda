/*
 * Created on 15.04.2004 by woerner
 */
package org.exmaralda.coma.filters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * coma2/org.sfb538.coma2.filters/OpenFilter.java
 * 
 * @author woerner
 *  
 */
public class ComaFileFilter extends FileFilter {
	/**
	 * 
	 *  
	 */
	String description;
	String[] endings;
	boolean directories;
	public ComaFileFilter(String desc, String[] end, boolean dirs) {
		super();
		description = desc;
		directories = dirs;
		endings = end;
	}
	@Override
	public boolean accept(File f) {
		String filename = f.getName();
		return (filename.endsWith(".xml") | filename.endsWith(".coma") | f
				.isDirectory());
	}
	@Override
	public String getDescription() {
		return description;
	}
}