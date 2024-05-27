/*
 * AudioFileFilter.java
 *
 * Created on 6. November 2001, 16:39
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileFilters;

import java.io.File;

/**
 *
 * @author  Thomas
 * @version 
 */
public class MediaFileFilter extends javax.swing.filechooser.FileFilter {

    public static String[] ACCEPTED_SUFFIXES = {"wav","mp3", "mpg", "mov", "avi", "divx", "mp4", "aif", "aiff", "wmv", "ogg", "ogv", "oga", "ogx", "webm"};
    /** Creates new AudioFileFilter */
    public MediaFileFilter() {
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
    
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
	if (extension != null) {
            for (String ACCEPTED_SUFFIX : ACCEPTED_SUFFIXES) {
                if (extension.equalsIgnoreCase(ACCEPTED_SUFFIX)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // The description of this filter
    @Override
    public String getDescription() {
        return "Common media files (WAV, MP3, MPG, MPV, AVI, DIVX, MP4, AIF, WMV, OGG)";
    }    
    

}