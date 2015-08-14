/*
 * AbstractCorpusProcessor.java
 *
 * Created on 10. Oktober 2006, 17:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.jdom.transform.XSLTransformException;
import org.xml.sax.*;
import org.jdom.*;
/**
 *
 * @author thomas
 */
public class UpdateCounts extends AbstractCorpusProcessor {
    
    
    String[] KEYS = {   "# HIAT:ip", 
                        "# HIAT:w",
                        "# HIAT:non-pho",
                        "# e",
                        "# sc",
                        "# HIAT:u"
    };

    
    /** Creates a new instance of AbstractCorpusProcessor */

    public UpdateCounts(String corpusName) {
        super(corpusName);
    }
    
    public static void main(String[] args){
        try {
            //String filename = args[0];
            String filename = "S:\\TP-E5\\SKOBI-ORDNER AKTUELL\\PBU_CORPUS_22MAI2007.xml";
            UpdateCounts wtf = new UpdateCounts(filename);
            wtf.doIt();
            wtf.writeBack(filename);
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
    }

    public void writeBack(String filename){
        try {
            FileIO.writeDocumentToLocalFile(filename, this.corpus);        
        } catch (IOException ex) {
            ex.printStackTrace();
        }        
    }
    
    public void process(String filename) throws JexmaraldaException, SAXException {
        try {
            Document st = FileIO.readDocumentFromLocalFile(filename);
            for (String key : KEYS){
                // <ud-information attribute-name="# HIAT:ip">8931</ud-information>
                String xp1 = "//ud-information[@attribute-name='" + key + "']";
                Element udinf = (Element)(getSingle(xp1,st.getRootElement()));
                if (udinf==null) continue;
                String count = udinf.getText();
                System.out.println(key);
                String xp2 = "../Description/Key[@Name='" + key + "']";
                Element comaKey = (Element)(getSingle(xp2,getCurrentCorpusNode()));                
                if (comaKey!=null){
                    System.out.println(key + " " + count + " " + comaKey.getText());
                    comaKey.setText(count);
                } else {
                    Element newKey = new Element("Key");
                    newKey.setAttribute("Name", key);
                    newKey.setText(count);
                    getCurrentCorpusNode().getParentElement().getChild("Description").addContent(newKey);
                }
            }
        } catch (XSLTransformException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }               
    }

    public String getXpathToTranscriptions() {
        return SEGMENTED_FILE_XPATH;
    }
    
}
