/*
 * HTMLFileFilter.java
 *
 * Created on 19. Juni 2007, 16:39
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
public class HTMLFileFilter extends javax.swing.filechooser.FileFilter {
    
    /** Creates a new instance of HTMLFileFilter */
    public HTMLFileFilter() {
    }
    
    public boolean accept(File f) {
        String name = f.getAbsolutePath();
        return (f.isDirectory() || 
                name.substring(Math.max(0,name.length()-4)).equalsIgnoreCase("HTML") ||
                name.substring(Math.max(0,name.length()-3)).equalsIgnoreCase("HTM") 
                );
    }
    public String getDescription() {
        return "HTML files (*.html, *.htm)";
    }
    
}
