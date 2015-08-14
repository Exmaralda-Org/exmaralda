/*
 * ExportRTFPartitureAction.java
 *
 * Created on 17. Juni 2003, 11:13
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

/**
 * Exports the current transcription as a partitur in RTF
 * Menu: File --> Visualise --> Export RTF partiture
 * @author  thomas
 */
public class ExportRTFPartitureAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ExportRTFPartitureAction */
    public ExportRTFPartitureAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("RTF partiture...", icon, t);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                        
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportRTFAction!");
        table.commitEdit(true);
        exportRTFPartiture();        
    }
    
    private void exportRTFPartiture(){
        org.exmaralda.partitureditor.interlinearText.InterlinearText it =
            ItConverter.BasicTranscriptionToInterlinearText(table.getModel().getTranscription(), table.getModel().getTierFormatTable());
        if (table.getFrameEndPosition()>=0){((org.exmaralda.partitureditor.interlinearText.ItBundle)it.getItElementAt(0)).frameEndPosition=table.getFrameEndPosition();}
        System.out.println("Transcript converted to interlinear text.");
        if (table.rtfParameters.prependAdditionalInformation){
            table.rtfParameters.additionalStuff = table.getModel().getTranscription().getHead().toRTF();
        }
        ExportRTFPartitureFileDialog dialog = new ExportRTFPartitureFileDialog(it, table.rtfParameters, table.rtfDirectory);
        boolean success = dialog.saveRTF(table);
        if (success){
            table.rtfDirectory = dialog.getFilename();
        }
        table.rtfParameters.additionalStuff = "";
        table.rtfParameters.clearMappings();
    }
    
    
}
