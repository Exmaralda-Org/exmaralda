/*
 * MakePartiturFiles.java
 *
 * Created on 21. November 2006, 15:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.fsm.FSMException;
import java.io.IOException;
import java.util.Vector;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.segment.CHATSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;
import org.xml.sax.SAXException;
import org.jdom.*;
import org.jdom.transform.*;

/**
 *
 * @author thomas
 */
public class WriteCHATFilesFromHIAT extends AbstractBasicTranscriptionProcessor {
    
    //String STYLESHEET = ""; //Constants.LIST2HTMLStylesheet;
    //String suffix = "_ulist";
    //String toTopLevel = "../../../";
    //XSLTransformer transformer;
    
    
    /** Creates a new instance of MakePartiturFiles */
    public WriteCHATFilesFromHIAT(String corpusName) {
        super(corpusName);        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String comafile = "T:\\TP-E5\\VORBILD\\ENDFAS_SKOBI_GoldStandard.coma";
            if (args.length>0){
                comafile = args[0];
            }
            WriteCHATFilesFromHIAT wul = new WriteCHATFilesFromHIAT(comafile);
            wul.doIt();
            //wul.output(args[1]);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
    }

    public void processTranscription(BasicTranscription bt) {
        FileOutputStream fos = null;
        try {
            HIATSegmentation segmentor = new HIATSegmentation();
            SegmentedTranscription st = segmentor.BasicToSegmented(bt);
            ListTranscription lt = st.toListTranscription(new SegmentedToListInfo(st, SegmentedToListInfo.HIAT_UTTERANCE_SEGMENTATION));
            //lt.getHead().getMetaInformation().relativizeReferencedFile(currentFilename);
            lt.getBody().sort();
            // replace pause bullets: they cause trouble
            for (int pos=0; pos<lt.getBody().getNumberOfSpeakerContributions(); pos++){
                SpeakerContribution c = lt.getBody().getSpeakerContributionAt(pos);
                Vector v = c.getMain().getAllSegmentsWithName("HIAT:non-pho");
                for (Object o : v){
                    AtomicTimedSegment ats = (AtomicTimedSegment)o;
                    ats.setDescription(ats.getDescription().replace('\u2022', '#'));
                }
                for (int pos2=0; pos2<c.getNumberOfAnnotations(); pos2++){
                    AbstractSegmentVector asv = c.getAnnotationAt(pos2);
                    for (Object o2 : asv){
                        TimedAnnotation ta = (TimedAnnotation)o2;
                        ta.setDescription(ta.getDescription().replace('\u2022', '#'));
                    }
                }
            }
            String chatFilename = getCurrentDirectoryname() + System.getProperty("file.separator") + "export" + System.getProperty("file.separator") + getNakedFilenameWithoutSuffix() + ".cha";
            //lt.writeXMLToFile("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\list.xml", "none");
            String chatText = CHATSegmentation.toText(lt, "HIAT:u");

            // Added 20-07-2010: Additional replacements HIAT > CHAT
            chatText = chatText.replaceAll("\\(\\(unv[^\\)]+\\)\\)", "xxx");    // unintelligible
            chatText = chatText.replaceAll("\\(\\(\\.{1,4}\\)\\)", "www");      // not transcribed (in PAIDUS!)
            System.out.println("started writing document " + chatFilename + "...");
            fos = new FileOutputStream(new File(chatFilename));
            fos.write(chatText.getBytes("UTF-8"));
            fos.close();
            System.out.println("document written.");
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (FSMException ex) {
            ex.printStackTrace();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
    }
}
