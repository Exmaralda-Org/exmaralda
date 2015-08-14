/*
 * PreferencesUtilities.java
 *
 * Created on 16. Juni 2008, 09:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.utilities;


/**
 *
 * @author thomas
 */
public class PreferencesUtilities {
    
    public static String ROOT = "org.exmaralda.orthonormal";
    static java.util.prefs.Preferences preferences;
    
    static {
        preferences = java.util.prefs.Preferences.userRoot().node(ROOT);
    }

    public static boolean getBooleanProperty(String propertyName, boolean defaultValue) {
        return preferences.getBoolean(propertyName, defaultValue);
    }
    
    
    public static String getProperty(String propertyName, String defaultValue){
        return preferences.get(propertyName, defaultValue);
    }
    
    public static void setProperty(String propertyName, String propertyValue){
        preferences.put(propertyName, propertyValue);
    }
    
    
    
    
}
