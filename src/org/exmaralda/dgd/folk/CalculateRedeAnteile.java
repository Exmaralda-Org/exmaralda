/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.folk;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class CalculateRedeAnteile extends AbstractFOLKAnalyzer{

    public CalculateRedeAnteile(String FOLK_DIRECTORY, String[] FOLK_FILES, String session_id) {
        super(FOLK_DIRECTORY, FOLK_FILES, session_id);
    }

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*String[] files = {
            "FOLK_E_00003_SE_01_T_01_DF_01.fln",
            "FOLK_E_00015_SE_01_T_01_DF_01.fln",
            "FOLK_E_00028_SE_01_T_01_DF_01.fln",
            "FOLK_E_00029_SE_01_T_01_DF_01.fln",
            "FOLK_E_00031_SE_01_T_01_DF_01.fln",
            "FOLK_E_00032_SE_01_T_01_DF_01.fln",
            "FOLK_E_00033_SE_01_T_01_DF_01.fln",
            "FOLK_E_00034_SE_01_T_01_DF_01.fln",
            "FOLK_E_00035_SE_01_T_01_DF_01.fln",
            "FOLK_E_00036_SE_01_T_01_DF_01.fln",
            "FOLK_E_00037_SE_01_T_01_DF_01.fln",
            "FOLK_E_00038_SE_01_T_01_DF_01.fln",
            "FOLK_E_00056_SE_01_T_01_DF_01.fln",
            "FOLK_E_00057_SE_01_T_01_DF_01.fln",
            "FOLK_E_00058_SE_01_T_01_DF_01.fln",
            "FOLK_E_00059_SE_01_T_01_DF_01.fln",
            "FOLK_E_00060_SE_01_T_01_DF_01.fln",
            "FOLK_E_00061_SE_01_T_01_DF_01.fln",
            "FOLK_E_00062_SE_01_T_01_DF_01.fln"            
        };*/
        
        File[] f = new File("U:\\FOLK_DGD_2\\transcripts\\FOLK_MINIMAL_REFERENZ").listFiles();
        String[] files = new String[f.length];
        int count=0;
        for (File file : f){
            files[count]=file.getName();
            System.out.println(files[count]);
            count++;
        }
        String session_id = "CBBB46F121CDDEE35A3EDBD9BEBDFD15";
        CalculateRedeAnteile cra = new CalculateRedeAnteile("U:\\FOLK_DGD_2\\transcripts\\FOLK_MINIMAL_REFERENZ", files, session_id);
        try {
            cra.go();
            cra.writeResult("C:\\Users\\Schmidt\\Desktop\\FOLK_QUANT\\REDEANTEILE_ALLE.xml");
        } catch (IOException ex) {
            Logger.getLogger(CalculateRedeAnteile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processFile(File f) throws IOException {
        System.out.println(f.getAbsolutePath());
        try {
            Document doc = FileIO.readDocumentFromLocalFile(f);
            List speakers = XPath.selectNodes(doc, "//speaker");
            for (Object o : speakers){
                Element speaker = (Element)o;
                String speakerID = speaker.getAttributeValue("speaker-id");
                List contributionsForThisSpeaker = XPath.selectNodes(doc, "//contribution[@speaker-reference='" + speakerID +"']");
                int wordCount = 0;
                double timeCount = 0.0;
                for (Object o2 : contributionsForThisSpeaker){
                    Element contribution = (Element)o2;
                    List words = XPath.selectNodes(contribution, "descendant::w");
                    wordCount+=words.size();
                    double startTime = Double.parseDouble(contribution.getAttributeValue("time"));
                    double endTime = Double.parseDouble(((Element)(XPath.selectSingleNode(contribution, "descendant::time[last()]"))).getAttributeValue("time"));
                    timeCount+= (endTime - startTime);
                }
                Element e = new Element("count");
                e.setAttribute("speaker", speaker.getAttributeValue("dgd-id"));
                e.setAttribute("sigle", speakerID);
                e.setAttribute("transcript", doc.getRootElement().getAttributeValue("dgd-id"));
                e.setAttribute("event", doc.getRootElement().getAttributeValue("dgd-id").substring(0, 12));
                e.setAttribute("wordCount", Integer.toString(wordCount));
                e.setAttribute("timeCount", Double.toString(timeCount));
                e.setAttribute("contributionCount", Integer.toString(contributionsForThisSpeaker.size()));
                //String role = metadata.getMetadataValue(doc.getRootElement().getAttributeValue("dgd-id").substring(0, 12), 
                //        speaker.getAttributeValue("dgd-id"), "ses_rolle_s");                
                //e.setAttribute("role", role);
                //String beschreibung = metadata.getMetadataValue(doc.getRootElement().getAttributeValue("dgd-id").substring(0, 12), 
                //        "", "e_beschreibung");                
                //e.setAttribute("beschreibung", beschreibung);
                resultElement.addContent(e);
            }
        } catch (JDOMException ex) {
            throw new IOException(ex);
        } 
    }

}
