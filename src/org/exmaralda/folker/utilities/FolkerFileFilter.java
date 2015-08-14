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
public class FolkerFileFilter extends javax.swing.filechooser.FileFilter {
    
    /** Creates a new instance of XMLFileFilter */
    public FolkerFileFilter() {
    }

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".flk") || f.getName().toLowerCase().endsWith(".fln");
    }

    @Override
    public String getDescription() {
        return FOLKERInternationalizer.getString("misc.folkertranscription");
    }
    
}
