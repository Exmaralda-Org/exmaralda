/*
 * PageSetupAction.java
 *
 * Created on 17. Juni 2003, 09:15
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import org.exmaralda.partitureditor.jexmaraldaswing.PageSetupDialog;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;

/**
 * Opens a dialog for editing the page setup (i.e. size, margins, etc.)
 * Menu: File --> Page Setup...
 * @author  thomas
 */
public class PageSetupAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of PageSetupAction */
    public PageSetupAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Page setup...", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        table.commitEdit(true);
        System.out.println("pageSetupAction!");
        pageSetup();        
    }
    
    private void pageSetup(){              
        String os = System.getProperty("os.name").substring(0,3);
        if (!os.equalsIgnoreCase("mac")) {        
            table.pageFormat = java.awt.print.PrinterJob.getPrinterJob().pageDialog(table.pageFormat);
            table.printParameters.setPaperMeasures(table.pageFormat);
            table.rtfParameters.setPaperMeasures(table.pageFormat);
            table.htmlParameters.setWidth(table.rtfParameters.getPixelWidth());
        } else {
            PageSetupDialog dialog = new PageSetupDialog(table.printParameters, table.parent, true);
            dialog.show();
            if (dialog.OK){
                org.exmaralda.partitureditor.interlinearText.PageOutputParameters pop = dialog.getPageOutputParameters();
                table.pageFormat.setPaper(org.exmaralda.partitureditor.interlinearText.PageOutputParameters.makePaperFromParameters(pop));
                if (pop.landscape){
                    table.pageFormat.setOrientation(java.awt.print.PageFormat.LANDSCAPE);
                } else {
                    table.pageFormat.setOrientation(java.awt.print.PageFormat.PORTRAIT);
                }
                table.printParameters.setPaperMeasures(pop);
                table.rtfParameters.setPaperMeasures(pop);
                table.htmlParameters.setWidth(table.rtfParameters.getPixelWidth());                
            }
        }
    }
    
    
}
