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
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
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
        ActionListener actionListener2 = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                gotoNextSearchResult();
            }

        };
        KeyStroke stroke =  KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        getRootPane().registerKeyboardAction(actionListener2, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
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
            } catch (JDOMException ex) {
                ex.printStackTrace();
                settings.put("LAST-ANNOTATION-FILE", "");
            } catch (IOException ex) {
                ex.printStackTrace();
                settings.put("LAST-ANNOTATION-FILE", "");
            }
        }

    }

    private ExmaraldaApplication getApplication() {
        if (partitur==null) return null;
        Object o = partitur.getTopLevelAncestor();
        if (o instanceof ExmaraldaApplication){
            //System.out.println("Jippie!");
            return (ExmaraldaApplication)o;
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
                partitur.commitEdit(true);
                // changed 09-09-2010: need to consider cells with span
                int span = partitur.getModel().getCellSpan(selectionStartRow, selectionEndCol);
                if (selectionEndCol+span<partitur.getModel().getTranscription().getBody().getCommonTimeline().getNumberOfTimelineItems()-1){
                    partitur.setNewSelection(selectionStartRow, selectionEndCol+span, true);
                }
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

        topPanel = new javax.swing.JPanel();
        annotationSpecificationPanel = new javax.swing.JPanel();
        openAnnotationSpecificationButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        currentFileLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        autoJumpCheckBox = new javax.swing.JCheckBox();
        annotationSetsTabbedPane = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionEditorPane = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Annotation Panel");

        topPanel.setLayout(new java.awt.BorderLayout());

        openAnnotationSpecificationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/Open.gif"))); // NOI18N
        openAnnotationSpecificationButton.setText("Open...");
        openAnnotationSpecificationButton.setToolTipText("Open an annotation specification file");
        openAnnotationSpecificationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAnnotationSpecificationButtonActionPerformed(evt);
            }
        });
        annotationSpecificationPanel.add(openAnnotationSpecificationButton);

        topPanel.add(annotationSpecificationPanel, java.awt.BorderLayout.PAGE_START);

        jLabel1.setText("Current File: ");
        jPanel1.add(jLabel1);

        currentFileLabel.setForeground(java.awt.SystemColor.activeCaption);
        currentFileLabel.setText("none");
        jPanel1.add(currentFileLabel);

        topPanel.add(jPanel1, java.awt.BorderLayout.CENTER);

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        autoJumpCheckBox.setText("Auto jump");
        jPanel3.add(autoJumpCheckBox);

        jPanel2.add(jPanel3, java.awt.BorderLayout.SOUTH);

        annotationSetsTabbedPane.setPreferredSize(new java.awt.Dimension(200, 400));
        jPanel2.add(annotationSetsTabbedPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(250, 100));
        jScrollPane1.setViewportView(descriptionEditorPane);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.SOUTH);

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
            } catch (JDOMException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(rootPane, ex.getLocalizedMessage());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(rootPane, ex.getLocalizedMessage());
            }
        }
    }//GEN-LAST:event_openAnnotationSpecificationButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AnnotationDialog dialog = new AnnotationDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JTabbedPane annotationSetsTabbedPane;
    private javax.swing.JPanel annotationSpecificationPanel;
    private javax.swing.JCheckBox autoJumpCheckBox;
    private javax.swing.JLabel currentFileLabel;
    private javax.swing.JEditorPane descriptionEditorPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton openAnnotationSpecificationButton;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

    public void beforeSelect(JCSelectEvent arg0) {

    }

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

    public void afterSelect(JCSelectEvent arg0) {

    }

    private void readSpecification(File selectedFile) throws JDOMException, IOException {
        annotationSpecification.read(selectedFile);
        annotationSetsTabbedPane.removeAll();
        for (String category : annotationSpecification.exmaraldaTierCategories){
            AnnotationSetPanel asp = new AnnotationSetPanel(annotationSpecification.getAnnotationSet(category));
            asp.setAnnotationDialog(this);
            annotationSetsTabbedPane.add(category, asp);
        }
        currentFileLabel.setText(selectedFile.getName());
        currentFileLabel.setToolTipText(selectedFile.getAbsolutePath());
        int count=0;
        for (String shortcut : annotationSpecification.keyboardShortcuts.keySet()){
            count++;
            final String tag = annotationSpecification.keyboardShortcuts.get(shortcut);

            AbstractAction action = new AbstractAction(){
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

}
