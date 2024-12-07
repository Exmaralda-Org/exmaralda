/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs;

import java.beans.PropertyChangeEvent;
import java.io.*;
/**
 *
 * @author thomas
 */
public class LegacyExportFileDialog extends AbstractFileFilterDialog  implements java.beans.PropertyChangeListener {



    /** Creates new ExportTASXDialog
     * @param startDirectory */
    public LegacyExportFileDialog(String startDirectory) {
        super();
        boolean thisIsAMac = System.getProperty("os.name").substring(0,3).equalsIgnoreCase("mac");
        if (thisIsAMac){
            setPreferredSize(new java.awt.Dimension(800, 600));
        }
        setCurrentDirectory(new File(startDirectory).getParentFile());
        setDialogTitle("Export file (legacy formats)");
        setAcceptAllFileFilterUsed(false);
        addChoosableFileFilter(TASXFileFilter);
        addChoosableFileFilter(AGFileFilter);
        setFileFilter(TASXFileFilter);
        setMultiSelectionEnabled(false);
        addPropertyChangeListener("fileFilterChanged", this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // do nothing
    }


}
