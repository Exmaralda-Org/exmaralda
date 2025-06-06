/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ChooseSegmentationDialog.java
 *
 * Created on 07.12.2009, 17:00:00
 */

package org.exmaralda.partitureditor.jexmaraldaswing;

import java.util.Enumeration;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;

/**
 *
 * @author thomas
 */
public class ChooseSegmentationDialog extends javax.swing.JDialog {

    /** Creates new form ChooseSegmentationDialog */
    public ChooseSegmentationDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public String getSegmentationCode(){
        Enumeration e = buttonGroup1.getElements();
        while (e.hasMoreElements()){
            JRadioButton b = (JRadioButton)(e.nextElement());
            if (b.isSelected()){
                return b.getText();
            }
        }
        return "GENERIC";
    }
    
    public String getCustomFSMPath(){
        return customFSMTextField.getText();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        segmentationPickerPanel = new javax.swing.JPanel();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jRadioButton9 = new javax.swing.JRadioButton();
        customFSMPanel = new javax.swing.JPanel();
        customFSMTextField = new javax.swing.JTextField();
        customFSMBrowseButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Segmentation");

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel1.add(okButton);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        mainPanel.setLayout(new java.awt.BorderLayout());

        segmentationPickerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Built-in segmentation algorithms"));
        segmentationPickerPanel.setLayout(new java.awt.GridLayout(3, 3));

        buttonGroup1.add(jRadioButton7);
        jRadioButton7.setSelected(true);
        jRadioButton7.setText("GENERIC");
        segmentationPickerPanel.add(jRadioButton7);

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("HIAT");
        segmentationPickerPanel.add(jRadioButton1);

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("DIDA");
        segmentationPickerPanel.add(jRadioButton2);

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("GAT");
        segmentationPickerPanel.add(jRadioButton3);

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setText("cGAT_MINIMAL");
        segmentationPickerPanel.add(jRadioButton4);

        buttonGroup1.add(jRadioButton5);
        jRadioButton5.setText("CHAT");
        segmentationPickerPanel.add(jRadioButton5);

        buttonGroup1.add(jRadioButton6);
        jRadioButton6.setText("IPA");
        segmentationPickerPanel.add(jRadioButton6);

        buttonGroup1.add(jRadioButton8);
        jRadioButton8.setText("CHAT_MINIMAL");
        segmentationPickerPanel.add(jRadioButton8);

        buttonGroup1.add(jRadioButton9);
        jRadioButton9.setText("INEL_EVENT_BASED");
        segmentationPickerPanel.add(jRadioButton9);

        mainPanel.add(segmentationPickerPanel, java.awt.BorderLayout.CENTER);

        customFSMPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Custom FSM"));

        customFSMTextField.setMaximumSize(new java.awt.Dimension(2000, 20));
        customFSMTextField.setMinimumSize(new java.awt.Dimension(400, 20));
        customFSMTextField.setPreferredSize(new java.awt.Dimension(400, 20));
        customFSMPanel.add(customFSMTextField);

        customFSMBrowseButton.setText("Browse...");
        customFSMBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customFSMBrowseButtonActionPerformed(evt);
            }
        });
        customFSMPanel.add(customFSMBrowseButton);

        mainPanel.add(customFSMPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void customFSMBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customFSMBrowseButtonActionPerformed
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(new ParameterFileFilter("xml", "Finite State Machines (*.xml)"));
        int ret = jfc.showOpenDialog(this);
        if (ret==JFileChooser.APPROVE_OPTION){
            customFSMTextField.setText(jfc.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_customFSMBrowseButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ChooseSegmentationDialog dialog = new ChooseSegmentationDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton customFSMBrowseButton;
    private javax.swing.JPanel customFSMPanel;
    private javax.swing.JTextField customFSMTextField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel segmentationPickerPanel;
    // End of variables declaration//GEN-END:variables

}
