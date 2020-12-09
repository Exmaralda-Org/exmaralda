/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas.schmidt
 */

// new 01-12-2020, for DULKO, issue #229, make this a separate class as far as it is independent of the Partitur-Editor
public class EXMARaLDATransformer {
    
    BasicTranscription transcription;
    AbstractSegmentation segmentation;
    String[] parameters;
    String[][] xslParameters;
    
    List<String> warnings;

    public EXMARaLDATransformer(BasicTranscription transcription, AbstractSegmentation segmentation, String[] parameters) {
        this.transcription = transcription;
        this.segmentation = segmentation;
        this.parameters = parameters;
    }

    public EXMARaLDATransformer(BasicTranscription transcription, AbstractSegmentation segmentation, String[] parameters, String[][] xslParameters) {
        this(transcription, segmentation, parameters);
        this.xslParameters = xslParameters;
    }

    public List<String> getWarnings() {
        return warnings;
    }
    
    
    
    /*
        parameters[0] -- transformation base -- one of (basic-transcription, segmented-transcription, list-transcription, Modena TEI, TEI)
        parameters[1] -- segmentation -- one of (NONE, GENERIC, HIAT, GAT, CHAT, DIDA, IPA)
        parameters[2] -- list unit -- a free string like 'HIAT:u'
        parameters[3] -- stylesheet path -- can be internal or external
        parameters[4] -- output type -- one of (html, xml, txt, self-transformation, other)
    */
    
    

    public String transform() throws JDOMException, IOException, SAXException, FSMException, ParserConfigurationException, TransformerException{
        Document baseDocument = null;
        if (parameters[0].startsWith("basic")){
            baseDocument = IOUtilities.readDocumentFromString(transcription.toXML());
        } else if ((parameters[0].startsWith("segmented")) || (parameters[0].startsWith("list"))){
            SegmentedTranscription st = null;
            if (parameters[1].equals("NONE")){
                st = transcription.toSegmentedTranscription();
            } else {
                //AbstractSegmentation as = table.getAbstractSegmentation(parameters[1]);
                //st = as.BasicToSegmented(transcription);
                st = segmentation.BasicToSegmented(transcription);
            }
            if (parameters[0].startsWith("segmented")){
                baseDocument = IOUtilities.readDocumentFromString(st.toXML());
            } else {
                ListTranscription lt = null;
                int listConversionType = 0;
                if ((parameters[1].equals("HIAT")) && (parameters[2].equals("HIAT:u"))){
                    listConversionType = 1;
                }
                if ((parameters[1].equals("CHAT")) && (parameters[2].equals("CHAT:u"))){
                    listConversionType = 3;
                }
                if ((parameters[1].equals("GAT")) && (parameters[2].equals("GAT:pe"))){
                    listConversionType = 3;
                }
                lt = st.toListTranscription(new SegmentedToListInfo(st, listConversionType));
                lt.getBody().sort();
                baseDocument = IOUtilities.readDocumentFromString(lt.toXML());
            }
        } else if (parameters[0].startsWith("Modena")){
            File temp = File.createTempFile("modena_temp", "xml");
            temp.deleteOnExit();
            new TEIConverter("/org/exmaralda/partitureditor/jexmaralda/xsl/EXMARaLDA2TEI_Modena.xsl").writeModenaTEIToFile(transcription, temp.getAbsolutePath());
            baseDocument = IOUtilities.readDocumentFromLocalFile(temp.getAbsolutePath());
        } else if (parameters[0].startsWith("TEI")){
            File temp = File.createTempFile("tei_temp", "xml");
            temp.deleteOnExit();
            //new TEIConverter().writeGenericTEIToFile(transcription, temp.getAbsolutePath());
            // changed 01-12-2020, issue #236
            TEIConverter teiConverter = new TEIConverter();
            if (parameters[1].startsWith("HIAT")){
                teiConverter.writeHIATISOTEIToFile(transcription, temp.getAbsolutePath()); 
            } else if (parameters[1].startsWith("cGAT_MINIMAL")){
                teiConverter.writeCGATMINIMALISOTEIToFile(transcription, temp.getAbsolutePath(), null); 
            } else {
                teiConverter.writeGenericISOTEIToFile(transcription, temp.getAbsolutePath());
            }
            baseDocument = IOUtilities.readDocumentFromLocalFile(temp.getAbsolutePath());
        }

        String resultString;
        if (parameters[3].length()>0){
            StylesheetFactory sf = new StylesheetFactory(true);
            String sourceString = IOUtilities.documentToString(baseDocument);
            try {
                if (parameters[3].startsWith("/")){
                    resultString = sf.applyInternalStylesheetToString(parameters[3], sourceString, xslParameters);
                } else {
                    resultString = sf.applyExternalStylesheetToString(parameters[3], sourceString, xslParameters);
                }
            } catch (TransformerException ex){
                warnings = sf.getWarnings();
                throw(ex);
            }
        } else {
            resultString = IOUtilities.documentToString(baseDocument);
        }

        return resultString;
    }
    
    
}
