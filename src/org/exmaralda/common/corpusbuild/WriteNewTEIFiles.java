/*
 * AbstractCorpusProcessor.java
 *
 * Created on 10. Oktober 2006, 17:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.jdom.transform.XSLTransformException;
import org.xml.sax.*;
import org.jdom.*;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
/**
 *
 * @author thomas
 */
public class WriteNewTEIFiles extends AbstractBasicTranscriptionProcessor {
    
    
    String suffix = "_TEI";
    TEIConverter teiConverter = new TEIConverter();
    
    /** Creates a new instance of AbstractCorpusProcessor */

    public WriteNewTEIFiles(String corpusName,
                            String s
                        ) {
        super(corpusName);
        suffix = s;
    }
    
    public static void main(String[] args){
        try {
            WriteNewTEIFiles wtf = new WriteNewTEIFiles(args[0], args[2]);
            wtf.doIt();
            wtf.output(args[1]);
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void processTranscription(BasicTranscription bt) {
            String teiFilename =    getCurrentDirectoryname()
                                    + System.getProperty("file.separator")
                                    + "export"
                                    + System.getProperty("file.separator")
                                    + getNakedFilenameWithoutSuffix()
                                    + suffix
                                    + ".xml";
            try {
                teiConverter.writeNewHIATTEIToFile(bt, teiFilename);
            } catch (FSMException ex) {
                ex.printStackTrace();
            } catch (XSLTransformException ex) {
                ex.printStackTrace();
            } catch (JDOMException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }
       
    
}
