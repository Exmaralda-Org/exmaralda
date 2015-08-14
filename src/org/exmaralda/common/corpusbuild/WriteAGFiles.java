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
import org.exmaralda.partitureditor.jexmaralda.convert.AIFConverter;
/**
 *
 * @author thomas
 */
public class WriteAGFiles extends AbstractBasicTranscriptionProcessor {
    
    
    public String currentFilename;
    String suffix = "_AG";
    AIFConverter aifConverter = new AIFConverter();
    
    /** Creates a new instance of AbstractCorpusProcessor */

    public WriteAGFiles(   String corpusName,
                            String s
                        ) {
        super(corpusName);
        suffix = s;
    }
    
    public static void main(String[] args){
        try {
            WriteAGFiles wtf = new WriteAGFiles(args[0], args[2]);
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
        String agFilename =    getCurrentDirectoryname() 
                                + System.getProperty("file.separator")
                                + "export"
                                + System.getProperty("file.separator")
                                + getNakedFilenameWithoutSuffix()
                                + suffix
                                + ".xml";
        try {
            aifConverter.writeAIFToFile(bt, agFilename);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
