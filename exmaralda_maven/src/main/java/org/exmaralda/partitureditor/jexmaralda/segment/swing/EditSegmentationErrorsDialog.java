/*
 * EditSegmentationErrorsDialog.java
 *
 * Created on 15. Februar 2005, 10:37
 */

package org.exmaralda.partitureditor.jexmaralda.segment.swing;

import java.util.*;
import javax.swing.event.ListSelectionListener;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.segment.*;
import org.exmaralda.partitureditor.search.*;
import org.exmaralda.partitureditor.fsm.*;
import org.xml.sax.*;
import javax.swing.SwingUtilities;

/**
 *
 * @author  thomas
 */
public class EditSegmentationErrorsDialog extends javax.swing.JDialog implements ListSelectionListener {
    
    javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
    
    AbstractSegmentation segmentationAlgorithm;
    BasicTranscription transcription;
    boolean tableIsUpdating = false;

    /** Creates new form EditSegmentationErrorsDialog */
    public EditSegmentationErrorsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();      
        errorsTable.setModel(new ErrorsTableModel(new Vector()));
        errorsTable.getSelectionModel().addListSelectionListener(this);
        formatTable();
        org.exmaralda.common.helpers.Internationalizer.internationalizeDialogToolTips(this);
    }
    
    public void setErrors (Vector errors){        
        //errorsTable = new javax.swing.JTable(new ErrorsTableModel(errors));
        //errorsTable.setModel(new ErrorsTableModel(errors));        
        tableIsUpdating = true;
        ((ErrorsTableModel)(errorsTable.getModel())).setErrors(errors);
        formatTable();
        tableIsUpdating = false;
    }
    
    public void setupDialog(BasicTranscription bt, int seg, String ptef){
        switch (seg){
            case AbstractSegmentation.HIAT_SEGMENTATION :
                segmentationAlgorithm = new HIATSegmentation(ptef);
                break;
            case AbstractSegmentation.DIDA_SEGMENTATION :
                segmentationAlgorithm = new DIDASegmentation(ptef);
                break;
            case AbstractSegmentation.CHAT_SEGMENTATION :
                segmentationAlgorithm = new CHATSegmentation(ptef);
                break;
            case AbstractSegmentation.GAT_SEGMENTATION :
                segmentationAlgorithm = new GATSegmentation(ptef);
                break;
            case AbstractSegmentation.IPA_SEGMENTATION :
                segmentationAlgorithm = new IPASegmentation(ptef);
                break;
            case AbstractSegmentation.GENERIC_SEGMENTATION :
                segmentationAlgorithm = new GenericSegmentation(ptef);
                break;
            case AbstractSegmentation.GAT_MINIMAL_SEGMENTATION :
                segmentationAlgorithm = new cGATMinimalSegmentation(ptef);
                break;
            case AbstractSegmentation.CHAT_MINIMAL_SEGMENTATION :
                segmentationAlgorithm = new CHATMinimalSegmentation();
                break;
            case AbstractSegmentation.INEL_EVENT_BASED :
                segmentationAlgorithm = new InelEventBasedSegmentation();
                break;
        }
        transcription = bt;
        updateErrors();
    }
    
    void updateErrors(){
        try{
            Vector errors = segmentationAlgorithm.getSegmentationErrors(transcription);
            setErrors(errors);
        } catch (SAXException se){
            javax.swing.JOptionPane.showMessageDialog(this, "SAX Error while reading the FSM");
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        errorsTablePanel = new javax.swing.JPanel();
        errorsTableScrollPane = new javax.swing.JScrollPane();
        errorsTable = new javax.swing.JTable();
        detailedErrorsPanel = new javax.swing.JPanel();
        buttonsPanel = new javax.swing.JPanel();
        gotoButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        separatorPanel = new javax.swing.JPanel();
        tliTierPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tierIDLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tliIDLabel = new javax.swing.JLabel();
        errorMessagePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        errorMessageTextArea = new javax.swing.JTextArea();
        processedOutputPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        processedOutputTextArea = new javax.swing.JTextArea();
        overviewPanel = new javax.swing.JPanel();
        countErrorsLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit segmentation errors");

        errorsTablePanel.setPreferredSize(new java.awt.Dimension(452, 200));
        errorsTablePanel.setLayout(new javax.swing.BoxLayout(errorsTablePanel, javax.swing.BoxLayout.LINE_AXIS));

        errorsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        errorsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        errorsTable.setPreferredSize(null);
        errorsTable.setMaximumSize(null);
        errorsTable.setMinimumSize(null);
        errorsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                errorsTableMouseClicked(evt);
            }
        });
        errorsTableScrollPane.setViewportView(errorsTable);

        errorsTablePanel.add(errorsTableScrollPane);

        getContentPane().add(errorsTablePanel, java.awt.BorderLayout.CENTER);

        detailedErrorsPanel.setLayout(new javax.swing.BoxLayout(detailedErrorsPanel, javax.swing.BoxLayout.Y_AXIS));

        buttonsPanel.setLayout(new javax.swing.BoxLayout(buttonsPanel, javax.swing.BoxLayout.LINE_AXIS));

        gotoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/jexmaralda/segment/swing/Goto.gif"))); // NOI18N
        gotoButton.setText("Go to");
        gotoButton.setEnabled(false);
        gotoButton.setMaximumSize(new java.awt.Dimension(120, 33));
        gotoButton.setPreferredSize(new java.awt.Dimension(120, 33));
        gotoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gotoButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(gotoButton);

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/jexmaralda/segment/swing/Refresh.gif"))); // NOI18N
        refreshButton.setText("Refresh");
        refreshButton.setMaximumSize(new java.awt.Dimension(120, 33));
        refreshButton.setPreferredSize(new java.awt.Dimension(120, 33));
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(refreshButton);

        detailedErrorsPanel.add(buttonsPanel);

        separatorPanel.setPreferredSize(new java.awt.Dimension(1, 20));
        separatorPanel.setLayout(new javax.swing.BoxLayout(separatorPanel, javax.swing.BoxLayout.LINE_AXIS));
        detailedErrorsPanel.add(separatorPanel);

        tliTierPanel.setLayout(new javax.swing.BoxLayout(tliTierPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        jLabel1.setText("Tier ID:      ");
        jLabel1.setForeground(new java.awt.Color(0, 51, 204));
        tliTierPanel.add(jLabel1);
        tliTierPanel.add(tierIDLabel);

        jLabel2.setText("        ");
        tliTierPanel.add(jLabel2);

        jLabel3.setFont(new java.awt.Font("MS Sans Serif", 1, 11));
        jLabel3.setForeground(new java.awt.Color(0, 51, 204));
        jLabel3.setText("Timeline item ID:      ");
        tliTierPanel.add(jLabel3);
        tliTierPanel.add(tliIDLabel);

        detailedErrorsPanel.add(tliTierPanel);

        errorMessagePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Error message", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("MS Sans Serif", 1, 11), new java.awt.Color(0, 51, 204))); // NOI18N
        errorMessagePanel.setLayout(new javax.swing.BoxLayout(errorMessagePanel, javax.swing.BoxLayout.LINE_AXIS));

        errorMessageTextArea.setEditable(false);
        errorMessageTextArea.setRows(5);
        errorMessageTextArea.setLineWrap(true);
        errorMessageTextArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(errorMessageTextArea);

        errorMessagePanel.add(jScrollPane1);

        detailedErrorsPanel.add(errorMessagePanel);

        processedOutputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Processed output", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("MS Sans Serif", 1, 11), new java.awt.Color(0, 51, 204))); // NOI18N
        processedOutputPanel.setPreferredSize(new java.awt.Dimension(114, 100));
        processedOutputPanel.setLayout(new javax.swing.BoxLayout(processedOutputPanel, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane2.setPreferredSize(new java.awt.Dimension(102, 100));

        processedOutputTextArea.setEditable(false);
        processedOutputTextArea.setRows(20);
        processedOutputTextArea.setLineWrap(true);
        processedOutputTextArea.setWrapStyleWord(true);
        jScrollPane2.setViewportView(processedOutputTextArea);

        processedOutputPanel.add(jScrollPane2);

        detailedErrorsPanel.add(processedOutputPanel);

        getContentPane().add(detailedErrorsPanel, java.awt.BorderLayout.SOUTH);

        countErrorsLabel.setText("- segmentation errors");
        overviewPanel.add(countErrorsLabel);

        getContentPane().add(overviewPanel, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void errorsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_errorsTableMouseClicked
        // TODO add your handling code here:
        if (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount()==2){
            this.gotoButtonActionPerformed(null);
        }
        
    }//GEN-LAST:event_errorsTableMouseClicked

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        // TODO add your handling code here:
        updateErrors();
        gotoButton.setEnabled(false);
        errorMessageTextArea.setText("");
        processedOutputTextArea.setText("");
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void gotoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gotoButtonActionPerformed
        // TODO add your handling code here:
        FSMException fsme = ((ErrorsTableModel)(errorsTable.getModel())).getFSMExceptionAt(errorsTable.getSelectedRow());
        final org.exmaralda.partitureditor.search.EventSearchResult esr = new org.exmaralda.partitureditor.search.EventSearchResult();
        esr.tierID = fsme.getTierID();
        Event ev = new Event();
        ev.setStart(fsme.getTLI());
        esr.event = ev;
        fireSearchResult(esr);                                

    }//GEN-LAST:event_gotoButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try{
            EditSegmentationErrorsDialog dialog = new EditSegmentationErrorsDialog(new javax.swing.JFrame(), true);
            BasicTranscription bt = new BasicTranscription("d:\\edinburgh\\d\\aaa_beispiele\\helge_neu\\helge_errors.xml");
            Vector errors = new org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation().getSegmentationErrors(bt);
            dialog.setErrors(errors);
            dialog.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private void formatTable(){
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width;
        int y = screenSize.height;
        errorsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        errorsTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        errorsTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        errorsTable.getColumnModel().getColumn(3).setPreferredWidth(600);

        countErrorsLabel.setText(Integer.toString(errorsTable.getModel().getRowCount()) + " segmentation errors");
    }
    
    public void show(){
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width;
        int y = screenSize.height;
        this.setSize(Math.round(x*0.5f),this.getPreferredSize().height);
        java.awt.Dimension dialogSize = this.getPreferredSize();
        setLocation(screenSize.width/2 - dialogSize.width/2, screenSize.height/2 - dialogSize.height/2);
        //super.setVisible(true);
        super.show();
    }
    
    @Override
    public void valueChanged(javax.swing.event.ListSelectionEvent e) {
        if (tableIsUpdating || e.getValueIsAdjusting()) return;        
        int row = errorsTable.getSelectedRow();
        gotoButton.setEnabled(row>=0);
        String tierID = (String)(errorsTable.getValueAt(row, 0));
        tierIDLabel.setText(tierID);
        String tliID = (String)(errorsTable.getValueAt(row, 1));
        tliIDLabel.setText(tliID);
        String errorMessage = (String)(errorsTable.getValueAt(row, 2));
        errorMessageTextArea.setText(errorMessage);
        String processedOutput = (String)(errorsTable.getValueAt(row, 3));
        processedOutputTextArea.setText(processedOutput);        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JLabel countErrorsLabel;
    private javax.swing.JPanel detailedErrorsPanel;
    private javax.swing.JPanel errorMessagePanel;
    private javax.swing.JTextArea errorMessageTextArea;
    private javax.swing.JTable errorsTable;
    private javax.swing.JPanel errorsTablePanel;
    private javax.swing.JScrollPane errorsTableScrollPane;
    private javax.swing.JButton gotoButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel overviewPanel;
    private javax.swing.JPanel processedOutputPanel;
    private javax.swing.JTextArea processedOutputTextArea;
    private javax.swing.JButton refreshButton;
    private javax.swing.JPanel separatorPanel;
    private javax.swing.JLabel tierIDLabel;
    private javax.swing.JLabel tliIDLabel;
    private javax.swing.JPanel tliTierPanel;
    // End of variables declaration//GEN-END:variables

    public void addSearchResultListener(SearchResultListener l) {
         listenerList.add(SearchResultListener.class, l);
    }
    
    public void removeSearchResultListener(SearchResultListener l) {
         listenerList.remove(SearchResultListener.class, l);
    }
    
    public void removeAllListeners(){
        listenerList = new javax.swing.event.EventListenerList();
    }

    protected void fireSearchResult(EventSearchResult esr) {
        System.out.println("Firing search result");
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
             if (listeners[i]==SearchResultListener.class) {                
                ((SearchResultListener)listeners[i+1]).processSearchResult(esr);             
            }
         }
    }
    
}