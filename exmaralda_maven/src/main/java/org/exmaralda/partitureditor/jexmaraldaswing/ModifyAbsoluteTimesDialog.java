/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ModifyAbsoluteTimesDialog.java
 *
 * Created on 16.03.2012, 09:43:16
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import javax.swing.JOptionPane;
import org.exmaralda.common.helpers.TimeTextField;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.folker.utilities.TimeStringFormatter;
import org.exmaralda.partitureditor.jexmaralda.Timeline;

/**
 *
 * @author thomas
 */
public class ModifyAbsoluteTimesDialog extends javax.swing.JDialog {

    Timeline timeline;
    public boolean approved = false;
    
    double shiftAmount = Double.NaN;
    double scaleAmount = Double.NaN;
    
    public static int SHIFT_MODIFICATION = 0;
    public static int SCALE_MODIFICATION = 1;
    
    /** Creates new form ModifyAbsoluteTimesDialog
     * @param tl
     * @param index
     * @param parent
     * @param modal */
    public ModifyAbsoluteTimesDialog(Timeline tl, int index, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        timeline = tl;
        firstToZeroButton.setEnabled(timeline!=null && timeline.getNumberOfTimelineItems()>0 && timeline.getTimelineItemAt(0).getTime()>=0.0);
        if (timeline!=null && index>=0 && index<timeline.getNumberOfTimelineItems()){
            double time = timeline.getTimelineItemAt(index).getTime();
            if (time>=0){
                String timeString = TimeStringFormatter.formatMiliseconds(time * 1000.0, 4);
                currentTimeTextField.setText(timeString);
                targetTimeTextField.setText(timeString);
            }
        }
    }

    public int getModificationType(){
        if (shiftRadioButton.isSelected()) return SHIFT_MODIFICATION;
        return SCALE_MODIFICATION;
    }
    
    void setValues() throws Exception{
        shiftAmount = TimeStringFormatter.parseString(shiftTextField.getText(), TimeTextField.CHECK_REGEX) / 1000.0;
        scaleAmount = Double.parseDouble(scaleTextField.getText());
        
    }
    
    public double getShiftAmount(){
        return shiftAmount;
    }
    
