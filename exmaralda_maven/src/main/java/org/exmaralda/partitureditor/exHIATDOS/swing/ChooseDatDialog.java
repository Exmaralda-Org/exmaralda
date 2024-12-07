/*
 * ChooseDatDialog.java
 *
 * Created on 29. Oktober 2002, 11:59
 */

package org.exmaralda.partitureditor.exHIATDOS.swing;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.OpenBasicTranscriptionDialog;



/**
 *
 * @author  Thomas
 * @version 
 */
public class ChooseDatDialog extends OpenBasicTranscriptionDialog {

    /** Creates new ImportPraatDialog */
    public ChooseDatDialog(String startDirectory) {
        super(startDirectory);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter("dat", "HIAT-DOS Transkriptdateien"));
        setDialogTitle("Transkriptdatei auswählen");        
    }
    
}
