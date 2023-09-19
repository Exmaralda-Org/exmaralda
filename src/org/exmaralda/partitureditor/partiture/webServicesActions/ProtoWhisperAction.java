/*
 * NewAction.java
 *
 * Created on 16. Juni 2003, 16:23
 */

package org.exmaralda.partitureditor.partiture.webServicesActions;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;
import org.exmaralda.webservices.Whisper4EXMARaLDA;
import org.exmaralda.webservices.WhisperConnector;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;



/**
 * Creates a new transcription
 * Menu: File --> New
 * @author  thomas
 */
public class ProtoWhisperAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    
    /** Creates a new instance of NewAction
     * @param t
     * @param icon */
    public ProtoWhisperAction(PartitureTableWithActions t) {
        super("Whisper...", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        try {
            table.commitEdit(true);
            System.out.println("ProtoWhisperAction!");
            whisper();
            table.transcriptionChanged = false;
            table.clearUndo();
            table.clearSearchResult();
            table.setFrameEndPosition(-2);
        } catch (JexmaraldaException | IOException | JDOMException | SAXException | FSMException ex) {
            System.out.println(ex.getMessage());
            javax.swing.JOptionPane.showMessageDialog(table.getParent(), ex.getLocalizedMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void whisper() throws JexmaraldaException, IOException, JDOMException, SAXException, FSMException{
        
        String wavPath = table.getModel().getTranscription().getHead().getMetaInformation().getReferencedFile("wav");
        if (wavPath==null){
            String message = "No WAV audio file assigned to this transcription.\n" 
                             + "Whisper cannot run without a WAV audio file.\n" 
                             + "Please use Transcription > Recording to assign a WAV audio file.";
            JOptionPane.showMessageDialog(table, message);
            return;
        }
        
        // get the transcription and the current selection
        final BasicTranscription bt = table.getModel().getTranscription();
        final String tierID = bt.getBody().getTierAt(table.selectionStartRow).getID();
        final String startID = bt.getBody().getCommonTimeline().getTimelineItemAt(table.selectionStartCol).getID();
        // this must be the bug of issue #42: beware of events stretching more than one timeline item!
        // final String endID = bt.getBody().getCommonTimeline().getTimelineItemAt(table.selectionEndCol+1).getID();
        int span = table.getModel().getCellSpan(table.selectionStartRow, table.selectionEndCol);
        final String endID = bt.getBody().getCommonTimeline().getTimelineItemAt(table.selectionEndCol+span).getID();

        // do this in a thread so we can report progress
        Thread whisperThread = new Thread(){
            @Override
            public void run() {
                try {
                    final Whisper4EXMARaLDA w4e = new Whisper4EXMARaLDA();
                    final File audioFile = w4e.createWhisperInputFile(bt, tierID, startID, endID);

                    // call MAUS with the files and write the result to a temporary text grid file
                    String whisperKey = Whisper4EXMARaLDA.getWhisperKey();
                    WhisperConnector wc = new WhisperConnector(whisperKey);
                    String result = wc.callWhisperSimple(audioFile);

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                success(result);
                            } catch (IOException | JexmaraldaException ex) {
                                System.out.println(ex.getMessage());
                                JOptionPane.showMessageDialog(table, ex);
                            }
                        }
                    });


                } catch (JexmaraldaException | IOException | SAXException | FSMException | JDOMException | URISyntaxException ex) {
                    System.out.println(ex.getMessage());
                    JOptionPane.showMessageDialog(table, ex);
                } 
            }
            
        };
        whisperThread.start();

        
    }
    
    public void success(String result) throws IOException, JexmaraldaException{
            JOptionPane.showMessageDialog(table, result);        
    }
}



