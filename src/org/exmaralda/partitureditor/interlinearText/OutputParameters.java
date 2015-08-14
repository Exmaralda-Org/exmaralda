/*
 * OutputParameters.java
 *
 * Created on 8. Maerz 2002, 14:23
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public abstract class OutputParameters extends BreakParameters {

    public static final short PX_UNIT = 0;
    public static final short CM_UNIT = 1;
    public static final short INCH_UNIT = 2;   
    public static final short TWIPS_UNIT = 3;

    public static final short DINA4_VERTICAL = 100;
    public static final short DINA4_HORIZONTAL = 101;
    public static final short DINA3_VERTICAL = 102;
    public static final short DINA3_HORIZONTAL = 103;

    public boolean putSyncPointsOutside = true;
    public boolean drawFrame = true;
    public boolean prependAdditionalInformation = true;

    boolean isFirstLine = false;
    boolean isLastLine = false;
    boolean isLastChunk = false;
    boolean isOutside = false;
    
    public String frame = "btlr";
    public String frameColor = "#R00G00B00";
    public String frameStyle = "Solid";
    public String additionalStuff = "";

    public boolean glueAdjacent = false;
    public boolean glueEmpty = false;
    

    SyncPoints syncPoints;
    Format emptyFormat;
    
    /** Creates new OutputParameters */
    public OutputParameters() {
        emptyFormat = new Format();
        emptyFormat.setProperty("font:size","2");
    }
    
    public java.awt.Color makeFrameColor(){
        try {
            // color is of the form #RxxGxxBxx where xx is a hexadecimal number
            int r = Integer.parseInt(frameColor.substring(2,4),16);
            int g = Integer.parseInt(frameColor.substring(5,7),16);
            int b = Integer.parseInt(frameColor.substring(8),16);
            return new java.awt.Color(r,g,b);
        } catch (Exception e){
            return java.awt.Color.white;        
        }        
    }

    public void setFrameColor(java.awt.Color color){
        String r = Integer.toHexString(color.getRed());
        if (r.length()==1) {r="0" + r;}
        String g = Integer.toHexString(color.getGreen());
        if (g.length()==1) {g="0" + g;}
        String b = Integer.toHexString(color.getBlue());
        if (b.length()==1) {b="0" + b;}
        frameColor = "#R" + r + "G" + g + "B" + b;        
    }
    
    public void setSettings(String usernode){
        super.setSettings(usernode);
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(usernode);
        putSyncPointsOutside = (settings.get("General-PutTimelineOutside","TRUE").equalsIgnoreCase("TRUE"));
        prependAdditionalInformation = (settings.get("General-PrependMetaInformation","TRUE").equalsIgnoreCase("TRUE"));
    }

}
