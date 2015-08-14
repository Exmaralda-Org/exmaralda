/*
 * COMASearchResultListBrowser.java
 *
 * Created on 22. Januar 2008, 17:35
 */

package org.exmaralda.exakt.exmaraldaSearch.swing;

import javax.swing.*;
import java.awt.*;
import org.exmaralda.exakt.search.analyses.AnalysisInterface;
import org.exmaralda.exakt.search.analyses.ClosedCategoryListAnalysis;

/**
 *
 * @author  thomas
 */
public class COMASearchResultListBrowser extends javax.swing.JPanel {
    
    //COMASearchResultListTableModel tableModel;
    COMAKWICTableSorter tableModel;
    
    /** Creates new form COMASearchResultListBrowser */
    public COMASearchResultListBrowser() {
        initComponents();
    }
    
//    public void setSearchResultTableModel(COMASearchResultListTableModel csrltm){
    public void setSearchResultTableModel(COMAKWICTableSorter csrltm){
        setSearchResultTableModel(csrltm,0);
    }
    
    //public void setSearchResultTableModel(COMASearchResultListTableModel csrltm, int initialIndex){
    public void setSearchResultTableModel(COMAKWICTableSorter csrltm, int initialIndex){
        tableModel = csrltm;
        countTextField.setText(Integer.toString(initialIndex+1));
        setData();
    }

    public void validateData(){
        String countString = countTextField.getText();
        int position = Integer.parseInt(countString)-1;
        // changed 12-03-2012
        tableModel.setValueAt(new Boolean(selectionCheckBox.isSelected()), position, 1);
        
        int count = 0;
        for (int col=1; col<tableModel.getColumnCount(); col++){
            if (!((COMASearchResultListTableModel)(tableModel.getTableModel())).isAnalysisColumn(col-1)) continue;
            AnalysisInterface ai = ((COMASearchResultListTableModel)(tableModel.getTableModel())).getAnalysisForColumn(col-1);
            Component component = ((JPanel)(analysisPanel.getComponent(count))).getComponent(1);
            //System.out.println(component.toString());
            if (ai instanceof org.exmaralda.exakt.search.analyses.ClosedCategoryListAnalysis){
                JComboBox c = (JComboBox)component;
                tableModel.setValueAt(c.getSelectedItem(), position, col);
            } else if (ai instanceof org.exmaralda.exakt.search.analyses.FreeAnalysis){
                JTextField c = (JTextField)component;
                tableModel.setValueAt(c.getText(), position, col);
            } else if (ai instanceof org.exmaralda.exakt.search.analyses.BinaryAnalysis){
                JCheckBox c = (JCheckBox)component;
                tableModel.setValueAt(c.isSelected(), position, col);
            }
            count++;
        }        
    }
    
