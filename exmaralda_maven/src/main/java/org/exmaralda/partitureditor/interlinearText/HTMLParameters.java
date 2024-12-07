/*
 * HTMLParameters.java
 *
 * Created on 1. Maerz 2002, 16:50
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class HTMLParameters extends OutputParameters {

    public String linkTarget = "_blank";
    public boolean outputAnchors = true;
    public boolean useJavaScript = false;
    public boolean makeLinks = true;
    public String onClickTarget = "";
    public long rowHeight = -1;
    // added for version 1.3.3., 21-Nov-2006
    public double stretchFactor = 1.4;
    
    
    //public String frame = "tblr";
    
    /** Creates new HTMLParameters */
    public HTMLParameters() {       
        setWidth(-1);       // i.e. no line breaks
    }
    
    @Override
    public void setSettings(String usernode){
        super.setSettings(usernode);
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(usernode);
        try {
            // changed for version 1.2.6. : minimum width is 200
            double preferencesWidth = Double.parseDouble(settings.get("HTML-Width","600"));
            if (preferencesWidth>0 && preferencesWidth<200){preferencesWidth=200;}
            setWidth(preferencesWidth);
        } catch (NumberFormatException nfe) {
            setWidth(600);
            nfe.printStackTrace();
        }
        
    }
      

}
