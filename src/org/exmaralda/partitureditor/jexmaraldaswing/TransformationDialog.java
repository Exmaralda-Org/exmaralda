/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TransformationDialog.java
 *
 * Created on 11.02.2009, 14:54:19
 */

package org.exmaralda.partitureditor.jexmaraldaswing;

import java.awt.HeadlessException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.exmaralda.coma.actions.AbstractXMLSaveAsDialog;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.convert.EXMARaLDATransformationScenarios;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.ChooseStylesheetDialog;
import org.exmaralda.partitureditor.partiture.PartiturEditor;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
public class TransformationDialog extends javax.swing.JDialog {

    static String BUILT_IN_SCENARIOS = EXMARaLDATransformationScenarios.BUILT_IN_SCENARIOS;
    ExmaraldaApplication app;
    BasicTranscription transcription;

    public boolean approved = false;

    List scenarios = new ArrayList();

    /** Creates new form TransformationDialog */
    public TransformationDialog(java.awt.Frame parent, boolean modal, ExmaraldaApplication a) {
        super(parent, modal);
        initComponents();

        app = a;

        loadScenarios();
        initScenarios();
    }

    public String[] getParameters() {
        String[] returnValue = new String[5];
        returnValue[0] = (String) transformationBaseComboBox.getSelectedItem();
        returnValue[1] = (String) segmentationComboBox.getSelectedItem();
        returnValue[2] = listUnitTextField.getText();
        returnValue[3] = stylesheetTextField.getText();
        returnValue[4] = (String) outputComboBox.getSelectedItem();
        return returnValue;
    }
    
    public String[][] getXSLParameters(){
        Map<String, String> parametersMap = new HashMap<>();
        for (int row = 0; row<xslParametersTable.getModel().getRowCount(); row++){
            String parameterName = (String) xslParametersTable.getModel().getValueAt(row, 0);
            if (parameterName!=null && parameterName.length()>0){
                String parameterValue = (String) xslParametersTable.getModel().getValueAt(row, 1);
                parametersMap.put(parameterName, parameterValue);
            }
        }
        String[][] result = new String[parametersMap.keySet().size()][2];
        int i=0;
        for (String parameterName : parametersMap.keySet()){
            result[i][0] = parameterName;
            result[i][1] = parametersMap.get(parameterName);
            i++;
        }
        return result;
    }

    public BasicTranscription getTranscription() {
        return transcription;
    }

