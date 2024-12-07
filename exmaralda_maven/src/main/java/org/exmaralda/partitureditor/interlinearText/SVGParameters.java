/*
 * SVGParameters.java
 *
 * Created on 4. Februar 2005, 12:08
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  thomas
 */
public class SVGParameters extends OutputParameters{
    
    public double scaleFactor = 1.0;
    SyncPoint nextSyncPoint;
    
    
    /** Creates a new instance of SVGParameters */
    public SVGParameters() {
    }
    
    public void setSettings(String usernode){
        super.setSettings(usernode);
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(usernode);
        try {
            double preferencesWidth = Double.parseDouble(settings.get("SVG-Width","400"));
            if (preferencesWidth>0 && preferencesWidth<200){preferencesWidth=200;}
            setWidth(preferencesWidth);
        } catch (NumberFormatException nfe) {
            setWidth(-1);
        }
        try {
            double sf = Double.parseDouble(settings.get("SVG-ScaleFactor","1.0"));
            scaleFactor = sf;
        } catch (NumberFormatException nfe) {
            scaleFactor = 1.0;
        }
    }
    
}
