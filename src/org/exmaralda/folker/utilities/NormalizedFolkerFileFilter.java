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
public class NormalizedFolkerFileFilter extends javax.swing.filechooser.FileFilter {
    
    /** Creates a new instance of XMLFileFilter */
    public NormalizedFolkerFileFilter() {
    }

    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".fln");
    }

    public String getDescription() {
        return "Normalisierte FOLKER-Transkriptionen (*.fln)";
    }
    
}
