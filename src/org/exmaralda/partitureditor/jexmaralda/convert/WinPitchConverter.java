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
public class WinPitchConverter {
    
    static final String WINPITCH2EX_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/WinPitch2BasicTranscription.xsl";

    
    /** Creates a new instance of ELANConverter */
    public WinPitchConverter() {
    }
    

    /** reads the EAF file specified by filename and returns an EXMARaLDA BasicTranscription */
    public BasicTranscription readWinPitchFromFile(String filename) throws JexmaraldaException,
                                                                       SAXException, 
                                                                       IOException, 
                                                                       ParserConfigurationException, 
                                                                       TransformerConfigurationException, 
                                                                       TransformerException  {

        String cleanedUpVersion = removeEmptyLinesAndDTD(filename);

        StylesheetFactory ssf = new StylesheetFactory(true);
        //String basicTrans = ssf.applyInternalStylesheetToExternalXMLFile(WINPITCH2EX_STYLESHEET, filename);
        String basicTrans = ssf.applyInternalStylesheetToString(WINPITCH2EX_STYLESHEET, cleanedUpVersion);
        
        // creates a new (empty) basic transcription 
        BasicTranscription bt = new BasicTranscription();
        // reads the result of the XSLT transformation into that basic transcription 
        bt.BasicTranscriptionFromString(basicTrans);
        bt.getBody().smoothTimeline();
        bt.getHead().getMetaInformation().resolveReferencedFile(filename);
        return bt;
    }

    public String removeEmptyLinesAndDTD(String filename) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        StringBuffer result = new StringBuffer();
        FileInputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, "ISO-8859-1");
        BufferedReader br = new BufferedReader(isr);
        String nextLine="";
        while ((nextLine = br.readLine()) != null){
            if ((nextLine.trim().length()>0) && (!(nextLine.trim().startsWith("<!DOCTYPE")))){
                result.append(nextLine);
            }
        }
        br.close();
        return result.toString();
    }
    
}
