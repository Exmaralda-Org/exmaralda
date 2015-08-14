/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MetaInfoWizardPanel.java
 *
 * Created on 22.01.2010, 15:22:33
 */

package org.exmaralda.exakt.wizard.newtranscriptionwizard;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.MetaInformation;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;

/**
 *
 * @author thomas
 */
public class MetaInfoWizardPanel extends javax.swing.JPanel implements ListSelectionListener {

    Document comaDoc = null;
    String comaPath = null;

    /** Creates new form MetaInfoWizardPanel */
    public MetaInfoWizardPanel() {
        initComponents();
        comaPanel.setVisible(false);
        ChangeListener changeListener = new ChangeListener() {
          public void stateChanged(ChangeEvent changEvent) {
              enterPanel.setVisible(enterRadioButton.isSelected());
              comaPanel.setVisible(comaRadioButton.isSelected());
          }
        };
        enterRadioButton.addChangeListener(changeListener);
        comaRadioButton.addChangeListener(changeListener);
        communicationsList.addListSelectionListener(this);
    }

    public MetaInformation getData(){
        MetaInformation mi = new MetaInformation();
        if (!comaRadioButton.isSelected()){
            mi.setProjectName(projectNameTextField.getText());
            mi.setTranscriptionName(transcriptionNameTextField.getText());
            mi.setTranscriptionConvention(transcriptionConventionTextField.getText());
            mi.setComment(commentTextArea.getText());
        } else {
            Element c = getSelectedComaCommunication();
            mi.setTranscriptionName(c.getAttributeValue("Name") + " " + Integer.toString(c.getChildren("Transcription").size()+1));
            List l = c.getChild("Description").getChildren("Key");
            for (Object o : l){
                Element key = (Element)o;
                mi.getUDMetaInformation().setAttribute(key.getAttributeValue("Name"), key.getTextNormalize());
            }
        }
        System.out.println(mi.toXML());
        return mi;
    }

    public Element getSelectedComaCommunication(){
        if (!(comaRadioButton.isSelected())) return null;
        return (Element)(communicationsList.getSelectedValue());
    }

    public String getComaPath(){
        if (!(comaRadioButton.isSelected())) return null;
        //return comaFileTextField.getText();
        return comaPath;
    }

    public Document getComaDocument(){
        return comaDoc;
    }

