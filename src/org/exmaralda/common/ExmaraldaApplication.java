/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common;

/**
 *
 * @author thomas
 */
public interface ExmaraldaApplication {
    
	// would it hurt to have a getFrame()-Method for the main application frame? (Kai)
	
    /* returns the version string */
    public String getVersion();
    
    /** returns the application name */
    public String getApplicationName();
    
    /** returns the top level node for writing preferences */
    public String getPreferencesNode();
    
    /** returns the path to the welcome screen */
    public javax.swing.ImageIcon getWelcomeScreen();

    public void resetSettings();

}
