/*
 * RTFFileFilter.java
 *
 * Created on 24. September 2001, 11:59
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
public class RTFFileFilter extends javax.swing.filechooser.FileFilter {

    /** Creates new RTFFileFilter */
    public RTFFileFilter() {
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
            if (extension.equalsIgnoreCase("RTF")) {
                    return true;
            } else {
                return false;
            }
    	}

        return false;
    }
    
    // The description of this filter
    public String getDescription() {
        return "Microsoft Rich Text Format (RTF)";
    }    

}