    public void setComaPath(String path) throws JDOMException, IOException{
        comaDoc = IOUtilities.readDocumentFromLocalFile(path);
        comaPath = path;
        File file = new File(path);
        comaFileTextField.setText(file.getName());
        comaFileTextField.setToolTipText(file.getAbsolutePath());
        Iterator comms = comaDoc.getDescendants(new ElementFilter("Communication"));
        final Vector<Element> commElements = new Vector<Element>();
        while (comms.hasNext()){
            Element e = (Element)(comms.next());
            commElements.addElement(e);
        }
        AbstractListModel listModel = new AbstractListModel(){
            public int getSize() {
                return commElements.size();
            }
            public Object getElementAt(int index) {
                return commElements.elementAt(index);
            }
        };
        DefaultListCellRenderer listRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel)(super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus));
                Element e = (Element)value;
                label.setText(e.getAttributeValue("Name"));
                return label;
            }
        };
        communicationsList.setModel(listModel);
        communicationsList.setCellRenderer(listRenderer);
        if (listModel.getSize()>0){
            communicationsList.setSelectedIndex(0);
        }        
    }

    void setComaUse() {
        comaRadioButton.setSelected(true);
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
        chooseSourcePanel = new javax.swing.JPanel();
        enterRadioButton = new javax.swing.JRadioButton();
        comaRadioButton = new javax.swing.JRadioButton();
        mainPanel = new javax.swing.JPanel();
        enterPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        projectNameTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        transcriptionNameTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        transcriptionConventionTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        commentTextArea = new javax.swing.JTextArea();
        comaPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        comaFileTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        communicationsList = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        comaDataEditorPane = new javax.swing.JEditorPane();

        setLayout(new java.awt.BorderLayout());

        chooseSourcePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Source"));
        chooseSourcePanel.setLayout(new javax.swing.BoxLayout(chooseSourcePanel, javax.swing.BoxLayout.Y_AXIS));

        buttonGroup1.add(enterRadioButton);
        enterRadioButton.setSelected(true);
        enterRadioButton.setText("Enter metadata");
        chooseSourcePanel.add(enterRadioButton);

        buttonGroup1.add(comaRadioButton);
        comaRadioButton.setText("Get metadata from Coma file");
        chooseSourcePanel.add(comaRadioButton);

        add(chooseSourcePanel, java.awt.BorderLayout.NORTH);

        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.LINE_AXIS));

        enterPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridLayout(3, 2));

        jLabel1.setText("Project Name: ");
        jPanel1.add(jLabel1);
        jPanel1.add(projectNameTextField);

        jLabel2.setText("Transcription Name: ");
        jPanel1.add(jLabel2);

        transcriptionNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transcriptionNameTextFieldActionPerformed(evt);
            }
        });
        jPanel1.add(transcriptionNameTextField);

        jLabel3.setText("Transcription Convention: ");
        jPanel1.add(jLabel3);
        jPanel1.add(transcriptionConventionTextField);

        enterPanel.add(jPanel1, java.awt.BorderLayout.NORTH);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Comment"));

        commentTextArea.setColumns(20);
        commentTextArea.setRows(5);
        jScrollPane1.setViewportView(commentTextArea);

        enterPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        mainPanel.add(enterPanel);

        comaPanel.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jLabel4.setText("Coma file: ");
        jPanel2.add(jLabel4);

        comaFileTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
        comaFileTextField.setPreferredSize(new java.awt.Dimension(300, 20));
        jPanel2.add(comaFileTextField);

        browseButton.setText("Browse...");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });
        jPanel2.add(browseButton);

        comaPanel.add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane2.setViewportView(communicationsList);

        jPanel3.add(jScrollPane2);

        jScrollPane3.setPreferredSize(new java.awt.Dimension(258, 130));

        comaDataEditorPane.setContentType("text/html");
        jScrollPane3.setViewportView(comaDataEditorPane);

        jPanel3.add(jScrollPane3);

        comaPanel.add(jPanel3, java.awt.BorderLayout.CENTER);

        mainPanel.add(comaPanel);

        add(mainPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setFileFilter(new ParameterFileFilter("coma", "Coma files (*.coma)"));
        if (fileDialog.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            try {
                setComaPath(fileDialog.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
}//GEN-LAST:event_browseButtonActionPerformed

    private void transcriptionNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transcriptionNameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_transcriptionNameTextFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel chooseSourcePanel;
    private javax.swing.JEditorPane comaDataEditorPane;
    private javax.swing.JTextField comaFileTextField;
    private javax.swing.JPanel comaPanel;
    private javax.swing.JRadioButton comaRadioButton;
    private javax.swing.JTextArea commentTextArea;
    private javax.swing.JList communicationsList;
    private javax.swing.JPanel enterPanel;
    private javax.swing.JRadioButton enterRadioButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField projectNameTextField;
    private javax.swing.JTextField transcriptionConventionTextField;
    private javax.swing.JTextField transcriptionNameTextField;
    // End of variables declaration//GEN-END:variables

    public void valueChanged(ListSelectionEvent e) {
        Element comm = (Element)communicationsList.getSelectedValue();
        if (comm!=null){
            String text = "<h2 style='font-family:sans-serif;font-size:11pt'>" + comm.getAttributeValue("Name")+ "</h2><p style='font-family:sans-serif;font-size:11pt'/> ";
            List l = comm.getChild("Description").getChildren("Key");
            for (Object o : l){
                Element key = (Element)o;
                text+="<b>" + key.getAttributeValue("Name") + ": </b>" + key.getTextNormalize() + "<br/>";
            }
            text+="</p>";
            
            comaDataEditorPane.setText(text);
            comaDataEditorPane.setCaretPosition(0);
        }
    }

}
