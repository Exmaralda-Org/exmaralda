/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package org.exmaralda.webservices.swing;

import java.text.DecimalFormat;
import java.util.HashMap;
import org.exmaralda.folker.gui.TierSelectionPanel;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Tier;

/**
 *
 * @author bernd
 */
public class InsertWhisperResultDialog extends javax.swing.JDialog {

    BasicTranscription bt;
    
    TierSelectionPanel tierSelectionPanel;
    
    public boolean approved;
    
     DecimalFormat df = new DecimalFormat("#.##");

    
    /**
     * Creates new form InsertWhisperResultDialog
     * @param parent
     * @param modal
     */
    public InsertWhisperResultDialog(java.awt.Frame parent, boolean modal,
            BasicTranscription bt, String resultText, double startTime, double endTime) {
        super(parent, modal);

        this.bt = bt.makeCopy();
        
        String startID = bt.getBody().getCommonTimeline().insertTimelineItemWithTime(startTime, 0.01);
        String endID = bt.getBody().getCommonTimeline().insertTimelineItemWithTime(startTime, 0.01);
        
        initComponents();

        startLabel.setText(df.format(startTime));
        endLabel.setText(df.format(endTime));
        recognizedTextArea.setText(resultText);
        
        tierSelectionPanel = new TierSelectionPanel(bt, startID, endID);
        leftPanel.add(tierSelectionPanel);
        
        pack();
        
        recognizedTextArea.requestFocus();
        recognizedTextArea.setCaretPosition(0);
    }
    
    
    public HashMap<String, Object> getParameters() {
        HashMap<String, Object> result = new HashMap<>();
        Object selectedValue = tierSelectionPanel.tierList.getSelectedValue();
        if (selectedValue!=null){
            result.put("TIER-ID", ((Tier)selectedValue).getID());
        } else {
            result.put("TIER-ID", null);
        }
        result.put("TEXT", recognizedTextArea.getText());
        return result;
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        startLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        endLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        recognizedTextArea = new javax.swing.JTextArea();
        leftPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Whisper ASR Result");

        jLabel1.setForeground(new java.awt.Color(0, 153, 51));
        jLabel1.setText("Start: ");
        topPanel.add(jLabel1);

        startLabel.setForeground(new java.awt.Color(0, 153, 51));
        startLabel.setText("0.0");
        topPanel.add(startLabel);

        jLabel4.setForeground(new java.awt.Color(255, 51, 51));
        jLabel4.setText("End: ");
        topPanel.add(jLabel4);

        endLabel.setForeground(new java.awt.Color(255, 51, 51));
        endLabel.setText("99.99");
        topPanel.add(endLabel);

        getContentPane().add(topPanel, java.awt.BorderLayout.PAGE_START);

        recognizedTextArea.setColumns(40);
        recognizedTextArea.setLineWrap(true);
        recognizedTextArea.setRows(5);
        recognizedTextArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(recognizedTextArea);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        leftPanel.setLayout(new javax.swing.BoxLayout(leftPanel, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(leftPanel, java.awt.BorderLayout.WEST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InsertWhisperResultDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InsertWhisperResultDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InsertWhisperResultDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InsertWhisperResultDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InsertWhisperResultDialog dialog = new InsertWhisperResultDialog(new javax.swing.JFrame(), true, null,"", 0.0,0.0 );
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
    private javax.swing.JLabel endLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JTextArea recognizedTextArea;
    private javax.swing.JLabel startLabel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
