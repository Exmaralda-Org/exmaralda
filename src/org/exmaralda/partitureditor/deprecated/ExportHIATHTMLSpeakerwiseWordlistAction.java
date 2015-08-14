/*
 * ExportHTMLUtteranceListAction.java
 *
 * Created on 17. Juni 2003, 16:00
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.partiture.transcriptionActions.AbstractFSMSegmentationAction;
import org.exmaralda.partitureditor.deprecated.segmentationActions.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.fsm.*;
import javax.swing.JFileChooser;
import java.util.*;
import org.xml.sax.*;
import java.io.*;


/**
 *
 * @author  thomas
 */
public class ExportHIATHTMLSpeakerwiseWordlistAction extends AbstractFSMSegmentationAction {
    
    static String XSL_STYLESHEET = "/org/exmaralda/partitureditor/jexmaralda/xsl/HIATSegmented2WordlistBySpeaker.xsl";
    
    /** Creates a new instance of ExportHTMLUtteranceListAction */
    public ExportHIATHTMLSpeakerwiseWordlistAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Word list by speaker (HTML)", icon, t); 
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("ExportHIATHTMLSpeakerwiseWordlistAction!");
        table.commitEdit(true);
        exportHIATHTMLSpeakerwiseWordlist();        
    }
    
    private void exportHIATHTMLSpeakerwiseWordlist() {
         try{
             BasicTranscription bt = table.getModel().getTranscription().makeCopy();
             org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation hs = new org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation(table.hiatFSM);
             SegmentedTranscription st = hs.BasicToSegmented(bt);
             
             org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf = 
                     new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory(true);
             
             String wordlisthtml = sf.applyInternalStylesheetToString(XSL_STYLESHEET, st.toXML());
             
             JFileChooser dialog = new JFileChooser();
             dialog.setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter("html", "HTML files (*.html"));
             int ret = dialog.showSaveDialog(table.getParent());
             if (ret==JFileChooser.APPROVE_OPTION){
                System.out.println("started writing document...");
                FileOutputStream fos = new FileOutputStream(dialog.getSelectedFile());
                fos.write(wordlisthtml.getBytes("UTF-8"));
                fos.close();
                System.out.println("document written.");                 
                table.htmlDirectory = dialog.getSelectedFile().getAbsolutePath();
             }             
        } catch (ParserConfigurationException ex) {
            SAXException se = new SAXException(ex);
            //processSAXException(se);
        } catch (IOException ex) {
            SAXException se = new SAXException(ex);
            //processSAXException(se);
        } catch (TransformerConfigurationException ex) {
            SAXException se = new SAXException(ex);
            //processSAXException(se);
        } catch (TransformerException ex) {
            SAXException se = new SAXException(ex);
            //processSAXException(se);
        } catch (SAXException se){
            //processSAXException(se);
         } catch (FSMException fsme){
            //processFSMException(fsme);
         } 
    }
    
    
}
