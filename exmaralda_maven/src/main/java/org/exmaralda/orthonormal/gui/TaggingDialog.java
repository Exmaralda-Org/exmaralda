/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * WordNormalizationDialog.java
 *
 * Created on 04.03.2010, 16:20:32
 */

package org.exmaralda.orthonormal.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.orthonormal.lexicon.Tagset;
import org.exmaralda.orthonormal.utilities.PreferencesUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
public class TaggingDialog extends javax.swing.JDialog implements MouseListener, MouseMotionListener {

    String lemma;
    String pos;
    
    static final String[][] DEFAULT_FREQUENT_TAGS = {
        {"NGIRR","I"},  
        {"NGHES","H"},  
        {"PTKMA","M"},  
        {"PTKIFG","P"},  
        {"ADV","A"},  
        //{"PTK","P"},  
        //{"SIEITJ","I"},  
        //{"XY","X"},  
        {"VVINF","N"},  
        {"VVFIN","V"}  
        //{"SIERSP","R"}  
    };
    
    static String[][] QUICK_TAGS = DEFAULT_FREQUENT_TAGS;

    public boolean escaped = false;
    static ComboBoxModel comboBoxModel;
    
    static {
        try {
            comboBoxModel = new Tagset().getComboBoxModel();
            
            // new 28-05-2018, issue #154
            String type = PreferencesUtilities.getProperty("tagset-type", "xml");
            String xmlPath = PreferencesUtilities.getProperty("tagset-path", null);
            if (("xml-local".equals(type)) && (xmlPath!=null)){
                try {
                    Document annotationSpecificationDocument = FileIO.readDocumentFromLocalFile(xmlPath);
                    Element quickTagsElement = annotationSpecificationDocument.getRootElement().getChild("quick-tags");
                    if (quickTagsElement!=null){
                        List l = quickTagsElement.getChildren("quick-tag");
                        QUICK_TAGS = new String[l.size()][2];
                        int i=0;
                        for (Object o : l){
                            Element quickTagElement = (Element)o;
                            QUICK_TAGS[i][0] = quickTagElement.getAttributeValue("tag");
                            QUICK_TAGS[i][1] = quickTagElement.getAttributeValue("key");
                            i++;
                        }
                    }
                } catch (JDOMException ex) {
                    Logger.getLogger(TaggingDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(TaggingDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }        
            
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /** Creates new form WordNormalizationDialog
     * @param parent
     * @param modal
     * @param wordElement */
    public TaggingDialog(java.awt.Frame parent, boolean modal, Element wordElement) {
        super(parent, modal);
        initComponents();
        
        setFrequentTags(QUICK_TAGS);
        
        pack();
        
        posComboBox.setModel(comboBoxModel);
        posComboBox.setRenderer(new TagCellRenderer());
        posComboBox.setEditor(new TagComboBoxEditor());
        posComboBox.getEditor().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                posComboBoxActionPerformed(e);
            }
        });
        
        // Does not work because comboBoxModel contains JDOM elements, not strings
        // will have to understand and modify AutoCompletion
        //AutoCompletion.enable(posComboBox);
        
        wordLabel.setText(wordElement.getText());
        
        if (wordElement.getAttribute("n")!=null){
            normalizationLabel.setText(wordElement.getAttributeValue("n"));
        } else {
            normalizationLabel.setText("");            
        }
        
        
        lemma = wordElement.getText();
        if (wordElement.getAttribute("lemma")!=null){
            lemma = wordElement.getAttributeValue("lemma");
        }
        lemmaTextField.setText(lemma);

        pos = "XXX";
        if (wordElement.getAttribute("pos")!=null){
            pos = wordElement.getAttributeValue("pos");
        }
        posComboBox.setSelectedItem(pos);

        lemmaTextField.selectAll();
        lemmaTextField.requestFocus();

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                escaped=true;
                exitDialog();
            }
        };
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

    }

    public String getLemma(){
        return lemma;
    }

    public String getPOS(){
        return pos;
    }


