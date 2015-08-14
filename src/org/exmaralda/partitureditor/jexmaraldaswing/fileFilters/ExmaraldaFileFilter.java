/*
 * ExmaraldaFileFilter.java
 *
 * Created on 1. August 2001, 14:00
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileFilters;

import java.io.File;

/**
 *
 * @author  Thomas
 * @version 
 */
public class ExmaraldaFileFilter extends javax.swing.filechooser.FileFilter {

    /** Creates new ExmaraldaFileFilter */
    public ExmaraldaFileFilter() {
    }

    private String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
	if (extension != null) {
            if ((extension.equalsIgnoreCase("EXB")) || (extension.equalsIgnoreCase("XML"))){
                    return true;
            } else {
                return false;
            }
    	}

        return false;
    }
    
    // The description of this filter
    public String getDescription() {
        return "EXMARaLDA Basic Transcription (XML, EXB)";
    }    
    
}