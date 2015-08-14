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
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.segment.CHATSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;
import org.xml.sax.SAXException;
import org.jdom.*;

/**
 *
 * @author thomas
 */
public class WriteCHATFiles extends AbstractBasicTranscriptionProcessor {
    
    //String STYLESHEET = ""; //Constants.LIST2HTMLStylesheet;
    //String suffix = "_ulist";
    //String toTopLevel = "../../../";
    //XSLTransformer transformer;
    
    
    /** Creates a new instance of MakePartiturFiles */
    public WriteCHATFiles(String corpusName) {
        super(corpusName);        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            WriteCHATFiles wul = new WriteCHATFiles(args[0]);
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
            SegmentedTranscription st = bt.toSegmentedTranscription();
            ListTranscription lt = st.toListTranscription(new SegmentedToListInfo(st, SegmentedToListInfo.TURN_SEGMENTATION));
            lt.getHead().getMetaInformation().relativizeReferencedFile(currentFilename);
            lt.getBody().sort();
            String chatFilename = getCurrentDirectoryname() + System.getProperty("file.separator") + "export" + System.getProperty("file.separator") + getNakedFilenameWithoutSuffix() + ".cha";
            String chatText = CHATSegmentation.toText(lt, "e");
            System.out.println("started writing document " + chatFilename + "...");
            fos = new FileOutputStream(new File(chatFilename));
            fos.write(chatText.getBytes("UTF-8"));
            fos.close();
            System.out.println("document written.");
        } catch (JexmaraldaException ex) {
            Logger.getLogger(WriteCHATFiles.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WriteCHATFiles.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(WriteCHATFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }
    
}
