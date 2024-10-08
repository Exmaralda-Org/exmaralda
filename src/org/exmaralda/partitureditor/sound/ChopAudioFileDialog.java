/*
 * ChopAudioFileDialog.java
 *
 * Created on 21. April 2005, 14:11
 */

package org.exmaralda.partitureditor.sound;

import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import javax.swing.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import javax.sound.sampled.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author  thomas
 */
public class ChopAudioFileDialog extends org.exmaralda.partitureditor.jexmaraldaswing.JEscapeDialog {
    

    BasicTranscription transcription;
    String pathToSoundFile = "";
    StringBuffer messages = new StringBuffer();
    java.awt.Frame p; 
    public Tier newTier;
    
    public boolean newTierCreated = false;
    public boolean existingTierUpdated = false;
    
    /** Creates new form ChopAudioFileDialog */
    public ChopAudioFileDialog(java.awt.Frame parent, boolean modal, BasicTranscription bt, String fn) {
        super(parent, modal);
        p = parent;
        initComponents();
        this.getRootPane().setDefaultButton(okButton);                
        String startPath = System.getProperty("user.home") + System.getProperty("file.separator") + "Audio";
        directoryTextField.setText(startPath);
        baseFilenameTextField.setText("AudioSnippet");
        transcription = bt;
        pathToSoundFile = fn;
        newTier = new Tier();
        newTier.setType("l");
        newTier.setCategory("aud");
        newTier.setID(transcription.getBody().getFreeID());
        newTier.setDisplayName("audio-links");
        String[] tierNames = new String[bt.getBody().getNumberOfTiers()];
        for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
            Tier tier = bt.getBody().getTierAt(pos);
            tierNames[pos] = tier.getDescription(bt.getHead().getSpeakertable()) + "    (" + tier.getID() + ")";
        }
        DefaultComboBoxModel cbm = new DefaultComboBoxModel(tierNames);
        tierComboBox.setModel(cbm);
        pack();
        org.exmaralda.common.helpers.Internationalizer.internationalizeDialogToolTips(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        basedOnButtonGroup = new javax.swing.ButtonGroup();
        linkToButtonGroup = new javax.swing.ButtonGroup();
        mainPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        basedOnPanel = new javax.swing.JPanel();
        baseOnTimelineRadioButton = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        baseOnTierRadioButton = new javax.swing.JRadioButton();
        tierComboBox = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        LinkToPanel = new javax.swing.JPanel();
        linkToSelectedTierButton = new javax.swing.JRadioButton();
        linkToNewTierButton = new javax.swing.JRadioButton();
        dontLinkButton = new javax.swing.JRadioButton();
        directoryPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        directoryTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        baseFilenamePanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        baseFilenameTextField = new javax.swing.JTextField();
        filenameDetailsPanel = new javax.swing.JPanel();
        appendEventDescriptionCheckBox = new javax.swing.JCheckBox();
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setTitle("Chop audio");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setLayout(new java.awt.GridLayout(1, 2));

        basedOnPanel.setLayout(new java.awt.GridLayout(3, 1));

        basedOnPanel.setBorder(new javax.swing.border.EtchedBorder());
        baseOnTimelineRadioButton.setSelected(true);
        baseOnTimelineRadioButton.setText("Based on the timeline");
        basedOnButtonGroup.add(baseOnTimelineRadioButton);
        baseOnTimelineRadioButton.setAlignmentY(0.0F);
        baseOnTimelineRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baseOnTimelineRadioButtonActionPerformed(evt);
            }
        });

        basedOnPanel.add(baseOnTimelineRadioButton);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));

        baseOnTierRadioButton.setText("Based on events in tier");
        basedOnButtonGroup.add(baseOnTierRadioButton);
        baseOnTierRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                baseOnTierRadioButtonActionPerformed(evt);
            }
        });

        jPanel1.add(baseOnTierRadioButton);

        tierComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tierComboBoxActionPerformed(evt);
            }
        });

        jPanel1.add(tierComboBox);

        basedOnPanel.add(jPanel1);

        basedOnPanel.add(jPanel3);

        jPanel2.add(basedOnPanel);

        LinkToPanel.setLayout(new java.awt.GridLayout(3, 1));

        LinkToPanel.setBorder(new javax.swing.border.EtchedBorder());
        linkToSelectedTierButton.setText("Link to the selected tier");
        linkToButtonGroup.add(linkToSelectedTierButton);
        linkToSelectedTierButton.setEnabled(false);
        LinkToPanel.add(linkToSelectedTierButton);

        linkToNewTierButton.setSelected(true);
        linkToNewTierButton.setText("Link to a new tier");
        linkToButtonGroup.add(linkToNewTierButton);
        LinkToPanel.add(linkToNewTierButton);

        dontLinkButton.setText("Don't link");
        linkToButtonGroup.add(dontLinkButton);
        LinkToPanel.add(dontLinkButton);

        jPanel2.add(LinkToPanel);

        mainPanel.add(jPanel2);

        directoryPanel.setLayout(new javax.swing.BoxLayout(directoryPanel, javax.swing.BoxLayout.X_AXIS));

        directoryPanel.setPreferredSize(new java.awt.Dimension(400, 23));
        jLabel3.setText("Directory: ");
        jLabel3.setPreferredSize(new java.awt.Dimension(100, 15));
        jLabel3.setMaximumSize(new java.awt.Dimension(100, 15));
        directoryPanel.add(jLabel3);

        directoryTextField.setPreferredSize(new java.awt.Dimension(200, 20));
        directoryTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
        directoryPanel.add(directoryTextField);

        browseButton.setText("Browse...");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        directoryPanel.add(browseButton);

        mainPanel.add(directoryPanel);

        baseFilenamePanel.setLayout(new javax.swing.BoxLayout(baseFilenamePanel, javax.swing.BoxLayout.X_AXIS));

        baseFilenamePanel.setPreferredSize(new java.awt.Dimension(400, 20));
        jLabel4.setText("Base filename: ");
        jLabel4.setPreferredSize(new java.awt.Dimension(100, 15));
        jLabel4.setMaximumSize(new java.awt.Dimension(100, 15));
        baseFilenamePanel.add(jLabel4);

        baseFilenameTextField.setPreferredSize(new java.awt.Dimension(250, 20));
        baseFilenameTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
        baseFilenamePanel.add(baseFilenameTextField);

        mainPanel.add(baseFilenamePanel);

        filenameDetailsPanel.setLayout(new javax.swing.BoxLayout(filenameDetailsPanel, javax.swing.BoxLayout.X_AXIS));

        appendEventDescriptionCheckBox.setText("Append event description");
        appendEventDescriptionCheckBox.setEnabled(false);
        filenameDetailsPanel.add(appendEventDescriptionCheckBox);

        mainPanel.add(filenameDetailsPanel);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(cancelButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        change = false;
        setVisible(false);
        dispose();
        
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // TODO add your handling code here:
        try{
            doChop();
        } catch (IOException ioe){
            message("=========", true);
            message("ERROR!!!!", true);
            message(ioe.getLocalizedMessage(), true);
        }
        org.exmaralda.partitureditor.exSync.swing.MessageDialog md = new org.exmaralda.partitureditor.exSync.swing.MessageDialog(p,true, messages);
        md.setTitle("Messages");
        md.show();
        change = true;
        newTierCreated = linkToNewTierButton.isSelected();
        existingTierUpdated = linkToSelectedTierButton.isSelected();
        setVisible(false);
        dispose();
        
    }//GEN-LAST:event_okButtonActionPerformed

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new javax.swing.JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int val = fc.showSaveDialog(this.getParent());
        if (val==JFileChooser.APPROVE_OPTION){
            directoryTextField.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void baseOnTierRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baseOnTierRadioButtonActionPerformed
        // TODO add your handling code here:
        linkToSelectedTierButton.setEnabled(baseOnTierRadioButton.isSelected());    
        appendEventDescriptionCheckBox.setEnabled(baseOnTierRadioButton.isSelected());
        if (!linkToSelectedTierButton.isEnabled() && linkToSelectedTierButton.isSelected()){
            linkToNewTierButton.setSelected(true);
        }
    }//GEN-LAST:event_baseOnTierRadioButtonActionPerformed

    private void baseOnTimelineRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baseOnTimelineRadioButtonActionPerformed
        // TODO add your handling code here:
        linkToSelectedTierButton.setEnabled(baseOnTierRadioButton.isSelected());
        appendEventDescriptionCheckBox.setEnabled(baseOnTierRadioButton.isSelected());
        if (!linkToSelectedTierButton.isEnabled() && linkToSelectedTierButton.isSelected()){
            linkToNewTierButton.setSelected(true);
        }        
    }//GEN-LAST:event_baseOnTimelineRadioButtonActionPerformed

    private void tierComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tierComboBoxActionPerformed
        // TODO add your handling code here:
        baseOnTierRadioButton.setSelected(true);      
        baseOnTierRadioButtonActionPerformed(evt);
    }//GEN-LAST:event_tierComboBoxActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try{
            String transFile = "d:\\edinburgh\\d\\aaa_beispiele\\helge_neu\\helge_basic.xml";
            String soundFile = "d:\\edinburgh\\d\\aaa_beispiele\\helge_neu\\helge.wav";
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            BasicTranscription bt = new BasicTranscription(transFile);            
            new ChopAudioFileDialog(new javax.swing.JFrame(), true, bt, soundFile).show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel LinkToPanel;
    private javax.swing.JCheckBox appendEventDescriptionCheckBox;
    private javax.swing.JPanel baseFilenamePanel;
    private javax.swing.JTextField baseFilenameTextField;
    private javax.swing.JRadioButton baseOnTierRadioButton;
    private javax.swing.JRadioButton baseOnTimelineRadioButton;
    private javax.swing.ButtonGroup basedOnButtonGroup;
    private javax.swing.JPanel basedOnPanel;
    private javax.swing.JButton browseButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel directoryPanel;
    private javax.swing.JTextField directoryTextField;
    private javax.swing.JRadioButton dontLinkButton;
    private javax.swing.JPanel filenameDetailsPanel;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.ButtonGroup linkToButtonGroup;
    private javax.swing.JRadioButton linkToNewTierButton;
    private javax.swing.JRadioButton linkToSelectedTierButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox tierComboBox;
    // End of variables declaration//GEN-END:variables

           
    Vector getBoundariesForTimeline(){        
        Vector result = new Vector();
        Timeline timeline = transcription.getBody().getCommonTimeline();
        double firstTime = timeline.getTimelineItemAt(0).getTime();     
        String firstID = timeline.getTimelineItemAt(0).getID();     
        for (int pos=1; pos<timeline.getNumberOfTimelineItems(); pos++){
            double nextTime = timeline.getTimelineItemAt(pos).getTime();
            String nextID = timeline.getTimelineItemAt(pos).getID();
            System.out.println(firstTime + "//" + nextTime);
            if (nextTime>firstTime){
                Object[] o = new Object[4];
                o[0] = firstTime;
                o[1] = firstID;
                o[2] = nextTime;
                o[3] = nextID;
                result.addElement(o);
                firstTime = nextTime;
                firstID = nextID;
            }
        }
        return result;
    }
                                
    Vector getBoundariesForTier(int tierNo){        
        Vector result = new Vector();
        Timeline timeline = transcription.getBody().getCommonTimeline();        
        Tier tier = transcription.getBody().getTierAt(tierNo);
        for (int pos=0; pos<tier.getNumberOfEvents(); pos++){
            Event event = tier.getEventAt(pos);
            double start = timeline.getPreviousTime(event.getStart());
            double end = timeline.getNextTime(event.getEnd());
            Object[] o = new Object[4];
            o[0] = start;
            o[1] = event.getStart();
            o[2] = end;
            o[3] = event.getEnd();
            result.addElement(o);
        }
        return result;
    }

    void anchorTimeline() throws IOException {
        AudioInputStream audioInputStream;
        AudioFormat audioFormat = null;
        SourceDataLine line = null;
        File soundFile = null;
        soundFile = new File(pathToSoundFile);
        try {
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);    
        } catch (UnsupportedAudioFileException uafe){
            IOException wrappedException = new IOException("Unsupported audio file:" + uafe.getLocalizedMessage());
            throw wrappedException;
        }
        audioFormat = audioInputStream.getFormat();        

        if (audioFormat.getFrameSize() == AudioSystem.NOT_SPECIFIED){
            IOException wrappedException = new IOException("Audio format does not support this operation.");
            throw wrappedException;
        }
        
        long frameLength = audioInputStream.getFrameLength();
        float frameRate = audioFormat.getFrameRate();
        audioInputStream.close();        
        
        double totalLength = frameLength / frameRate;        
        
        transcription.getBody().getCommonTimeline().anchorTimeline(0.0, totalLength);
    }
    
    void writeFiles(Vector times) throws IOException {
        AudioProcessor ap = new AudioProcessor();
        Tier selectedTier = null;
        if (baseOnTierRadioButton.isSelected()){
            selectedTier = transcription.getBody().getTierAt(tierComboBox.getSelectedIndex());
        }
        for (int pos=0; pos<times.size(); pos++){
            Object[] o = (Object[])(times.elementAt(pos));
            double startTime = ((Double)(o[0])).doubleValue();
            double endTime = ((Double)(o[2])).doubleValue();
            
            String dirName = directoryTextField.getText();
            String baseFilename = baseFilenameTextField.getText();
            String filename =   dirName 
                                + System.getProperty("file.separator") 
                                + baseFilename
                                + "_";
            if (pos<9) filename+="0";
            if (pos<99) filename+="0";
            if (pos<999) filename+="0";
            filename+= Integer.toString(pos+1);
            if (appendEventDescriptionCheckBox.isSelected() && (selectedTier!=null)){
                Event event = selectedTier.getEventAt(pos);
                String d = event.getDescription();
                System.out.println(d);
                String desc = d.substring(0, Math.min(d.length(),20));
                System.out.println(desc);
                for (int i=0; i<desc.length(); i++){
                    char c = desc.charAt(i);                    
                    if (((c>=65) && (c<=90)) || ((c>=97) && (c<=122))){
                        filename+=c;
                    }
                }                
            }
            filename += ".wav";
            message("Getting part " + Double.toString(startTime) + " to " + Double.toString(endTime), false);
            message("Writing " + filename + "...", false);
            ap.writePart(startTime, endTime, pathToSoundFile, filename);
            message("File written.", false);
            if (linkToSelectedTierButton.isSelected()){
                Event event = selectedTier.getEventAt(pos);
                event.setMedium("aud");
                event.setURL(filename);
            } else if (linkToNewTierButton.isSelected()){
                Event event = new Event();
                event.setStart((String)(o[1]));
                event.setEnd((String)(o[3]));                
                event.setDescription("Audio-Link");
                event.setMedium("aud");
                event.setURL(filename);
                newTier.addEvent(event);
            }
        }
    }
    
    void doChop() throws IOException {
        
        Timeline timeline = transcription.getBody().getCommonTimeline();        

        if (!timeline.isConsistent()){
            message("Making timeline consistent...", true); 
            timeline.makeConsistent();
        }
        
        message("Anchoring timeline...", true);
        anchorTimeline();
        
        String dirName = directoryTextField.getText();
        File dir = new File(dirName);
        if (!dir.canRead()){
            message("Creating directory " + dirName, true);
            dir.mkdirs();
        }

        Vector times = null;
        message("Getting times...", true);
        if (baseOnTimelineRadioButton.isSelected()){
            times = getBoundariesForTimeline();
        } else if (baseOnTierRadioButton.isSelected()){
            times = getBoundariesForTier(tierComboBox.getSelectedIndex());
        }
        message("...got " + Integer.toString(times.size()) + " value pairs.", false);
        
        message("", true);
        this.writeFiles(times);     
        
        message("Done.", true);
        
    }
    
    void message(String m, boolean newLine){
        messages.append(m + System.getProperty("line.separator"));
    }
    
    public void show(){
        java.awt.Dimension dialogSize = this.getPreferredSize();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(screenSize.width/2 - dialogSize.width/2, screenSize.height/2 - dialogSize.height/2);        
        super.show();
    }
    
    
}
