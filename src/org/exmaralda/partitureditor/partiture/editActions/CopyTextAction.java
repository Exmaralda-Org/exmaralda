/*
 * CopyTextAction.java
 *
 * Created on 17. Juni 2003, 12:39
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;


/**
 *
 * @author  thomas
 */
public class CopyTextAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    public boolean markOverlaps = false;

    /** Creates a new instance of CopyTextAction */
    public CopyTextAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Copy", icon, t);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("copyTextAction!");
        copyText();        
    }
    
    // completely changed for version 1.3.4., 16-02-2007
    // now copies RTF to the clipboard
    private void copyText(){
        String text = new String();
        if (table.isEditing){
            javax.swing.JTextField editingComponent = (javax.swing.JTextField)(table.getEditingComponent());
            if (editingComponent!=null){
                text = editingComponent.getSelectedText();
            }
        } else {
            int startR = 0; int endR = table.getModel().getNumRows();
            int startC = 0; int endC = table.getModel().getNumColumns();
            if ((table.selectionStartRow >=0) && (table.selectionEndRow>=0) && (table.selectionStartCol>=0) && (table.selectionEndCol>=0)){
                // some arbitrary area is selected
                startR = table.selectionStartRow; endR = table.selectionEndRow+1;
                startC = table.selectionStartCol; endC = table.selectionEndCol+1;
            } else if ((table.selectionStartCol<0) && (table.selectionStartRow>=0) && (table.selectionEndRow>=0)){
                // one or several complete rows are selected
                startR = table.selectionStartRow; endR = table.selectionEndRow+1;
            } else if ((table.selectionStartRow<0) && (table.selectionStartCol>=0) && (table.selectionEndCol>=0)){
                // one or several complete columns are selected
                // place code for RTF copying here
                BasicTranscription newTranscription = table.getCurrentSelectionAsNewTranscription();
                int timelineStart = table.selectionStartCol;
                org.exmaralda.partitureditor.interlinearText.InterlinearText it =
                    ItConverter.BasicTranscriptionToInterlinearText(newTranscription, table.getModel().getTierFormatTable(), timelineStart);
                if (table.getFrameEndPosition()>=0){((org.exmaralda.partitureditor.interlinearText.ItBundle)it.getItElementAt(0)).frameEndPosition=table.getFrameEndPosition();}
                // added 15-06-2009
                if (markOverlaps){
                    it.markOverlaps("[", "]");
                }
                System.out.println("Transcript converted to interlinear text.");
                //System.out.println("Beginning trim");
                it.trim(table.rtfParameters);
                if (table.rtfParameters.glueAdjacent){
                    it.glueAdjacentItChunks(table.rtfParameters.glueEmpty, table.rtfParameters.criticalSizePercentage);
                }
                it.copyRTFToClipboard(table.rtfParameters);
                return;
            }
            //System.out.println("copying " + startR + " to " + endR + " and " + startC + " to " + endC);
            for (int row=startR; row<endR; row++){
                for (int col=startC; col<endC; col++){
                    try{
                        Event event = table.getModel().getEvent(row,col);
                        text+=event.getDescription();
                    } catch (JexmaraldaException je){}
                }
                text+=System.getProperty("line.separator");
            }
        }
        java.awt.datatransfer.StringSelection ss = new java.awt.datatransfer.StringSelection(text);
        table.getToolkit().getSystemClipboard().setContents(ss,ss);
    }
    
    /**private void copyText(){
        String text = new String();
        if (table.isEditing){
            javax.swing.JTextField editingComponent = (javax.swing.JTextField)(table.getEditingComponent());
            if (editingComponent!=null){
                text = editingComponent.getSelectedText();
            }
        } else {
            int startR = 0; int endR = table.getModel().getNumRows();
            int startC = 0; int endC = table.getModel().getNumColumns();
            if ((table.selectionStartRow >=0) && (table.selectionEndRow>=0) && (table.selectionStartCol>=0) && (table.selectionEndCol>=0)){
                // some arbitrary area is selected
                startR = table.selectionStartRow; endR = table.selectionEndRow+1;
                startC = table.selectionEndCol; endC = table.selectionEndCol+1;
            } else if ((table.selectionStartCol<0) && (table.selectionStartRow>=0) && (table.selectionEndRow>=0)){
                // one or several complete rows are selected
                startR = table.selectionStartRow; endR = table.selectionEndRow+1;
            } else if ((table.selectionStartRow<0) && (table.selectionStartCol>=0) && (table.selectionEndCol>=0)){
                // one or several complete columns are selected
                startC = table.selectionStartCol; endC = table.selectionEndCol+1;
            }
            for (int row=startR; row<endR; row++){
                for (int col=startC; col<endC; col++){
                    try{
                        Event event = table.getModel().getEvent(row,col);
                        text+=event.getDescription();
                    } catch (JexmaraldaException je){}
                }
                text+=System.getProperty("line.separator");
            }
        }
        java.awt.datatransfer.StringSelection ss = new java.awt.datatransfer.StringSelection(text);
        table.getToolkit().getSystemClipboard().setContents(ss,ss);
    }**/
    
    
}