    public void setTranscription(BasicTranscription transcription) {
        this.transcription = transcription;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scenariosPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        transformationScenariosComboBox = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        parametersPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        transformationBasePanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        transformationBaseComboBox = new javax.swing.JComboBox();
        segmentationPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        segmentationComboBox = new javax.swing.JComboBox();
        listUnitPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        listUnitTextField = new javax.swing.JTextField();
        stylesheetOuterPanel = new javax.swing.JPanel();
        stylesheetPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        stylesheetTextField = new javax.swing.JTextField();
        browseStylesheetButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        xslParametersTable = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        outputPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        outputComboBox = new javax.swing.JComboBox();
        buttonPanel = new javax.swing.JPanel();
        transformButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();

        setTitle("Transformation");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        scenariosPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Scenarios"));
        scenariosPanel.setLayout(new javax.swing.BoxLayout(scenariosPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.X_AXIS));

        jLabel1.setText("Transformation Scenarios: ");
        jPanel2.add(jLabel1);

        transformationScenariosComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        transformationScenariosComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transformationScenariosComboBoxActionPerformed(evt);
            }
        });
        jPanel2.add(transformationScenariosComboBox);

        scenariosPanel.add(jPanel2);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setMaximumSize(new java.awt.Dimension(200, 50));
        descriptionTextArea.setMinimumSize(new java.awt.Dimension(200, 50));
        jScrollPane1.setViewportView(descriptionTextArea);

        jPanel3.add(jScrollPane1);

        scenariosPanel.add(jPanel3);

        getContentPane().add(scenariosPanel, java.awt.BorderLayout.NORTH);

        parametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters"));
        parametersPanel.setLayout(new javax.swing.BoxLayout(parametersPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setLayout(new java.awt.GridLayout(3, 1));

        transformationBasePanel.setLayout(new javax.swing.BoxLayout(transformationBasePanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel2.setText("Transform: ");
        jLabel2.setMaximumSize(new java.awt.Dimension(100, 14));
        jLabel2.setMinimumSize(new java.awt.Dimension(100, 14));
        jLabel2.setPreferredSize(new java.awt.Dimension(100, 14));
        transformationBasePanel.add(jLabel2);

        transformationBaseComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "basic-transcription", "segmented-transcription", "list-transcription", "Modena TEI", "TEI" }));
        transformationBaseComboBox.setMaximumSize(new java.awt.Dimension(150, 20));
        transformationBaseComboBox.setMinimumSize(new java.awt.Dimension(150, 20));
        transformationBaseComboBox.setPreferredSize(new java.awt.Dimension(150, 20));
        transformationBaseComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transformationBaseComboBoxActionPerformed(evt);
            }
        });
        transformationBasePanel.add(transformationBaseComboBox);

        jPanel1.add(transformationBasePanel);

        segmentationPanel.setLayout(new javax.swing.BoxLayout(segmentationPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setText("Segmentation: ");
        jLabel3.setMaximumSize(new java.awt.Dimension(100, 14));
        jLabel3.setMinimumSize(new java.awt.Dimension(100, 14));
        jLabel3.setPreferredSize(new java.awt.Dimension(100, 14));
        segmentationPanel.add(jLabel3);

        segmentationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "NONE", "GENERIC", "HIAT", "GAT", "cGAT_MINIMAL", "CHAT", "DIDA", "IPA", "INEL_EVENT_BASED", " " }));
        segmentationComboBox.setEnabled(false);
        segmentationComboBox.setMaximumSize(new java.awt.Dimension(150, 20));
        segmentationComboBox.setMinimumSize(new java.awt.Dimension(150, 20));
        segmentationComboBox.setPreferredSize(new java.awt.Dimension(150, 20));
        segmentationPanel.add(segmentationComboBox);

        jPanel1.add(segmentationPanel);

        listUnitPanel.setLayout(new javax.swing.BoxLayout(listUnitPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel4.setText("List unit: ");
        jLabel4.setMaximumSize(new java.awt.Dimension(100, 14));
        jLabel4.setMinimumSize(new java.awt.Dimension(100, 14));
        jLabel4.setPreferredSize(new java.awt.Dimension(100, 14));
        listUnitPanel.add(jLabel4);

        listUnitTextField.setEnabled(false);
        listUnitTextField.setMaximumSize(new java.awt.Dimension(100, 20));
        listUnitTextField.setMinimumSize(new java.awt.Dimension(100, 20));
        listUnitTextField.setPreferredSize(new java.awt.Dimension(100, 20));
        listUnitPanel.add(listUnitTextField);

        jPanel1.add(listUnitPanel);

        parametersPanel.add(jPanel1);

        stylesheetOuterPanel.setLayout(new java.awt.BorderLayout());

        stylesheetPanel.setLayout(new javax.swing.BoxLayout(stylesheetPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel5.setText("Stylesheet: ");
        jLabel5.setMaximumSize(new java.awt.Dimension(100, 14));
        jLabel5.setMinimumSize(new java.awt.Dimension(100, 14));
        jLabel5.setPreferredSize(new java.awt.Dimension(100, 14));
        stylesheetPanel.add(jLabel5);

        stylesheetTextField.setMaximumSize(new java.awt.Dimension(600, 20));
        stylesheetTextField.setMinimumSize(new java.awt.Dimension(100, 20));
        stylesheetTextField.setPreferredSize(new java.awt.Dimension(300, 20));
        stylesheetPanel.add(stylesheetTextField);

        browseStylesheetButton.setText("Browse...");
        browseStylesheetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseStylesheetButtonActionPerformed(evt);
            }
        });
        stylesheetPanel.add(browseStylesheetButton);

        stylesheetOuterPanel.add(stylesheetPanel, java.awt.BorderLayout.CENTER);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(452, 120));

        xslParametersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Parameter", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(xslParametersTable);

        jPanel4.add(jScrollPane2);

        stylesheetOuterPanel.add(jPanel4, java.awt.BorderLayout.SOUTH);

        parametersPanel.add(stylesheetOuterPanel);

        jPanel5.setLayout(new java.awt.GridLayout(1, 1));

        outputPanel.setLayout(new javax.swing.BoxLayout(outputPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel6.setText("Output: ");
        jLabel6.setMaximumSize(new java.awt.Dimension(100, 14));
        jLabel6.setMinimumSize(new java.awt.Dimension(100, 14));
        jLabel6.setPreferredSize(new java.awt.Dimension(100, 14));
        outputPanel.add(jLabel6);

        outputComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "html", "xml", "txt", "self-transformation", "other" }));
        outputComboBox.setMaximumSize(new java.awt.Dimension(150, 20));
        outputComboBox.setMinimumSize(new java.awt.Dimension(150, 20));
        outputComboBox.setPreferredSize(new java.awt.Dimension(150, 20));
        outputComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputComboBoxActionPerformed(evt);
            }
        });
        outputPanel.add(outputComboBox);

        jPanel5.add(outputPanel);

        parametersPanel.add(jPanel5);

        getContentPane().add(parametersPanel, java.awt.BorderLayout.CENTER);

        transformButton.setText("Apply transformation...");
        transformButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transformButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(transformButton);

        saveButton.setText("Save transformation...");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(saveButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initScenarios() {
        transformationScenariosComboBox.setModel(EXMARaLDATransformationScenarios.getComboBoxModel(scenarios));
        //transformationScenariosComboBox.setModel(new DefaultComboBoxModel(scenarioNames));
        transformationScenariosComboBox.setSelectedIndex(0);
    }
    private void loadScenarios() {
        try {
            scenarios = EXMARaLDATransformationScenarios.readScenarios(app);
        } catch (IOException ex) {
            Logger.getLogger(TransformationDialog.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "<html>Could not read scenarios: <br/><br/>" + ex.getLocalizedMessage() + "</html>");
        }
    }

    private void transformationBaseComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transformationBaseComboBoxActionPerformed
       int selection = transformationBaseComboBox.getSelectedIndex();
       segmentationComboBox.setEnabled(selection==1 || selection==2);
       listUnitTextField.setEnabled(selection==2);
    }//GEN-LAST:event_transformationBaseComboBoxActionPerformed

    private void outputComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputComboBoxActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_outputComboBoxActionPerformed

    private void transformationScenariosComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transformationScenariosComboBoxActionPerformed
        int index = transformationScenariosComboBox.getSelectedIndex();
        Element scenario = ((Element)(scenarios.get(index)));

        String text = scenario.getChildText("description");
        descriptionTextArea.setText(text);

        String base = scenario.getChild("input").getAttributeValue("type");
        transformationBaseComboBox.setSelectedItem(base);

        String segmentation = scenario.getChild("segmentation").getAttributeValue("type");
        segmentationComboBox.setSelectedItem(segmentation);

        String listUnit = scenario.getChildText("list-unit");
        listUnitTextField.setText(listUnit);

        String styleSheet = scenario.getChildText("stylesheet");
        stylesheetTextField.setText(styleSheet);

        String output = scenario.getChild("output").getAttributeValue("suffix");
        outputComboBox.setSelectedItem(output);
        
        
        xslParametersTable.setModel(getTableModel(null));        
        Element p = scenario.getChild("parameters");
        if (p!=null){
            List l = p.getChildren("parameter");
            xslParametersTable.setModel(getTableModel(l));
        }

    }//GEN-LAST:event_transformationScenariosComboBoxActionPerformed

    private void browseStylesheetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseStylesheetButtonActionPerformed
        ChooseStylesheetDialog dialog = new ChooseStylesheetDialog(stylesheetTextField.getText());
        if (dialog.showOpenDialog(this)==javax.swing.JFileChooser.APPROVE_OPTION){
            stylesheetTextField.setText(dialog.getSelectedFile().getAbsolutePath());
        }

    }//GEN-LAST:event_browseStylesheetButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        try {
            // Save current scenario to user scenario
            java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(app.getPreferencesNode());
            String path = settings.get("TRANSFORMATION_SCENARIOS_FILE", "");
            if (path.length() == 0) {
                //there is no user scenario file as yet
                AbstractXMLSaveAsDialog dialog = new AbstractXMLSaveAsDialog("xml");
                dialog.setDialogTitle("Choose a file for user scenarios");
                int ret = dialog.showSaveDialog(this);
                if (ret != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                Document doc = new Document(new Element("transformation-scenarios"));
                try {
                    path = dialog.getSelectedFile().getAbsolutePath();
                    IOUtilities.writeDocumentToLocalFile(path, doc);
                    settings.put("TRANSFORMATION_SCENARIOS_FILE", path);
                } catch (IOException ex) {
                    Logger.getLogger(TransformationDialog.class.getName()).log(Level.SEVERE, null, ex);     
                    JOptionPane.showMessageDialog(this, "Could not write user scenarios: \n" + ex.getLocalizedMessage());
                    return;
                }
            }
            // ask for a name and a description of the scenario
            NewTransformationScenarioDialog d = new NewTransformationScenarioDialog(null, true);
            d.setLocationRelativeTo(this);
            d.setVisible(true);
            String name = d.nameTextField.getText();
            String description = d.descriptionTextArea.getText();
            // create a new scenario element
            Element scenarioElement = new Element("tranformation-scenario");
            Element nameElement = new Element("name");
            nameElement.setText(name);
            Element descriptionElement = new Element("description");
            descriptionElement.setText(description);
            Element inputElement = new Element("input");
            inputElement.setAttribute("type", (String) (transformationBaseComboBox.getSelectedItem()));
            Element segmentationElement = new Element("segmentation");
            segmentationElement.setAttribute("type", (String) (segmentationComboBox.getSelectedItem()));
            Element listunitElement = new Element("list-unit");
            listunitElement.setText(listUnitTextField.getText());
            Element stylesheetElement = new Element("stylesheet");
            stylesheetElement.setText(stylesheetTextField.getText());
            Element outputElement = new Element("output");
            outputElement.setAttribute("suffix", (String) (outputComboBox.getSelectedItem()));
            scenarioElement.addContent(nameElement);
            scenarioElement.addContent(descriptionElement);
            scenarioElement.addContent(inputElement);
            scenarioElement.addContent(segmentationElement);
            scenarioElement.addContent(listunitElement);
            scenarioElement.addContent(stylesheetElement);
            scenarioElement.addContent(outputElement);
            
            String[][] xslParameters = getXSLParameters();
            if (xslParameters!=null && xslParameters.length>0){
                Element parametersElement = new Element("parameters");
                for (String[] p : xslParameters){
                    Element parameterElement = new Element("parameter");
                    parameterElement.setAttribute("name", p[0]);
                    parameterElement.setText(p[1]);
                    parametersElement.addContent(parameterElement);
                }
                scenarioElement.addContent(parametersElement);
            }
            
            
            
            scenarios.add(scenarioElement);

            DefaultComboBoxModel dcbm = (DefaultComboBoxModel) transformationScenariosComboBox.getModel();
            dcbm.addElement(name);
            
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    transformationScenariosComboBox.setSelectedIndex(transformationScenariosComboBox.getModel().getSize() - 1);
                }
            });
            Document userScenariosDoc = IOUtilities.readDocumentFromLocalFile(path);
            userScenariosDoc.getRootElement().addContent(scenarioElement);
            IOUtilities.writeDocumentToLocalFile(path, userScenariosDoc);
        } catch (HeadlessException | IOException | JDOMException ex) {
            Logger.getLogger(TransformationDialog.class.getName()).log(Level.SEVERE, null, ex);     
            JOptionPane.showMessageDialog(this, "Could not access user scenarios: \n" + ex.getLocalizedMessage());
        }




    }//GEN-LAST:event_saveButtonActionPerformed

    private void transformButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transformButtonActionPerformed
        approved = true;
        setVisible(false);
    }//GEN-LAST:event_transformButtonActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        transformationScenariosComboBox.requestFocus();
    }//GEN-LAST:event_formComponentShown



    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new TransformationDialog(new javax.swing.JFrame(), true, new PartiturEditor()).show();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseStylesheetButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel listUnitPanel;
    private javax.swing.JTextField listUnitTextField;
    private javax.swing.JComboBox outputComboBox;
    private javax.swing.JPanel outputPanel;
    private javax.swing.JPanel parametersPanel;
    private javax.swing.JButton saveButton;
    private javax.swing.JPanel scenariosPanel;
    private javax.swing.JComboBox segmentationComboBox;
    private javax.swing.JPanel segmentationPanel;
    private javax.swing.JPanel stylesheetOuterPanel;
    private javax.swing.JPanel stylesheetPanel;
    private javax.swing.JTextField stylesheetTextField;
    private javax.swing.JButton transformButton;
    private javax.swing.JComboBox transformationBaseComboBox;
    private javax.swing.JPanel transformationBasePanel;
    private javax.swing.JComboBox transformationScenariosComboBox;
    private javax.swing.JTable xslParametersTable;
    // End of variables declaration//GEN-END:variables

    private TableModel getTableModel(List l) {
        if (l==null){
            return new DefaultTableModel(
                new Object [][] {{null, null},{null, null},{null, null}, {null, null}},
                new String [] {"Parameter", "Value"}
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
          };
        } else {
            DefaultTableModel dfm = new DefaultTableModel();
            dfm.setColumnCount(2);
            dfm.setRowCount(l.size());
            int row=0;
            for (Object o : l){
                Element e = (Element)o;
                dfm.setValueAt(e.getAttributeValue("name"), row, 0);
                dfm.setValueAt(e.getText(), row, 1);
                row++;
            }
            dfm.setColumnIdentifiers(new String [] {"Parameter", "Value"});
            return dfm;
        }
    }

}
