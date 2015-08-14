/*
 * ApplyFormatStylesheetAction.java
 *
 * Created on 6. Oktober 2003, 15:36
 */

package org.exmaralda.partitureditor.partiture.formatActions;

import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

/**
 *
 * @author  thomas
 */
public class ApplyFormatStylesheetAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ApplyFormatStylesheetAction */
    public ApplyFormatStylesheetAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Apply stylesheet", icon, t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("applyFormatStylesheetAction!");
        table.commitEdit(true);
        applyStylesheet();                
    }
    
    private void applyStylesheet(){
        boolean done = false;
        if (table.transcription2FormattableStylesheet.length()>0){
             try {
                // einen Stylesheet-Prozessor initialisieren
                org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory();

                // den StylesheetProzessor 
                // ein externes Stylesheet (mit dem Pfad table.transcription2FormattableStylesheet)
                // auf die XML-String-Repräsentation der Transkription (table.getModel().getTranscription().toXML())
                // anwenden lassen. Das Ergebnis (formatTableString)
                // ist eine XML-String-Repräsentation einer Formatierungstabelle
                String formatTableString = sf.applyExternalStylesheetToString(table.transcription2FormattableStylesheet, 
                                                                              table.getModel().getTranscription().toXML());
                // eine neue Formatierungstabelle initialisieren
                TierFormatTable tft = new TierFormatTable();
                // die Formatierungstabelle aus einer XML-String-Repräsentation einlesen
                tft.TierFormatTableFromString(formatTableString);
                // die Formatierungstabelle des Partitur-Editors auf die neue Formatierungstabelle setzen
                // (dadurch wird automatisch die Partitur neu formatiert
                table.getModel().setTierFormatTable(tft);            
                done = true;
                table.formatChanged = true;
             } catch (Exception e){                
                javax.swing.JOptionPane dialog = new javax.swing.JOptionPane();
                String text = new String("There was a problem with " + System.getProperty("line.separator"));
                text+=table.transcription2FormattableStylesheet + " : " + System.getProperty("line.separator");
                text+=e.getLocalizedMessage() + System.getProperty("line.separator");
                text+="Using internal stylesheet instead.";                    
                dialog.showMessageDialog(table.getParent(), text);           
             }                
        }
        // if no custom stylesheet is specified,
        // simply apply the built in stylesheet
        if (!done){
             try {
                org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory();

                String formatTableString = sf.applyInternalStylesheetToString("/org/exmaralda/partitureditor/jexmaralda/xsl/FormatTable4BasicTranscription.xsl", 
                                                                              table.getModel().getTranscription().toXML());
                TierFormatTable tft = new TierFormatTable();
                tft.TierFormatTableFromString(formatTableString);
                table.getModel().setTierFormatTable(tft);            
                done = true;
                table.formatChanged = true;
             } catch (Exception e){
                 e.printStackTrace();
                // should never get here if build was correct
             } 
        }
    }
    
}