    private void exitDialog() {
        lemma = lemmaTextField.getText();
        Object selectedTag = posComboBox.getEditor().getItem();
        //Object selectedTag = posComboBox.getSelectedItem();
        if (selectedTag instanceof String){
            pos = (String)selectedTag;
        } else {
            Element e = (Element)selectedTag;
            String tag = e.getChild("tag").getAttributeValue("name");            
        }
        this.setVisible(false);
        this.dispose();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        labelPanel = new javax.swing.JPanel();
        wordLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(30, 32767));
        normalizationLabel = new javax.swing.JLabel();
        controlsPanel = new javax.swing.JPanel();
        lemmaTextField = new javax.swing.JTextField();
        posComboBox = new javax.swing.JComboBox();
        buttonPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        mainPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 1, true));
        mainPanel.setLayout(new java.awt.BorderLayout());

        wordLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        wordLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        wordLabel.setText("jLabel1");
        wordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelPanel.add(wordLabel);
        labelPanel.add(filler1);

        normalizationLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        normalizationLabel.setForeground(java.awt.Color.red);
        normalizationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        normalizationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelPanel.add(normalizationLabel);

        mainPanel.add(labelPanel, java.awt.BorderLayout.NORTH);

        lemmaTextField.setForeground(new java.awt.Color(0, 102, 0));
        lemmaTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
        lemmaTextField.setMaximumSize(new java.awt.Dimension(400, 24));
        lemmaTextField.setPreferredSize(new java.awt.Dimension(200, 20));
        lemmaTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lemmaTextFieldActionPerformed(evt);
            }
        });
        controlsPanel.add(lemmaTextField);

        posComboBox.setEditable(true);
        posComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ADJA", "ADJD", "ADV", "APPRART" }));
        posComboBox.setMinimumSize(new java.awt.Dimension(150, 18));
        posComboBox.setPreferredSize(new java.awt.Dimension(150, 18));
        posComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                posComboBoxActionPerformed(evt);
            }
        });
        posComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                posComboBoxFocusGained(evt);
            }
        });
        controlsPanel.add(posComboBox);

        mainPanel.add(controlsPanel, java.awt.BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lemmaTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lemmaTextFieldActionPerformed
        exitDialog();
    }//GEN-LAST:event_lemmaTextFieldActionPerformed

    private void posComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_posComboBoxActionPerformed
        if (evt.getSource()==posComboBox.getEditor().getEditorComponent()){
            exitDialog();
        }
    }//GEN-LAST:event_posComboBoxActionPerformed

    private void posComboBoxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_posComboBoxFocusGained
       posComboBox.getEditor().selectAll();
    }//GEN-LAST:event_posComboBoxFocusGained



    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                TaggingDialog dialog = new TaggingDialog(new javax.swing.JFrame(), true, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
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
    private javax.swing.JPanel controlsPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel labelPanel;
    private javax.swing.JTextField lemmaTextField;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel normalizationLabel;
    private javax.swing.JComboBox posComboBox;
    private javax.swing.JLabel wordLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseClicked(MouseEvent e) {
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
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    private void setFrequentTags(String[][] tags) {
        for (final String[] combi : tags){
            String buttonText = "<html>";
            int i = combi[0].indexOf(combi[1]);
            buttonText+=combi[0].substring(0,i) + "<b>" + combi[0].substring(i,i+1) + "</b>" + combi[0].substring(i+1);
            buttonText+="</html>";
            JButton thisButton = new JButton(buttonText);
            buttonPanel.add(thisButton);
            ActionListener actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    quickTag(combi[0]);
                }
            };
            thisButton.addActionListener(actionListener);
            thisButton.setToolTipText("ALT + " + combi[1]);
            KeyStroke stroke = KeyStroke.getKeyStroke("alt " + combi[1]);
            getRootPane().registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);                        
        }
    }
    
    private void quickTag(String tag){
        posComboBox.setSelectedItem(tag);
        exitDialog();
    }

}