    public double getScaleAmount(){
        return scaleAmount;
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        modifyButtonGroup = new javax.swing.ButtonGroup();
        mainPanel = new javax.swing.JPanel();
        shiftPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        shiftRadioButton = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        shiftTextField = new TimeTextField("01:54.3");
        jPanel6 = new javax.swing.JPanel();
        firstToZeroButton = new javax.swing.JButton();
        scalePanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        scaleRadioButton = new javax.swing.JRadioButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        scaleTextField = new javax.swing.JTextField();
        checkPointPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        currentTimePanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        currentTimeTextField = new TimeTextField("45:12.23");
        targetTimePanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        targetTimeTextField = new TimeTextField("45:45.11");
        useCheckPointButton = new javax.swing.JButton();
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(FOLKERInternationalizer.getString("dialog.times.title"));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));

        shiftPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        shiftPanel.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        modifyButtonGroup.add(shiftRadioButton);
        shiftRadioButton.setSelected(true);
        shiftRadioButton.setText(FOLKERInternationalizer.getString("dialog.times.shift"));
        jPanel2.add(shiftRadioButton);

        shiftPanel.add(jPanel2, java.awt.BorderLayout.NORTH);

        jLabel1.setText(FOLKERInternationalizer.getString("dialog.times.shifttext"));
        jPanel3.add(jLabel1);

        shiftTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        shiftTextField.setText("01:54.3");
        shiftTextField.setMaximumSize(new java.awt.Dimension(300, 20));
        shiftTextField.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel3.add(shiftTextField);

        shiftPanel.add(jPanel3, java.awt.BorderLayout.CENTER);

        firstToZeroButton.setText(FOLKERInternationalizer.getString("dialog.times.first2zero"));
        firstToZeroButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstToZeroButtonActionPerformed(evt);
            }
        });
        jPanel6.add(firstToZeroButton);

        shiftPanel.add(jPanel6, java.awt.BorderLayout.PAGE_END);

        mainPanel.add(shiftPanel);

        scalePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        scalePanel.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        modifyButtonGroup.add(scaleRadioButton);
        scaleRadioButton.setText(FOLKERInternationalizer.getString("dialog.times.scale"));
        jPanel4.add(scaleRadioButton);

        scalePanel.add(jPanel4, java.awt.BorderLayout.NORTH);

        jLabel2.setText(FOLKERInternationalizer.getString("dialog.times.scaletext"));
        jPanel7.add(jLabel2);

        scaleTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        scaleTextField.setText("0.8");
        scaleTextField.setMaximumSize(new java.awt.Dimension(300, 20));
        scaleTextField.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel7.add(scaleTextField);

        scalePanel.add(jPanel7, java.awt.BorderLayout.CENTER);

        mainPanel.add(scalePanel);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText(FOLKERInternationalizer.getString("dialog.times.checkpoint"));
        checkPointPanel.add(jLabel3);

        currentTimePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setText(FOLKERInternationalizer.getString("dialog.times.currenttime"));
        currentTimePanel.add(jLabel4);

        currentTimeTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        currentTimeTextField.setText("45:12.23");
        currentTimeTextField.setMaximumSize(new java.awt.Dimension(300, 20));
        currentTimeTextField.setPreferredSize(new java.awt.Dimension(100, 20));
        currentTimePanel.add(currentTimeTextField);

        checkPointPanel.add(currentTimePanel);

        targetTimePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setText(FOLKERInternationalizer.getString("dialog.times.targettime"));
        targetTimePanel.add(jLabel5);

        targetTimeTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        targetTimeTextField.setText("45:45.11");
        targetTimeTextField.setMaximumSize(new java.awt.Dimension(300, 20));
        targetTimeTextField.setPreferredSize(new java.awt.Dimension(100, 20));
        targetTimePanel.add(targetTimeTextField);

        checkPointPanel.add(targetTimePanel);

        useCheckPointButton.setText(FOLKERInternationalizer.getString("dialog.times.usecheckpoint"));
        useCheckPointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useCheckPointButtonActionPerformed(evt);
            }
        });
        checkPointPanel.add(useCheckPointButton);

        mainPanel.add(checkPointPanel);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(okButton);

        cancelButton.setText(FOLKERInternationalizer.getString("error.cancel"));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void firstToZeroButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstToZeroButtonActionPerformed
        double delta = timeline.getTimelineItemAt(0).getTime();
        shiftTextField.setText("-" + Double.toString(delta));
    }//GEN-LAST:event_firstToZeroButtonActionPerformed

    private void useCheckPointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useCheckPointButtonActionPerformed
        String source = this.currentTimeTextField.getText();
        String target = this.targetTimeTextField.getText();
        try {
            double sourceMS = TimeStringFormatter.parseString(source, TimeTextField.CHECK_REGEX);
            double targetMS = TimeStringFormatter.parseString(target, TimeTextField.CHECK_REGEX);
            
            double delta = (targetMS - sourceMS) / 1000.0;
            shiftTextField.setText(Double.toString(delta));
            
            double m = targetMS / sourceMS;
            scaleTextField.setText(Double.toString(m));
            
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            JOptionPane.showMessageDialog(this, FOLKERInternationalizer.getString("dialog.times.wrongformat"));
        }
    }//GEN-LAST:event_useCheckPointButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        try {
            setValues();
            approved = true;
            dispose();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            JOptionPane.showMessageDialog(this, FOLKERInternationalizer.getString("dialog.times.wrongformat"));
        }

    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        approved = false;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        this.requestFocus();
    }//GEN-LAST:event_formComponentShown

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
            java.util.logging.Logger.getLogger(ModifyAbsoluteTimesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ModifyAbsoluteTimesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ModifyAbsoluteTimesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ModifyAbsoluteTimesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ModifyAbsoluteTimesDialog dialog = new ModifyAbsoluteTimesDialog(null, 0, new javax.swing.JFrame(), true);
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
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel checkPointPanel;
    private javax.swing.JPanel currentTimePanel;
    private javax.swing.JTextField currentTimeTextField;
    private javax.swing.JButton firstToZeroButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel mainPanel;
    private javax.swing.ButtonGroup modifyButtonGroup;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel scalePanel;
    private javax.swing.JRadioButton scaleRadioButton;
    private javax.swing.JTextField scaleTextField;
    private javax.swing.JPanel shiftPanel;
    private javax.swing.JRadioButton shiftRadioButton;
    private javax.swing.JTextField shiftTextField;
    private javax.swing.JPanel targetTimePanel;
    private javax.swing.JTextField targetTimeTextField;
    private javax.swing.JButton useCheckPointButton;
    // End of variables declaration//GEN-END:variables
}