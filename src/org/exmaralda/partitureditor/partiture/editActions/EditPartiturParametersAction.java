/*
 * EditPartiturParametersAction.java
 *
 * Created on 16. Juni 2003, 16:05
 */

package org.exmaralda.partitureditor.partiture.editActions;

import org.exmaralda.partitureditor.jexmaraldaswing.PartiturParametersDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;

/**
 *
 * opens a dialog for editing the partitur parameters
 * Menu: File --> Edit partitur parameters
 * @author  thomas
 */
public class EditPartiturParametersAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    
    /** Creates a new instance of EditPartiturParametersAction */
    public EditPartiturParametersAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Partitur preferences...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        table.commitEdit(true);
        System.out.println("editPartiturParametersAction!");
        editPartiturParameters();        
    }
    
    private void editPartiturParameters(){
        PartiturParametersDialog dialog = new PartiturParametersDialog(
                                            table.parent, true, 
                                            table.printParameters, table.rtfParameters, 
                                            table.htmlParameters, table.svgParameters);
        org.exmaralda.partitureditor.interlinearText.PageOutputParameters pop = table.printParameters;
        if (dialog.editPartiturParameters()){
            table.printParameters = dialog.getPrintParameters();
            // changed 05-10-2006
            //table.printParameters.setPaperMeasures(table.pageFormat);
            table.printParameters.setPaperMeasures(pop);
            table.rtfParameters = dialog.getRTFParameters();
            // changed 05-10-2006
            //table.rtfParameters.setPaperMeasures(table.pageFormat);
            table.rtfParameters.setPaperMeasures(pop);
            table.htmlParameters = dialog.getHTMLParameters();
            table.svgParameters = dialog.getSVGParameters();
            table.status("Partitur parameters edited");

        }
    }
    
    
}
