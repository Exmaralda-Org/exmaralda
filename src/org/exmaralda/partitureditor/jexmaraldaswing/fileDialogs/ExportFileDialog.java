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
public class ExportFileDialog extends AbstractFileFilterDialog  implements java.beans.PropertyChangeListener {


    public CHATExportAccessoryPanel chatExportAccessoryPanel = new CHATExportAccessoryPanel();
    public AudacityExportAccessoryPanel audacityExportAccessoryPanel = new AudacityExportAccessoryPanel();
    public TEIExportAccessoryPanel teiExportAccessoryPanel = new TEIExportAccessoryPanel();
    public TCFExportAccessoryPanel tcfExportAccessoryPanel = new TCFExportAccessoryPanel();

    /** Creates new ExportTASXDialog
     * @param startDirectory */
    public ExportFileDialog(String startDirectory) {
        super();
        boolean thisIsAMac = System.getProperty("os.name").substring(0,3).equalsIgnoreCase("mac");
        if (thisIsAMac){
            setPreferredSize(new java.awt.Dimension(800, 600));
        }
        setCurrentDirectory(new File(startDirectory).getParentFile());
        setDialogTitle("Export file");
        setAcceptAllFileFilterUsed(false);
        addChoosableFileFilter(EAFFileFilter);
        addChoosableFileFilter(PraatFileFilter);
        
        // issue #108
        //addChoosableFileFilter(FOLKERTranscriptionFileFilter);
        addChoosableFileFilter(FLKTranscriptionFileFilter);
        addChoosableFileFilter(FLNTranscriptionFileFilter);
        
        addChoosableFileFilter(TEIFileFilter);
        addChoosableFileFilter(TCFFileFilter);
        //addChoosableFileFilter(TEIModenaFileFilter);
        addChoosableFileFilter(CHATTranscriptFileFilter);
        addChoosableFileFilter(AudacityLabelFileFilter);
        addChoosableFileFilter(TreeTaggerFilter);
        addChoosableFileFilter(F4TextFileFilter);
        addChoosableFileFilter(SRTFileFilter);
        addChoosableFileFilter(TsvFileFilter);
        addChoosableFileFilter(TASXFileFilter);
        addChoosableFileFilter(AGFileFilter);
        // removed 10-04-2013
        //addChoosableFileFilter(ExmaraldaSegmentedTranscriptionFileFilter);
        setFileFilter(PraatFileFilter);
        setMultiSelectionEnabled(false);
        addPropertyChangeListener("fileFilterChanged", this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!(evt.getPropertyName().equals("fileFilterChanged"))) return;
        if (getFileFilter()==CHATTranscriptFileFilter){
            setAccessory(chatExportAccessoryPanel);
        } else if (getFileFilter()==TEIFileFilter){
            setAccessory(teiExportAccessoryPanel);
        } else if (getFileFilter()==TCFFileFilter){
            setAccessory(tcfExportAccessoryPanel);
        } else if (getFileFilter()==AudacityLabelFileFilter){
            setAccessory(audacityExportAccessoryPanel);
        } else {
            setAccessory(null);
        }
        revalidate();

    }

}
