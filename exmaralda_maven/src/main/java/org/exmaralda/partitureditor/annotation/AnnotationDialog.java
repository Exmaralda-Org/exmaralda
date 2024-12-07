/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AnnotationDialog.java
 *
 * Created on 14.10.2009, 17:22:12
 */

package org.exmaralda.partitureditor.annotation;

import com.klg.jclass.table.JCSelectEvent;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.partiture.PartitureCellStringEditor;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
public class AnnotationDialog extends javax.swing.JDialog implements com.klg.jclass.table.JCSelectListener {

    AnnotationSpecification annotationSpecification = new AnnotationSpecification();

    PartitureTableWithActions partitur;
    int selectionStartRow;
    int selectionEndRow;
    int selectionStartCol;
    int selectionEndCol;

    /** Creates new form AnnotationDialog */
    public AnnotationDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        DefaultComboBoxModel cbm = new DefaultComboBoxModel();
        for (String[] s : AnnotationSpecification.BUILT_IN_ANNOTATION_SPECIFICATIONS){
            cbm.addElement(s[0]);
        }
        internalAnnotationSpecificationCombBox.setModel(cbm);
        
        ActionListener actionListener2 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                gotoNextSearchResult();
            }

        };
        KeyStroke stroke =  KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        getRootPane().registerKeyboardAction(actionListener2, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        try {
            loadInternal();
        } catch (JDOMException | IOException ex) {
            Logger.getLogger(AnnotationDialog.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(rootPane, "Error reading internal specification:\n" + ex);            
        }
    }

    private void gotoNextSearchResult() {
        if (partitur!=null){
            partitur.findNext();
        }
    }

    public void setPartitur(PartitureTableWithActions partitur) {
        this.partitur = partitur;
        partitur.addSelectListener(this);


        // try to open the last annotation specification
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor");
        String path = settings.get("LAST-ANNOTATION-FILE", "");
        if (path.length()>0){
            try {
                File f = new File(path);
                readSpecification(f);
            } catch (JDOMException | IOException ex) {
                Logger.getLogger(AnnotationDialog.class.getName()).log(Level.SEVERE, null, ex);
                settings.put("LAST-ANNOTATION-FILE", "");
            }
        }

    }

    private ExmaraldaApplication getApplication() {
        if (partitur==null) return null;
        Object o = partitur.getTopLevelAncestor();
        if (o instanceof ExmaraldaApplication){
            ExmaraldaApplication exmaraldaApplication = (ExmaraldaApplication)o;
            //System.out.println("Jippie!");
            return exmaraldaApplication;
        }
        return null;
    }

    void processDescription(String description) {
        descriptionEditorPane.setText(description);
    }

    void processTag(String tag) {
        //System.out.println("TAG: " + tag);
        if (partitur.isEditing){
            PartitureCellStringEditor editor = (PartitureCellStringEditor)(partitur.getEditingComponent());
            editor.replaceSelection(tag);
            if (autoJumpCheckBox.isSelected()){
                autoJump();
            }
        } else if (partitur.aSeriesOfCellsIsSelected){
            Tier tier = partitur.getModel().getTier(selectionStartRow);
            // make sure to add the cell span to the selectionEndCol
            int add = partitur.getModel().getCellSpan(selectionStartRow, selectionEndCol);
            int lastColumn = Math.min(
                    selectionEndCol+add,
                    partitur.getModel().getTranscription().getBody().getCommonTimeline().getNumberOfTimelineItems()-1);
            for (int pos=selectionStartCol; pos<lastColumn; pos++){
                TimelineItem tli = partitur.getModel().getTimelineItem(pos);
                if (tier.containsEventAtStartPoint(tli.getID())){
                    //tier.removeEventAtStartPoint(tli.getID());
                    partitur.getModel().deleteEvent(selectionStartRow, pos);
                }
            }
            Event newEvent = new Event();
            newEvent.setStart(partitur.getModel().getTimelineItem(selectionStartCol).getID());
            newEvent.setEnd(partitur.getModel().getTimelineItem(lastColumn).getID());
            newEvent.setDescription(tag);
            tier.addEvent(newEvent);
            partitur.getModel().fireEventAdded(selectionStartRow, selectionStartCol, lastColumn);
            partitur.getModel().fireCellFormatChanged(selectionStartRow, selectionStartCol);
            if (autoJumpCheckBox.isSelected()){
                partitur.setNewSelection(selectionStartRow, lastColumn, true);
            }
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nextTypeButtonGroup = new javax.swing.ButtonGroup();
        topPanel = new javax.swing.JPanel();
        annotationSpecificationPanel = new javax.swing.JPanel();
        openExternalPanel = new javax.swing.JPanel();
        openAnnotationSpecificationButton = new javax.swing.JButton();
        openInternalPanel = new javax.swing.JPanel();
        internalAnnotationSpecificationCombBox = new javax.swing.JComboBox<>();
        openInternalButton = new javax.swing.JButton();
        filenamePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        currentFileLabel = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        autoJumpPanel = new javax.swing.JPanel();
        jumpButton = new javax.swing.JButton();
        autoJumpCheckBox = new javax.swing.JCheckBox();
        nextEventRadioButton = new javax.swing.JRadioButton();
        nextNonEmptyEventRadioButton = new javax.swing.JRadioButton();
        annotationSetsTabbedPane = new javax.swing.JTabbedPane();
        descriptionScrollPane = new javax.swing.JScrollPane();
        descriptionEditorPane = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Annotation Panel");
        setPreferredSize(new java.awt.Dimension(450, 610));

        topPanel.setLayout(new java.awt.BorderLayout());

        annotationSpecificationPanel.setPreferredSize(new java.awt.Dimension(500, 50));

        openAnnotationSpecificationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Open.gif"))); // NOI18N
        openAnnotationSpecificationButton.setText("Open specification...");
        openAnnotationSpecificationButton.setToolTipText("Open an annotation specification file");
        openAnnotationSpecificationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAnnotationSpecificationButtonActionPerformed(evt);
            }
        });
        openExternalPanel.add(openAnnotationSpecificationButton);

        annotationSpecificationPanel.add(openExternalPanel);

        openInternalPanel.setLayout(new javax.swing.BoxLayout(openInternalPanel, javax.swing.BoxLayout.LINE_AXIS));

        internalAnnotationSpecificationCombBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        internalAnnotationSpecificationCombBox.setToolTipText("Built-in annotation specifications");
        internalAnnotationSpecificationCombBox.setMinimumSize(new java.awt.Dimension(150, 33));
        internalAnnotationSpecificationCombBox.setPreferredSize(new java.awt.Dimension(220, 33));
        internalAnnotationSpecificationCombBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                internalAnnotationSpecificationCombBoxActionPerformed(evt);
            }
        });
        openInternalPanel.add(internalAnnotationSpecificationCombBox);

        openInternalButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/actions/document-open.png"))); // NOI18N
        openInternalButton.setToolTipText("Load built-in annotation specification");
        openInternalButton.setMaximumSize(new java.awt.Dimension(49, 33));
        openInternalButton.setMinimumSize(new java.awt.Dimension(49, 33));
        openInternalButton.setPreferredSize(new java.awt.Dimension(49, 33));
        openInternalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openInternalButtonActionPerformed(evt);
            }
        });
        openInternalPanel.add(openInternalButton);

        annotationSpecificationPanel.add(openInternalPanel);

        topPanel.add(annotationSpecificationPanel, java.awt.BorderLayout.CENTER);

        jLabel1.setText("Current File: ");
        filenamePanel.add(jLabel1);

        currentFileLabel.setForeground(java.awt.SystemColor.activeCaption);
        currentFileLabel.setText("none");
        filenamePanel.add(currentFileLabel);

        topPanel.add(filenamePanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

        mainPanel.setLayout(new java.awt.BorderLayout());

        jumpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/forward.png"))); // NOI18N
        jumpButton.setText("Jump");
        jumpButton.setToolTipText("Jump to next event without making changes to the current one");
        jumpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jumpButtonActionPerformed(evt);
            }
        });
        autoJumpPanel.add(jumpButton);

        autoJumpCheckBox.setText("Auto jump to: ");
        autoJumpCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoJumpCheckBoxActionPerformed(evt);
            }
        });
        autoJumpPanel.add(autoJumpCheckBox);

        nextTypeButtonGroup.add(nextEventRadioButton);
        nextEventRadioButton.setSelected(true);
        nextEventRadioButton.setText("Next event");
        nextEventRadioButton.setEnabled(false);
        autoJumpPanel.add(nextEventRadioButton);

        nextTypeButtonGroup.add(nextNonEmptyEventRadioButton);
        nextNonEmptyEventRadioButton.setText("Next non-empty event");
        nextNonEmptyEventRadioButton.setEnabled(false);
        autoJumpPanel.add(nextNonEmptyEventRadioButton);

        mainPanel.add(autoJumpPanel, java.awt.BorderLayout.SOUTH);

        annotationSetsTabbedPane.setPreferredSize(new java.awt.Dimension(200, 400));
        mainPanel.add(annotationSetsTabbedPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        descriptionScrollPane.setPreferredSize(new java.awt.Dimension(250, 100));
        descriptionScrollPane.setViewportView(descriptionEditorPane);

        getContentPane().add(descriptionScrollPane, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openAnnotationSpecificationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openAnnotationSpecificationButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new ParameterFileFilter("xml", "Annotation Specification File (*.xml)"));
        ExmaraldaApplication ea = getApplication();
        if (ea!=null){
            java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(ea.getPreferencesNode());
            String path = settings.get("LAST-ANNOTATION-FILE", System.getProperty("user.dir"));
            fc.setSelectedFile(new File(path));
        }
        int result = fc.showOpenDialog(fc);
        if (result==JFileChooser.APPROVE_OPTION){
            try {
                readSpecification(fc.getSelectedFile());
                if (ea!=null){
                    java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(ea.getPreferencesNode());
                    settings.put("LAST-ANNOTATION-FILE", fc.getSelectedFile().getAbsolutePath());
                }
            } catch (JDOMException | IOException ex) {
                Logger.getLogger(AnnotationDialog.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(rootPane, ex.getLocalizedMessage());
            }
        }
    }//GEN-LAST:event_openAnnotationSpecificationButtonActionPerformed

    private void internalAnnotationSpecificationCombBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_internalAnnotationSpecificationCombBoxActionPerformed
        try {
            loadInternal();
        } catch (JDOMException | IOException ex) {
            Logger.getLogger(AnnotationDialog.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(rootPane, "Error reading internal specification:\n" + ex);
        }
    }//GEN-LAST:event_internalAnnotationSpecificationCombBoxActionPerformed

    private void openInternalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openInternalButtonActionPerformed
        try {
            this.loadInternal();
        } catch (JDOMException | IOException ex) {
            Logger.getLogger(AnnotationDialog.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(rootPane, "Error reading internal specification:\n" + ex);
        }
    }//GEN-LAST:event_openInternalButtonActionPerformed

    private void autoJumpCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoJumpCheckBoxActionPerformed
        nextEventRadioButton.setEnabled(autoJumpCheckBox.isSelected());
        nextNonEmptyEventRadioButton.setEnabled(autoJumpCheckBox.isSelected());
    }//GEN-LAST:event_autoJumpCheckBoxActionPerformed

    private void jumpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jumpButtonActionPerformed
        autoJump();
    }//GEN-LAST:event_jumpButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                AnnotationDialog dialog = new AnnotationDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JTabbedPane annotationSetsTabbedPane;
    private javax.swing.JPanel annotationSpecificationPanel;
    private javax.swing.JCheckBox autoJumpCheckBox;
    private javax.swing.JPanel autoJumpPanel;
    private javax.swing.JLabel currentFileLabel;
    private javax.swing.JEditorPane descriptionEditorPane;
    private javax.swing.JScrollPane descriptionScrollPane;
    private javax.swing.JPanel filenamePanel;
    private javax.swing.JComboBox<String> internalAnnotationSpecificationCombBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton jumpButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JRadioButton nextEventRadioButton;
    private javax.swing.JRadioButton nextNonEmptyEventRadioButton;
    private javax.swing.ButtonGroup nextTypeButtonGroup;
    private javax.swing.JButton openAnnotationSpecificationButton;
    private javax.swing.JPanel openExternalPanel;
    private javax.swing.JButton openInternalButton;
    private javax.swing.JPanel openInternalPanel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void beforeSelect(JCSelectEvent arg0) {

    }

    @Override
    public void select(JCSelectEvent evt) {
        if (!isShowing()) return;
        selectionStartRow = Math.min(evt.getStartRow(), evt.getEndRow());
        selectionEndRow = Math.max(evt.getStartRow(), evt.getEndRow());
        selectionStartCol = Math.min(evt.getStartColumn(), evt.getEndColumn());
        selectionEndCol = Math.max(evt.getStartColumn(), evt.getEndColumn());
        if ((selectionStartRow>0) && (selectionStartRow==selectionEndRow)){
            String tierCat = partitur.getModel().getTier(selectionStartRow).getCategory();
            int index = annotationSpecification.exmaraldaTierCategories.indexOf(tierCat);
            if (index>=0){
                // select the tab with the annotation set for that tier category
                annotationSetsTabbedPane.setSelectedIndex(index);

                // check if there are dependencies
                Category annotationSet = annotationSpecification.getAnnotationSet(tierCat);
                if (annotationSet.dependson!=null){
                    Event matchingEvent = partitur.getModel().getTranscription().getBody().
                            findCorrespondingAnnotation(selectionStartRow,selectionStartCol,annotationSet.dependson);
                    if (matchingEvent!=null){
                        String parentTag = matchingEvent.getDescription();
                        Category matchingCategory = annotationSet.findMatchingCategory(parentTag);
                        if (matchingCategory!=null){
                            // collapse the tree and expand only the matching node
                            ((AnnotationSetPanel)(annotationSetsTabbedPane.getSelectedComponent())).showNode(matchingCategory);
                        }
                    }
                }

            }
        }

    }

    @Override
    public void afterSelect(JCSelectEvent arg0) {

    }

    private void readSpecification(File selectedFile) throws JDOMException, IOException {
        annotationSpecification.read(selectedFile);
        setupSpecification();
        currentFileLabel.setText(selectedFile.getName());
        currentFileLabel.setToolTipText(selectedFile.getAbsolutePath());        
    }

    private void loadInternal() throws JDOMException, IOException {
        int index = internalAnnotationSpecificationCombBox.getSelectedIndex();
        String name = AnnotationSpecification.BUILT_IN_ANNOTATION_SPECIFICATIONS[index][0];
        String path = AnnotationSpecification.BUILT_IN_ANNOTATION_SPECIFICATIONS[index][1];
        annotationSpecification.read(path);
        setupSpecification();
        currentFileLabel.setText(name);
        currentFileLabel.setToolTipText(path);        
    }

    private void setupSpecification() {
        annotationSetsTabbedPane.removeAll();
        for (String category : annotationSpecification.exmaraldaTierCategories){
            AnnotationSetPanel asp = new AnnotationSetPanel(annotationSpecification.getAnnotationSet(category));
            asp.setAnnotationDialog(this);
            annotationSetsTabbedPane.add(category, asp);
        }
        int count=0;
        for (String shortcut : annotationSpecification.keyboardShortcuts.keySet()){
            count++;
            final String tag = annotationSpecification.keyboardShortcuts.get(shortcut);

            AbstractAction action = new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    //System.out.println("ACTION " + tag);
                    processTag(tag);
                }
            };

            String actionName = "Action" + Integer.toString(count);
            getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(shortcut), actionName);
            getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(shortcut), actionName);
            getRootPane().getActionMap().put(actionName, action);

            //System.out.println("*****" + actionName + " / " + shortcut + " " + tag);


        }
    }

    private void autoJump() {
        partitur.commitEdit(true);
        // changed 09-09-2010: need to consider cells with span
        int span = partitur.getModel().getCellSpan(selectionStartRow, selectionEndCol);
        if (selectionEndCol+span<partitur.getModel().getTranscription().getBody().getCommonTimeline().getNumberOfTimelineItems()-1){
            if (nextEventRadioButton.isSelected()){
                partitur.setNewSelection(selectionStartRow, selectionEndCol+span, true);
            } else {
                partitur.findEvent();
            }
            // new 03-01-2023 for #356
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    PartitureCellStringEditor newEditor = (PartitureCellStringEditor)(partitur.getEditingComponent());
                    newEditor.selectAll();                        
                }
            });
        } else {
            JOptionPane.showMessageDialog(partitur, "Reached end of tier");
        }
    }

}
