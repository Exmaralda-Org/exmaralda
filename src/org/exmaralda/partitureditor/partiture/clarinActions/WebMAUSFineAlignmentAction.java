/*
 * NewAction.java
 *
 * Created on 16. Juni 2003, 16:23
 */

package org.exmaralda.partitureditor.partiture.clarinActions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.exmaralda.alignment.FineAligner;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;
import org.exmaralda.webservices.swing.CLARINProgressDialog;
import org.exmaralda.webservices.swing.MAUSFineAlignmentParameterDialog;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;



/**
 * Creates a new transcription
 * Menu: File --> New
 * @author  thomas
 */
public class WebMAUSFineAlignmentAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    CLARINProgressDialog pbd;
    MAUSFineAlignmentParameterDialog mausFineAlignmentParameterDialog;
    
    /** Creates a new instance of NewAction
     * @param t
     * @param icon */
    public WebMAUSFineAlignmentAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Web MAUS Fine alignment...", icon, t);
        mausFineAlignmentParameterDialog = new MAUSFineAlignmentParameterDialog(table.parent, true);        
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        try {
            table.commitEdit(true);
            System.out.println("WebMAUSFineAlignmentAction!");
            webMAUSFineAlignment();
            table.transcriptionChanged = false;
            table.clearUndo();
            table.clearSearchResult();
            table.setFrameEndPosition(-2);
        } catch (JexmaraldaException | IOException | JDOMException | SAXException | FSMException ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(table.getParent(), ex.getLocalizedMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void webMAUSFineAlignment() throws JexmaraldaException, IOException, JDOMException, SAXException, FSMException{
        
        String wavPath = table.getModel().getTranscription().getHead().getMetaInformation().getReferencedFile("wav");
        if (wavPath==null){
            String message = "No WAV audio file assigned to this transcription. \n"
                    + "WebMAUS cannot run without a WAV audio file. \n"
                    + "Please use Transcription > Recording to assign a WAV audio file.";
            JOptionPane.showMessageDialog(table, message);
            return;
        }
        
        // let the user define parameters for the fine alignment       
        mausFineAlignmentParameterDialog.setLocationRelativeTo(table);
        mausFineAlignmentParameterDialog.setVisible(true);
        if (!mausFineAlignmentParameterDialog.approved) return;
        final HashMap<String, Object> mausParameters = mausFineAlignmentParameterDialog.getMAUSFineAlignmentParameters();
                       

        pbd = new CLARINProgressDialog(table.parent, false);
        pbd.setLocationRelativeTo(table.parent);
        pbd.setTitle("CLARIN-D & WebMAUS... ");
        //pbd.setAlwaysOnTop(true);
        pbd.setVisible(true);
        
        BasicTranscription inputBT = table.getModel().getTranscription().makeCopy();
        final FineAligner fineAligner = new FineAligner(inputBT);
        fineAligner.doFineAlignment();
        BasicTranscription outputBT = fineAligner.getTranscription(); 

        // do this in a thread so we can report progress
        Thread mausThread = new Thread(){
            @Override
            public void run() {
                // todo
            }
            
        };
        mausThread.start();

        
    }
    
    public void success(File praatFile, File wavFile, HashMap<String, Object> mausParameters,
            String tierID, String startID, String endID) throws IOException, JexmaraldaException{
        // todo
        table.resetData();           
    }
}



