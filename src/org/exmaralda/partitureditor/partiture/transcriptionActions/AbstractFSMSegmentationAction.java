/*
 * AbstractFSMSegmentationAction.java
 *
 * Created on 9. Juli 2003, 11:12
 */

package org.exmaralda.partitureditor.partiture.transcriptionActions;

import org.exmaralda.partitureditor.fsm.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.xml.sax.*;
import javax.swing.JOptionPane;
import org.exmaralda.common.helpers.Internationalizer;

/**
 *
 * @author  thomas
 */
public abstract class AbstractFSMSegmentationAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    public static final int CONTINUE_OPTION = 0;
    public static final int CANCEL_OPTION = 1;
    public static final int EDIT_OPTION = 2;
    
    /** Creates a new instance of AbstractFSMSegmentationAction */
    public AbstractFSMSegmentationAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("FSM segmentation", icon, t); 
    }
    
    /** Creates a new instance of AbstractFSMSegmentationAction */
    public AbstractFSMSegmentationAction(String name,  javax.swing.ImageIcon icon, PartitureTableWithActions t) {
        super(Internationalizer.getString(name), icon, t); 
    }

    /** Creates a new instance of AbstractFSMSegmentationAction */
    public AbstractFSMSegmentationAction(String name,  PartitureTableWithActions t) {
        super(Internationalizer.getString(name), t);
    }

    public void processFSMException(FSMException fsme){
        javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();

        String po = org.exmaralda.partitureditor.jexmaralda.StringUtilities.stripXMLElements(fsme.getProcessedOutput());
        String po2 = "..." + po.substring(Math.max(0,po.length()-50));
        String tli = Integer.toString(table.getModel().getTranscription().getBody().getCommonTimeline().lookupID(fsme.getTLI()));
        String tier = fsme.getTierID();
        String lineSep = System.getProperty("line.separator");
        String messageText = fsme.getMessage() + lineSep +  "Processed Output:" + lineSep + po2 + lineSep + "TLI: " + tli +
                            lineSep + "Tier: " + tier;
        JOptionPane.showMessageDialog(  table, messageText,"FSM Error",javax.swing.JOptionPane.INFORMATION_MESSAGE);

        // make a dummy search result and send it to the table
        // so that it scrolls to the offending positiion
        org.exmaralda.partitureditor.search.EventSearchResult esr = new org.exmaralda.partitureditor.search.EventSearchResult();
        esr.tierID = tier;
        Event ev = new Event();
        ev.setStart(fsme.getTLI());
        esr.event = ev;
        table.processSearchResult(esr);        
    }
    
    public void processSAXException(SAXException se){
        JOptionPane.showMessageDialog(  table,
            se.getMessage(),
            "SAX Error",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);         
    }
    
    public void showListCouldNotBeCompletelyOrderedMessage(){
        //javax.swing.JOptionPane warningDialog = new javax.swing.JOptionPane();
        JOptionPane.showMessageDialog(table,
            "The list could not be completely ordered!\n Some segments may have been skipped.",
            "Information",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);                 
    }
    
    public int showOrderingProblemDialog(String message){
        //javax.swing.JOptionPane warningDialog = new javax.swing.JOptionPane();
        String[] options = {"Continue", "Cancel", "Edit"};
        return JOptionPane.showOptionDialog( table,
                                               message, 
                                               "Warning", 
                                               -1, 
                                               JOptionPane.WARNING_MESSAGE, 
                                               null, 
                                               options,
                                               "Continue");
        
    }
}
