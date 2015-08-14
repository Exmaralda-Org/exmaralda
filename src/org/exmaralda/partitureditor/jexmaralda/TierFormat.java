package org.exmaralda.partitureditor.jexmaralda;

import java.awt.*;
import java.io.*;
/*
 * TierFormat.java
 *
 * Created on 20. April 2001, 16:24
 */



/**
 *
 * @author  Thomas
 * @version 
 */
public class TierFormat extends java.util.Properties {

    private String tierref;


    private static String[] AWT_COLOR_NAMES = {"white", "lightGray", "gray", "darkGray", "black", "red", "pink", "orange",
        "yellow", "green", "magenta", "cyan", "blue"};
    
    private static String[] CSS_COLOR_NAMES = {"White", "Silver", "Silver", "Gray", "Black", "Red", "Fuchsia", "White", "Yellow", 
        "Green", "Fuchsia", "Aqua", "Blue"};
        
    private static java.awt.Color[] COLOR_VALUES = {java.awt.Color.white, java.awt.Color.lightGray, java.awt.Color.gray, java.awt.Color.darkGray, 
        java.awt.Color.black, java.awt.Color.red, java.awt.Color.pink, java.awt.Color.orange,
        java.awt.Color.yellow, java.awt.Color.green, java.awt.Color.magenta, java.awt.Color.cyan, java.awt.Color.blue};
    
    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new TierFormat */
    public TierFormat() {
        tierref = new String();
        this.setProperty("font-face","Plain");                  
        this.setProperty("font-size", "12"); 
        this.setProperty("text-alignment","Left");              
        this.setProperty("font-color","black");                 
        this.setProperty("bg-color","white");                  
        this.setProperty("font-name","Times New Roman");                   
        this.setProperty("chunk-border","");                   
        this.setProperty("chunk-border-color","#R00G00B00");                   
        this.setProperty("chunk-border-style","solid");           
        this.setProperty("row-height-calculation","Generous");        
        this.setProperty("fixed-row-height","10");
    }
    
    public TierFormat(String defaultFont){
        this();
        this.setProperty("font-name",defaultFont);                           
    }

    /** Creates new TierFormat with tierref tr, styleName sn, size s and so on */
    public TierFormat(String tr, String sn, int s, String an, String tcn, String bgcn, String fn) {
        this();
        tierref = new String(tr);
        this.setProperty("font-face",sn);                  
        this.setProperty("font-size", Integer.toString(s));
        this.setProperty("text-alignment",an);             
        this.setProperty("font-color",tcn);                
        this.setProperty("bg-color",bgcn);                 
        this.setProperty("font-name",fn);                  
    }
    
    /** Creates new standard TierFormat for the given tier */
    public TierFormat(String type, String tr){
        this();
        tierref = new String(tr);
        this.setProperty("font-color","black");            
        this.setProperty("bg-color","white");              
        if (type.equals("t")){
            this.setProperty("font-face","Plain");         
            this.setProperty("font-size","12");            
            this.setProperty("text-alignment","Left");     
        }
        else if (type.equals("d")){
            this.setProperty("font-face","Italic");         
            this.setProperty("font-size","8");             
            this.setProperty("text-alignment","Left");      
        }
        else if (type.equals("l")){
            this.setProperty("font-face","Bold");         
            this.setProperty("font-size","8");             
            this.setProperty("text-alignment","Left");      
        }
        else if (type.equals("a")){
            this.setProperty("font-face","Plain");         
            this.setProperty("font-size","9");             
            this.setProperty("text-alignment","Left");      
        }
        else if (type.equals("u")){
            this.setProperty("font-face","Plain");         
            this.setProperty("font-size","10");             
            this.setProperty("text-alignment","Left");      
        }
        this.setProperty("font-name",StringUtilities.getBestDefaultFont()); 
    }

