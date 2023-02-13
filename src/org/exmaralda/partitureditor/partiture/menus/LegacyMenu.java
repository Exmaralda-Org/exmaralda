/*
 * this.java
 *
 * Created on 1. Juli 2003, 15:11
 */

package org.exmaralda.partitureditor.partiture.menus;

import java.awt.Color;
import javax.swing.JLabel;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class LegacyMenu extends AbstractTableMenu {
    

    /** Creates a new instance of EventMenu
     * @param t */
    public LegacyMenu(PartitureTableWithActions t) {
        super(t);

        this.setText("Legacy");
        this.setForeground(Color.BLUE);

        JLabel fileLabel = new JLabel("File");
        fileLabel.setForeground(Color.GRAY);
        add(fileLabel);
        add(table.legacyImportAction);
        add(table.legacyExportAction);
        add(table.legacyOutputAction);

        addSeparator();

        JLabel sfb538Label = new JLabel("SFB 538");
        sfb538Label.setForeground(Color.GRAY);
        add(sfb538Label);
        add(table.syllableStructureAction);
        add(table.k8MysteryConverterAction);
        add(table.exSyncEventShrinkerAction);
        add(table.exSyncCleanupAction);
        addSeparator();
        
        
        JLabel sfb632Label = new JLabel("SFB 632");
        sfb632Label.setForeground(Color.GRAY);
        add(sfb632Label);
        add(table.appendSpaceAction);
        addSeparator();
        
        JLabel sinLabel = new JLabel("SiN");
        sinLabel.setForeground(Color.GRAY);
        add(sinLabel);
        add(table.stadtspracheWordSegmentationAction);
        add(table.stadtspracheTierSegmentationAction);
        addSeparator();

        JLabel inelLabel = new JLabel("INEL");
        inelLabel.setForeground(Color.GRAY);
        add(inelLabel);
        add(table.importActionInel);

        /*JLabel transcriptionLabel = new JLabel("Transcription");
        transcriptionLabel.setForeground(Color.GRAY);
        add(transcriptionLabel);*/
        
    }
    
}
