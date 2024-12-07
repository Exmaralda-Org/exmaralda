/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AlignmentPanel.java
 *
 * Created on 02.01.2013, 16:49:31
 */
package org.exmaralda.partitureditor.alignmentPanel;

import com.klg.jclass.table.JCSelectEvent;
import com.klg.jclass.table.JCSelectListener;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.exmaralda.folker.utilities.Constants;
import org.exmaralda.folker.utilities.TimeStringFormatter;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;
import org.exmaralda.partitureditor.partiture.PartiturEditor;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;
import org.exmaralda.partitureditor.sound.Playable;
import org.exmaralda.partitureditor.sound.PlayableEvent;
import org.exmaralda.partitureditor.sound.PlayableListener;

/**
 *
 * @author Schmidt
 */
public class AlignmentPanel extends javax.swing.JPanel implements JCSelectListener, PlayableListener {

    PartitureTableWithActions table;
    Playable player;
    ArrayList<String> eventTexts = new ArrayList<String>();
    
    int totalCols = 0;
    
    /** Creates new form AlignmentPanel
     * @param table
     * @param player */
    public AlignmentPanel(PartitureTableWithActions table, Playable player) {
        this.table = table;
        this.player = player;
        double rememberPosition = player.getCurrentPosition();
        Container c = table.getTopLevelAncestor();
        if (c instanceof PartiturEditor){
            rememberPosition = ((PartiturEditor)c).controller.timeViewer.getCursorTime() / 1000.0;
        }
        System.out.println("Remember position: " + rememberPosition);
        indexTexts();
        initComponents();
        
        AbstractAction mainAction = new AbstractAction(){

            @Override
            public void actionPerformed(ActionEvent e) {
                align();
            }
            
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(' '), "mainAction");
        getActionMap().put("mainAction", mainAction);
        
        alignButton.setAction(mainAction);
        alignButton.setIcon(new Constants().getIcon(Constants.BIG_TIMESTAMP_EVENT_ICON));
        alignButton.setToolTipText("Align!");
        
        table.addSelectListener(this);
        player.addPlayableListener(this);

        //make sure that only one column is selected in the table
        if (table.selectionStartCol<1){
            table.setNewSelection(-1, -1, 0, 0);
        } else {
            table.setNewSelection(-1, -1, table.selectionStartCol, table.selectionStartCol);
        }
        
        //player.setStartTime(0.0);
        // changed 09-04-2015
        player.setStartTime(rememberPosition);
        player.setEndTime(player.getTotalLength());
        
        String s = TimeStringFormatter.formatMiliseconds(rememberPosition*1000.0,2);
        timeLabel.setText(s);
        
        
        int currentColumn = table.selectionStartCol;
        totalCols = table.getNumColumns();
        columnCountLabel.setText("[" + Integer.toString(currentColumn+1) + "/" + totalCols + "]");
        
        
        
        jScrollPane2.setVisible(false);
        jScrollPane1.setPreferredSize(new Dimension(table.getWidth() - 100, 200));
        
        
    }
    