    /** Creates new standard TierFormat for the given tier */
    public TierFormat(String type, String tr, String defaultFont){
        this(type, tr);
        this.setProperty("font-name",defaultFont);                           
    }
    
    
    public TierFormat makeCopy(){
        TierFormat result = new TierFormat();
        result.setTierref(this.getTierref());
        for (java.util.Enumeration e = this.propertyNames() ; e.hasMoreElements() ;) {
             String propertyName = (String)e.nextElement();
             String propertyValue = this.getProperty(propertyName);
             result.setProperty(propertyName, propertyValue);
        }
        return result;
    }
    
    // ********************************************
    // ********** GET AND SET METHODS *************
    // ********************************************

    public String getTierref(){
        return tierref;
    }
    
    public void setTierref(String t){
        tierref = new String(t);
    }
    
    public String getStyleName(){
        return this.getProperty("font-face");
    }
    
    public void setStyleName(String sn){
        this.setProperty("font-face",sn);  //styleName = new String(sn);        
    }
    
    public int getStyle(){
        if (this.getProperty("font-face").equalsIgnoreCase("Bold")) {return java.awt.Font.BOLD;}
        //if (styleName.equals("Bold")){return java.awt.Font.BOLD;}
        else if (this.getProperty("font-face").equalsIgnoreCase("Italic")) {return java.awt.Font.ITALIC;}
        //else if (styleName.equals("Italic")){return java.awt.Font.ITALIC;}
        return java.awt.Font.PLAIN;
    }            
        
    public void setStyle(int s){
        if (s==java.awt.Font.PLAIN) {setStyleName("Plain");}
        else if (s==java.awt.Font.BOLD) {setStyleName("Bold");}
        else if (s==java.awt.Font.ITALIC) {setStyleName("Italic");}
    }
    
    public int getSize(){
        return Integer.valueOf(this.getProperty("font-size")).intValue();   //return size;
    }
    
    public String getSizeAsString(){
        return this.getProperty("font-size");
    }
    
    public void setSize(int s){
        this.setProperty("font-size",Integer.toString(s));  // size = s;        
    } 
    
    public String getAlignmentName(){
        return this.getProperty("text-alignment");  //return alignmentName;
    }
    
    public void setAlignmentName(String an){
        this.setProperty("text-alignment",an);      //alignmentName = new String(an);
    }
    
    public int getAlignment(){
        String alignmentName = this.getProperty("text-alignment");
        if (alignmentName.equalsIgnoreCase("Center")){return javax.swing.SwingConstants.CENTER;}
        else if (alignmentName.equalsIgnoreCase("Right")){return javax.swing.SwingConstants.RIGHT;}
        return javax.swing.SwingConstants.LEFT;
    }
        
    
    public String getTextcolorName(){
        return this.getProperty("font-color");  //return textcolorName;
    }

    public java.awt.Color getTextcolor(){
        if (getTextcolorName().startsWith("#")){
             // i.e. color is of the form #RxxGxxBxx where xx is a hexadecimal number
             return getColor("font-color");
        }
        for (int i=0; i<COLOR_VALUES.length;i++){
            if (getTextcolorName().equalsIgnoreCase(AWT_COLOR_NAMES[i])){return COLOR_VALUES[i];}}
        return java.awt.Color.black;
    }
    
    public String getTextcolorCSSName(){
        java.awt.Color color = getTextcolor();
        String result = "#";
        String r = Integer.toHexString(color.getRed());
        if (r.length()==1) {r="0" + r;}
        String g = Integer.toHexString(color.getGreen());
        if (g.length()==1) {g="0" + g;}
        String b = Integer.toHexString(color.getBlue());
        if (b.length()==1) {b="0" + b;}
        result+=r+g+b;
        return result;
        //for (int i=0; i<COLOR_VALUES.length;i++){
          //  if (getTextcolor()==COLOR_VALUES[i]){return new String(CSS_COLOR_NAMES[i]);}}
        //return new String("black");
    }    
    
