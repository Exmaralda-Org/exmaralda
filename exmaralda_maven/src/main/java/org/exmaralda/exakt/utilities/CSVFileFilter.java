/*
 * CSVFileFilter.java
 *
 * Created on 09. Mai 2019, 09:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.utilities;

import java.io.*;

/**
 *
 * @author daniel
 */
public class CSVFileFilter extends javax.swing.filechooser.FileFilter {
    
    /** Creates a new instance of CSVFileFilter */
    public CSVFileFilter() {
    }
    
    public boolean accept(File f) {
        String name = f.getAbsolutePath();
        return (f.isDirectory() || name.substring(Math.max(0,name.length()-3)).equalsIgnoreCase("CSV"));
    }
    public String getDescription() {
        return "CSV file (*.csv)";
    }
    
    
}
