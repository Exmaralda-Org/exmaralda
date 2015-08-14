/*
 * SaveAudioPartDialog.java
 *
 * Created on 19. April 2005, 11:02
 */

package org.exmaralda.partitureditor.sound;

/**
 *
 * @author  thomas
 */
public class SaveAudioPartDialog extends AbstractSaveMediaDialog {
    
    /** Creates new form SaveAudioPartDialog */
    public SaveAudioPartDialog(java.awt.Frame parent, boolean modal, String fn, boolean enableLink) {
        super(parent, modal, fn, enableLink);
        this.setTitle("Save media snippet");
        filenameTextField.setText(findFreeFilename(fn, "wav"));
        org.exmaralda.common.helpers.Internationalizer.internationalizeDialogToolTips(this);
        
    }
    
    
}