    public void setTextcolorName(String tcn){
        this.setProperty("font-color",tcn);    //textcolorName = new String(tcn);
    }
    

    public java.awt.Color getBgcolor(){
        if (getBgcolorName().startsWith("#")){
            return getColor("bg-color");
        }
        for (int i=0; i<COLOR_VALUES.length;i++){
            if (getBgcolorName().equalsIgnoreCase(AWT_COLOR_NAMES[i])){return COLOR_VALUES[i];}}
        return java.awt.Color.white;
    }

    public String getBgcolorName(){
        return getProperty("bg-color");     //return bgcolorName;
    }
    
    public String getBgcolorCSSName(){
        java.awt.Color color = getBgcolor();
        String result = "#";
        String r = Integer.toHexString(color.getRed());
        if (r.length()==1) {r="0" + r;}
        String g = Integer.toHexString(color.getGreen());
        if (g.length()==1) {g="0" + g;}
        String b = Integer.toHexString(color.getBlue());
        if (b.length()==1) {b="0" + b;}
        result+=r+g+b;
        return result;
        //for (int i=0; i<COLOR_VALUES.length;i++){
        //    if (getBgcolor()==COLOR_VALUES[i]){return new String(CSS_COLOR_NAMES[i]);}}
        //return new String("black");
    }    

    public void setBgcolorName(String bcn){
        setProperty("bg-color",bcn);     //bgcolorName = new String(bcn);
    }

    public void setColor(String propertyName, java.awt.Color color){
        String r = Integer.toHexString(color.getRed());
        if (r.length()==1) {r="0" + r;}
        String g = Integer.toHexString(color.getGreen());
        if (g.length()==1) {g="0" + g;}
        String b = Integer.toHexString(color.getBlue());
        if (b.length()==1) {b="0" + b;}
        String propertyValue = "#R" + r + "G" + g + "B" + b;
        setProperty(propertyName, propertyValue);
    }
    
    public java.awt.Color getColor(String propertyName){
        String colorName = getProperty(propertyName);
        try {
            // color is of the form #RxxGxxBxx where xx is a hexadecimal number
            int r = Integer.parseInt(colorName.substring(2,4),16);
            int g = Integer.parseInt(colorName.substring(5,7),16);
            int b = Integer.parseInt(colorName.substring(8),16);
            return new java.awt.Color(r,g,b);
        } catch (Exception e){
            return java.awt.Color.white;        
        }
    }
    
    public String getFontName(){
        return getProperty("font-name");
    }
    
    public void setFontName(String fn){
        setProperty("font-name",fn);    //fontName = new String(fn);
    }
    
    public Font getFont(){
        return new java.awt.Font(getFontName(),getStyle(),getSize());
        //return new java.awt.Font(fontName,getStyle(),size);
    }

    // added 07-01-02
    public Font getFont(int scaleConstant){
        int adjustedSize = Math.max(4,getSize()+scaleConstant);
        return new java.awt.Font(getFontName(),getStyle(),adjustedSize);
    }

    public void setFont(Font f){
        setFontName(f.getFontName());
        setSize(f.getSize());
        setStyle(f.getStyle());
    }
    
    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************    

    public String toXML(){
        StringBuffer sb = new StringBuffer();
        sb.append("<tier-format tierref=\"" + getTierref() +"\">");
        for (java.util.Enumeration e = propertyNames() ; e.hasMoreElements() ;) {
             String propertyName = (String)e.nextElement();
             String propertyValue = getProperty(propertyName);
             sb.append("<property name=\"");
             sb.append(propertyName);
             sb.append("\">");
             sb.append(propertyValue);
             sb.append("</property>");             
        }      
        sb.append("</tier-format>");
        return sb.toString();
    }
    
    public void writeXML(FileOutputStream fos) throws IOException{
        fos.write(toXML().getBytes("UTF-8"));
    }
    
