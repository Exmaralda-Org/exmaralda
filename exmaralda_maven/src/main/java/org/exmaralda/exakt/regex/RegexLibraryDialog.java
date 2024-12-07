/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RegexLibraryDialog.java
 *
 * Created on 04.08.2010, 13:52:26
 */

package org.exmaralda.exakt.regex;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICSearchPanel;
import org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
public class RegexLibraryDialog extends javax.swing.JDialog
                                implements javax.swing.event.TreeSelectionListener {


    Element superLibrary;
    RegexLibraryTreeModel model;
    RegexLibraryEntryPanel entryPanel;

    Element userElement = null;

    EXAKT exaktFrame;

    HashSet<String> loadedLibrarys = new HashSet<String>();

    /** Creates new form RegexLibraryDialog */
    public RegexLibraryDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        entryPanel = new RegexLibraryEntryPanel();
        mainPanel.add(entryPanel, java.awt.BorderLayout.CENTER);
        pack();
        
        superLibrary = new Element("super-library");
        superLibrary.setAttribute("name", "RegEx");

        model = new RegexLibraryTreeModel(new RegexLibraryTreeNode(superLibrary));
        libraryTree.setModel(model);
        libraryTree.setCellRenderer(new RegexLibraryTreeCellRenderer());

        if (parent instanceof EXAKT){
            exaktFrame = (EXAKT) parent;
            try {
                loadEXAKTLibraries();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parent, ex.getLocalizedMessage());
            }
        }

        libraryTree.addTreeSelectionListener(this);
    }

    public void loadEXAKTLibraries() throws JDOMException, IOException{

        // internal library
        Document internalLibrary = new IOUtilities().readDocumentFromResource("/org/exmaralda/exakt/regex/regexlibrary.xml");
        Element root = internalLibrary.getRootElement();
        root.detach();
        superLibrary.addContent(root);

        // user library
        Preferences prefs = java.util.prefs.Preferences.userRoot().node(exaktFrame.getPreferencesNode());
        boolean canRead = false;
        Document userLibrary = null;
        String userLibraryPath = prefs.get("User-Regex-Library", "");
        while (!canRead){
            if (userLibraryPath.length()>0){
                try {
                    userLibrary = IOUtilities.readDocumentFromLocalFile(userLibraryPath);
                    canRead = true;
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            if (!canRead){
                UserLibraryDialog dialog = new UserLibraryDialog(exaktFrame, true);
                dialog.setLocationRelativeTo(exaktFrame);
                dialog.setVisible(true);
                userLibraryPath = dialog.getPath();
                if ((!(new File(userLibraryPath).exists()))){
                    Document d = new Document(new Element("regex-library"));
                    d.getRootElement().setAttribute("name", "user-library");
                    IOUtilities.writeDocumentToLocalFile(userLibraryPath, d);
                    prefs.put("User-Regex-Library", userLibraryPath);
                }
            }
        }

        userElement = userLibrary.getRootElement();
        userElement.detach();
        superLibrary.addContent(userElement);


        String all = prefs.get("User-Remote-Libraries", "");
        if (all.length()>0){
            for (String url : all.split("\\*")){
                try {
                    Document libDocument = FileIO.readDocumentFromURL(url);
                    Element libElement = libDocument.getRootElement();
                    libElement.detach();
                    superLibrary.addContent(libElement);
                    loadedLibrarys.add(url);
                } catch (Exception e){
                    // do nothing, just don't fucking load it
                    e.printStackTrace();
                }
            }
        }


        model = new RegexLibraryTreeModel(new RegexLibraryTreeNode(superLibrary));
        libraryTree.setModel(model);
    }

    public void addEntry(Element entry){
        // determine the current selection of the tree
        TreePath tp = libraryTree.getSelectionPath();
        RegexLibraryTreeNode folderNode = null;
        if (tp!=null){
            RegexLibraryTreeNode libraryNode = (RegexLibraryTreeNode) tp.getPathComponent(2);
            if (libraryNode.getElement().getAttributeValue("name").equals("user-library")){
                // i.e. something inside the user library was selected
                while ((!((RegexLibraryTreeNode) (tp.getLastPathComponent())).getName().equals("folder"))
                        && (!((RegexLibraryTreeNode) (tp.getLastPathComponent())).getName().equals("regex-library"))){
                    tp = tp.getParentPath();
                }
                folderNode = (RegexLibraryTreeNode) (tp.getLastPathComponent());

            } else {
                folderNode = (RegexLibraryTreeNode) model.getChild(model.getRoot(), 1);
            }
        } else {
            folderNode = (RegexLibraryTreeNode) model.getChild(model.getRoot(), 1);
        }
        folderNode.getElement().addContent(entry);
        folderNode.add(new RegexLibraryTreeNode(entry));
        model.nodeStructureChanged(folderNode);
        TreePath np = new TreePath(((RegexLibraryTreeNode)(folderNode.getChildAt(folderNode.getChildCount()-1))).getPath());
        libraryTree.setSelectionPath(np);
        libraryTree.makeVisible(np);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        treePanel = new javax.swing.JPanel();
        libraryTreeScrollPane = new javax.swing.JScrollPane();
        libraryTree = new javax.swing.JTree();
        buttonPanel = new javax.swing.JPanel();
        addLibraryButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        entryButtonPanel = new javax.swing.JPanel();
        copyToClipboardButton = new javax.swing.JButton();
        pasteToSearchExpressionField = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Regular Expression Libraries");

        treePanel.setLayout(new java.awt.BorderLayout());

        libraryTreeScrollPane.setPreferredSize(new java.awt.Dimension(200, 322));
        libraryTreeScrollPane.setViewportView(libraryTree);

        treePanel.add(libraryTreeScrollPane, java.awt.BorderLayout.CENTER);

        addLibraryButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/22x22/actions/document-open.png"))); // NOI18N
        addLibraryButton.setText("Add library...");
        addLibraryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLibraryButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(addLibraryButton);

        treePanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setLeftComponent(treePanel);

        mainPanel.setLayout(new java.awt.BorderLayout());

        copyToClipboardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/actions/edit-copy.png"))); // NOI18N
        copyToClipboardButton.setText("Copy to clipboard");
        copyToClipboardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyToClipboardButtonActionPerformed(evt);
            }
        });
        entryButtonPanel.add(copyToClipboardButton);

        pasteToSearchExpressionField.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/actions/edit-paste.png"))); // NOI18N
        pasteToSearchExpressionField.setText("Paste to search expression field");
        pasteToSearchExpressionField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteToSearchExpressionFieldActionPerformed(evt);
            }
        });
        entryButtonPanel.add(pasteToSearchExpressionField);

        mainPanel.add(entryButtonPanel, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setRightComponent(mainPanel);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pasteToSearchExpressionFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteToSearchExpressionFieldActionPerformed
        COMAKWICSearchPanel p = exaktFrame.getActiveSearchPanel();
        if (p!=null){
            p.pasteRegex(this.entryPanel.regexTextField.getText());
        }
    }//GEN-LAST:event_pasteToSearchExpressionFieldActionPerformed

    private void copyToClipboardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyToClipboardButtonActionPerformed
       java.awt.datatransfer.StringSelection ss = new java.awt.datatransfer.StringSelection(this.entryPanel.regexTextField.getText());
       this.getToolkit().getSystemClipboard().setContents(ss,ss);
       JOptionPane.showMessageDialog(this, "Regular expression copied to clipboard");
    }//GEN-LAST:event_copyToClipboardButtonActionPerformed

    private void addLibraryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLibraryButtonActionPerformed
        String url = JOptionPane.showInputDialog(entryPanel, "Please enter URL:", "http://");
        try {
            Document libDocument = FileIO.readDocumentFromURL(url);
            Element libElement = libDocument.getRootElement();
            libElement.detach();
            superLibrary.addContent(libElement);
            RegexLibraryTreeNode root = (RegexLibraryTreeNode) model.getRoot();
            root.add(new RegexLibraryTreeNode(libElement));
            model.nodeStructureChanged(root);
            loadedLibrarys.add(url);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(entryPanel,"Error reading\n" + url +"\n\n" + ex.getLocalizedMessage());
        }
    }//GEN-LAST:event_addLibraryButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RegexLibraryDialog dialog = new RegexLibraryDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton addLibraryButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton copyToClipboardButton;
    private javax.swing.JPanel entryButtonPanel;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTree libraryTree;
    private javax.swing.JScrollPane libraryTreeScrollPane;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton pasteToSearchExpressionField;
    private javax.swing.JPanel treePanel;
    // End of variables declaration//GEN-END:variables

    public void valueChanged(TreeSelectionEvent e) {
        if (libraryTree.getSelectionPath()==null) return;
        RegexLibraryTreeNode node = (RegexLibraryTreeNode) libraryTree.getSelectionPath().getLastPathComponent();
        if (node.getName().equals("entry")){
            ((RegexLibraryEntryPanel) entryPanel).setEntry(node.getElement());
        }
    }

    public void saveUserLibrary() throws IOException {
          RegexLibraryTreeNode userLibraryNode = (RegexLibraryTreeNode) model.getChild(model.getRoot(), 1);
          Element userLibraryElement = userLibraryNode.getElement();
          userLibraryElement.detach();
          Document userLibraryDocument = new Document(userLibraryElement);
          Preferences prefs = java.util.prefs.Preferences.userRoot().node(exaktFrame.getPreferencesNode());
          String userLibraryPath = prefs.get("User-Regex-Library", "");
          IOUtilities.writeDocumentToLocalFile(userLibraryPath, userLibraryDocument);
          String prefsString = "";
          for (String url : loadedLibrarys){
              prefsString+=url+"*";
          }
          prefs.put("User-Remote-Libraries", prefsString);
    }

}
