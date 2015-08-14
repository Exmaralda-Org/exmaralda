/*
 * WordMLTest.java
 *
 * Created on 19. April 2004, 13:59
 */

package org.exmaralda.partitureditor.interlinearText;

import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;

/**
 *
 * @author  thomas
 */
public class WordMLTest {
    
    /** Creates a new instance of WordMLTest */
    public WordMLTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            BasicTranscription bt = new BasicTranscription("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\narrow.xml");
            TierFormatTable tft = new TierFormatTable("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\narrow_f.xml");
            WordMLParameters param = new WordMLParameters();
            RTFParameters rtfParam = new RTFParameters();
            param.setWidth(400);
            param.setPaperMeasures(PageOutputParameters.DINA3_HORIZONTAL);
            param.landscape = true;
            param.saveSpace = true;
            param.includeSyncPoints = false;
            rtfParam.setWidth(300);
            param.useClFitText = true;
            param.smoothRightBoundary = true;
            param.frame="tlbr";
            rtfParam.smoothRightBoundary = true;
            InterlinearText it = ItConverter.BasicTranscriptionToInterlinearText(bt, tft);
            it.trim(param);
            String out = it.toWordML(param);
            //String rtf = it.toRTF(rtfParam);
            //String p = out.substring(3200, 3340);
            //System.out.println(p);

            System.out.println("started writing document...");
            FileOutputStream fos = new FileOutputStream("d:\\aaa_beispiele\\helge_neu\\out.xml");
            fos.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes("UTF-8"));
            fos.write(out.getBytes("UTF-8"));
            fos.close();
            System.out.println("document written.");
            
            /*System.out.println("started writing document...");
            FileOutputStream fos2 = new FileOutputStream("d:\\aaa_beispiele\\helge_neu\\out.rtf");
            fos2.write(rtf.getBytes());
            fos2.close();
            System.out.println("document written.");*/
        } catch (Throwable t){
            t.printStackTrace();
        }
        
    }
    
}
