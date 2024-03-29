/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import java.util.ArrayList;
import java.util.List;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.search.EventSearchResult;
import org.exmaralda.partitureditor.search.SearchResultListener;

/**
 *
 * @author thomas.schmidt
 */
public class ListEventsDialog extends javax.swing.JDialog {

    List<SearchResultListener> listenerList = new ArrayList<>();
    Tier tier;
    
    /**
     * Creates new form ListEventsDialog
     */
    public ListEventsDialog(java.awt.Frame parent, boolean modal, Tier tier) {
        super(parent, modal);
        this.tier = tier;
        initComponents();
        ListEventsListModel listModel = new ListEventsListModel(tier);
        eventList.setModel(listModel);
        displayNameLabel.setText(tier.getDisplayName());
    }
    
    public void addSearchResultListener(SearchResultListener listener){
        listenerList.add(listener);
    }
    
    public void fireSearchResult(EventSearchResult esr){
        for (SearchResultListener listener : listenerList){
            listener.processSearchResult(esr);
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
        eventListScrollPane = new javax.swing.JScrollPane();
        eventList = new javax.swing.JList<>();
        topPanel = new javax.swing.JPanel();
        displayNameLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("List events");
        setPreferredSize(new java.awt.Dimension(400, 600));

        eventListPanel.setLayout(new java.awt.BorderLayout());

        eventList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        eventList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                eventListMouseClicked(evt);
            }
        });
        eventListScrollPane.setViewportView(eventList);

        eventListPanel.add(eventListScrollPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(eventListPanel, java.awt.BorderLayout.CENTER);

        displayNameLabel.setText("jLabel1");
        topPanel.add(displayNameLabel);

        getContentPane().add(topPanel, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void eventListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_eventListMouseClicked
        if (evt.getClickCount()==2){
            int index = eventList.getSelectedIndex();
            Event event = tier.getEventAt(index);
            EventSearchResult esr = new EventSearchResult();
            esr.tierID = tier.getID();
            esr.event = event;
            esr.offset = 0;
            esr.tierName = tier.getDisplayName();
            esr.length = event.getDescription().length();
            fireSearchResult(esr);
            
        }
    }//GEN-LAST:event_eventListMouseClicked

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
            java.util.logging.Logger.getLogger(ListEventsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListEventsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListEventsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListEventsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
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
    private javax.swing.JLabel displayNameLabel;
    private javax.swing.JList<String> eventList;
    private javax.swing.JPanel eventListPanel;
    private javax.swing.JScrollPane eventListScrollPane;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
