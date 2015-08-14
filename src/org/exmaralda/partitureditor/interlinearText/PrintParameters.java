/*
 * PrintParameters.java
 *
 * Created on 19. Maerz 2002, 09:43
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class PrintParameters extends PageOutputParameters {

    float currentX;    
    float currentY;
    
    /** Creates new PrintParameters */
    public PrintParameters() {
        super();
    }
    
    public PrintParameters(java.awt.print.PageFormat pageFormat){
        super(pageFormat);
        resetCurrentX();
        resetCurrentY();
    }
    
    public void resetCurrentX(){
        currentX = new Double(getPaperMeasure("margin:left",OutputParameters.PX_UNIT)).floatValue();
    }
    
    public void resetCurrentY(){
        currentY = new Double(getPaperMeasure("margin:top",OutputParameters.PX_UNIT)).floatValue();
    }

}
