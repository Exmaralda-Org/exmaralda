/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.tei;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

/**
 *
 * @author bernd
 */
public class SpeechRateAnnotation {

    /**
     * @param args the command line arguments
     * @throws org.jdom.JDOMException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws JDOMException, IOException {
        new SpeechRateAnnotation().doit();
    }

    String[] TEI_FILES = {
        "C:\\Users\\bernd\\Dropbox\\work\\ZUMULT-COMA\\ESLO-DEMO\\ESLO_ENT_1001\\ESLO2_ENT_1001_C.xml",
        "C:\\Users\\bernd\\Dropbox\\work\\ZUMULT-COMA\\ESLO-DEMO\\ESLO_ENT_1005\\ESLO2_ENT_1005_C.xml",
        "C:\\Users\\bernd\\Dropbox\\work\\ZUMULT-COMA\\ESLO-DEMO\\ESLO_ENT_1013\\ESLO2_ENT_1013_C.xml",
    };

    static final Namespace teiNamespace = Namespace.getNamespace("tei", "http://www.tei-c.org/ns/1.0");
    
    
    private void doit() throws JDOMException, IOException {
        for (String TEI_FILE : TEI_FILES){
            File file = new File(TEI_FILE);
            Document document = FileIO.readDocumentFromLocalFile(file);
            XPath xpath = XPath.newInstance("//tei:annotationBlock");
            xpath.addNamespace(teiNamespace);
            XPath xpath2 = XPath.newInstance("descendant::tei:w");
            xpath2.addNamespace(teiNamespace);

            List l = xpath.selectNodes(document); 
            
            final DecimalFormat df = new DecimalFormat("0.00");
            

            for (Object o : l){
                Element ab = (Element)o;
                
                String startID = ab.getAttributeValue("start");
                XPath xpath3 = XPath.newInstance("//tei:when[@xml:id='" + startID + "']");
                xpath3.addNamespace(teiNamespace);
                Element startWhen = (Element) xpath3.selectSingleNode(document);
                double startTime = Double.parseDouble(startWhen.getAttributeValue("interval"));
                
                String endID = ab.getAttributeValue("end");
                XPath xpath4 = XPath.newInstance("//tei:when[@xml:id='" + endID + "']");
                xpath4.addNamespace(teiNamespace);
                Element endWhen = (Element) xpath4.selectSingleNode(document);
                double endTime = Double.parseDouble(endWhen.getAttributeValue("interval"));
                
                double duration = endTime - startTime;
                
                List l2 = xpath2.selectNodes(ab);
                int syllableCount = 0;
                for (Object o2 : l2){
                    Element wElement = (Element)o2;
                    String pho = wElement.getAttributeValue("pho");
                    int thisSyllableCount = pho.length() - pho.replaceAll("\\.", "").length() + 1;
                    syllableCount+=thisSyllableCount;
                }
                
                double speechrate = syllableCount / duration;
                
                Element spanGrpElement = new Element("spanGrp", teiNamespace);
                spanGrpElement.setAttribute("type", "speech-rate");
                spanGrpElement.setAttribute("subtype", "time-based");
                Element spanElement = new Element("span", teiNamespace);
                spanElement.setAttribute("from", startID);
                spanElement.setAttribute("to", endID);
                spanElement.setText(df.format(speechrate));
                ab.addContent(spanGrpElement);
                spanGrpElement.addContent(spanElement);
                
                

            }
    
            FileIO.writeDocumentToLocalFile(file, document);
        }
        
        


    }
    
}
