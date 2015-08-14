/*
 * LinkableFileFilter.java
 *
 * Created on 6. November 2001, 16:49
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileFilters;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;/**

/**
 *
 * @author  Thomas
 * @version 
 */
public class LinkableFileFilter extends javax.swing.filechooser.FileFilter {

    private static String[] ACCEPTED_SUFFIXES = {"jpg","bmp","png","gif","wav","mp3","au","mov","mpg","txt","html","pdf","rtf","doc"};

    /** Creates new LinkableFileFilter */
    public LinkableFileFilter() {
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
                for (int i=0; i<ACCEPTED_SUFFIXES.length; i++){
                    if (extension.equalsIgnoreCase(ACCEPTED_SUFFIXES[i])){
                        return true;
                    }
                }
        }
        return false;
    }
    
    // The description of this filter
    public String getDescription() {
        return "Standard linkable files";
    }    
    

}