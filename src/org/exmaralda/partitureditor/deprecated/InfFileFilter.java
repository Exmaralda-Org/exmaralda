/*
 * InfFileFilter.java
 *
 * Created on 29. Oktober 2002, 12:05
 */

package org.exmaralda.partitureditor.deprecated;

import java.io.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class InfFileFilter extends javax.swing.filechooser.FileFilter {

    private static String[] ACCEPTED_SUFFIXES = {"inf"};
    /** Creates new AudioFileFilter */
    public InfFileFilter() {
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
        return "HIAT-DOS Infodateien [*.inf]";
    }    
    

}

