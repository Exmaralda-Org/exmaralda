/*
 * MessageDialog.java
 *
 * Created on 21. Maerz 2003, 12:37
 */

package org.exmaralda.partitureditor.exSync.swing;

/**
 *
 * @author  thomas
 */
public class MessageDialog extends javax.swing.JDialog {
    
    StringBuffer message = null;
    /** Creates new form MessageDialog */
    public MessageDialog(java.awt.Frame parent, boolean modal, StringBuffer m) {
        super(parent, modal);
        initComponents();
        message = m;
        String mm = message.toString();
        if (mm.startsWith("<html>")){
            messageArea.setContentType("text/html");
            messageArea.setEditorKit(new javax.swing.text.html.HTMLEditorKit());
            messageArea.setEditable(false);
        }
        messageArea.setText(mm);
        //messageArea.setText("<h2>Wurst</h2>");
        this.getRootPane().setDefaultButton(okButton);     
        org.exmaralda.common.helpers.Internationalizer.internationalizeDialogToolTips(this);
        messageArea.setCaretPosition(0);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        messageArea = new javax.swing.JEditorPane();

        setTitle("ExSync \"import\" messages");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel1.add(okButton);

        saveButton.setText("Save as...");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jPanel1.add(saveButton);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(400, 200));

        messageArea.setEditable(false);
        jScrollPane2.setViewportView(messageArea);

        getContentPane().add(jScrollPane2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // Add your handling code here:
        SaveMessageDialog dialog = new SaveMessageDialog(message);
        dialog.saveMessage(this);
    }//GEN-LAST:event_saveButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // Add your handling code here:
        setVisible(false);
        dispose();        
    }//GEN-LAST:event_okButtonActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //new MessageDialog(new javax.swing.JFrame(), true).show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JEditorPane messageArea;
    private javax.swing.JButton okButton;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
 
    public void show(){
        java.awt.Dimension dialogSize = this.getPreferredSize();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(screenSize.width/2 - dialogSize.width/2, screenSize.height/2 - dialogSize.height/2);
        super.show();
    }

}