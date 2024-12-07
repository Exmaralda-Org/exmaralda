/*
 * XMLFileFilter.java
 *
 * Created on 19. Juni 2007, 16:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.utilities;

import java.io.*;

/**
 *
 * @author thomas
 */
public class XMLFileFilter extends javax.swing.filechooser.FileFilter {
    
    /** Creates a new instance of XMLFileFilter */
    public XMLFileFilter() {
    }
    
    public boolean accept(File f) {
        String name = f.getAbsolutePath();
        return (f.isDirectory() || name.substring(Math.max(0,name.length()-3)).equalsIgnoreCase("XML"));
    }
    public String getDescription() {
        return "XML files (*.xml)";
    }
    
    
}
