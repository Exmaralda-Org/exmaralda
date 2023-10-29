/*
 * NewAction.java
 *
 * Created on 16. Juni 2003, 16:23
 */

package org.exmaralda.partitureditor.partiture.webServicesActions;

import java.io.IOException;
import java.util.HashMap;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.partiture.PartitureTableEvent;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;
import org.exmaralda.webservices.swing.WebServiceProgessDialog;
import org.exmaralda.webservices.swing.WhisperParameterDialog;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;



/**
 * Creates a new transcription
 * Menu: File --> New
 * @author  thomas
 */
public class WhisperAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    WebServiceProgessDialog pbd;
    

    /** Creates a new instance of NewAction
     * @param t
     * @param icon */
    public WhisperAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Whisper...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        try {
            table.commitEdit(true);
            System.out.println("WhisperAction!");
            whisper();
            table.transcriptionChanged = false;
            table.clearUndo();
            table.clearSearchResult();
            table.setFrameEndPosition(-2);
            
            PartitureTableEvent event = new PartitureTableEvent(this, PartitureTableEvent.WHISPER_PARAMETERS_CHANGED, null);
            table.fireEvent(event);
            
            
        } catch (JexmaraldaException | IOException | JDOMException | SAXException | FSMException ex) {
            System.out.println(ex.getLocalizedMessage());
            javax.swing.JOptionPane.showMessageDialog(table.getParent(), ex.getLocalizedMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //List<Tier> resultTiers;
    //List<Tier> languageTiers;
    
    
    private void whisper() throws JexmaraldaException, IOException, JDOMException, SAXException, FSMException{
        
        
        // let the user define parameters
        WhisperParameterDialog whisperParameterDialog = new WhisperParameterDialog(table.parent, true);
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor");
        
        // retrieve values from preferences
        String apiKey = settings.get("WHISPER-API-KEY", "");
        String prompt = settings.get("WHISPER-PROMPT", "");
        String language = settings.get("WHISPER-LANGUAGE", "--");

        whisperParameterDialog.setParameters(apiKey, prompt, language);
        
        whisperParameterDialog.setLocationRelativeTo(table);
        whisperParameterDialog.setVisible(true);
        if (!whisperParameterDialog.approved) return;
        final HashMap<String, Object> whisperParameters = whisperParameterDialog.getWhisperParameters();
        
        // write the parameters to the preferences
        String apiKeyInput = (String) whisperParameters.get("API-KEY");
        settings.put("WHISPER-API-KEY", apiKeyInput);        
        String promptInput = (String)whisperParameters.get("PROMPT");
        settings.put("WHISPER-PROMPT", promptInput);        
        String languageInput = (String)whisperParameters.get("SOURCE-LANGUAGE");
        settings.put("WHISPER-LANGUAGE", languageInput);        
    }
    
}



