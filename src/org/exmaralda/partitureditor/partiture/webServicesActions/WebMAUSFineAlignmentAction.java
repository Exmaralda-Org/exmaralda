/*
 * NewAction.java
 *
 * Created on 16. Juni 2003, 16:23
 */

package org.exmaralda.partitureditor.partiture.webServicesActions;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.exmaralda.alignment.FineAligner;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;
import org.exmaralda.webservices.WebServiceProgressListener;
import org.exmaralda.webservices.swing.MAUSFineAlignmentParameterDialog;
import org.exmaralda.webservices.swing.WebServiceProgessDialog;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;



/**
 * Creates a new transcription
 * Menu: File --> New
 * @author  thomas
 */
public class WebMAUSFineAlignmentAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction
        implements WebServiceProgressListener 
        {
    
    WebServiceProgessDialog pbd;
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
            boolean proceed = table.checkSave();
            if (!proceed) {return;}
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
                       

        pbd = new WebServiceProgessDialog(table.parent, false);
        pbd.setLocationRelativeTo(table.parent);
        pbd.setTitle("WebMAUS Fine alignment... ");
        //pbd.setAlwaysOnTop(true);
        pbd.setVisible(true);
        
        
        BasicTranscription inputBT = table.getModel().getTranscription().makeCopy();
        //final FineAligner fineAligner = new FineAligner(inputBT);
        
        final FineAligner fineAligner = new FineAligner(inputBT, 
                (double) mausParameters.get("MIN-INTERVAL-LENGTH"),
                (double) mausParameters.get("MAX-INTERVAL-LENGTH"),
                (String) mausParameters.get("LANGUAGE")
        );
        fineAligner.addProgressListener(this);

        // do this in a thread so we can report progress
        Thread mausThread = new Thread(){
            @Override
            public void run() {
                try {
                    fineAligner.doFineAlignment(); 
                    BasicTranscription outputBT = fineAligner.getTranscription();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {   
                                success(outputBT);
                            } catch (IOException | JexmaraldaException ex) {
                                Logger.getLogger(WebMAUSFineAlignmentAction.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }                        
                    });
                    
                } catch (JexmaraldaException | IOException | JDOMException | SAXException | FSMException ex) {
                    Logger.getLogger(WebMAUSFineAlignmentAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        };
        mausThread.start();

        
    }
    
    public void success(BasicTranscription transcription) throws IOException, JexmaraldaException{
        // todo
        pbd.addText("Fine alignment done.");
        table.getModel().setTranscription(transcription);
        //table.resetData();           
    }

    @Override
    public void processProgress(String message, double progress) {
        pbd.addText(message);
    }
}