    public void setData(){
        String countString = countTextField.getText();
        int position;
        try {
            position = Integer.parseInt(countString)-1;
            if ((position<0) || (position>tableModel.getRowCount())) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Invalid data set number");
            ex.printStackTrace();
            countTextField.setText("1");
            setData();
            return;
        }
        
        previousButton.setEnabled(position>0);
        nextButton.setEnabled(position<tableModel.getRowCount()-1);        
        
        Boolean sel = (Boolean)(tableModel.getValueAt(position, 1));
        selectionCheckBox.setSelected(sel.booleanValue());
        
        String comm = (String)(tableModel.getValueAt(position, 2));
        communicationLabel.setText(comm);
        
        String spk = (String)(tableModel.getValueAt(position, 3));
        speakerLabel.setText(spk);
        
        String leftContext = (String)(tableModel.getValueAt(position, 4));
        String matchText = (String)(tableModel.getValueAt(position, 5));
        String rightContext = (String)(tableModel.getValueAt(position, 6));
        StringBuffer sb = new StringBuffer();
        sb.append("<p align=\"center\">");
        sb.append("<span style=\"color:rgb(100,100,100)\">");
        sb.append(leftContext);
        sb.append("</span>");
        sb.append("<span style=\"color:red;font-weight:bold\">");
        sb.append(matchText);
        sb.append("</span>");
        sb.append("<span style=\"color:rgb(100,100,100)\">");
        sb.append(rightContext);
        sb.append("</span>");
        sb.append("</p>");
        kwicEditorPane.setText(sb.toString());
        
        StringBuffer sb2 = new StringBuffer();    
        sb2.append("<html><head><style type=\"text/css\">");
        sb2.append("td.evenRow {background-color:rgb(210,210,210);font-family:Arial,sans-serif;font-size:10pt}");
        sb2.append("td.oddRow  {background-color:rgb(255,255,255);font-family:Arial,sans-serif;font-size:10pt}");
        sb2.append("</style></head><body><table border=\"1\">");
        int row = position;
        for (int col=7; col<tableModel.getColumnCount(); col++){
            sb2.append("<tr><td class=\"");
            if (col%2==0) {sb2.append("evenRow");} else {sb2.append("oddRow");}
            String colName = "";            
            if (tableModel.getColumnName(col)!=null) {colName = tableModel.getColumnName(col);}
            sb2.append("\"><b>" + colName + "</b></td>");
            sb2.append("<td class=\"");
            if (col%2==0) {sb2.append("evenRow");} else {sb2.append("oddRow");}
            String colValue = null;
            if (tableModel.getValueAt(row,col)!=null) {colValue = tableModel.getValueAt(row,col).toString();}
            sb2.append("\">" + colValue + "</td></tr>");                                
        }            
        sb2.append("</table></body></html>");
        metaInformationEditorPane.setText(sb2.toString());
        metaInformationEditorPane.setCaretPosition(0);
        
        analysisPanel.removeAll();
        for (int col=1; col<tableModel.getColumnCount(); col++){
            if (!(((COMASearchResultListTableModel)(tableModel.getTableModel())).isAnalysisColumn(col-1))) continue;
            AnalysisInterface ai = ((COMASearchResultListTableModel)(tableModel.getTableModel())).getAnalysisForColumn(col-1);
            JPanel panel = new JPanel();
            panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.X_AXIS));
            
