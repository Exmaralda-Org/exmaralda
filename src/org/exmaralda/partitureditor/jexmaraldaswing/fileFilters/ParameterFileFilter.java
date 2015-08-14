/*
 * EAFFileFilter.java
 *
 * Created on 12. November 2003, 17:36
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileFilters;

import java.io.File;


/**
 *
 * @author  thomas
 */
public class ParameterFileFilter extends javax.swing.filechooser.FileFilter {
    
    String[] suffix;
    String description = "";
    
    /** Creates a new instance of ParameterFileFilter */
    public ParameterFileFilter(String ext, String desc) {
        suffix = new String[1];
        suffix[0] = ext;
        description = desc;
    }

    /** Creates a new instance of ParameterFileFilter */
    public ParameterFileFilter(String[] ext, String desc) {
        suffix = ext;
        description = desc;
    }

    public String getSuffix(){
        return suffix[0];
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
            for (String suff : suffix){
                if (extension.equalsIgnoreCase(suff)){
                    return true;
                }
            }
        }
        return false;
    }

    // The description of this filter
    public String getDescription() {
        return description;
    }        
}
