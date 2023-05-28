/*
 * AudioPanel.java
 *
 * Created on 13. August 2004, 16:09
 */

package org.exmaralda.partitureditor.sound;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import javax.swing.*;
import java.awt.image.*;
import org.exmaralda.partitureditor.praatPanel.PraatPanelListener;
import org.exmaralda.partitureditor.praatPanel.PraatPanelEvent;
import java.io.File;
import org.exmaralda.folker.timeview.TimeSelectionEvent;
import org.exmaralda.folker.utilities.TimeStringFormatter;
import org.exmaralda.partitureditor.jexmaraldaswing.RecordingsListCellRenderer;

/**
 *
 * @author  thomas
 */
public class AudioPanel extends javax.swing.JDialog implements PlayableListener {
    
    javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
    javax.swing.Timer timer;
    
    private Playable player;
    double totalLength = 0;
    
    double internalStartTime;
    double internalEndTime;
    
    boolean isPlaying = false;
    boolean isPausing = false;
    
    public String soundFileName;

    public String lastSnapshotFilename = System.getProperty("user.home") + System.getProperty("file.separator") + "snapshot1.png";
    public String lastAudioPartFilename = System.getProperty("user.home") + System.getProperty("file.separator") + "audiosnippet1.wav";
    public boolean snapshotWouldBeLinkeable = false;

    private boolean videoIsNorth = true;
    
    boolean stopPressed = false;
    
    // the actual dimensions of the video file
    int sourceWidth = -1;
    int sourceHeight = -1;
    
    
    /** Creates new form AudioPanel
     * @param parent
     * @param modal
     * @param player */
    public AudioPanel(java.awt.Frame parent, boolean modal, Playable player) {
        super(parent, modal);
        initComponents();
        
        availableFilesComboBox.setRenderer(new RecordingsListCellRenderer());

        // 27-05-2015: get rid of it - it is useless and causes trouble
        controlPanel.setVisible(false);
                        
        pack();
        
        org.exmaralda.common.helpers.Internationalizer.internationalizeDialogToolTips(this);
        //setPlayer(makePlayer());
        setPlayer(player);
        getPlayer().addPlayableListener(this);       
        registerKeyStrokes();

        minimize(true);
        
        
    }
    
    public void addPlayableListener(PlayableListener l){
        getPlayer().addPlayableListener(l);
    }
    
    public void addPraatPanelListener(PraatPanelListener l) {
         listenerList.add(PraatPanelListener.class, l);
    }

    public void setStartTime(double t){
        //System.out.println("Setting start time");
        internalStartTime = t;
        /*if ((!isPlaying) && (syncStartCheckBox.isSelected())){
            //System.out.println("Updating start time slider");
            updateStartTimeSlider();
        }*/
    }
    
    
    public void setEndTime(double t){
        internalEndTime = t;
        /*if ((!isPlaying) && (syncEndCheckBox.isSelected())){
            updateEndTimeSlider();
        }*/
    }
    


    public boolean setSoundFile(String path) {
        return openSoundFile(path);    
    }

    public void setAvailableSoundFiles(java.util.Vector<String> paths){
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        if ((paths.isEmpty()) || (paths.elementAt(0).trim().length()==0)){
            dcbm.addElement("--- no media files available ---");
            availableFilesComboBox.setModel(dcbm);
            availableFilesComboBox.setEnabled(false);
            return;
        }

        for (String path : paths){
            dcbm.addElement(new File(path));
        }
        availableFilesComboBox.setModel(dcbm);
        availableFilesComboBox.setEnabled(true);

    }
    
