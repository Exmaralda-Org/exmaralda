/*
 * SearchProgressMonitor.java
 *
 * Created on 15. Januar 2007, 15:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search.swing;

import java.awt.Component;
import org.exmaralda.exakt.search.*;

/**
 *
 * @author thomas
 */
public class SearchProgressMonitor extends javax.swing.ProgressMonitor implements SearchListenerInterface {
    
    public SearchProgressMonitor(Component parentComponent, Object message, String note){
        super(parentComponent, message, note, 0, 100);
    }
    
    public void processSearchEvent(SearchEvent se) {        
        this.setProgress((int)(Math.round(100.0*se.getProgress())));
        this.setNote((String)(se.getData()));
    }
    
}
