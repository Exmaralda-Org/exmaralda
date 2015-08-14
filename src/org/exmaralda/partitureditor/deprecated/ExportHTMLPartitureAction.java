/*
 * exportHTMLPartitureAction.java
 *
 * Created on 17. Juni 2003, 11:19
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.deprecated.ExportHTMLPartitureFileDialog;
import org.exmaralda.partitureditor.jexmaralda.Head;
import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

/**
 * Exports the current transcription as a partiture in HTML
 * Menu: File --> Visualise --> Export HTML partiture
 * @author  thomas
 */
public class ExportHTMLPartitureAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of exportHTMLPartitureAction */
    public ExportHTMLPartitureAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("HTML partiture...", icon, t);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                                
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exportHTMLAction!");
        table.commitEdit(true);
        exportHTMLPartiture();        
    }
    
    private void exportHTMLPartiture(){
        org.exmaralda.partitureditor.interlinearText.InterlinearText it =
            ItConverter.BasicTranscriptionToInterlinearText(table.getModel().getTranscription(), table.getModel().getTierFormatTable());
        System.out.println("Transcript converted to interlinear text.");
        if (table.getFrameEndPosition()>=0){((org.exmaralda.partitureditor.interlinearText.ItBundle)it.getItElementAt(0)).frameEndPosition=table.getFrameEndPosition();}
        
        Head head = table.getModel().getTranscription().getHead();
        if (table.htmlParameters.prependAdditionalInformation){
            if (table.head2HTMLStylesheet.length()>0){
                try{
                    org.exmaralda.partitureditor.jexmaralda.convert.HTMLConverter converter = new org.exmaralda.partitureditor.jexmaralda.convert.HTMLConverter();
                    table.htmlParameters.additionalStuff = converter.HeadToHTML(head, table.head2HTMLStylesheet);
                } catch (Exception e){
                    javax.swing.JOptionPane dialog = new javax.swing.JOptionPane();
                    String text = new String("There was a problem with " + System.getProperty("line.separator"));
                    text+=table.head2HTMLStylesheet + " : " + System.getProperty("line.separator");
                    text+=e.getLocalizedMessage() + System.getProperty("line.separator");
                    text+="Using internal stylesheet instead.";                    
                    dialog.showMessageDialog(table.getParent(), text);
                    table.htmlParameters.additionalStuff = head.toHTML();
                }                
            } else {
                // if no custon stylesheet is specified,
                // simply apply the built in stylesheet
                table.htmlParameters.additionalStuff = head.toHTML();
            }
        }
        
        ExportHTMLPartitureFileDialog dialog = new ExportHTMLPartitureFileDialog(it, table.htmlParameters, table.htmlDirectory);
        boolean success = dialog.saveHTML(table);
        table.htmlDirectory = dialog.getFilename();
        table.htmlParameters.additionalStuff = "";
        //table.reexportHTMLAction.setEnabled(true);
    }
    
    
}
