/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RecordingsWizardPanel.java
 *
 * Created on 25.01.2010, 14:57:54
 */

package org.exmaralda.exakt.wizard.newtranscriptionwizard;

import java.io.File;
import javax.swing.JFileChooser;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;

/**
 *
 * @author thomas
 */
public class RecordingsWizardPanel extends javax.swing.JPanel {

    File lastDirectory = null;

    /** Creates new form RecordingsWizardPanel */
    public RecordingsWizardPanel() {
        initComponents();
    }

    public String[] getPaths(){
        String[] result = new String[3];
        result[0] = wavTextField.getText();
        result[1] = mp3TextField.getText();
        result[2] = otherTextField.getText();
        return result;
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
        jLabel1 = new javax.swing.JLabel();
        browseWavButton = new javax.swing.JButton();
        wavTextField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        browseMp3Button = new javax.swing.JButton();
        mp3TextField = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        browseOtherButton = new javax.swing.JButton();
        otherTextField = new javax.swing.JTextField();

        setLayout(new java.awt.GridLayout(6, 1));

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("WAV file: ");
        jPanel1.add(jLabel1, java.awt.BorderLayout.WEST);

        browseWavButton.setText("Browse...");
        browseWavButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseWavButtonActionPerformed(evt);
            }
        });
        jPanel1.add(browseWavButton, java.awt.BorderLayout.EAST);

        add(jPanel1);
        add(wavTextField);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("MP3 file: ");
        jPanel2.add(jLabel2, java.awt.BorderLayout.WEST);

        browseMp3Button.setText("Browse...");
        browseMp3Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseMp3ButtonActionPerformed(evt);
            }
        });
        jPanel2.add(browseMp3Button, java.awt.BorderLayout.EAST);

        add(jPanel2);
        add(mp3TextField);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("Other media file: ");
        jPanel3.add(jLabel3, java.awt.BorderLayout.WEST);

        browseOtherButton.setText("Browse...");
        browseOtherButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseOtherButtonActionPerformed(evt);
            }
        });
        jPanel3.add(browseOtherButton, java.awt.BorderLayout.EAST);

        add(jPanel3);
        add(otherTextField);
    }// </editor-fold>//GEN-END:initComponents

    private void browseWavButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseWavButtonActionPerformed
           JFileChooser fileChooser = new JFileChooser();
           fileChooser.setFileFilter(new ParameterFileFilter("wav", "WAV Audio file (*.wav)"));
           fileChooser.setCurrentDirectory(lastDirectory);
           if (fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
               lastDirectory = fileChooser.getSelectedFile().getParentFile();
               wavTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
           }
    }//GEN-LAST:event_browseWavButtonActionPerformed

    private void browseMp3ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseMp3ButtonActionPerformed
           JFileChooser fileChooser = new JFileChooser();
           fileChooser.setFileFilter(new ParameterFileFilter("mp3", "MP3 Audio file (*.mp3)"));
           fileChooser.setCurrentDirectory(lastDirectory);
           if (fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
               lastDirectory = fileChooser.getSelectedFile().getParentFile();
               mp3TextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
           }

    }//GEN-LAST:event_browseMp3ButtonActionPerformed

    private void browseOtherButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseOtherButtonActionPerformed
           JFileChooser fileChooser = new JFileChooser();
           fileChooser.setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.MediaFileFilter());
           fileChooser.setCurrentDirectory(lastDirectory);
           if (fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
               lastDirectory = fileChooser.getSelectedFile().getParentFile();
               otherTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
           }

    }//GEN-LAST:event_browseOtherButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseMp3Button;
    private javax.swing.JButton browseOtherButton;
    private javax.swing.JButton browseWavButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    public javax.swing.JTextField mp3TextField;
    public javax.swing.JTextField otherTextField;
    public javax.swing.JTextField wavTextField;
    // End of variables declaration//GEN-END:variables

}