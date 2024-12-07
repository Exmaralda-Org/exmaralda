/*
 * SendHTMLPartitureToBrowserAction.java
 *
 * Created on 17. Juni 2003, 11:23
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import org.exmaralda.partitureditor.jexmaralda.Head;
import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;
import com.klg.jclass.table.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.io.*;

/**
 *
 * @author  thomas
 */
public class SendHTMLPartitureToBrowserAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of SendHTMLPartitureToBrowserAction */
    public SendHTMLPartitureToBrowserAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Show partiture in browser", icon, t);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift H"));            
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("sendHTMLPartitureToBrowser!");
        table.commitEdit(true);
        sendHTMLPartitureToBrowser();        
    }
    
    private void sendHTMLPartitureToBrowser(){
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
                       
        if (table.htmlParameters.getWidth()>0) {
             it.trim(table.htmlParameters);
         }
         try {
             final File tempFile = File.createTempFile("EXMARaLDA", ".html");        
             tempFile.deleteOnExit();
             //it.writeHTMLToFile(table.standardHTMLName, table.htmlParameters);
             it.writeHTMLToFile(tempFile.getAbsolutePath(), table.htmlParameters);
             javax.swing.SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    table.launchBrowser(tempFile);
                }
             });
             
         } catch (IOException ioe){
             javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
             errorDialog.showMessageDialog(  table,
                "Could not write HTML to default file.\n" + "Error message was:\n" + ioe.getMessage(),
                "IO error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
         }     
     }
    
    
}
