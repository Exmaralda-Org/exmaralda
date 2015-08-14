/*
 * MakeSyllableStructureTierAction.java
 *
 * Created on 22. April 2004, 11:48
 */

package org.exmaralda.partitureditor.partiture.sfb538Actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.exSync.ExSyncCleanup;
import org.exmaralda.partitureditor.exSync.swing.ExSyncCleanupDialog;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.jexmaralda.convert.K8MysteryConverter;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class ExSyncCleanupAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of MakeSyllableStructureTierAction */
    public ExSyncCleanupAction(PartitureTableWithActions t) {
        super("[SFB 538] K1: ExSync Cleanup", t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("ExSyncCleanupAction!");
        table.commitEdit(true);
        try {
            doit();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(table, ex.getLocalizedMessage());
        }
    }
    
    private void doit() throws JexmaraldaException{
        ExSyncCleanupDialog d = new ExSyncCleanupDialog(table.parent, true);
        d.setLocationRelativeTo(table.parent);
        d.setVisible(true);
        if (!d.ok) return;

        BasicTranscription bt = table.getModel().getTranscription().makeCopy();
        TierFormatTable tft = table.getModel().getTierFormatTable();
        ExSyncCleanup esc = new ExSyncCleanup(bt);
        String message = "";
        if (d.NBSPCheckBox.isSelected()){
            int i1 = esc.replaceNonBreakingSpace();
            message+=Integer.toString(i1) + " non breaking spaces replaced.\n";
        }
        if (d.ellipsisCheckBox.isSelected()){
            int i2 = esc.replaceEllipsisDots();
            message+=Integer.toString(i2) + " ellipsis dots replaced.\n";
        }
        if (d.ligatureCheckBox.isSelected()){
            int i4 = esc.moveLigature();
            message+=Integer.toString(i4) + " ligature symbols moved.\n";
        }
        if (d.isolatedPuntuationCheckBox.isSelected()){
            int i3 = esc.moveIsolatedPunctuation();
            message+=Integer.toString(i3) + " isolated punctuation sequences moved.\n";
        }
        if (d.colonsCheckBox.isSelected()){
            int i5 = esc.moveInitialColons();
            message+=Integer.toString(i5) + " initial colons moved.\n";
        }
        if (d.spacesCheckBox.isSelected()){
            int i6 = esc.moveInitialSpaces();
            message+=Integer.toString(i6) + " initial spaces moved.\n";
        }
        if (d.incomprehensibleCheckBox.isSelected()){
            int i7 = esc.replaceIncomprehensible();
            message+=Integer.toString(i7) + " notations for incomprehensible passages replaced.\n";

        }
        if (d.splitCheckBox.isSelected()){
            int i8 = esc.splitAtUtteranceEndSymbols();
            message+=Integer.toString(i8) + " events split at utterance end symbols.\n";
        }
        if (d.splitCheckBox.isSelected()){
            int i9 = esc.normalizeWhitespace();
            message+=Integer.toString(i9) + " whitespace normalizations.\n";
        }

        JOptionPane.showMessageDialog(table, message);

        table.getModel().setTranscriptionAndTierFormatTable(bt, tft);
        //table.getModel().resetTranscription();
        table.showAllTiers();
        table.setFilename("untitled.exb");
        table.linkPanelDialog.getLinkPanel().emptyContents();
        table.largeTextField.setText("");
        //table.reexportHTMLAction.setEnabled(false);
        table.restoreAction.setEnabled(false);
        table.reconfigureAutoSaveThread();
        table.setupMedia();
        table.status("New transcription");
        table.transcriptionChanged = true;




    }

}
