/*
 * EAFFileFilter.java
 *
 * Created on 12. November 2003, 17:36
 */

package org.exmaralda.partitureditor.deprecated;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;


/**
 *
 * @author  thomas
 */
public class TASXFileFilter extends javax.swing.filechooser.FileFilter {
    
    /** Creates a new instance of EAFFileFilter */
    public TASXFileFilter() {
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
            if (extension.equalsIgnoreCase("XML")){
                return true;
            } else {
                return false;
            }
    	}

        return false;
    }

    // The description of this filter
    public String getDescription() {
        return "TASX File (*.xml)";
    }        
}
