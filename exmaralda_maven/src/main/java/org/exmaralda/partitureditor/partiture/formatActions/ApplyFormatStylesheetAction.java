/*
 * ApplyFormatStylesheetAction.java
 *
 * Created on 6. Oktober 2003, 15:36
 */

package org.exmaralda.partitureditor.partiture.formatActions;

import java.io.IOException;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.partiture.*;
import org.xml.sax.SAXException;

/**
 *
 * @author  thomas
 */
public class ApplyFormatStylesheetAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ApplyFormatStylesheetAction
     * @param t
     * @param icon */
    public ApplyFormatStylesheetAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Apply stylesheet", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("applyFormatStylesheetAction!");
        table.commitEdit(true);
        applyStylesheet();   
        table.saveTierFormatTable = true;
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
             } catch (IOException | ParserConfigurationException | TransformerException | SAXException e){                
                javax.swing.JOptionPane dialog = new javax.swing.JOptionPane();
                String text = "There was a problem with " + System.getProperty("line.separator");
                text+=table.transcription2FormattableStylesheet + " : " + System.getProperty("line.separator");
                text+=e.getLocalizedMessage() + System.getProperty("line.separator");
                text+="Using internal stylesheet instead.";                    
                JOptionPane.showMessageDialog(table.getParent(), text);           
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
             } catch (IOException | ParserConfigurationException | TransformerException | SAXException e){
                 e.printStackTrace();
                // should never get here if build was correct
             } 
        }
    }
    
}
