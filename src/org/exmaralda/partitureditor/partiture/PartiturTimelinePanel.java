/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PartiturTimelinePanel.java
 *
 * Created on 26.11.2008, 14:52:22
 */

package org.exmaralda.partitureditor.partiture;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import org.exmaralda.folker.application.AbstractTimeviewPartiturPlayerControl;
import org.exmaralda.folker.timeview.*;
import org.exmaralda.folker.utilities.TimeStringFormatter;
import org.exmaralda.partitureditor.praatPanel.PraatPanelEvent;

/**
 *
 * @author thomas
 */
public class PartiturTimelinePanel extends javax.swing.JPanel 
                                    implements TimeSelectionListener, AdjustmentListener {

    // compile again!
    
    AbstractTimeProportionalViewer timeViewer;


    int dividerLocation= 150;
    PartitureTableWithActions partitur;

    /** Creates new form PartiturTimelinePanel */
    public PartiturTimelinePanel(JPanel largeTextFieldPanel, AbstractTimeProportionalViewer tv, PartitureTableWithActions partitur) {
        initComponents();
        partiturPanel.add(partitur);
        this.partitur = partitur;
        add(largeTextFieldPanel, java.awt.BorderLayout.NORTH);
        progressBarPanel.add(partitur.progressBar);
        timeViewer = tv;
        timeViewerScrollPane.setViewportView(timeViewer);
        timeViewerScrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
        //partitur.getHorizSB().addAdjustmentListener(this);

        String os = System.getProperty("os.name").substring(0,3);
        if (os.equalsIgnoreCase("mac")) {
            configureForMac();
        }


    }

    public void setTimeViewVisible(boolean visible){
        timeViewerPanel.setVisible(visible);
        if (visible){
            splitPane.setDividerSize(10);
            splitPane.setDividerLocation(dividerLocation);
        } else {
            dividerLocation = Math.max(splitPane.getDividerLocation(), 150);
            splitPane.setDividerSize(1);
            splitPane.setDividerLocation(0);
        }
    }

    public void setTimelineViewer(AbstractTimeProportionalViewer timeViewer){
        timeViewerScrollPane.setViewportView(timeViewer);
    }

    public void assignActions(final AbstractTimeviewPartiturPlayerControl atppc){
        playSelectionButton.setAction(atppc.playSelectionAction);
        playSelectionButton.setToolTipText("Play selection");
        playLastSecondOfSelectionButton.setAction(atppc.playLastSecondOfSelectionAction);
        playLastSecondOfSelectionButton.setToolTipText("Play last second of selection");
        playFirstSecondOfSelectionButton.setAction(atppc.playFirstSecondOfSelectionAction);
        playFirstSecondOfSelectionButton.setToolTipText("Play first second of selection");
        playFirstSecondBeforeSelectionButton.setAction(atppc.playFirstSecondBeforeSelectionAction);
        playFirstSecondBeforeSelectionButton.setToolTipText("Play second before selection");
        playFirstSecondAfterSelectionButton.setAction(atppc.playFirstSecondAfterSelectionAction);
        playFirstSecondAfterSelectionButton.setToolTipText("Play second after selection");
        loopSelectionButton.setAction(atppc.loopSelectionAction);
        loopSelectionButton.setToolTipText("Loop selection");
        playButton.setAction(atppc.playAction);
        pauseButton.setAction(atppc.pauseAction);
        pauseButton.setToolTipText("Pause");
        stopButton.setAction(atppc.stopAction);
        stopButton.setToolTipText("Stop");
        shiftSelectionButton.setAction(atppc.shiftSelectionAction);
        shiftSelectionButton.setToolTipText("Shift selection");
        detachSelectionButton.setAction(atppc.detachSelectionAction);
        detachSelectionButton.setToolTipText("Detach selection");
        atppc.setAddEventInvoker(addEventButton);

        addEventButton.setAction(atppc.addEventInPartiturAction);
        addEventButton.setToolTipText("Add event");

        appendIntervalButton.setAction(atppc.appendIntervalInPartiturAction);
        appendIntervalButton.setToolTipText("Append interval");

        assignTimesButton.setAction(atppc.assignTimesAction);
        assignTimesButton.setToolTipText("Assign times");

        navigateButton.setAction(atppc.navigateAction);
        navigateButton.setToolTipText("Navigate in the recording");

        zoomToggleButton.setAction(atppc.changeZoomAction);
        zoomToggleButton.setToolTipText("Zoom in/out");

    }

    void assignKeyStrokes(final AbstractTimeviewPartiturPlayerControl atppc) {
        InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getActionMap();

        // for add interval (Ctrl PLUS)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.CTRL_DOWN_MASK), "addEvent");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.CTRL_DOWN_MASK), "addEvent");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.CTRL_DOWN_MASK), "addEvent");
        am.put("addEvent", atppc.addEventInPartiturAction);

        // for add interval (Alt PLUS)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.ALT_DOWN_MASK), "appendInterval");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.ALT_DOWN_MASK), "appendInterval");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.ALT_DOWN_MASK), "appendInterval");
        am.put("appendInterval", atppc.appendIntervalInPartiturAction);

    }


    void configureForMac(){
        playFirstSecondBeforeSelectionButton.putClientProperty("JButton.buttonType", "segmented");
        playFirstSecondBeforeSelectionButton.putClientProperty("JButton.segmentPosition","first");
        playFirstSecondOfSelectionButton.putClientProperty("JButton.buttonType", "segmented");
        playFirstSecondOfSelectionButton.putClientProperty("JButton.segmentPosition","middle");
        playSelectionButton.putClientProperty("JButton.buttonType", "segmented");
        playSelectionButton.putClientProperty("JButton.segmentPosition","middle");
        playLastSecondOfSelectionButton.putClientProperty("JButton.buttonType", "segmented");
        playLastSecondOfSelectionButton.putClientProperty("JButton.segmentPosition","middle");
        playFirstSecondAfterSelectionButton.putClientProperty("JButton.buttonType", "segmented");
        playFirstSecondAfterSelectionButton.putClientProperty("JButton.segmentPosition","last");
    }

    void storeSettings(String preferencesNode) {
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(preferencesNode);

        settings.put("DIVIDER-LOCATION", Integer.toString(splitPane.getDividerLocation()));
        settings.put("PIXELS-PER-SECOND", Double.toString(timeViewer.getPixelsPerSecond()));
    }

    void loadSettings(String preferencesNode){
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(preferencesNode);
        dividerLocation = Math.max(200, settings.getInt("DIVIDER-LOCATION", 200));
        double pps = settings.getDouble("PIXELS-PER-SECOND", 10.0);
        timeViewer.setPixelsPerSecond(pps);
        partitur.getModel().setPixelsPerSecond(pps);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        timeViewerPanel = new javax.swing.JPanel();
        timeViewerValuesPanel = new javax.swing.JPanel();
        numbersPanel = new javax.swing.JPanel();
        startTimeLabel = new javax.swing.JLabel();
        durationPanel = new javax.swing.JPanel();
        durationLabel = new javax.swing.JLabel();
        endTimeLabel = new javax.swing.JLabel();
        navigationPanel = new javax.swing.JPanel();
        navigateButton = new javax.swing.JButton();
        zoomPanel = new javax.swing.JPanel();
        zoomToggleButton = new javax.swing.JToggleButton();
        timeViewerControlPanel = new javax.swing.JPanel();
        playerControlsPanel = new javax.swing.JPanel();
        selectionControlsPanel = new javax.swing.JPanel();
        playFirstSecondBeforeSelectionButton = new javax.swing.JButton();
        playFirstSecondOfSelectionButton = new javax.swing.JButton();
        playSelectionButton = new javax.swing.JButton();
        playLastSecondOfSelectionButton = new javax.swing.JButton();
        playFirstSecondAfterSelectionButton = new javax.swing.JButton();
        loopSelectionButton = new javax.swing.JButton();
        playButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        selectionControlPanel = new javax.swing.JPanel();
        shiftSelectionButton = new javax.swing.JButton();
        detachSelectionButton = new javax.swing.JButton();
        assignTimesButton = new javax.swing.JButton();
        playbackModeToggleButton = new javax.swing.JToggleButton();
        eventControlPanel = new javax.swing.JPanel();
        addEventButton = new javax.swing.JButton();
        appendIntervalButton = new javax.swing.JButton();
        timeViewerScrollPane = new javax.swing.JScrollPane();
        bufferPanel = new javax.swing.JPanel();
        partiturPanel = new javax.swing.JPanel();
        progressBarPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        splitPane.setDividerLocation(100);
        splitPane.setDividerSize(10);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        timeViewerPanel.setLayout(new java.awt.BorderLayout());

        timeViewerValuesPanel.setLayout(new java.awt.BorderLayout());

        startTimeLabel.setForeground(new java.awt.Color(102, 255, 0));
        startTimeLabel.setText("-");
        startTimeLabel.setToolTipText("Selection start");
        numbersPanel.add(startTimeLabel);

        durationPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        durationPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                durationPanelMouseClicked(evt);
            }
        });

        durationLabel.setText("-");
        durationLabel.setToolTipText("Selection duration (click to copy!) ");
        durationLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                durationLabelMouseClicked(evt);
            }
        });
        durationPanel.add(durationLabel);

        numbersPanel.add(durationPanel);

        endTimeLabel.setForeground(new java.awt.Color(255, 51, 102));
        endTimeLabel.setText("-");
        endTimeLabel.setToolTipText("Selection end");
        numbersPanel.add(endTimeLabel);

        timeViewerValuesPanel.add(numbersPanel, java.awt.BorderLayout.CENTER);

        navigateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/othericons/compass_icon.gif"))); // NOI18N
        navigationPanel.add(navigateButton);

        timeViewerValuesPanel.add(navigationPanel, java.awt.BorderLayout.WEST);

        zoomPanel.setLayout(new javax.swing.BoxLayout(zoomPanel, javax.swing.BoxLayout.LINE_AXIS));

        zoomToggleButton.setText("jToggleButton1");
        zoomPanel.add(zoomToggleButton);

        timeViewerValuesPanel.add(zoomPanel, java.awt.BorderLayout.EAST);

        timeViewerPanel.add(timeViewerValuesPanel, java.awt.BorderLayout.NORTH);

        timeViewerControlPanel.setLayout(new java.awt.BorderLayout());

        selectionControlsPanel.setLayout(new javax.swing.BoxLayout(selectionControlsPanel, javax.swing.BoxLayout.LINE_AXIS));

        playFirstSecondBeforeSelectionButton.setText("jButton1");
        selectionControlsPanel.add(playFirstSecondBeforeSelectionButton);

        playFirstSecondOfSelectionButton.setText("jButton1");
        selectionControlsPanel.add(playFirstSecondOfSelectionButton);

        playSelectionButton.setText("jButton1");
        selectionControlsPanel.add(playSelectionButton);

        playLastSecondOfSelectionButton.setText("jButton1");
        selectionControlsPanel.add(playLastSecondOfSelectionButton);

        playFirstSecondAfterSelectionButton.setText("jButton1");
        selectionControlsPanel.add(playFirstSecondAfterSelectionButton);

        playerControlsPanel.add(selectionControlsPanel);

        loopSelectionButton.setText("jButton1");
        playerControlsPanel.add(loopSelectionButton);

        playButton.setText("jButton1");
        playerControlsPanel.add(playButton);

        pauseButton.setText("jButton1");
        playerControlsPanel.add(pauseButton);

        stopButton.setText("jButton1");
        playerControlsPanel.add(stopButton);

        timeViewerControlPanel.add(playerControlsPanel, java.awt.BorderLayout.CENTER);

        shiftSelectionButton.setText("jButton1");
        selectionControlPanel.add(shiftSelectionButton);

        detachSelectionButton.setText("jButton1");
        selectionControlPanel.add(detachSelectionButton);

        assignTimesButton.setText("jButton1");
        selectionControlPanel.add(assignTimesButton);

        playbackModeToggleButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        playbackModeToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/categories/applications-multimedia.png"))); // NOI18N
        playbackModeToggleButton.setText("P");
        playbackModeToggleButton.setToolTipText("Switch playback mode on/off");
        playbackModeToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playbackModeToggleButtonActionPerformed(evt);
            }
        });
        selectionControlPanel.add(playbackModeToggleButton);

        timeViewerControlPanel.add(selectionControlPanel, java.awt.BorderLayout.EAST);

        addEventButton.setText("jButton1");
        addEventButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEventButtonActionPerformed(evt);
            }
        });
        eventControlPanel.add(addEventButton);

        appendIntervalButton.setText("jButton1");
        eventControlPanel.add(appendIntervalButton);

        timeViewerControlPanel.add(eventControlPanel, java.awt.BorderLayout.WEST);

        timeViewerPanel.add(timeViewerControlPanel, java.awt.BorderLayout.SOUTH);
        timeViewerPanel.add(timeViewerScrollPane, java.awt.BorderLayout.CENTER);

        bufferPanel.setLayout(new javax.swing.BoxLayout(bufferPanel, javax.swing.BoxLayout.LINE_AXIS));
        timeViewerPanel.add(bufferPanel, java.awt.BorderLayout.WEST);

        splitPane.setLeftComponent(timeViewerPanel);

        partiturPanel.setLayout(new javax.swing.BoxLayout(partiturPanel, javax.swing.BoxLayout.Y_AXIS));
        splitPane.setRightComponent(partiturPanel);

        add(splitPane, java.awt.BorderLayout.CENTER);

        progressBarPanel.setLayout(new javax.swing.BoxLayout(progressBarPanel, javax.swing.BoxLayout.Y_AXIS));
        add(progressBarPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void durationPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_durationPanelMouseClicked
        copyDuration();
    }//GEN-LAST:event_durationPanelMouseClicked

    private void durationLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_durationLabelMouseClicked
        copyDuration();
    }//GEN-LAST:event_durationLabelMouseClicked

    private void playbackModeToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playbackModeToggleButtonActionPerformed
        if (playbackModeToggleButton.isSelected()){
            playbackModeToggleButton.setForeground(Color.RED);
            //fireEvent(new PraatPanelEvent(PraatPanelEvent.PLAYBACK_MODE_ON));
            partitur.processTime(new PraatPanelEvent(PraatPanelEvent.PLAYBACK_MODE_ON));

        } else {
            playbackModeToggleButton.setForeground(Color.BLACK);
            //fireEvent(new PraatPanelEvent(PraatPanelEvent.PLAYBACK_MODE_OFF));
            partitur.processTime(new PraatPanelEvent(PraatPanelEvent.PLAYBACK_MODE_OFF));
        }

        //firePlaybackModeToggled();
}//GEN-LAST:event_playbackModeToggleButtonActionPerformed

    private void addEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEventButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addEventButtonActionPerformed

    void copyDuration(){
        java.awt.datatransfer.StringSelection ss = new java.awt.datatransfer.StringSelection(durationLabel.getText());
        this.getToolkit().getSystemClipboard().setContents(ss,ss);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEventButton;
    private javax.swing.JButton appendIntervalButton;
    private javax.swing.JButton assignTimesButton;
    private javax.swing.JPanel bufferPanel;
    private javax.swing.JButton detachSelectionButton;
    public javax.swing.JLabel durationLabel;
    private javax.swing.JPanel durationPanel;
    public javax.swing.JLabel endTimeLabel;
    private javax.swing.JPanel eventControlPanel;
    private javax.swing.JButton loopSelectionButton;
    private javax.swing.JButton navigateButton;
    private javax.swing.JPanel navigationPanel;
    private javax.swing.JPanel numbersPanel;
    public javax.swing.JPanel partiturPanel;
    private javax.swing.JButton pauseButton;
    private javax.swing.JButton playButton;
    private javax.swing.JButton playFirstSecondAfterSelectionButton;
    private javax.swing.JButton playFirstSecondBeforeSelectionButton;
    private javax.swing.JButton playFirstSecondOfSelectionButton;
    private javax.swing.JButton playLastSecondOfSelectionButton;
    private javax.swing.JButton playSelectionButton;
    private javax.swing.JToggleButton playbackModeToggleButton;
    private javax.swing.JPanel playerControlsPanel;
    private javax.swing.JPanel progressBarPanel;
    private javax.swing.JPanel selectionControlPanel;
    private javax.swing.JPanel selectionControlsPanel;
    private javax.swing.JButton shiftSelectionButton;
    private javax.swing.JSplitPane splitPane;
    public javax.swing.JLabel startTimeLabel;
    private javax.swing.JButton stopButton;
    private javax.swing.JPanel timeViewerControlPanel;
    private javax.swing.JPanel timeViewerPanel;
    private javax.swing.JScrollPane timeViewerScrollPane;
    private javax.swing.JPanel timeViewerValuesPanel;
    private javax.swing.JPanel zoomPanel;
    public javax.swing.JToggleButton zoomToggleButton;
    // End of variables declaration//GEN-END:variables

    public void processTimeSelectionEvent(TimeSelectionEvent event) {
        double s = event.getStartTime();
        double e = event.getEndTime();
        if ((s>=0) && (e>=0)){
            startTimeLabel.setText(TimeStringFormatter.formatMiliseconds(s, 2));
            endTimeLabel.setText(TimeStringFormatter.formatMiliseconds(e, 2));
            double duration = e-s;
            durationLabel.setText(Double.toString(Math.round(duration)/1000.0));
            if (event.isSelectionAttached()){
                startTimeLabel.setForeground(Color.GREEN);
                endTimeLabel.setForeground(Color.RED);
            } else {
                startTimeLabel.setForeground(Color.BLUE);
                endTimeLabel.setForeground(Color.BLUE);
            }
        } else {
            startTimeLabel.setText("-");
            endTimeLabel.setText("-");
            durationLabel.setText("-");
        }
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getValueIsAdjusting()){
            return;
        }
        if ((!partitur.getModel().timeProportional) || partitur.waitForSelection){
            return;
        }
        if (e.getSource() == timeViewerScrollPane.getHorizontalScrollBar()){
            int value = timeViewerScrollPane.getHorizontalScrollBar().getValue();
            partitur.getHorizSB().setValue(value);
        } else {
            int value = partitur.getHorizSB().getValue();
            timeViewerScrollPane.getHorizontalScrollBar().setValue(value);
        }
    }

    public void setBuffer(int width){
        Dimension d = new Dimension(width,1);
        bufferPanel.setMaximumSize(d);
        bufferPanel.setPreferredSize(d);
        bufferPanel.setSize(d);
        this.revalidate();
    }



}
