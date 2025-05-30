/*
 * EditTimelineItemFormatDialog.java
 *
 * Created on 7. Juni 2002, 15:53
 */

package org.exmaralda.partitureditor.jexmaraldaswing;

/**
 *
 * @author  Thomas
 */
public class EditTimelineItemFormatDialog extends JEscapeDialog {

    static String[] ABSOLUTE_TIME_FORMATS = {"Decimal","Time (hh:mm:ss.xxx)"};
    org.exmaralda.partitureditor.jexmaralda.TimelineItemFormat timelineItemFormat;
    
    
    /** Creates new form EditTimelineItemFormatDialog */
    public EditTimelineItemFormatDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        org.exmaralda.common.helpers.Internationalizer.internationalizeDialogToolTips(this);
    }

    /** Creates new form EditTimelineItemFormatDialog */
    public EditTimelineItemFormatDialog(java.awt.Frame parent, boolean modal, org.exmaralda.partitureditor.jexmaralda.TimelineItemFormat tlif) {
        super(parent, modal);
        change = true;
        timelineItemFormat = new org.exmaralda.partitureditor.jexmaralda.TimelineItemFormat();
        initComponents();
        this.getRootPane().setDefaultButton(okButton);        
        nthNumberingTextField.setText(Short.toString(tlif.getNthNumbering()));
        nthAbsoluteTimeTextField.setText(Short.toString(tlif.getNthAbsolute()));
        if (tlif.getAbsoluteTimeFormat().equals("decimal")) {absoluteTimeFormatComboBox.setSelectedIndex(0);}
        else {absoluteTimeFormatComboBox.setSelectedIndex(1);}
        milisecondsDigitsTextField.setText(Short.toString(tlif.getMilisecondsDigits()));
        org.exmaralda.common.helpers.Internationalizer.internationalizeDialogToolTips(this);
    }
    
    public org.exmaralda.partitureditor.jexmaralda.TimelineItemFormat getTimelineItemFormat(){
        return timelineItemFormat;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        nthNumberingTextField = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        nthAbsoluteTimeTextField = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        absoluteTimeFormatComboBox = new javax.swing.JComboBox(ABSOLUTE_TIME_FORMATS);
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        milisecondsDigitsTextField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setTitle("Edit timeline item format");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel1.setLayout(new java.awt.GridLayout(4, 1));

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jLabel2.setText("Show every n-th numbering: ");
        jLabel2.setPreferredSize(new java.awt.Dimension(218, 16));
        jPanel3.add(jLabel2);

        nthNumberingTextField.setText("jTextField1");
        jPanel3.add(nthNumberingTextField);

        jPanel1.add(jPanel3);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setText("Show every n-th absolute time: ");
        jLabel3.setPreferredSize(new java.awt.Dimension(218, 16));
        jPanel4.add(jLabel3);

        nthAbsoluteTimeTextField.setText("jTextField2");
        jPanel4.add(nthAbsoluteTimeTextField);

        jPanel1.add(jPanel4);

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        jLabel4.setText("Absolute time format: ");
        jLabel4.setPreferredSize(new java.awt.Dimension(218, 16));
        jPanel5.add(jLabel4);
        jPanel5.add(absoluteTimeFormatComboBox);

        jPanel1.add(jPanel5);

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        jLabel5.setText("Miliseconds digits: ");
        jLabel5.setMinimumSize(new java.awt.Dimension(218, 16));
        jLabel5.setPreferredSize(new java.awt.Dimension(218, 16));
        jPanel6.add(jLabel5);

        milisecondsDigitsTextField.setText("jTextField3");
        jPanel6.add(milisecondsDigitsTextField);

        jPanel1.add(jPanel6);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel2.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel2.add(cancelButton);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // Add your handling code here:
        String errors = new String();
        try {timelineItemFormat.setNthNumbering(Short.parseShort(nthNumberingTextField.getText()));}
        catch (NumberFormatException nfe) {errors+="Incorrect value for 'Show every n-th numbering'\n";}
        try {timelineItemFormat.setNthAbsolute(Short.parseShort(nthAbsoluteTimeTextField.getText()));}
        catch (NumberFormatException nfe) {errors+="Incorrect value for 'Show every n-th absolute time'\n";}
        try {timelineItemFormat.setMilisecondsDigits(Short.parseShort(milisecondsDigitsTextField.getText()));}
        catch (NumberFormatException nfe) {errors+="Incorrect value for 'Miliseconds digits'\n";}
        if (errors.length()>0){
            javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
            errorDialog.showMessageDialog(this, errors);       
            return;
        }
        if (timelineItemFormat.getNthNumbering()<0) {errors+="Negative value for 'Show every n-th numbering' not allowed\n";}
        if (timelineItemFormat.getNthAbsolute()<0) {errors+="Negative value for 'Show every n-th absolute time' not allowed\n";}
        if (timelineItemFormat.getMilisecondsDigits()<0) {errors+="Negative value for 'Miliseconds digits' not allowed\n";}
        if (errors.length()>0){
            javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
            errorDialog.showMessageDialog(this, errors);       
            return;
        }
        if (absoluteTimeFormatComboBox.getSelectedIndex()==0){timelineItemFormat.setAbsoluteTimeFormat("decimal");}
        else {timelineItemFormat.setAbsoluteTimeFormat("time");}
        change = true;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // Add your handling code here:
        change = false;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        change = false;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        nthNumberingTextField.requestFocus();
    }//GEN-LAST:event_formComponentShown

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new EditTimelineItemFormatDialog(new javax.swing.JFrame(), true).show();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox absoluteTimeFormatComboBox;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JTextField milisecondsDigitsTextField;
    private javax.swing.JTextField nthAbsoluteTimeTextField;
    private javax.swing.JTextField nthNumberingTextField;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

    public boolean editTimelineItemFormat(){
        java.awt.Dimension dialogSize = this.getPreferredSize();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(screenSize.width/2 - dialogSize.width/2, screenSize.height/2 - dialogSize.height/2);
        show();
        return change;
    }

}
