/*
 * SVGTest.java
 *
 * Created on 3. Februar 2005, 11:27
 */

package org.exmaralda.partitureditor.interlinearText;

import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
/**
 *
 * @author  thomas
 */
public class SVGTest {
    
    /** Creates a new instance of SVGTest */
    public SVGTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String path = "d:\\svg\\";
        try{
            BasicTranscription bt = new BasicTranscription("d:\\test.xml");
            TierFormatTable tft = new TierFormatTable("d:\\edinburgh\\d\\AAA_Beispiele\\Helge_neu\\Helge_It_Format.xml");
            InterlinearText it = ItConverter.BasicTranscriptionToInterlinearText(bt, tft);
            SVGParameters param = new SVGParameters();
            param.scaleFactor = 1.5;
            //param.frameStyle="Dashed";
            //param.frameColor="#R99G00B00";
            param.includeSyncPoints=true;
            param.putSyncPointsOutside=true;
            param.removeEmptyLines = true;  
            ((org.exmaralda.partitureditor.interlinearText.ItBundle)it.getItElementAt(0)).frameEndPosition=11;

            param.setWidth(500);
            it.trim(param);            
            it.writeSVGToFile(param, "d:\\svg\\out.html", "", "PartiturFl");
            //System.out.println(it.toXML());
            /*for (int pos=0; pos< it.getNumberOfItElements(); pos++){
                String filename = path + "partiturflaeche" + pos + ".svg";
                if (it.getItElementAt(pos) instanceof ItBundle){
                    ItBundle bundle = (ItBundle)(it.getItElementAt(pos));
                    bundle.writeSVG(filename, param);
                    //String svg = bundle.toSVG(param);
                    //System.out.println(svg);
                }
            }*/
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
}