    public boolean reset(){
        //if (player instanceof AbstractPlayer) ((AbstractPlayer)player).reset();
        soundFileName = null;
        //soundFileNameLabel.setText("--- no file loaded ---");
        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        dcbm.addElement("--- no media files available ---");
        availableFilesComboBox.setModel(dcbm);
        availableFilesComboBox.setEnabled(false);

        playButton.setEnabled(false);
        pauseToggleButton.setEnabled(false);
        stopButton.setEnabled(false);
        totalLength = 0.0;
        totalLengthLabel.setText("0.0");
        /*startTimeSlider.setValue(0);
        startTimeSlider.setEnabled(false);
        endTimeSlider.setValue(1000000);*/
        endPositionLabel.setText("0.0");
        setEndTime(0);            

        /*endTimeSlider.setEnabled(false);
        positionSlider.setValue(0);
        positionSlider.setEnabled(false);*/

        grabButton.setEnabled(false);
        cutButton.setEnabled(false);

        status("No audio/video file referenced");        

        videoDisplayPanel.removeAll();
        pack();
        return true;
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filesPanel = new javax.swing.JPanel();
        availableFilesComboBox = new javax.swing.JComboBox();
        buttonsPanel = new javax.swing.JPanel();
        grabButton = new javax.swing.JButton();
        cutButton = new javax.swing.JButton();
        controlsPanel = new javax.swing.JPanel();
        currentPositionPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        zeroPositionLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        startPositionLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        currentPositionLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        endPositionLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        totalLengthLabel = new javax.swing.JLabel();
        controlPanel = new javax.swing.JPanel();
        playButton = new javax.swing.JButton();
        pauseToggleButton = new javax.swing.JToggleButton();
        stopButton = new javax.swing.JButton();
        previousTLIButton = new javax.swing.JButton();
        nextTLIButton = new javax.swing.JButton();
        playbackModeToggleButton = new javax.swing.JToggleButton();
        statusPanel = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        videoPanel = new javax.swing.JPanel();
        videoDisplayPanel = new javax.swing.JPanel();

        setTitle("Audio/Video panel");
        addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentRemoved(java.awt.event.ContainerEvent evt) {
                formComponentRemoved(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        filesPanel.setLayout(new javax.swing.BoxLayout(filesPanel, javax.swing.BoxLayout.LINE_AXIS));

        availableFilesComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "--- no media files available ---", " " }));
        availableFilesComboBox.setEnabled(false);
        availableFilesComboBox.setMaximumSize(new java.awt.Dimension(1000, 40));
        availableFilesComboBox.setPreferredSize(new java.awt.Dimension(400, 80));
        availableFilesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                availableFilesComboBoxActionPerformed(evt);
            }
        });
        filesPanel.add(availableFilesComboBox);

        buttonsPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        buttonsPanel.setLayout(new javax.swing.BoxLayout(buttonsPanel, javax.swing.BoxLayout.X_AXIS));

        grabButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/sound/Grab.gif"))); // NOI18N
        grabButton.setToolTipText("Save/Link current video image");
        grabButton.setEnabled(false);
        grabButton.setMaximumSize(new java.awt.Dimension(35, 35));
        grabButton.setMinimumSize(new java.awt.Dimension(35, 35));
        grabButton.setPreferredSize(new java.awt.Dimension(35, 35));
        grabButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grabButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(grabButton);

        cutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/sound/Cut.gif"))); // NOI18N
        cutButton.setToolTipText("Save/Link audio snippet");
        cutButton.setEnabled(false);
        cutButton.setMaximumSize(new java.awt.Dimension(35, 35));
        cutButton.setMinimumSize(new java.awt.Dimension(35, 35));
        cutButton.setPreferredSize(new java.awt.Dimension(35, 35));
        cutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(cutButton);

        filesPanel.add(buttonsPanel);

        getContentPane().add(filesPanel, java.awt.BorderLayout.NORTH);

        controlsPanel.setLayout(new javax.swing.BoxLayout(controlsPanel, javax.swing.BoxLayout.Y_AXIS));

        currentPositionPanel.setEnabled(false);
        currentPositionPanel.setLayout(new java.awt.GridLayout(1, 5));

        zeroPositionLabel.setForeground(new java.awt.Color(102, 102, 102));
        zeroPositionLabel.setText("0.0");
        jPanel4.add(zeroPositionLabel);

        currentPositionPanel.add(jPanel4);

        jPanel1.setToolTipText("Start time");

        startPositionLabel.setForeground(new java.awt.Color(0, 204, 51));
        startPositionLabel.setText("0.0");
        startPositionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                startPositionLabelMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                startPositionLabelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                startPositionLabelMouseReleased(evt);
            }
        });
        startPositionLabel.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                startPositionLabelMouseWheelMoved(evt);
            }
        });
        jPanel1.add(startPositionLabel);

        currentPositionPanel.add(jPanel1);

        currentPositionLabel.setText("0.0");
        currentPositionLabel.setToolTipText("Current time");
        currentPositionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                currentPositionLabelMouseClicked(evt);
            }
        });
        jPanel2.add(currentPositionLabel);

        currentPositionPanel.add(jPanel2);

        endPositionLabel.setForeground(new java.awt.Color(255, 0, 51));
        endPositionLabel.setText("0.0");
        endPositionLabel.setToolTipText("Stop time");
        endPositionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                endPositionLabelMouseClicked(evt);
            }
        });
        endPositionLabel.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                endPositionLabelMouseWheelMoved(evt);
            }
        });
        jPanel3.add(endPositionLabel);

        currentPositionPanel.add(jPanel3);

        totalLengthLabel.setForeground(new java.awt.Color(102, 102, 102));
        totalLengthLabel.setText("0.0");
        totalLengthLabel.setToolTipText("Sound file length");
        jPanel5.add(totalLengthLabel);

        currentPositionPanel.add(jPanel5);

        controlsPanel.add(currentPositionPanel);

        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/sound/Start.gif"))); // NOI18N
        playButton.setToolTipText("Play (F1)");
        playButton.setEnabled(false);
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });
        controlPanel.add(playButton);

        pauseToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/sound/Pause.gif"))); // NOI18N
        pauseToggleButton.setToolTipText("Pause (F2)");
        pauseToggleButton.setEnabled(false);
        pauseToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseToggleButtonActionPerformed(evt);
            }
        });
        controlPanel.add(pauseToggleButton);

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/sound/Stop.gif"))); // NOI18N
        stopButton.setToolTipText("Stop (F3)");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });
        controlPanel.add(stopButton);

        previousTLIButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/sound/PreviousTLI.gif"))); // NOI18N
        previousTLIButton.setToolTipText("Select previous timeline item (F11)");
        previousTLIButton.setMaximumSize(new java.awt.Dimension(28, 24));
        previousTLIButton.setMinimumSize(new java.awt.Dimension(28, 24));
        previousTLIButton.setPreferredSize(new java.awt.Dimension(28, 24));
        previousTLIButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousTLIButtonActionPerformed(evt);
            }
        });
        controlPanel.add(previousTLIButton);

        nextTLIButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/sound/NextTLI.gif"))); // NOI18N
        nextTLIButton.setToolTipText("Select next timeline item (F12)");
        nextTLIButton.setMaximumSize(new java.awt.Dimension(28, 24));
        nextTLIButton.setMinimumSize(new java.awt.Dimension(28, 24));
        nextTLIButton.setPreferredSize(new java.awt.Dimension(28, 24));
        nextTLIButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextTLIButtonActionPerformed(evt);
            }
        });
        controlPanel.add(nextTLIButton);

        playbackModeToggleButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        playbackModeToggleButton.setText("P");
        playbackModeToggleButton.setEnabled(false);
        playbackModeToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playbackModeToggleButtonActionPerformed(evt);
            }
        });
        controlPanel.add(playbackModeToggleButton);

        controlsPanel.add(controlPanel);

        statusPanel.setLayout(new javax.swing.BoxLayout(statusPanel, javax.swing.BoxLayout.LINE_AXIS));

        statusLabel.setText("Status");
        statusPanel.add(statusLabel);

        controlsPanel.add(statusPanel);

        getContentPane().add(controlsPanel, java.awt.BorderLayout.SOUTH);

        videoPanel.setVisible(false);
        videoPanel.setLayout(new java.awt.BorderLayout());

        videoDisplayPanel.setLayout(new java.awt.BorderLayout());
        videoPanel.add(videoDisplayPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(videoPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void endPositionLabelMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_endPositionLabelMouseWheelMoved
    }//GEN-LAST:event_endPositionLabelMouseWheelMoved

    private void startPositionLabelMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_startPositionLabelMouseWheelMoved
    }//GEN-LAST:event_startPositionLabelMouseWheelMoved

    private void startPositionLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_startPositionLabelMouseReleased
        // DO NOTHING
    }//GEN-LAST:event_startPositionLabelMouseReleased

    private void startPositionLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_startPositionLabelMousePressed
        // DO NOTHING
    }//GEN-LAST:event_startPositionLabelMousePressed

    private void cutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutButtonActionPerformed
            final double fStartTime = internalStartTime / 1000.0;
            final double fEndTime = internalEndTime / 1000.0;
            SaveAudioPartDialog dialog = new SaveAudioPartDialog(null, true, lastAudioPartFilename, snapshotWouldBeLinkeable);
            String info = "[Cut audio from "
                    + TimeStringFormatter.formatMiliseconds(fStartTime*1000.0, 2) + " to "
                    + TimeStringFormatter.formatMiliseconds(fEndTime*1000.0, 2) + "]";
            dialog.setExtraInfo(info);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (dialog.change) {
                if ((fEndTime - fStartTime)>30.0){
                    String message = "Going to save audio snippet in background.\nThis may take a while.";
                    JOptionPane.showMessageDialog(this, message);
                }
                String fn = dialog.getFilename();
                if (!fn.contains(".")) fn+=".wav";                
                final String filename = fn;
                final boolean sendLink = dialog.sendLink();
                final AudioPanel myself = this;
                Thread backgroundThread = new Thread(new Runnable(){

                    @Override
                    public void run() {
                        try {
                            myself.status("Saving audio snippet as " + filename + "...");
                            AudioProcessor ap = new AudioProcessor();
                            ap.writePart(fStartTime, fEndTime, soundFileName, filename);
                            myself.status("Saved audio snippet as " + filename + ".");
                            lastAudioPartFilename = filename;
                            if (snapshotWouldBeLinkeable && sendLink) {
                                fireLinkAudioSnippet();
                            }
                            String message = "Audio snippet saved successfully.";
                            JOptionPane.showMessageDialog(myself, message);
                        } catch (IOException ioe) {
                            String message = "Could not save audio snippet: " + ioe.getLocalizedMessage();
                            javax.swing.JOptionPane.showMessageDialog(myself, message);
                        }
                        
                    }                    
                });
                
                //SwingUtilities.invokeLater(backgroundThread);
                backgroundThread.start();
            }
        
    }//GEN-LAST:event_cutButtonActionPerformed

    private void grabButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grabButtonActionPerformed
        // TODO add your handling code here:        
        java.awt.Image img = null;

        if (getPlayer() instanceof JDSPlayer){
            JDSPlayer jDSPlayer = (JDSPlayer)getPlayer();
            img = jDSPlayer.grabFrame();            
        } else if (getPlayer() instanceof MMFPlayer){
            MMFPlayer mMFPlayer = (MMFPlayer)getPlayer();
            img = mMFPlayer.grabFrame();            
        } else if (getPlayer() instanceof JavaFXPlayer){
            JavaFXPlayer javaFXPlayer = (JavaFXPlayer)getPlayer();
            img = javaFXPlayer.grabFrame();  
        } else if (getPlayer() instanceof AVFPlayer){
            AVFPlayer aVFPlayer = (AVFPlayer)getPlayer();
            img = aVFPlayer.grabFrame();  
        }

        if (img==null) return;
        BufferedImage buffimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);        
        java.awt.Graphics2D g = buffimg.createGraphics();
        g.drawImage(img, null, null);
        try{
            SaveSnapshotDialog dialog = new SaveSnapshotDialog(null, true, lastSnapshotFilename, snapshotWouldBeLinkeable);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (dialog.change) {
                String filename = dialog.getFilename();
                javax.imageio.ImageIO.write(buffimg, "png", new java.io.File(filename));
                lastSnapshotFilename = filename;
                if (snapshotWouldBeLinkeable && dialog.sendLink()){
                    fireLinkSnapShot();
                }
            }
        } catch (java.io.IOException ioe){
            String message = "Could not save snapshot: " + ioe.getLocalizedMessage();
            javax.swing.JOptionPane.showMessageDialog(this, message);
        }
    }//GEN-LAST:event_grabButtonActionPerformed

    private void previousTLIButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousTLIButtonActionPerformed
        // TODO add your handling code here:
        fireSelectPreviousTLI();
    }//GEN-LAST:event_previousTLIButtonActionPerformed

    private void nextTLIButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextTLIButtonActionPerformed
        // TODO add your handling code here:
        fireSelectNextTLI();
    }//GEN-LAST:event_nextTLIButtonActionPerformed

    private void currentPositionLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_currentPositionLabelMouseClicked
    }//GEN-LAST:event_currentPositionLabelMouseClicked

    private void endPositionLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_endPositionLabelMouseClicked
    }//GEN-LAST:event_endPositionLabelMouseClicked

    private void startPositionLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_startPositionLabelMouseClicked
    }//GEN-LAST:event_startPositionLabelMouseClicked

    public void minimize(boolean mini){
        //sliderPanel.setVisible(!mini);
        //mainPanel.setVisible(!mini);
        pack();
    }
    
    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
    }//GEN-LAST:event_stopButtonActionPerformed

    
    private void pauseToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseToggleButtonActionPerformed
    }//GEN-LAST:event_pauseToggleButtonActionPerformed

    
    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        //doPlay();
    }//GEN-LAST:event_playButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        if (!(player instanceof AVFPlayer)){
            setVisible(false);
            dispose();
        }
    }//GEN-LAST:event_closeDialog

    private void playbackModeToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playbackModeToggleButtonActionPerformed
        if (playbackModeToggleButton.isSelected()){
            playbackModeToggleButton.setForeground(Color.RED);
        } else {
            playbackModeToggleButton.setForeground(Color.BLACK);            
        }
        firePlaybackModeToggled();
}//GEN-LAST:event_playbackModeToggleButtonActionPerformed

    private void availableFilesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_availableFilesComboBoxActionPerformed
        fireFileSelectionChanged(availableFilesComboBox.getSelectedIndex());
    }//GEN-LAST:event_availableFilesComboBoxActionPerformed

    private void formComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_formComponentRemoved
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentRemoved

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // new 28-01-2016
        System.out.println("This is AudioPanel.formComponentResized.");
        if (player==null) return;
        if (player.getVisibleComponent()==null) return;
        int videoDisplayPanelWidth = videoDisplayPanel.getWidth();
        int videoDisplayPanelHeight = videoDisplayPanel.getHeight();
        System.out.println("Current dimensions: [" + videoDisplayPanelWidth + "/" + videoDisplayPanelHeight + "]");
        Dimension dimensionByWidth = calculateDimensionByWidth(sourceWidth, sourceHeight, videoDisplayPanelWidth);
        Dimension dimensionByHeight = calculateDimensionByHeight(sourceWidth, sourceHeight, videoDisplayPanelHeight);
        // use the smaller of the two because it is guaranteed to fit
        if (dimensionByWidth.width < dimensionByHeight.width){
           player.getVisibleComponent().setPreferredSize(dimensionByWidth);
           /*if (player instanceof CocoaQTPlayer){
                player.getVisibleComponent().setSize(dimensionByWidth);
                player.getVisibleComponent().setMinimumSize(dimensionByWidth);
                player.getVisibleComponent().setMaximumSize(dimensionByWidth);                
           } */
           System.out.println("Dimensions after resize: [" + dimensionByWidth.getWidth() + "/" + dimensionByWidth.getHeight() + "]");
           System.out.println(player.getVisibleComponent().toString());
        } else {
           player.getVisibleComponent().setPreferredSize(dimensionByHeight);            
           /*if (player instanceof CocoaQTPlayer){
                player.getVisibleComponent().setSize(dimensionByHeight);
                player.getVisibleComponent().setMinimumSize(dimensionByHeight);
                player.getVisibleComponent().setMaximumSize(dimensionByHeight);                
           } */
           System.out.println("Dimensions after resize: [" + dimensionByHeight.getWidth() + "/" + dimensionByHeight.getHeight() + "]");
           System.out.println(player.getVisibleComponent().toString());
        }
        
        
    }//GEN-LAST:event_formComponentResized
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //ELANDSPlayer player = new ELANDSPlayer();
        //JMFPlayer player = new JMFPlayer();
        //VLCPlayer player = new VLCPlayer();
        //CocoaQTPlayer player = new CocoaQTPlayer();
        JavaFXPlayer player = new JavaFXPlayer();
        try {
            player.setSoundFile("/Users/thomasschmidt/Desktop/MEDIA-TEST/BECKHAMS_MPEG-1.mpg");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        AudioPanel ap = new AudioPanel(new javax.swing.JFrame(), true, player);
        ap.show();
        //ap.setSoundFile("/Users/thomasschmidt/Desktop/MEDIA-TEST/BECKHAMS_MPEG-1.mpg");
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox availableFilesComboBox;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JPanel controlsPanel;
    private javax.swing.JLabel currentPositionLabel;
    private javax.swing.JPanel currentPositionPanel;
    private javax.swing.JButton cutButton;
    private javax.swing.JLabel endPositionLabel;
    private javax.swing.JPanel filesPanel;
    private javax.swing.JButton grabButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JButton nextTLIButton;
    private javax.swing.JToggleButton pauseToggleButton;
    private javax.swing.JButton playButton;
    private javax.swing.JToggleButton playbackModeToggleButton;
    private javax.swing.JButton previousTLIButton;
    private javax.swing.JLabel startPositionLabel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JButton stopButton;
    private javax.swing.JLabel totalLengthLabel;
    private javax.swing.JPanel videoDisplayPanel;
    private javax.swing.JPanel videoPanel;
    private javax.swing.JLabel zeroPositionLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void processPlayableEvent(PlayableEvent e) {
        int type = e.getType();
        if (type==PlayableEvent.POSITION_UPDATE){
                double pos = e.getPosition();
                currentPositionLabel.setText(TimeStringFormatter.formatMiliseconds(pos*1000.0,1));
        }        
    }    
    
    private void status (String text){
        //System.out.println(text);
        statusLabel.setText(org.exmaralda.common.helpers.Internationalizer.getString(text));
    }

    public boolean isVideo(){
        return (player.getVisibleComponent()!=null);
    }
    
    private boolean openSoundFile(String filename){
        System.out.println("(3a) AudioPanel: openSoundFile");

        soundFileName = filename;
        
        status("Audio/Video file " + new File(filename).getName() + " opened successfully.");
      
        videoDisplayPanel.removeAll();
        
        // restructured 28-01-2016
        Component c = getPlayer().getVisibleComponent();
        if (c!=null){
            System.out.println("(3b) AudioPanel: taking care of the visual component");
         
            // 1. determine the actual size of the video
            // this may work differently in different players
            if (getPlayer() instanceof JDSPlayer){
                JDSPlayer jdsp = (JDSPlayer)getPlayer();
                sourceWidth = jdsp.wrappedPlayer.getSourceWidth();
                sourceHeight = jdsp.wrappedPlayer.getSourceHeight();           
                System.out.println("JDSPlayer says the movie " + new File(filename).getName() 
                        + " has width " + sourceWidth + "and  height " + sourceHeight);
            } else if (getPlayer() instanceof JavaFXPlayer){
                JavaFXPlayer jdsp = (JavaFXPlayer)getPlayer();
                sourceWidth = jdsp.wrappedPlayer.getSourceWidth();
                sourceHeight = jdsp.wrappedPlayer.getSourceHeight();           
                System.out.println("JavaFXPlayer says the movie " + new File(filename).getName() 
                        + " has width " + sourceWidth + "and  height " + sourceHeight);
            } else if (getPlayer() instanceof AVFPlayer){
                AVFPlayer jdsp = (AVFPlayer)getPlayer();
                sourceWidth = jdsp.wrappedPlayer.getSourceWidth();
                sourceHeight = jdsp.wrappedPlayer.getSourceHeight();           
                System.out.println("AVFPlayer says the movie " + new File(filename).getName() 
                        + " has width " + sourceWidth + "and  height " + sourceHeight);
            }else if (getPlayer() instanceof MMFPlayer) {
                 MMFPlayer cqtp = (MMFPlayer)getPlayer();
                 sourceWidth = cqtp.wrappedPlayer.getSourceWidth();
                 sourceHeight = cqtp.wrappedPlayer.getSourceHeight();     
                 System.out.println("MMFPlayer says the movie " + new File(filename).getName() 
                         + " has width " + sourceWidth + " and  height " + sourceHeight);
            } else {
                // this includes JMF and the other, more shitty like, players
                videoDisplayPanel.add(c);
                videoPanel.setVisible(true);
                sourceWidth = c.getPreferredSize().width;
                sourceHeight = c.getPreferredSize().height;              
                System.out.println("JMFPlayer says the movie " + new File(filename).getName() 
                        + " has width " + sourceWidth + "and  height " + sourceHeight);                
            }
            /*else if (getPlayer() instanceof CocoaQTPlayer) {
            CocoaQTPlayer cqtp = (CocoaQTPlayer)getPlayer();
            sourceWidth = cqtp.wrappedPlayer.getSourceWidth();
            sourceHeight = cqtp.wrappedPlayer.getSourceHeight();
            System.out.println("CocoaQTPlayer says the movie " + new File(filename).getName()
            + " has width " + sourceWidth + " and  height " + sourceHeight);
            // New 06-12-2016: try to at least get some sensible aspect ratio
            if ((sourceWidth==1) && (sourceHeight==1)){
            sourceWidth = 480;
            sourceHeight = 270;
            System.out.println("CocoaQTPlayer has set movie width/height to 480/270");
            }
            } */ 
            
            
            // 2. set the initial size to a maximum of 480 width
            Dimension initialDimension = calculateInitialDimension(sourceWidth, sourceHeight);    
            
            // 3. now do the rest
            c.setPreferredSize(initialDimension);
            System.out.println("Preferred size set to: " + initialDimension.width + "/" + initialDimension.height);
            
            /*if (getPlayer() instanceof JMFPlayer){
                JMFPlayer jmfp = (JMFPlayer)getPlayer();                
                grabButton.setEnabled(jmfp.fgc!=null);
                cutButton.setEnabled(AudioProcessor.isCuttable(filename));                
            } else */if (getPlayer() instanceof JDSPlayer || getPlayer() instanceof AVFPlayer || getPlayer() instanceof MMFPlayer || getPlayer() instanceof JavaFXPlayer){
                // new 06-12-2016
                videoDisplayPanel.add(c);
                System.out.println("(3c) AudioPanel: added the visual component");
                System.out.println(c.toString());
                /*if (player instanceof CocoaQTPlayer){
                    c.setSize(initialDimension);
                    c.setMinimumSize(initialDimension);
                    c.setMaximumSize(initialDimension);                
                }*/
                if ((getPlayer() instanceof AVFPlayer) && 
                        ((new File(filename).getName().toUpperCase().endsWith(".WAV")) ||
                        (new File(filename).getName().toUpperCase().endsWith(".MP3")))){
                    System.out.println("We have AVFPlayer player with a sound file!");
                    /*videoDisplayPanel.setPreferredSize(new java.awt.Dimension(1, 1));*/
                    grabButton.setEnabled(false);
                } else {
                    videoDisplayPanel.setPreferredSize(c.getPreferredSize());
                    grabButton.setEnabled(true);
                }
                cutButton.setEnabled(AudioProcessor.isCuttable(filename));                 
                videoPanel.setVisible(true);                                    
            } else {
                // all the other crap players...
                videoPanel.setVisible(true);                
            }

            
        } else {
            // this is the case where there is no visible component
            videoPanel.setVisible(false);
        }

        //moved here 24-04-2017 issue #73
        totalLength = getPlayer().getTotalLength();
        totalLengthLabel.setText(TimeStringFormatter.formatMiliseconds(totalLength*1000.0,1));
        endPositionLabel.setText(TimeStringFormatter.formatMiliseconds(totalLength*1000.0,1));
        

        pack();

        return true;

    }
    
    /*private void disablePositionToSomethingButtons(){
        positionToStartButton.setEnabled(false);
        positionToStartButton.setBackground(new java.awt.Color(204,204,204));
        positionToStopButton.setEnabled(false);
        positionToStopButton.setBackground(new java.awt.Color(204,204,204));
    }*/

    /*private void enablePositionToSomethingButtons(){
        positionToStartButton.setEnabled(true);
        positionToStartButton.setBackground(new java.awt.Color(0,204,51));
        positionToStopButton.setEnabled(true);
        positionToStopButton.setBackground(new java.awt.Color(255,0,51));
    }*/
    
    /*public void enableGetButtons(boolean enabled){
        sendStartTimeButton.setEnabled(enabled);
        sendStopTimeButton.setEnabled(enabled);
        sendPositionTimeButton.setEnabled(enabled);
    }*/
    
    protected void fireEvent(PraatPanelEvent event){
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
             if (listeners[i]==PraatPanelListener.class) {
                ((PraatPanelListener)listeners[i+1]).processTime(event);             
            }
         }
    }

    protected void fireFileSelectionChanged(int indexOfFile){
        fireEvent(new PraatPanelEvent(PraatPanelEvent.SOUND_FILE_SELECTION_CHANGED, indexOfFile));         
    }

    protected void fireCursorTime(double time) {
        fireEvent(new PraatPanelEvent(time));
    }
      
    protected void fireSelectNextTLI(){
        fireEvent(new PraatPanelEvent(PraatPanelEvent.SELECT_NEXT_TLI)); 
    }
    
    protected void fireSelectPreviousTLI(){
        fireEvent(new PraatPanelEvent(PraatPanelEvent.SELECT_PREVIOUS_TLI)); 
    }
    
    protected void fireLinkSnapShot(){
        fireEvent(new PraatPanelEvent(PraatPanelEvent.LINK_SNAPSHOT));         
    }

    protected void fireLinkAudioSnippet(){
        fireEvent(new PraatPanelEvent(PraatPanelEvent.LINK_AUDIO_SNIPPET));         
    }

    protected void firePlaybackModeToggled(){
        if (playbackModeToggleButton.isSelected()){
            fireEvent(new PraatPanelEvent(PraatPanelEvent.PLAYBACK_MODE_ON));
        } else {
            fireEvent(new PraatPanelEvent(PraatPanelEvent.PLAYBACK_MODE_OFF));
        }
    }


    void registerKeyStrokes(){
        // 15-12-2017 : these can go, I think
        /*InputMap im = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getRootPane().getActionMap();
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),"F1_Pressed");
        am.put("F1_Pressed", playAction);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),"F2_Pressed");
        am.put("F2_Pressed", pauseAction);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0),"F3_Pressed");
        am.put("F3_Pressed", stopAction);
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),"F5_Pressed");
        am.put("F5_Pressed", sendStartTimeAction);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0),"F6_Pressed");
        am.put("F6_Pressed", sendPositionTimeAction);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0),"F7_Pressed");
        am.put("F7_Pressed", sendStopTimeAction);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0),"F11_Pressed");
        am.put("F11_Pressed", previousTLIAction);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0),"F12_Pressed");
        am.put("F12_Pressed", nextTLIAction);*/
        

    }
    
    // 15-12-2017 : get rid of all these actions
    /*public Action playAction = 
        new AbstractAction() { public void actionPerformed(ActionEvent e) {playButton.doClick(); } };
    
    public Action pauseAction = 
        new AbstractAction(){ public void actionPerformed(ActionEvent e) {pauseToggleButton.doClick(); } };
    
    public Action stopAction = 
        new AbstractAction() { public void actionPerformed(ActionEvent e) {stopButton.doClick(); } };*/
    
    //public Action sendStartTimeAction = 
    //    new AbstractAction() { public void actionPerformed(ActionEvent e) {/*sendStartTimeButton.doClick(); */} };
    
    //public Action sendPositionTimeAction = 
    //    new AbstractAction() { public void actionPerformed(ActionEvent e) {/*sendPositionTimeButton.doClick(); */ }};
    
    //public Action sendStopTimeAction = 
    //    new AbstractAction() { public void actionPerformed(ActionEvent e) {/*sendStopTimeButton.doClick(); */} };
    
    /*public Action previousTLIAction = 
        new AbstractAction() { public void actionPerformed(ActionEvent e) {previousTLIButton.doClick(); } };
    
    public Action nextTLIAction = 
        new AbstractAction() {public void actionPerformed(ActionEvent e) {nextTLIButton.doClick(); } };*/
    
    @Override
    public void show(){
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        java.awt.Dimension dialogSize = this.getPreferredSize();
        this.setLocation(Math.round((screenSize.width - dialogSize.width)/2), screenSize.height - dialogSize.height - 30);
        //super.setVisible(true);
        super.show();
    }
    
    
    /*public void activateTimelineMode(){
        this.syncStartCheckBox.setSelected(true);
        this.syncEndCheckBox.setSelected(true);
    }*/
    
    
    public Playable getPlayer() {
        return player;
    }

    public void setPlayer(Playable player) {
        this.player = player;
        if (player instanceof JDSPlayer){
            setTitle(getTitle() + " [JDS]");
        } else if (player instanceof MMFPlayer){
            setTitle(getTitle() + " [MMF]");
        } else if (player instanceof BASAudioPlayer){
            setTitle(getTitle() + " [BAS-Audio]");
        } else if (player instanceof JavaFXPlayer){
            setTitle(getTitle() + " [JavaFX]");           
        } else if (player instanceof AVFPlayer){
            setTitle(getTitle() + " [AVF]");           
        } 
    }

    int MAXIMAL_INITIAL_WIDTH = 480;
    
    private Dimension calculateInitialDimension(int sourceWidth, int sourceHeight) {
        if (sourceWidth<=MAXIMAL_INITIAL_WIDTH){
            // don't make it bigger than necessary
            return new java.awt.Dimension(sourceWidth, sourceHeight);                    
        }
        return calculateDimensionByWidth(sourceWidth, sourceHeight, MAXIMAL_INITIAL_WIDTH);
    }
    
    private Dimension calculateDimensionByWidth(int sourceWidth, int sourceHeight, int actualWidth) {
        double ratio = (double)((double)actualWidth/(double)sourceWidth);
        int calculatedHeight = (int) Math.round(ratio * sourceHeight);
        Dimension result = new Dimension(actualWidth, calculatedHeight);
        return result;
    }
    
    private Dimension calculateDimensionByHeight(int sourceWidth, int sourceHeight, int actualHeight) {
        double ratio = (double)((double)actualHeight/(double)sourceHeight);
        int calculatedWidth = (int) Math.round(ratio * sourceWidth);
        Dimension result = new Dimension(calculatedWidth, actualHeight);
        return result;
    }

    public void setTimeSelection(TimeSelectionEvent event) {
        internalStartTime = event.getStartTime();
        internalEndTime = event.getEndTime();
        startPositionLabel.setText(TimeStringFormatter.formatMiliseconds(event.getStartTime(), 1));
        endPositionLabel.setText(TimeStringFormatter.formatMiliseconds(event.getEndTime(), 1));
        cutButton.setEnabled((internalStartTime!=internalEndTime) && (AudioProcessor.isCuttable(soundFileName)));
    }

    
        
}
