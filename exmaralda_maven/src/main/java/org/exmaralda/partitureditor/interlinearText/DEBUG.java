/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.interlinearText;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.AbstractEventTier;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas.schmidt
 */
public class DEBUG {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerException, TransformerConfigurationException, JexmaraldaException, FSMException {
        new DEBUG().doit();
    }
    
    //String TEST_FILE = "D:\\AGD-DATA\\dgd2_data\\transcripts\\MIKO\\MIKO_E_00002_SE_01_T_01_DF_01.fln";
    //String TEST_FILE = "N:\\Workspace\\MIKO\\MIKO\\fln-dgd\\MIKO_E_00001_SE_01_T_01_DF_01.fln";
    //String IN_DIR = "N:\\Workspace\\MIKO\\MIKO\\fln-dgd";
    String IN_DIR = "D:\\AGD-DATA\\dgd2_data\\transcripts\\HMOT";

    private void doit() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerException, TransformerConfigurationException, JexmaraldaException, FSMException {
        File[] testFiles = new File(IN_DIR).listFiles();
        
        for (File TEST_FILE : testFiles){
            
            System.out.println(TEST_FILE.getName());
        
            Document xmlDocument = FileIO.readDocumentFromLocalFile(TEST_FILE);
            BasicTranscription bt = org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.readXMLAsBasicTranscription(xmlDocument);

            bt.getBody().stratify(AbstractEventTier.STRATIFY_BY_DISTRIBUTION);

            /*bt.writeXMLToFile("C:\\Users\\thomas.schmidt\\Desktop\\DEBUG\\IT_2021\\MIKO2.exb", "none");


            BasicTranscription bt2 = new BasicTranscription("N:\\Workspace\\MIKO\\MIKO\\MIKO_E_00002\\MIKO_E_00002_SE_01_T_01.exb");
            TEIConverter teiConverter = new TEIConverter();
            teiConverter.writeHIATISOTEIToFile(bt2, "C:\\Users\\thomas.schmidt\\Desktop\\DEBUG\\IT_2021\\MIKO2_TEI.xml");*/

            //TierFormatTable tft = new TierFormatTable(bt);
            bt.getBody().stratify(AbstractEventTier.STRATIFY_BY_DISTRIBUTION);
            //TierFormatTable tft = TierFormatTable.makeTierFormatTableForFolker(bt);
            TierFormatTable tft = TierFormatTable.makeTierFormatTableForDGD2(bt);
            InterlinearText it = ItConverter.BasicTranscriptionToInterlinearText(bt, tft);

            /*it.markOverlaps("[", "]");

            int frameEndPosition = -1;
            for (int pos=0; pos<bt.getBody().getNumberOfTiers();pos++){
                if (bt.getBody().getTierAt(pos).getSpeaker()==null){
                    frameEndPosition = pos-1;
                    break;
                }
            }
            if (frameEndPosition>=0){
                ((ItBundle)it.getItElementAt(0)).frameEndPosition=frameEndPosition;
            }*/

            HTMLParameters htmlParameters = new HTMLParameters();
            htmlParameters.setWidth(480.0);
            htmlParameters.smoothRightBoundary = true;
            htmlParameters.includeSyncPoints = false;
            htmlParameters.putSyncPointsOutside = false;

            it.trim(htmlParameters);
            it.reorder();
        }
    }
    
}
