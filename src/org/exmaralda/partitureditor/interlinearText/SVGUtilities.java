/*
 * SVGUtilities.java
 *
 * Created on 3. Februar 2005, 10:30
 */

package org.exmaralda.partitureditor.interlinearText;

import java.awt.Color;
/**
 *
 * @author  thomas
 */
public class SVGUtilities {
    
    public static String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public static String SVG_DOCTYPE_DECLARATION = "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11-flat-20030114.dtd\">";
    
    /** Creates a new instance of SVGUtilities */
    public SVGUtilities() {
    }
    
    static String drawBorder(String border, String borderStyle, String borderColor, String backgroundColor,
                             double x, double y, double w, double h ){
       
       StringBuffer sb = new StringBuffer();
       // fill the specified space with the specified background color
       if (!backgroundColor.equalsIgnoreCase("#RFFGFFBFF")){   // no need to fill white areas (GIBT NUR AERGER)
           String[][] attributes = {{"width", Double.toString(w)},
                                    {"height", Double.toString(h)},
                                    {"x", Double.toString(x)},
                                    {"y", Double.toString(y)},
                                    {"fill", toSVGColor(backgroundColor)}};
            sb.append(XMLUtilities.makeXMLOpenElement("rect", attributes));
            sb.append(XMLUtilities.makeXMLCloseElement("rect"));
       }    
       // set color and style for border
       String borderStroke = "";
       if (borderStyle.equalsIgnoreCase("Solid")){borderStroke = "none";} 
       else if (borderStyle.equalsIgnoreCase("Dashed")){borderStroke = "3,3";} // 3 pixel line, 3 pixel gap}
       else if (borderStyle.equalsIgnoreCase("Dotted")){borderStroke = "1,1";} // 1 pixel line, 1 pixel gap   
       String borderCol = toSVGColor(borderColor);

       // draw borders 
       String[][] attributes;
       if (border.indexOf('l')>=0){
           String[][] attributes2 = {{"x1", Double.toString(x)},
                                     {"y1", Double.toString(y)},
                                     {"x2", Double.toString(x)},
                                     {"y2", Double.toString(y+h)},
                                     {"stroke-dasharray", borderStroke},
                                     {"stroke", borderCol}};
           sb.append(XMLUtilities.makeXMLOpenElement("line", attributes2));
           sb.append(XMLUtilities.makeXMLCloseElement("line"));
       }
       if (border.indexOf('r')>=0){
           String[][] attributes2 = {{"x1", Double.toString(x+w)},
                                     {"y1", Double.toString(y)},
                                     {"x2", Double.toString(x+w)},
                                     {"y2", Double.toString(y+h)},
                                     {"stroke-dasharray", borderStroke},
                                     {"stroke", borderCol}};
           sb.append(XMLUtilities.makeXMLOpenElement("line", attributes2));
           sb.append(XMLUtilities.makeXMLCloseElement("line"));
       }
       if (border.indexOf('t')>=0){
           String[][] attributes2 = {{"x1", Double.toString(x)},
                                     {"y1", Double.toString(y)},
                                     {"x2", Double.toString(x+w)},
                                     {"y2", Double.toString(y)},
                                     {"stroke-dasharray", borderStroke},
                                     {"stroke", borderCol}};
           sb.append(XMLUtilities.makeXMLOpenElement("line", attributes2));
           sb.append(XMLUtilities.makeXMLCloseElement("line"));
       }
       if (border.indexOf('b')>=0){
           String[][] attributes2 = {{"x1", Double.toString(x)},
                                     {"y1", Double.toString(y+h)},
                                     {"x2", Double.toString(x+w)},
                                     {"y2", Double.toString(y+h)},
                                     {"stroke-dasharray", borderStroke},
                                     {"stroke", borderCol}};
           sb.append(XMLUtilities.makeXMLOpenElement("line", attributes2));
           sb.append(XMLUtilities.makeXMLCloseElement("line"));
       }
       return sb.toString();
    }
    
    public static String toSVGColor(String c){
        String r = c.substring(2,4);
        String g = c.substring(5,7);
        String b = c.substring(8);
        return "#" + r + g + b;
    }
    
    
}
