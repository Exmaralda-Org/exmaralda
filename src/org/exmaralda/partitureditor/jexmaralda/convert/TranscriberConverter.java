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
public class TranscriberConverter {
    
    /** the XSLT stylesheet for converting an EAF document to an EXMARaLDA basic transcription */
    static final String NORMALIZE_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/NormalizeTranscriber.xsl";
    /** the XSLT stylesheet for converting  an EXMARaLDA basic transcription  to an EAF document*/
    static final String TRANS2EX_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/Transcriber2BasicTranscription.xsl";

    
    /** Creates a new instance of ELANConverter */
    public TranscriberConverter() {
    }
    

    /** reads the EAF file specified by filename and returns an EXMARaLDA BasicTranscription
     * @param filename
     * @return
     * @throws org.exmaralda.partitureditor.jexmaralda.JexmaraldaException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws javax.xml.transform.TransformerConfigurationException  */
    public BasicTranscription readTranscriberFromFile(String filename) throws JexmaraldaException,
                                                                       SAXException, 
                                                                       IOException, 
                                                                       ParserConfigurationException, 
                                                                       TransformerConfigurationException, 
                                                                       TransformerException  {
        StylesheetFactory ssf = new StylesheetFactory(true);

        String transWithoutDTD = removeEmptyLinesAndDTD(filename);
        //String normalizedTrans = ssf.applyInternalStylesheetToExternalXMLFile(NORMALIZE_STYLESHEET, filename);
        String normalizedTrans = ssf.applyInternalStylesheetToString(NORMALIZE_STYLESHEET, transWithoutDTD);
        String basicTrans = ssf.applyInternalStylesheetToString(TRANS2EX_STYLESHEET, normalizedTrans);
        // creates a new (empty) basic transcription 
        BasicTranscription bt = new BasicTranscription();
        // reads the result of the XSLT transformation into that basic transcription 
        bt.BasicTranscriptionFromString(basicTrans);
        String repairMessage = bt.getBody().repair();
        bt.getHead().getMetaInformation().setComment(bt.getHead().getMetaInformation().getComment() + "\n\n" + repairMessage);
        bt.getHead().getMetaInformation().resolveReferencedFile(filename);
        return bt;
    }

    public String removeEmptyLinesAndDTD(String filename) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        /* <?xml version="1.0" encoding="UTF-8"?> */
        FileInputStream fis1 = new FileInputStream(filename);
        InputStreamReader isr1 = new InputStreamReader(fis1, "ISO-8859-1");
        BufferedReader br1 = new BufferedReader(isr1);
        String firstLine = br1.readLine();
        int i1 = firstLine.indexOf("encoding");
        int i2 = firstLine.lastIndexOf("\"");
        // 30-04-2025, changed for #200
        if (i2<0){
            i2 = firstLine.lastIndexOf("'");
        }
        String encoding = "UTF-8";
        if (i2>0){
            encoding = firstLine.substring(i1+10, i2);
            System.out.println("TRS encoding= " + encoding);
        }
        br1.close();
        
        StringBuilder result = new StringBuilder();
        FileInputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, encoding);
        //InputStreamReader isr = new InputStreamReader(fis, "ISO-8859-1");
        //InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String nextLine="";
        while ((nextLine = br.readLine()) != null){
            if ((nextLine.trim().length()>0) && (!(nextLine.trim().startsWith("<!DOCTYPE")))){
                result.append(nextLine);
            } else if (nextLine.trim().length()==0){
                result.append(" ");
            }
        }
        br.close();
        return result.toString();
    }

    
}
