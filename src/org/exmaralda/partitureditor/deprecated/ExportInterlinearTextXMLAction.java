/*
 * ExportInterlinearTextXMLAction.java
 *
 * Created on 17. Juni 2003, 11:03
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;

/**
 * Exports the current transcription as an Interlinear Text file
 * acording to the respective EXMARaLDA DTD
 * Menu: File --> Export --> Export Interlinear Text XML
 * @author  thomas
 */
public class ExportInterlinearTextXMLAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ExportInterlinearTextXMLAction */
    public ExportInterlinearTextXMLAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Interlinear text XML...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportInterlinearTextXMLAction!");
        table.commitEdit(true);
        exportInterlinearTextXML();        
    }
    
    private void exportInterlinearTextXML(){
        ExportInterlinearTextXMLDialog dialog = new ExportInterlinearTextXMLDialog(table.homeDirectory, table.getModel().getTranscription(),
        table.getModel().getTierFormatTable(), table.printParameters, table.rtfParameters, table.htmlParameters);
        boolean success = dialog.exportInterlinearTextXML(table.parent);
    }
    
    
}
