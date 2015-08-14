/*
 * BasicTranscriptionDTDFileFilter.java
 *
 * Created on 2. August 2001, 16:37
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
public class BasicTranscriptionDTDFileFilter extends javax.swing.filechooser.FileFilter {

    /** Creates new BasicTranscriptionDTDFileFilter */
    public BasicTranscriptionDTDFileFilter() {
    }

    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

	if (f.getName().equalsIgnoreCase("basic-transcription.dtd")) {
                    return true;
            } 
        return false;
    }
    
    // The description of this filter
    public String getDescription() {
        return "Document Type Definition (DTD)";
    }    

}