/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.matchlist;

import java.io.File;
import org.jdom.Element;

/**
 *
 * @author Schmidt
 */
public interface MatchListListener {
 
    public void processMatchListEvent(File workingDirectory, Element contribution);
    
}
