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
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.jdom.transform.XSLTransformException;
import org.xml.sax.*;
import org.jdom.*;
import org.exmaralda.partitureditor.jexmaralda.convert.PraatConverter;
/**
 *
 * @author thomas
 */
public class WritePraatFiles extends AbstractBasicTranscriptionProcessor {
    
    
    public String currentFilename;
    String suffix = "_PRAAT";
    PraatConverter praatConverter = new PraatConverter();
    
    /** Creates a new instance of AbstractCorpusProcessor */

    public WritePraatFiles(String corpusName, String s) {
        super(corpusName);
        suffix = s;
    }
    
    public static void main(String[] args){
        try {
            WritePraatFiles wtf = new WritePraatFiles(args[0], args[2]);
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
        String praatFilename =    getCurrentDirectoryname() 
                                + System.getProperty("file.separator")
                                + "export"
                                + System.getProperty("file.separator")
                                + getNakedFilenameWithoutSuffix()
                                + suffix
                                + ".textGrid";
        try {
            praatConverter.writePraatToFile(bt, praatFilename);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