    // ********************************************
    // ********** HTML CONVERSION *****************
    // ********************************************

    public String toCSS(){
       String result = new String();
       result += "\t\t font-family: \"" + getFontName() + "\";\n";
       result += "\t\t font-size: " + getSizeAsString() + "pt;\n";
       result += "\t\t font-style: ";
       if (getStyleName().equals("Italic")){result+= "italic;";}
       else {result+="normal;";}
       result += "\n\t\t font-weight: ";
       if (getStyleName().equals("Bold")){result+= "bold;";}
       else {result+="normal;";}
       result += "\n\t\t color: " + getTextcolorCSSName() + ";\n"; 
       result += "\t\t background-color: " + getBgcolorCSSName() + ";\n";
       result += "\t\ttext-align: " + getAlignmentName() + ";\n";
       return result;
    }

    // ********************************************
    // ********** RTF CONVERSION *****************
    // ********************************************

    public String toRTF(String fontID){
        String result = new String();
        if (getStyleName().equals("Italic")){result+="\\i";}
        else if (getStyleName().equals("Bold")){result+="\\b";}
        else if (getStyleName().equals("Plain")){result+="\\plain";}
        result+="\\f"+ fontID;  // reference to fonttbl
        result+="\\fs" + getSize()*2;   // font size
        return result;
    }
    
    public void convertColors(){
        for (java.util.Enumeration e = this.propertyNames() ; e.hasMoreElements() ;) {
             String propertyName = (String)e.nextElement();
             String propertyValue = this.getProperty(propertyName);
             if ((propertyName.endsWith("-color") && (!propertyValue.startsWith("#")))){
                String result = "#R00G00B00";
                for (int i=0; i<COLOR_VALUES.length;i++){
                    if (propertyValue.equalsIgnoreCase(AWT_COLOR_NAMES[i])){
                        String r = Integer.toHexString(COLOR_VALUES[i].getRed());
                        if (r.length()<2) {r="0" + r;}
                        String g = Integer.toHexString(COLOR_VALUES[i].getGreen());
                        if (g.length()<2) {g="0" + g;}
                        String b = Integer.toHexString(COLOR_VALUES[i].getBlue());
                        if (b.length()<2) {b="0" + b;}
                        result = "#R" + r + "G" + g + "B" + b;
                        break;
                    }
                }                
                this.setProperty(propertyName,result);
             }
        }
    }
    
    public void removeBorder(String propertyName, String whichBorder){
        String border = getProperty(propertyName);
        String newBorder = new String();
        for (int pos=0; pos<border.length(); pos++){
            boolean add=true;
            for (int pos2=0; pos2<whichBorder.length(); pos2++){
                if (whichBorder.charAt(pos2)==border.charAt(pos)){
                    add=false;
                    break;
                }
            }
            if (add) {newBorder+=border.charAt(pos);}
        }
        setProperty(propertyName, newBorder);
    }
    
    // replaces attribute names like "font:name"
    // by attribute names like "font_name"
    // this is because otherwise there will be
    // a problem with namespaces when using
    // XSL-transformations
    // also replaces "fixed-row-width" by "fixed-row-height" because the former is nonsense
    void replaceOldAttributeNames(){
        for (java.util.Enumeration e = this.propertyNames() ; e.hasMoreElements() ;) {
             String propertyName = (String)e.nextElement();
             String propertyValue = this.getProperty(propertyName);
             if (propertyName.indexOf(':')>=0){
                this.remove(propertyName);
                int position = propertyName.indexOf(':');
                String newPropertyName = propertyName.substring(0, position) + "-" + propertyName.substring(position+1);
                this.setProperty(newPropertyName, propertyValue);
             }
        }
        if (this.containsKey("fixed-row-width")){
            String fixedHeight = this.getProperty("fixed-row-width", "10");
            this.remove("fixed-row-width");
            this.put("fixed-row-height", fixedHeight);
        }
    }
    
}