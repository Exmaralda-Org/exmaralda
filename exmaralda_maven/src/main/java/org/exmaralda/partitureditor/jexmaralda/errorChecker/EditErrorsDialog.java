/*
 * EditErrorsDialog.java
 *
 * Created on 14. November 2007, 17:46
 */

package org.exmaralda.partitureditor.jexmaralda.errorChecker;

import java.awt.event.MouseEvent;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import org.jdom.*;
import java.util.*;
import java.io.*;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
/**
 *
 * @author  thomas
 */
public class EditErrorsDialog extends org.exmaralda.partitureditor.jexmaraldaswing.OKCancelDialog 
                                implements  javax.swing.event.ListSelectionListener,
                                            java.awt.event.MouseListener {
    
    ErrorListModel listModel;
    Vector<ErrorCheckerListener> listenerList = new Vector<ErrorCheckerListener>();
    String currentPath = "";
    
    /** Creates new form EditErrorsDialog */
    public EditErrorsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Error List");
        initComponents();
        errorList.setCellRenderer(new ErrorListCellRenderer());
        errorList.addListSelectionListener(this);
        errorList.addMouseListener(this);
    }
    
    public void setErrorList(Document errorDocument){
        listModel = new ErrorListModel(errorDocument);
        errorList.setModel(listModel);
        countLabel.setText(listModel.getSize() + " errors in " + listModel.getTranscriptionCount() + " transcriptions");
    }
    
    public void addErrorCheckerListener(ErrorCheckerListener ecl){
        listenerList.addElement(ecl);
    }
    
    public void fireError(Element error){
        String file = error.getAttributeValue("file");
        String tier = error.getAttributeValue("tier");
        String start = error.getAttributeValue("start");
        for (ErrorCheckerListener ecl : listenerList){
            ecl.processError(file, tier, start);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        fileLabel = new javax.swing.JLabel();
        tierLabel = new javax.swing.JLabel();
        startLabel = new javax.swing.JLabel();
        listScrollPane = new javax.swing.JScrollPane();
        errorList = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        openButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        countLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel.setPreferredSize(new java.awt.Dimension(300, 400));
        mainPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));
        jPanel1.add(fileLabel);
        jPanel1.add(tierLabel);
        jPanel1.add(startLabel);

        mainPanel.add(jPanel1, java.awt.BorderLayout.SOUTH);

        listScrollPane.setPreferredSize(new java.awt.Dimension(258, 400));

        errorList.setBackground(new java.awt.Color(255, 255, 204));
        listScrollPane.setViewportView(errorList);

        mainPanel.add(listScrollPane, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Open.gif"))); // NOI18N
        openButton.setText("Open...");
        openButton.setToolTipText("Open error list");
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });
        jPanel3.add(openButton);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Save.gif"))); // NOI18N
        saveButton.setText("Save");
        saveButton.setToolTipText("Save error list");
        saveButton.setEnabled(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jPanel3.add(saveButton);

        jPanel2.add(jPanel3);
        jPanel2.add(countLabel);

        mainPanel.add(jPanel2, java.awt.BorderLayout.NORTH);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if (currentPath.length()<=0){
            
        }
        try {
            Utilities.writeErrorList(listModel.getErrorList(), currentPath);
        } catch (Exception ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setFileFilter(new ParameterFileFilter("xml", "XML file (*.xml)"));
        
        String lastPath = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor").get("error-list-path", "");
        chooser.setCurrentDirectory(new File(lastPath));

        int r = chooser.showOpenDialog(this);
        if (r==JFileChooser.APPROVE_OPTION){
            String path = chooser.getSelectedFile().getAbsolutePath();
            java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor").put("error-list-path", path);
            try {
                Document d = Utilities.readErrorList(path);
                setErrorList(d);
                currentPath = path;
                setTitle(getTitle() + " [" + path + "]");
                saveButton.setEnabled(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }            
        }
    }//GEN-LAST:event_openButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EditErrorsDialog eed = new EditErrorsDialog(new javax.swing.JFrame(), true);
                try {
                    Document d = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromLocalFile("d:\\errorlist.xml");
                    
                    eed.setErrorList(d);
                    eed.setVisible(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (JDOMException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Object o = errorList.getSelectedValue();
        if (o==null) return;
        Element error = (Element)o;
        fileLabel.setText("File: " + error.getAttributeValue("file") + "      ");
        tierLabel.setText("Tier: " + error.getAttributeValue("tier") + "      ");
        startLabel.setText("Start: " + error.getAttributeValue("start"));
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 2){
             Object item = errorList.getSelectedValue();
             //System.out.println("Double clicked on " + item);
             Element error = (Element)item;
             error.setAttribute("done", "yes");
             listModel.setDone(errorList.getSelectedIndex());
             this.fireError(error);
         }
     }

    public void setOpenSaveButtonsVisible(boolean aFlag){
        openButton.setVisible(aFlag);
        saveButton.setVisible(aFlag);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel countLabel;
    private javax.swing.JList errorList;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane listScrollPane;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton openButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel startLabel;
    private javax.swing.JLabel tierLabel;
    // End of variables declaration//GEN-END:variables
    
}