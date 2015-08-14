/*
 * ContributionTextPaneListener.java
 *
 * Created on 2. Juli 2008, 17:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import java.awt.event.MouseEvent;
import org.exmaralda.folker.data.Timepoint;

/**
 *
 * @author thomas
 */
public interface ContributionTextPaneListener {
    
    public void validateContribution();
    
    public void processTimepoint(org.exmaralda.folker.data.Timepoint tp);

    public void processTimepoint(Timepoint tp, MouseEvent e);
    
}
