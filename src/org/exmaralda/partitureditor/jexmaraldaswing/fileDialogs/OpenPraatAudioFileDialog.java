/*
 * OpenPraatAudioFileDialog.java
 *
 * Created on 17. Mai 2004, 10:44
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs;


/**
 *
 * @author  thomas
 */
public class OpenPraatAudioFileDialog extends OpenBasicTranscriptionDialog {
    
    /** Creates a new instance of OpenPraatAudioFileDialog */
    public OpenPraatAudioFileDialog() {
        super();
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.PraatAudioFileFilter());
        setDialogTitle("Choose an audio file");                   
    }
    
}
