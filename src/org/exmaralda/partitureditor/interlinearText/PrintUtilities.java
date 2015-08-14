/*
 * PrintUtilities.java
 *
 * Created on 20. Maerz 2002, 13:55
 */

package org.exmaralda.partitureditor.interlinearText;

import java.awt.*;
import java.awt.geom.*;
/**
 *
 * @author  Thomas
 * @version 
 */
public class PrintUtilities {

    static float ST = 0.05f;    // stroke thikness
    /** Creates new PrintUtilities */
    public PrintUtilities() {
    }
    
    static void drawBorder(Graphics2D graphics, Rectangle2D.Double r, 
                           String border, String borderStyle, Color borderColor, Color backgroundColor){
       // convert the boundaries to integers
       int x = Math.round(new Double(r.x).floatValue());
       int y = Math.round(new Double(r.y).floatValue());   
       int w = Math.round(new Double(r.width).floatValue());;
       int h = Math.round(new Double(r.height).floatValue());;
       // fill the specified space with the specified background color
       if (!backgroundColor.equals(Color.white)){   // no need to fill white areas (GIBT NUR �SCHER)
            graphics.setColor(backgroundColor);
            graphics.fillRect(x,y,w,h);
       }    
       // set color and style for border
       graphics.setColor(borderColor);
       if (borderStyle.equalsIgnoreCase("Solid")){
           graphics.setStroke(new BasicStroke(ST));
       }
       else if (borderStyle.equalsIgnoreCase("Dashed")){
            // 3 pixel line, 3 pixel gap
            float[] dashPattern = {3,3};
            graphics.setStroke(new BasicStroke(ST, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f, dashPattern, 0));
       }
       else if (borderStyle.equalsIgnoreCase("Dotted")){
            // 1 pixel line, 1 pixel gap
            float[] dashPattern = {1,1};
            graphics.setStroke(new BasicStroke(ST, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f, dashPattern, 0));
       }

       // draw borders 
       if (border.indexOf('l')>=0){
            graphics.drawLine(x,y,x,y+h);
       }
       if (border.indexOf('r')>=0){
            graphics.drawLine(x+w,y,x+w,y+h);
       }
       if (border.indexOf('t')>=0){
            graphics.drawLine(x,y,x+w,y);
       }
       if (border.indexOf('b')>=0){
            graphics.drawLine(x,y+h,x+w,y+h);
       }
    }

}
