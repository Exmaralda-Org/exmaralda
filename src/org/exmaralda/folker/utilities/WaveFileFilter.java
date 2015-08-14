/*
 * XMLFileFilter.java
 *
 * Created on 14. Mai 2008, 15:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.utilities;

import java.io.File;

/**
 *
 * @author thomas
 */
public class WaveFileFilter extends javax.swing.filechooser.FileFilter {
    
    /** Creates a new instance of XMLFileFilter */
    public WaveFileFilter() {
    }

    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".wav");
    }

    public String getDescription() {
        return "WAV files (*.wav)";
    }
    
}
