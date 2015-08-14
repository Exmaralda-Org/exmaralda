/*
 * Class.java
 *
 * Created on 27. Februar 2002, 13:31
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class BreakParameters extends java.util.Properties {

    double width;
    public char[] wordEndChars = {' ','\'','-','\u00B7'};
    //public char[] wordEndChars = {' ','-','\u00B7'};
    public boolean[] respectWordBoundaries = {true, true, true, true, true, true};
    public int tolerance = 30;
    public int rightMarginBuffer = 0;
    public int pageBreakTolerance = 20;
    public int additionalLabelSpace = 5;    // in pixels
    public boolean includeSyncPoints = true;
    public boolean removeEmptyLines = true;
    public boolean numberItBundles = true;
    public boolean smoothRightBoundary = true;
    public boolean saveSpace = false;
    java.util.Vector cutChunks;
    
    /** Creates new Class */
    public BreakParameters() {
        cutChunks = new java.util.Vector();        
    }

    public double getWidth(){
        return width;
    }
    
    public void setWidth(double w){
        width = w;
    }
    
    public void setWidth(java.awt.print.PageFormat pageFormat){
        width = pageFormat.getImageableX();
        System.out.println("Width set to " + width + " pixels");
    }
    
    public int getTolerance(){
        return tolerance;
    }
    
    public void setTolerance(int t){
        tolerance = t;
    }
    
    public boolean getIncludeSyncPoints(){
        return includeSyncPoints;
    }
    
    public void setIncludeSyncPoints(boolean isp){
        includeSyncPoints = isp;
    }
    
    public char[] getWordEndChars(){
        return wordEndChars;
    }
    
    public void setWordEndChars(char[] wec){
        wordEndChars=wec;
    }
    
    public boolean respectWordBoundariesForBreakType(short bt){
        return respectWordBoundaries[bt];
    }
    
    public void setRespectWordBoundaries(   boolean nb_time, 
                                            boolean nb_notime, 
                                            boolean nb_dep, 
                                            boolean nb_link, 
                                            boolean b, 
                                            boolean img)    {
        boolean[] newValues = {nb_time, nb_notime, nb_dep, nb_link, b, img};             
        respectWordBoundaries = newValues;
    }
    
    public boolean isWordBoundary(char c){
        for (int pos=0; pos<this.wordEndChars.length; pos++){
            if (wordEndChars[pos]==c) return true;
        }
        return false;
    }
    
    public void setSettings(String usernode){
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(usernode);
        boolean rwb = (settings.get("Break-RespectWordBoundaries","TRUE").equalsIgnoreCase("TRUE"));
        setRespectWordBoundaries(rwb,rwb,rwb,rwb,rwb,rwb);
        
        // new 05-03-2012
        String wb = (settings.get("Break-WordBoundaries"," '-\u00B7"));        
        char[] wbArray = new char[wb.length()];
        for (int i=0; i<wb.length(); i++){
            wbArray[i]=wb.charAt(i);
        }
        setWordEndChars(wbArray);
        
        
        try {
            tolerance = Integer.parseInt(settings.get("Break-HorizontalTolerance","30"));
        } catch (NumberFormatException nfe) {
            tolerance = 30;
        }
        try {
            pageBreakTolerance = Integer.parseInt(settings.get("Break-VerticalTolerance","20"));
        } catch (NumberFormatException nfe) {
            pageBreakTolerance = 20;
        }
        try {
            additionalLabelSpace = Integer.parseInt(settings.get("Break-AdditionalLabelSpace","5"));
        } catch (NumberFormatException nfe) {
            additionalLabelSpace = 5;
        }
        includeSyncPoints = (settings.get("General-IncludeTimeline","TRUE").equalsIgnoreCase("TRUE"));
        removeEmptyLines = (settings.get("Break-RemoveEmptyLines","TRUE").equalsIgnoreCase("TRUE"));
        numberItBundles = (settings.get("Break-NumberPartiturAreas","TRUE").equalsIgnoreCase("TRUE"));
        smoothRightBoundary = (settings.get("Break-SmoothRightBoundaries","TRUE").equalsIgnoreCase("TRUE"));        
        saveSpace = (settings.get("Break-SaveSpace","FALSE").equalsIgnoreCase("TRUE")); 
        
    }
    
}
