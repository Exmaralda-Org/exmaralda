/*
 * ExmaraldaFileFilter.java
 *
 * Created on 1. August 2001, 14:00
 */

package org.exmaralda.partitureditor.deprecated;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class PraatFileFilter extends javax.swing.filechooser.FileFilter {

    /** Creates new ExmaraldaFileFilter */
    public PraatFileFilter() {
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
            if (extension.equalsIgnoreCase("textGrid")){
                    return true;
            } else {
                return false;
            }
    	}

        return false;
    }
    
    // The description of this filter
    public String getDescription() {
        return "Praat Text Grid (*.textGrid)";
    }    
    
}