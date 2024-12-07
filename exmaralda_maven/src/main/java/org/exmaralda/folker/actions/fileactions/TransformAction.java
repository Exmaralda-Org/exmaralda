/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.actions.fileactions;

import org.exmaralda.folker.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.folker.application.ApplicationControl;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.GATParser;
import org.exmaralda.folker.gui.TransformDialog;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.folker.utilities.Constants;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class TransformAction extends AbstractApplicationAction {
    
    /** Creates a new instance of OpenAction
     * @param ac
     * @param name
     * @param icon */
    public TransformAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** TransformAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        TransformDialog transformDialog = new TransformDialog(ac.getFrame(), true);
        transformDialog.setLocationRelativeTo(ac.getFrame());
        transformDialog.setVisible(true);
        if (transformDialog.approved){
            String xsl = transformDialog.getXSL();
            File xslFile = new File(xsl);
            if (!(xslFile.exists())){
                ac.displayException("XSL file\n" + xsl + "\n does not exist");
                return;
            }
            
            String output = transformDialog.getOutput();
            File outputFile = new File(output);
            if (outputFile.exists()){
                int ret = JOptionPane.showConfirmDialog(ac.getFrame(), "Output file\n" + outputFile + "\n exists. Overwrite?");
                if (!(ret==JOptionPane.OK_OPTION)) return;
            }
            
            StylesheetFactory sf = new StylesheetFactory(true);
            EventListTranscription elt = ac.getTranscription();
            Document transcriptionDoc = EventListTranscriptionXMLReaderWriter.toJDOMDocument(elt, null);
            GATParser ap = new GATParser(Constants.getAlphabetLanguage());
            if (((ApplicationControl)applicationControl).PARSE_LEVEL!=3){
                for (int level=1; level<=2; level++){
                    ap.parseDocument(transcriptionDoc, level);
                }
            } else {
                // added 08-05-2014
                ap.parseDocument(transcriptionDoc, 1);
                ap.parseDocument(transcriptionDoc, 3);                        
            }
            
            String docString = org.exmaralda.common.jdomutilities.IOUtilities.documentToString(transcriptionDoc);
            
            try {
                String resultString = sf.applyExternalStylesheetToString(xsl, docString);
                FileOutputStream fos = new FileOutputStream(outputFile);
                fos.write(resultString.getBytes("UTF-8"));
                fos.close();                    
                JOptionPane.showMessageDialog(ac.getFrame(), "Output written to\n" + output);
            } catch (SAXException ex) {
                Logger.getLogger(TransformAction.class.getName()).log(Level.SEVERE, null, ex);
                ac.displayException(ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(TransformAction.class.getName()).log(Level.SEVERE, null, ex);
                ac.displayException(ex);
            } catch (IOException ex) {
                Logger.getLogger(TransformAction.class.getName()).log(Level.SEVERE, null, ex);
                ac.displayException(ex);
            } catch (TransformerException ex) {
                Logger.getLogger(TransformAction.class.getName()).log(Level.SEVERE, null, ex);
                ac.displayException(ex);
            }
        }
    }
    
}
