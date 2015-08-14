/*
 * AddBookmarkAction.java
 *
 * Created on 11. November 2004, 11:22
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;
import org.exmaralda.partitureditor.jexmaraldaswing.TransformationDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.partiture.*;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author  thomas
 */
public class TransformationAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    TransformationDialog transformationDialog;

    /** Creates a new instance of AddBookmarkAction */
    public TransformationAction(PartitureTableWithActions t) {
        super("Transformation...", t);
        transformationDialog = new TransformationDialog(table.parent, true, (ExmaraldaApplication)(table.parent));
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("transformationAction");
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
                transform();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(table, "Transformation failed: \n" + ex.getLocalizedMessage());
        }
    }

    public void transform() throws JDOMException, IOException, SAXException, FSMException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException{
        BasicTranscription transcription = table.getModel().getTranscription().makeCopy();
        String[] parameters = transformationDialog.getParameters();
        Document baseDocument = null;
        if (parameters[0].startsWith("basic")){
            baseDocument = IOUtilities.readDocumentFromString(transcription.toXML());
        } else if ((parameters[0].startsWith("segmented")) || (parameters[0].startsWith("list"))){
            SegmentedTranscription st = null;
            if (parameters[1].equals("NONE")){
                st = transcription.toSegmentedTranscription();
            } else {
                AbstractSegmentation as = table.getAbstractSegmentation(parameters[1]);
                st = as.BasicToSegmented(transcription);
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
            new TEIConverter().writeGenericTEIToFile(transcription, temp.getAbsolutePath());
            baseDocument = IOUtilities.readDocumentFromLocalFile(temp.getAbsolutePath());
        }

        String resultString = "";
        if (parameters[3].length()>0){
            StylesheetFactory sf = new StylesheetFactory(true);
            String sourceString = IOUtilities.documentToString(baseDocument);
            if (parameters[3].startsWith("/")){
                resultString = sf.applyInternalStylesheetToString(parameters[3], sourceString);
            } else {
                resultString = sf.applyExternalStylesheetToString(parameters[3], sourceString);
            }
        } else {
            resultString = IOUtilities.documentToString(baseDocument);
        }

        if (parameters[4].equals("self-transformation")){
            table.checkSave();
            BasicTranscription selfTransformedTranscription = new BasicTranscription();
            selfTransformedTranscription.BasicTranscriptionFromString(resultString);
            table.getModel().setTranscription(selfTransformedTranscription);
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
