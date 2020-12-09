/*
 * AddBookmarkAction.java
 *
 * Created on 11. November 2004, 11:22
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.partitureditor.jexmaralda.convert.EXMARaLDATransformer;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
import org.exmaralda.partitureditor.jexmaraldaswing.TransformationDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.partiture.*;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author  thomas
 */
public class TransformationAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    TransformationDialog transformationDialog;
    
    int rememberTheStart = 0;

    /** Creates a new instance of AddBookmarkAction */
    public TransformationAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Transformation...", icon, t);
        transformationDialog = new TransformationDialog(table.parent, true, (ExmaraldaApplication)(table.parent));
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("transformationAction");
        rememberTheStart = table.getSelectionStartPosition();
        table.commitEdit(true);
        transformation();
    }
    
    private void transformation(){
        try {
            transformationDialog.setTranscription(table.getModel().getTranscription());
            transformationDialog.setLocationRelativeTo(table);
            transformationDialog.approved=false;
            transformationDialog.setVisible(true);
            if (transformationDialog.approved){
                BasicTranscription transcription = table.getModel().getTranscription().makeCopy();
                String[] parameters = transformationDialog.getParameters();
                String[][] xslParameters = transformationDialog.getXSLParameters();
                AbstractSegmentation segmentation = table.getAbstractSegmentation(parameters[1]);
                transform(transcription, segmentation, parameters, xslParameters);
            }
        } catch (IOException | ParserConfigurationException | FSMException | JexmaraldaException | JDOMException | SAXException ex) {
            Logger.getLogger(TransformationAction.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(table, "Transformation failed: \n" + ex.getLocalizedMessage());
        }
    }

    public void transform(BasicTranscription transcription, AbstractSegmentation segmentation, String[] parameters, String[][] xslParameters) throws JDOMException, IOException, SAXException, FSMException, ParserConfigurationException, JexmaraldaException{
        /*BasicTranscription transcription = table.getModel().getTranscription().makeCopy();
        String[] parameters = transformationDialog.getParameters();
        String[][] xslParameters = transformationDialog.getXSLParameters();
        AbstractSegmentation segmentation = table.getAbstractSegmentation(parameters[1]);*/
        
        // new 01-12-2020, for DULKO, issue #229
        EXMARaLDATransformer exmaraldaTransformer = new EXMARaLDATransformer(transcription, segmentation, parameters, xslParameters);
        String resultString;
        try {
            resultString = exmaraldaTransformer.transform();
        } catch (TransformerException ex) {
            Logger.getLogger(TransformationAction.class.getName()).log(Level.SEVERE, null, ex);
            String message = "<html><b>Transformation failed: </b><br/>" + ex.getLocalizedMessage();
            if (!(exmaraldaTransformer.getWarnings().isEmpty())){
                message+="<br/><br/><b>Messages:</b><br/>";
                for (String w : exmaraldaTransformer.getWarnings()){
                    message+=w + "<br/>";
                }
            }
            message+="</html>";
            JOptionPane.showMessageDialog(table, message, "Transformation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (parameters[4].equals("self-transformation")){
            table.checkSave();
            BasicTranscription selfTransformedTranscription = new BasicTranscription();
            selfTransformedTranscription.BasicTranscriptionFromString(resultString);
            table.getModel().setTranscription(selfTransformedTranscription);
            // issue #231
            table.makeColumnVisible(rememberTheStart);
            return;
        }

        JFileChooser jfc = new JFileChooser();
        if (!(parameters[4].equals("other"))){
            ParameterFileFilter pff = new ParameterFileFilter(parameters[4], parameters[4]);
            jfc.addChoosableFileFilter(pff);
        }

        // added 25-05-2009
        Preferences prefs = java.util.prefs.Preferences.userRoot().node(((ExmaraldaApplication)(table.getTopLevelAncestor())).getPreferencesNode());
        jfc.setSelectedFile(new File(prefs.get("LAST-TRANSFORM-DIR", System.getProperty("user.dir"))));


        int ret = jfc.showSaveDialog(table);
        if (ret==JFileChooser.APPROVE_OPTION){
            File file = jfc.getSelectedFile();
            if (!(file.getName().contains("."))){
                if (!(parameters[4].equals("other"))){
                    file = new File(file.getAbsolutePath()+ "." + parameters[4]);
                }
            }
            if (file.exists()){
                String mess = file.getAbsolutePath() + "\nexists. Overwrite?";
                int choice = JOptionPane.showConfirmDialog(table, mess, "Warning", JOptionPane.YES_NO_OPTION);
                if (choice==JOptionPane.NO_OPTION) return;
            }

            System.out.println("started writing document...");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(resultString.getBytes("UTF-8"));
            fos.close();
            // added 25-05-2009
            prefs.put("LAST-TRANSFORM-DIR", file.getAbsolutePath());

            System.out.println("document written.");
        }




    }



    

    
}
