/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import com.klg.jclass.table.JCTableDataEvent;
import com.klg.jclass.table.JCTableDataListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.partiture.AbstractTranscriptionTableModel;
import org.exmaralda.partitureditor.search.EventSearchResult;
import org.exmaralda.partitureditor.search.SearchResultListener;
import org.exmaralda.partitureditor.sound.PlaySampleChainThread;
import org.exmaralda.partitureditor.sound.Playable;

// new for #382


/**
 *
 * @author thomas.schmidt
 */
public class ListEventsTableDialog extends javax.swing.JDialog implements JCTableDataListener, TableModelListener {

    List<SearchResultListener> searchResultListenerList = new ArrayList<>();
    List<JCTableDataListener> tableDataListenerList = new ArrayList<>();
    BasicTranscription transcription;
    Tier tier;
    ListEventsTableModel tableModel;
    
    int rowIndex;
    Playable player;
    
    /**
     * Creates new form ListEventsDialog
     * @param parent
     * @param modal
     * @param transcription
     * @param tier
     * @param rowIndex
     */
    public ListEventsTableDialog(java.awt.Frame parent, boolean modal, BasicTranscription transcription, Tier tier, int rowIndex) {
        super(parent, modal);
        this.tier = tier;
        this.transcription = transcription;
        initComponents();
        tableModel = new ListEventsTableModel(transcription, tier, rowIndex);
        tableModel.addTableModelListener(this);
        tableModel.fireTableDataChanged();
        eventTable.setModel(tableModel);
        eventTable.setDefaultRenderer(Double.class, new ListEventsTableCellRenderer());
        eventTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        eventTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        eventTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        eventTable.getColumnModel().getColumn(3).setMinWidth(250);
        eventTable.setRowHeight(18);
        displayNameLabel.setText(tier.getDisplayName());
        this.rowIndex = rowIndex;
        
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(
                new ActionListener() {     
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        dispose();
                    }
                }, 
                stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        KeyStroke playKeystroke
            = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_DOWN_MASK);
        getRootPane().registerKeyboardAction(
                new ActionListener() {     
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        playButtonActionPerformed(actionEvent);
                    }
                }, 
                playKeystroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        eventTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent e) {
                tableSelectionChanged(e);
            }

            private void tableSelectionChanged(ListSelectionEvent e) {
                if (e.getFirstIndex()>=0){
                    playButton.setEnabled(true);
                }
            }
            
        });
        
    }
    
    public void setPlayer(Playable player){
        this.player = player;
    }
    
    public void addSearchResultListener(SearchResultListener listener){
        searchResultListenerList.add(listener);
    }
    
    public void addTableDataListener(JCTableDataListener listener){
        tableDataListenerList.add(listener);
    }


    public void fireSearchResult(EventSearchResult esr){
        for (SearchResultListener listener : searchResultListenerList){
            listener.processSearchResult(esr);
        }
    }

    public void fireDataChanged(JCTableDataEvent evt){
        for (JCTableDataListener listener : tableDataListenerList){
            listener.dataChanged(evt);
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        eventListPanel = new javax.swing.JPanel();
        ebentTableScrollPane = new javax.swing.JScrollPane();
        eventTable = new javax.swing.JTable();
        topPanel = new javax.swing.JPanel();
        displayNameLabel = new javax.swing.JLabel();
        bottomPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        rightSidePanel = new javax.swing.JPanel();
        playButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("List events");

        eventListPanel.setLayout(new java.awt.BorderLayout());

        ebentTableScrollPane.setPreferredSize(new java.awt.Dimension(620, 400));

        eventTable.setAutoCreateRowSorter(true);
        eventTable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        eventTable.setModel(new javax.swing.table.DefaultTableModel(
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
        eventTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        eventTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        eventTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                eventTableMouseClicked(evt);
            }
        });
        ebentTableScrollPane.setViewportView(eventTable);

        eventListPanel.add(ebentTableScrollPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(eventListPanel, java.awt.BorderLayout.CENTER);

        displayNameLabel.setText("jLabel1");
        topPanel.add(displayNameLabel);

        getContentPane().add(topPanel, java.awt.BorderLayout.PAGE_START);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        bottomPanel.add(closeButton);

        getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);

        rightSidePanel.setLayout(new javax.swing.BoxLayout(rightSidePanel, javax.swing.BoxLayout.Y_AXIS));

        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/22x22/actions/media-playback-start.png"))); // NOI18N
        playButton.setToolTipText("Play the selected event(s)");
        playButton.setEnabled(false);
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });
        rightSidePanel.add(playButton);

        getContentPane().add(rightSidePanel, java.awt.BorderLayout.LINE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void eventTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_eventTableMouseClicked
        if (evt.getClickCount()==2){
            int index = eventTable.convertRowIndexToModel(eventTable.getSelectedRow());
            
            Event event = tier.getEventAt(index);
            EventSearchResult esr = new EventSearchResult();
            esr.tierID = tier.getID();
            esr.event = event;
            esr.offset = 0;
            esr.tierName = tier.getDisplayName();
            esr.length = event.getDescription().length();
            fireSearchResult(esr);
        }
    }//GEN-LAST:event_eventTableMouseClicked

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        if (player==null) return;
        if (eventTable.getSelectedRow()<0) return;
        player.stopPlayback();
        
        int[] rowIndices = eventTable.getSelectedRows();
        Timeline timeline = transcription.getBody().getCommonTimeline();
        List<double[]> startEndPairs = new ArrayList<>();
        for (int row : rowIndices){
            try {
                System.out.println("Row " + row);
                int modelIndex = eventTable.convertRowIndexToModel(row);
                Event event = tier.getEventAt(modelIndex);
                double startTime = timeline.getTimelineItemWithID(event.getStart()).getTime();
                double endTime = timeline.getTimelineItemWithID(event.getEnd()).getTime();
                double[] pair = {startTime, endTime};
                startEndPairs.add(pair);
            } catch (JexmaraldaException ex) {
                Logger.getLogger(ListEventsTableDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        PlaySampleChainThread playSampleChainThread = new PlaySampleChainThread(player, startEndPairs, 1000);
        playSampleChainThread.start();
        
        
    }//GEN-LAST:event_playButtonActionPerformed

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
            java.util.logging.Logger.getLogger(ListEventsTableDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListEventsTableDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListEventsTableDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListEventsTableDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                /*ListEventsDialog dialog = new ListEventsDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);*/
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel displayNameLabel;
    private javax.swing.JScrollPane ebentTableScrollPane;
    private javax.swing.JPanel eventListPanel;
    private javax.swing.JTable eventTable;
    private javax.swing.JButton playButton;
    private javax.swing.JPanel rightSidePanel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void dataChanged(JCTableDataEvent evt) {
        int command = evt.getCommand();
        switch(command){
            case JCTableDataEvent.RESET :
                this.dispose();
                break;
                
            case JCTableDataEvent.CHANGE_VALUE : 
                if (evt.getRow()==rowIndex){
                    tableModel.fireTableDataChanged();
                }
                break;
                
            case JCTableDataEvent.REMOVE_ROW :
                if (evt.getRow()==rowIndex){
                    dispose();
                }
                break;
                
            case JCTableDataEvent.ADD_COLUMN :
                tableModel.fireTableDataChanged();
                break;
            
            case JCTableDataEvent.REMOVE_COLUMN :
                tableModel.fireTableDataChanged();
                break;
                
            case JCTableDataEvent.CHANGE_ROW_LABEL :
                if (evt.getRow()==rowIndex){
                    tableModel.fireTableDataChanged();                    
                }
                break;
                
                               
            case AbstractTranscriptionTableModel.ROWS_SWAPPED :
                break;
                
            case AbstractTranscriptionTableModel.AREA_CHANGED :
                break;
            
            case AbstractTranscriptionTableModel.CELL_SPAN_CHANGED :
                if (evt.getRow()==rowIndex){
                    tableModel.fireTableDataChanged();
                }
                break;
        }


    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getType()==TableModelEvent.UPDATE){
            JCTableDataEvent event = new JCTableDataEvent(this, rowIndex, e.getFirstRow(), 0, 0, JCTableDataEvent.CHANGE_VALUE );
            this.fireDataChanged(event);
        }
    }
}
