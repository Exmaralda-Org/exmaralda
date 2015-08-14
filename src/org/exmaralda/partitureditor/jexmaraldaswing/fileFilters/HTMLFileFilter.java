/*
 * HTMLFileFilter.java
 *
 * Created on 24. September 2001, 11:52
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileFilters;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;


/**
 *
 * @author  Thomas
 * @version 
 */
public class HTMLFileFilter extends javax.swing.filechooser.FileFilter {

    /** Creates new HTMLFileFilter */
    public HTMLFileFilter() {
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
            if ((extension.equalsIgnoreCase("HTML")) || (extension.equalsIgnoreCase("HTM"))){
                    return true;
            } else {
                return false;
            }
    	}

        return false;
    }
    
    // The description of this filter
    public String getDescription() {
        return "Hypertext Markup Language (HTML)";
    }    
    

}