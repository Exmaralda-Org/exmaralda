/*
 * ELANConverter.java
 *
 * Created on 13. Oktober 2003, 15:00
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

// for parsing
import javax.xml.parsers.ParserConfigurationException;
 
import org.xml.sax.SAXException;

// For write operation
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

import java.io.*;
import org.exmaralda.partitureditor.jexmaralda.*;

/**
 *
 * @author  thomas
 */
public class AnvilConverter {
    
    /** the XSLT stylesheet for converting an EAF document to an EXMARaLDA basic transcription */
    //static final String NORMALIZE_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/NormalizeTranscriber.xsl";
    /** the XSLT stylesheet for converting  an EXMARaLDA basic transcription  to an EAF document*/
    static final String ANVIL2EX_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/Anvil2EXMARaLDA.xsl";

    
    /** Creates a new instance of ELANConverter */
    public AnvilConverter() {
    }
    

    /** reads the EAF file specified by filename and returns an EXMARaLDA BasicTranscription */
    public BasicTranscription readAnvilFromFile(String filename) throws JexmaraldaException,
                                                                       SAXException, 
                                                                       IOException, 
                                                                       ParserConfigurationException, 
                                                                       TransformerConfigurationException, 
                                                                       TransformerException  {
        StylesheetFactory ssf = new StylesheetFactory(true);

        //String normalizedTrans = ssf.applyInternalStylesheetToExternalXMLFile(NORMALIZE_STYLESHEET, filename);
        String basicTrans = ssf.applyInternalStylesheetToExternalXMLFile(ANVIL2EX_STYLESHEET, filename);
        // creates a new (empty) basic transcription 
        BasicTranscription bt = new BasicTranscription();
        // reads the result of the XSLT transformation into that basic transcription 
        bt.BasicTranscriptionFromString(basicTrans);
        return bt;
    }    
    
}
