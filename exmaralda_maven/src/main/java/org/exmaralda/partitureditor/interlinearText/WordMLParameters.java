/*
 * WordMLParameters.java
 *
 * Created on 19. April 2004, 13:08
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  thomas
 */
public class WordMLParameters extends RTFParameters {
    
    /** Creates a new instance of WordMLParameters */
    public WordMLParameters() {
    }
    
    public String toWordMLPaperMeasureString(){
        /*<w:sectPr>
                <w:pgSz w:w="11900" w:h="16839"/>
                <w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440" w:header="720" w:footer="720" w:gutter="0"/>
                <w:cols w:space="720"/>
        </w:sectPr>*/
        StringBuffer sb = new StringBuffer();
        sb.append("<w:sectPr>");
        sb.append("<w:pgSz w:w=\"" + Long.toString(Math.round(getPaperMeasure("paper:width",TWIPS_UNIT))) + "\" "); // width
        sb.append("w:h=\"" + Long.toString(Math.round(getPaperMeasure("paper:height",TWIPS_UNIT))) +"\"");
        if (landscape) {sb.append(" w:orient=\"landscape\"");}
        sb.append("/>");
        sb.append("<w:pgMar w:top=\"" + Long.toString(Math.round(getPaperMeasure("margin:top",TWIPS_UNIT))) + "\" ");
        sb.append("w:right=\"" + Long.toString(Math.round(getPaperMeasure("margin:right",TWIPS_UNIT))) + "\" ");
        sb.append("w:bottom=\"" + Long.toString(Math.round(getPaperMeasure("margin:bottom",TWIPS_UNIT))) + "\" ");
        sb.append("w:left=\"" + Long.toString(Math.round(getPaperMeasure("margin:left",TWIPS_UNIT))) + "\"/>");
        sb.append("</w:sectPr>");
        return sb.toString();
    }
    
}
