/*
 * XSLFileFilter.java
 *
 * Created on 6. Oktober 2003, 10:59
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileFilters;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 *
 * @author  thomas
 */
public class XSLFileFilter extends javax.swing.filechooser.FileFilter {
    
    /** Creates a new instance of XSLFileFilter */
    public XSLFileFilter() {
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
            if (extension.equalsIgnoreCase("XSL")){
                    return true;
            } else if (extension.equalsIgnoreCase("XSLT")){
                    return true;
            }
            else {
                return false;
            }
    	}

        return false;
    }
    
    // The description of this filter
    public String getDescription() {
        return "XSL Stylesheets (*.xsl, *.xslt)";
    }    
        
}