    public void align(){
        jPanel1.requestFocus();
        timeLabel.setForeground(Color.red);
        double time = player.getCurrentPosition();
        int currentColumn = table.selectionStartCol;
        table.getModel().getTranscription().getBody().getCommonTimeline().getTimelineItemAt(currentColumn+1).setTime(time);
        if (currentColumn<table.getNumColumns()-1){
            table.setLeftColumn(currentColumn+1);
            table.setNewSelection(-1, -1, currentColumn+1, currentColumn+1);
        }
        columnCountLabel.setText("[" + Integer.toString(currentColumn+1) + "/" + totalCols + "]");
        
        timeLabel.setForeground(Color.black);
        jPanel1.requestFocus();
        
        /*int selectedRow = transcriptionTable.getSelectedRow();
        if (selectedRow>=transcriptionTableModel.getRowCount()-1) return;
        transcriptionTableModel.setTime(selectedRow, 1, time);
        transcriptionTableModel.setTime(selectedRow+1, 0, time);
        transcriptionTable.getSelectionModel().setSelectionInterval(selectedRow+1, selectedRow+1);
        transcriptionTable.scrollRectToVisible(transcriptionTable.getCellRect(selectedRow+1, 0, true));*/    
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        columnCountLabel = new javax.swing.JLabel();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        alignButton = new javax.swing.JButton();
        timeLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        explainLabel1 = new javax.swing.JLabel();
        explainLabel2 = new javax.swing.JLabel();
        textPanel = new javax.swing.JPanel();
        fontSizeSlider = new javax.swing.JSlider();
        nowPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        eventTextArea = new javax.swing.JTextArea();
        beforePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        beforeTextArea = new javax.swing.JTextArea();
        afterPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        afterTextArea = new javax.swing.JTextArea();

        setLayout(new java.awt.BorderLayout());

        columnCountLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        columnCountLabel.setText("[0/0]");
        columnCountLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                columnCountLabelMousePressed(evt);
            }
        });
        jPanel1.add(columnCountLabel);

        startButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        startButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/22x22/actions/media-playback-start.png"))); // NOI18N
        startButton.setToolTipText("Start! ");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });
        jPanel1.add(startButton);

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/22x22/actions/media-playback-stop.png"))); // NOI18N
        stopButton.setToolTipText("Stop");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });
        jPanel1.add(stopButton);

        alignButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        alignButton.setText("Align!");
        jPanel1.add(alignButton);

        timeLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        timeLabel.setText("00:00:00.0");
        timeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                timeLabelMousePressed(evt);
            }
        });
        jPanel1.add(timeLabel);

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        explainLabel1.setText("Press the START button to start playback of the recording. ");
        jPanel2.add(explainLabel1);

        explainLabel2.setText("Press the ALIGN button or hit <SPACE> after you've heard the currently displayed text");
        jPanel2.add(explainLabel2);

        add(jPanel2, java.awt.BorderLayout.PAGE_END);

        textPanel.setLayout(new java.awt.BorderLayout());

        fontSizeSlider.setMaximum(48);
        fontSizeSlider.setMinimum(6);
        fontSizeSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        fontSizeSlider.setToolTipText("Change font size");
        fontSizeSlider.setValue(14);
        fontSizeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fontSizeSliderStateChanged(evt);
            }
        });
        textPanel.add(fontSizeSlider, java.awt.BorderLayout.WEST);

        nowPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Now"));
        nowPanel.setLayout(new javax.swing.BoxLayout(nowPanel, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setMaximumSize(new java.awt.Dimension(32767, 400));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(226, 300));

        eventTextArea.setEditable(false);
        eventTextArea.setBackground(new java.awt.Color(0, 0, 0));
        eventTextArea.setColumns(20);
        eventTextArea.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        eventTextArea.setForeground(new java.awt.Color(255, 255, 0));
        eventTextArea.setLineWrap(true);
        eventTextArea.setRows(5);
        eventTextArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(eventTextArea);

        nowPanel.add(jScrollPane1);

        textPanel.add(nowPanel, java.awt.BorderLayout.CENTER);

        beforeTextArea.setBackground(new java.awt.Color(153, 153, 153));
        beforeTextArea.setColumns(20);
        beforeTextArea.setFont(new java.awt.Font("SansSerif", 0, 10)); // NOI18N
        beforeTextArea.setRows(5);
        jScrollPane2.setViewportView(beforeTextArea);

        beforePanel.add(jScrollPane2);

        textPanel.add(beforePanel, java.awt.BorderLayout.NORTH);

        afterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Next"));
        afterPanel.setLayout(new javax.swing.BoxLayout(afterPanel, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane3.setMaximumSize(new java.awt.Dimension(32767, 100));

        afterTextArea.setEditable(false);
        afterTextArea.setBackground(new java.awt.Color(204, 204, 204));
        afterTextArea.setColumns(20);
        afterTextArea.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        afterTextArea.setRows(5);
        afterTextArea.setWrapStyleWord(true);
        jScrollPane3.setViewportView(afterTextArea);

        afterPanel.add(jScrollPane3);

        textPanel.add(afterPanel, java.awt.BorderLayout.SOUTH);

        add(textPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        player.startPlayback();
        jPanel1.requestFocus();        
    }//GEN-LAST:event_startButtonActionPerformed

    private void timeLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_timeLabelMousePressed

    }//GEN-LAST:event_timeLabelMousePressed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        player.setStartTime(player.getCurrentPosition());
        player.stopPlayback();
        jPanel1.requestFocus();        
    }//GEN-LAST:event_stopButtonActionPerformed

    private void columnCountLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_columnCountLabelMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_columnCountLabelMousePressed

    private void fontSizeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fontSizeSliderStateChanged
        int newFontSize = fontSizeSlider.getValue();
        eventTextArea.setFont(eventTextArea.getFont().deriveFont((float)newFontSize));
    }//GEN-LAST:event_fontSizeSliderStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel afterPanel;
    private javax.swing.JTextArea afterTextArea;
    private javax.swing.JButton alignButton;
    private javax.swing.JPanel beforePanel;
    private javax.swing.JTextArea beforeTextArea;
    private javax.swing.JLabel columnCountLabel;
    private javax.swing.JTextArea eventTextArea;
    private javax.swing.JLabel explainLabel1;
    private javax.swing.JLabel explainLabel2;
    private javax.swing.JSlider fontSizeSlider;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel nowPanel;
    private javax.swing.JButton startButton;
    private javax.swing.JButton stopButton;
    private javax.swing.JPanel textPanel;
    private javax.swing.JLabel timeLabel;
    // End of variables declaration//GEN-END:variables

    private void indexTexts() {
        BasicTranscription t = table.getModel().getTranscription();
        Timeline tl = t.getBody().getCommonTimeline();
        for (int pos=0; pos<tl.getNumberOfTimelineItems(); pos++){
            String thisText = "";
            TimelineItem tli = tl.getTimelineItemAt(pos);
            for (int i=0; i<t.getBody().getNumberOfTiers(); i++){
                Tier tier = t.getBody().getTierAt(i);
                if (!("t".equals(tier.getType()))) continue;
                for (int j=0; j<tier.getNumberOfEvents(); j++){
                    Event e = tier.getEventAt(j);
                    if (tli.getID().equals((e.getEnd()))){
                        thisText+=tier.getDescription(t.getHead().getSpeakertable()) + ": " + e.getDescription() + "\n";
                    }
                }
            }
            eventTexts.add(thisText);
        }
    }

    @Override
    public void beforeSelect(JCSelectEvent jcse) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void select(JCSelectEvent jcse) {
        int col = table.selectionStartCol;
        if (col>=0 && ((col+1)<eventTexts.size())){
            eventTextArea.setText(eventTexts.get(col+1));
        }
        if (col>=0 && ((col+1)<eventTexts.size())){
            beforeTextArea.setText(eventTexts.get(col));
        } else {
            beforeTextArea.setText("");           
        }
        if (col<eventTexts.size()-2){
            afterTextArea.setText(eventTexts.get(col+2));
        } else {
            afterTextArea.setText("");            
        }
    }

    @Override
    public void afterSelect(JCSelectEvent jcse) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void processPlayableEvent(PlayableEvent e) {
        if (e.getType()==PlayableEvent.POSITION_UPDATE){
            String s = TimeStringFormatter.formatMiliseconds(player.getCurrentPosition()*1000.0,2);
            this.timeLabel.setText(s);
        }
    }
}