            panel.add(new JLabel(ai.getName() + " "));
            if (ai instanceof org.exmaralda.exakt.search.analyses.ClosedCategoryListAnalysis){
                ClosedCategoryListAnalysis ccla = (ClosedCategoryListAnalysis)ai;
                JComboBox comboBox = new JComboBox(ccla.getCategories());
                comboBox.setPreferredSize(new java.awt.Dimension(300,25));
                comboBox.setMaximumSize(new java.awt.Dimension(1000,25));
                comboBox.setEditable(true);
                String v = (String)(tableModel.getValueAt(position,col));
                if (v!=null){
                    comboBox.setSelectedItem(v);
                } else {
                    comboBox.setSelectedItem("");
                }
                panel.add(comboBox);
            } else if (ai instanceof org.exmaralda.exakt.search.analyses.FreeAnalysis){
                JTextField textField = new JTextField();
                textField.setPreferredSize(new java.awt.Dimension(300,25));
                textField.setMaximumSize(new java.awt.Dimension(1000,25));
                textField.setEditable(true);
                String v = (String)(tableModel.getValueAt(position,col));
                textField.setText(v);
                panel.add(textField);
            } else if (ai instanceof org.exmaralda.exakt.search.analyses.BinaryAnalysis){
                JCheckBox checkBox = new JCheckBox();
                checkBox.setPreferredSize(new java.awt.Dimension(300,25));
                checkBox.setMaximumSize(new java.awt.Dimension(1000,25));
                Boolean v = (Boolean)(tableModel.getValueAt(position,col));
                //System.out.println("value: " + v);
                checkBox.setSelected((v!=null) && (v.booleanValue()));
                panel.add(checkBox);
            }
            analysisPanel.add(panel);
        }        
        analysisPanel.validate();
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        navigationPanel = new javax.swing.JPanel();
        previousButton = new javax.swing.JButton();
        countTextField = new javax.swing.JTextField();
        nextButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        searchResultPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        selectionCheckBox = new javax.swing.JCheckBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0));
        communication = new javax.swing.JLabel();
        communicationLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0));
        speaker = new javax.swing.JLabel();
        speakerLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        kwicEditorPane = new javax.swing.JEditorPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        metaInformationPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        metaInformationEditorPane = new javax.swing.JEditorPane();
        analysisPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        previousButton.setText("<");
        previousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });
        navigationPanel.add(previousButton);

        countTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        countTextField.setText("1");
        countTextField.setMaximumSize(new java.awt.Dimension(100, 19));
        countTextField.setMinimumSize(new java.awt.Dimension(100, 19));
        countTextField.setPreferredSize(new java.awt.Dimension(100, 19));
        navigationPanel.add(countTextField);

        nextButton.setText(">");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        navigationPanel.add(nextButton);

        add(navigationPanel, java.awt.BorderLayout.NORTH);

        mainPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        mainPanel.setLayout(new java.awt.BorderLayout());

        searchResultPanel.setLayout(new javax.swing.BoxLayout(searchResultPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        selectionCheckBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        selectionCheckBox.setToolTipText("(Un)check to (un)select this search result");
        selectionCheckBox.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 255), 5, true));
        selectionCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        selectionCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectionCheckBoxActionPerformed(evt);
            }
        });
        jPanel1.add(selectionCheckBox);
        jPanel1.add(filler2);

        communication.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        communication.setText("Communication: ");
        jPanel1.add(communication);

        communicationLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        communicationLabel.setForeground(new java.awt.Color(51, 51, 255));
        communicationLabel.setText("jLabel1");
        jPanel1.add(communicationLabel);
        jPanel1.add(filler1);

        speaker.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        speaker.setText("Speaker: ");
        jPanel1.add(speaker);

        speakerLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        speakerLabel.setForeground(new java.awt.Color(51, 51, 255));
        speakerLabel.setText("speakerLabel");
        jPanel1.add(speakerLabel);

        searchResultPanel.add(jPanel1);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(600, 80));

        kwicEditorPane.setContentType("text/html");
        kwicEditorPane.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(kwicEditorPane);

        searchResultPanel.add(jScrollPane1);

        mainPanel.add(searchResultPanel, java.awt.BorderLayout.NORTH);

        jSplitPane1.setDividerLocation(400);

        metaInformationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Metadata"));
        metaInformationPanel.setLayout(new javax.swing.BoxLayout(metaInformationPanel, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane2.setPreferredSize(new java.awt.Dimension(300, 200));

        metaInformationEditorPane.setContentType("text/html");
        jScrollPane2.setViewportView(metaInformationEditorPane);

        metaInformationPanel.add(jScrollPane2);

        jSplitPane1.setLeftComponent(metaInformationPanel);

        analysisPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Analyses"));
        analysisPanel.setPreferredSize(new java.awt.Dimension(300, 200));
        analysisPanel.setLayout(new javax.swing.BoxLayout(analysisPanel, javax.swing.BoxLayout.Y_AXIS));
        jSplitPane1.setRightComponent(analysisPanel);

        mainPanel.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        add(mainPanel, java.awt.BorderLayout.CENTER);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(closeButton);

        add(buttonPanel, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousButtonActionPerformed
         validateData();        
         int position = Integer.parseInt(countTextField.getText());
         countTextField.setText(Integer.toString(position-1));
         setData();
    }//GEN-LAST:event_previousButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
         validateData();
         int position = Integer.parseInt(countTextField.getText());
         countTextField.setText(Integer.toString(position+1));
         setData();
    }//GEN-LAST:event_nextButtonActionPerformed

    private void selectionCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectionCheckBoxActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_selectionCheckBoxActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        validateData();
        Container c = getTopLevelAncestor();
        if (c instanceof JDialog){
            ((JDialog)c).dispose();
        }
    }//GEN-LAST:event_closeButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel analysisPanel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel communication;
    private javax.swing.JLabel communicationLabel;
    private javax.swing.JTextField countTextField;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JEditorPane kwicEditorPane;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JEditorPane metaInformationEditorPane;
    private javax.swing.JPanel metaInformationPanel;
    private javax.swing.JPanel navigationPanel;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton previousButton;
    private javax.swing.JPanel searchResultPanel;
    private javax.swing.JCheckBox selectionCheckBox;
    private javax.swing.JLabel speaker;
    private javax.swing.JLabel speakerLabel;
    // End of variables declaration//GEN-END:variables
    
}
