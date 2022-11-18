/*
 * CopyTextAction.java
 *
 * Created on 17. Juni 2003, 12:39
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.exakt.utilities.HTMLSelection;


/**
 *
 * @author  thomas
 * new 18-11-2022 for issue #338
 */
public class CopyHTMLAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    public boolean markOverlaps = false;

    /** Creates a new instance of CopyTextAction
     * @param t
     * @param icon */
    public CopyHTMLAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Copy HTML", icon, t);
        //this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("copyHTMLAction!");
        copyHTML();        
    }
    
    // completely changed for version 1.3.4., 16-02-2007
    // now copies RTF to the clipboard
    private void copyHTML(){
        if (table.isEditing){
            // if the table is editing, we just want to selected text of the editing component
            javax.swing.JTextField editingComponent = (javax.swing.JTextField)(table.getEditingComponent());
            if (editingComponent!=null){
                String text = "<span>" + editingComponent.getSelectedText() + "</span>";
                table.getToolkit().getSystemClipboard().setContents(new HTMLSelection(text),null);
            }
            return;
        }
        BasicTranscription newTranscription = table.getCurrentSelectionAsNewTranscription();
        int timelineStart = table.selectionStartCol;
        org.exmaralda.partitureditor.interlinearText.InterlinearText it =
            ItConverter.BasicTranscriptionToInterlinearText(newTranscription, table.getModel().getTierFormatTable(), timelineStart);
        if (table.getFrameEndPosition()>=0){
            ((org.exmaralda.partitureditor.interlinearText.ItBundle)it.getItElementAt(0)).frameEndPosition=table.getFrameEndPosition();
        }
        // added 15-06-2009
        if (markOverlaps){
            it.markOverlaps("[", "]");
        }
        System.out.println("Transcript converted to interlinear text.");
        //System.out.println("Beginning trim");
        //it.trim(table.htmlParameters);
        //if (table.rtfParameters.glueAdjacent){
        //    it.glueAdjacentItChunks(table.rtfParameters.glueEmpty, table.rtfParameters.criticalSizePercentage);
        //}
        //it.copyRTFToClipboard(table.rtfParameters);
        String html = it.toHTML(table.htmlParameters);
        table.getToolkit().getSystemClipboard().setContents(new HTMLSelection(html),null);            
    }
    
   
}
