/*
 * Class.java
 *
 * Created on 8. Maerz 2002, 14:25
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class RTFParameters extends PageOutputParameters{

    
    /** maps font names to numbers in the rtf fonttable */
    private java.util.Hashtable fontMapping;
    /** maps color names to numbers in the rtf colortable */
    private java.util.Hashtable colorMapping;
    
    double labelWidth;    
    boolean isFirstChunk = false;
    boolean chunkSizeIsCritical;
    public boolean useClFitText = false;
    public double criticalSizePercentage = 0.95;
    public boolean makePageBreaks = true;
    boolean isFirstParagraph = false;
    
    /** Creates new RTFParameters */
    public RTFParameters() {
        /* paper measures all stored in twips */
        super();
        setPaperMeasures(DINA4_VERTICAL);
        fontMapping = new java.util.Hashtable();
        colorMapping = new java.util.Hashtable();
        addColorMapping(frameColor);
    }
    
    public int addFontMapping(String name){
        if (fontMapping.containsKey(name))return ((Integer)fontMapping.get(name)).intValue();
        int no=0;
        while (fontMapping.containsValue(new Integer(no))){no++;}
        fontMapping.put(name,new Integer(no));
        return no;
    }
    
    public int getFontMapping(String name){
        if (!fontMapping.containsKey(name)) return -1;
        return ((Integer)fontMapping.get(name)).intValue();
    }
    
    public int addColorMapping(String color){
        color = color.toUpperCase();
        if (colorMapping.containsKey(color))return ((Integer)colorMapping.get(color)).intValue();
        int no=0;
        while (colorMapping.containsValue(new Integer(no))){no++;}
        colorMapping.put(color,new Integer(no));
        return no;    
    }
    
    public int getColorMapping(String color){
        color = color.toUpperCase();
        if (!colorMapping.containsKey(color)) return -1;
        return ((Integer)colorMapping.get(color)).intValue();
    }

    
    public String toRTFPaperMeasureString(){
        StringBuffer sb = new StringBuffer();
        sb.append("\\paperw" + Long.toString(Math.round(getPaperMeasure("paper:width",TWIPS_UNIT))));
        sb.append("\\paperh" + Long.toString(Math.round(getPaperMeasure("paper:height",TWIPS_UNIT))));
        sb.append("\\margl" + Long.toString(Math.round(getPaperMeasure("margin:left",TWIPS_UNIT))));
        sb.append("\\margr" + Long.toString(Math.round(getPaperMeasure("margin:right",TWIPS_UNIT))));
        sb.append("\\margt" + Long.toString(Math.round(getPaperMeasure("margin:top",TWIPS_UNIT))));
        sb.append("\\margb" + Long.toString(Math.round(getPaperMeasure("margin:bottom",TWIPS_UNIT))));
        if (landscape) {sb.append("\\landscape");}
        return sb.toString();
    }
    
    public void setFrameColor(String colorName){
        frameColor = colorName;
        addColorMapping(frameColor);
    }
    
    public void clearMappings(){
        fontMapping = new java.util.Hashtable();
        colorMapping = new java.util.Hashtable();
        addColorMapping(frameColor);
    }
    
    // 
    @Override
    public void setSettings(String usernode){
        super.setSettings(usernode);

        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(usernode);
        
        makePageBreaks = (settings.get("RTF-MakePageBreaks","FALSE").equalsIgnoreCase("TRUE"));
        useClFitText = (settings.get("RTF-Use-ClFitText","TRUE").equalsIgnoreCase("TRUE"));
        glueAdjacent = (settings.get("RTF-Glue-Adjacent","FALSE").equalsIgnoreCase("TRUE"));
        glueEmpty = (settings.get("RTF-Glue-Empty","FALSE").equalsIgnoreCase("TRUE"));
        try {
            criticalSizePercentage = Double.parseDouble(settings.get("RTF-CriticalSizePercentage","0.95"));
        } catch (NumberFormatException nfe) {
            criticalSizePercentage = 0.95;
        }
        try {
            rightMarginBuffer = Integer.parseInt(settings.get("RTF-RightMarginBuffer","0"));
        } catch (NumberFormatException nfe) {
            rightMarginBuffer = 0;
        }
    }
}
