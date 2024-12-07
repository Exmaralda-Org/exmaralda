/*
 * SaveSnapshotDialog.java
 *
 * Created on 19. April 2005, 11:00
 */

package org.exmaralda.partitureditor.sound;

/**
 *
 * @author  thomas
 */
public class SaveSnapshotDialog extends AbstractSaveMediaDialog {
    
    /** Creates new form SaveSnapshotDialog */
    public SaveSnapshotDialog(java.awt.Frame parent, boolean modal, String fn, boolean enableLink) {
        super(parent, modal, fn, enableLink);
        filenameTextField.setText(findFreeFilename(fn, "png"));
        org.exmaralda.common.helpers.Internationalizer.internationalizeDialogToolTips(this);

    }
    
}
