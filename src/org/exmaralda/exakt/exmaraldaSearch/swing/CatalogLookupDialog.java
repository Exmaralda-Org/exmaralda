/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CatalogLookupDialog.java
 *
 * Created on 22.03.2011, 14:01:47
 */

package org.exmaralda.exakt.exmaraldaSearch.swing;

import java.awt.Frame;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.partiture.BrowserLauncher;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class CatalogLookupDialog extends javax.swing.JDialog implements ListSelectionListener, HyperlinkListener {

    String HTML_STYLESHEET = "/org/exmaralda/common/resources/CatalogResource2HTML.xsl";
    //String HTML_STYLESHEET = "/org/exmaralda/common/corpusbuild/corpuscatalog/Resource2HTML.xsl";
    static String REMOTE_RESOURCE_SELECTION_XPATH = "//resource[contains(@type,'exmaralda') and descendant::exmaralda-coma[@type='remote']]";
    static String DB_RESOURCE_SELECTION_XPATH = "//resource[contains(@type,'exmaralda') and descendant::exmaralda-rdb]";
    public boolean approved = false;
    String selectionXpath;
    Frame parentFrame;

    
    /** Creates new form CatalogLookupDialog */
    public CatalogLookupDialog(java.awt.Frame parent, boolean modal) {
        this(parent,modal, REMOTE_RESOURCE_SELECTION_XPATH);
    }
    
    public CatalogLookupDialog(java.awt.Frame parent, boolean modal, String xp) {        
        super(parent, modal);
        selectionXpath = xp;
        initComponents();
        resourcesList.setModel(new DefaultListModel());
        resourcesList.setCellRenderer(new CatalogEntryListCellRenderer());
        resourcesList.addListSelectionListener(this);

        resourceEditorPane.setContentType("text/html");
        resourceEditorPane.setEditorKit(new javax.swing.text.html.HTMLEditorKit());
        resourceEditorPane.setEditable(false);
        resourceEditorPane.addHyperlinkListener(this);

        if (parent instanceof ExmaraldaApplication){
            ExmaraldaApplication ea = (ExmaraldaApplication)parent;
            Preferences prefs = java.util.prefs.Preferences.userRoot().node(ea.getPreferencesNode());
            String url = prefs.get("Catalog-URL", "http://www.exmaralda.org/xml/EXMARaLDACorpusCatalog.xml");
            catalogURLTextField.setText(url);
        }


        doOpen();
    }

    public String getCOMAURL(){
        try {
            /* <resource name="HAMATAC">
                <full-name>Hamburg Map Task Corpus</full-name>
                <exmaralda-coma url="http://www1.uni-hamburg.de/exmaralda/files/maptask/MAPTASK.coma" login="http"/>
                <exmaralda-rdb url="jdbc:mysql://134.100.126.70:3306/exmaraldawww" corpus-name="MAPTASK"/>
            </resource> */
            Object o = resourcesList.getSelectedValue();
            Object o2 = XPath.newInstance("descendant::exmaralda-coma[@type='remote']").selectSingleNode(o);
            Element element = (Element)o2;
            return element.getAttributeValue("url");
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public String getLoginType(){
        /* <resource name="HAMATAC">
            <full-name>Hamburg Map Task Corpus</full-name>
            <exmaralda-coma url="http://www1.uni-hamburg.de/exmaralda/files/maptask/MAPTASK.coma" login="http"/>
            <exmaralda-rdb url="jdbc:mysql://134.100.126.70:3306/exmaraldawww" corpus-name="MAPTASK"/>
        </resource> */
        Object o = resourcesList.getSelectedValue();
        Element element = (Element)o;
        return element.getChild("exmaralda-coma").getAttributeValue("login");
    }

    public String getDBURL(){
        Object o = resourcesList.getSelectedValue();
        Element element = (Element)o;
        return element.getChild("exmaralda-rdb").getAttributeValue("url");
    }

    public String getDBCorpusName(){
        Object o = resourcesList.getSelectedValue();
        Element element = (Element)o;
        return element.getChild("exmaralda-rdb").getAttributeValue("corpus-name");
    }

    void doOpen(){
        String urlString = catalogURLTextField.getText();
        try {
            URL url = new URL(urlString);
            openCatalog(url);
        } catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(rootPane, ex.getLocalizedMessage());
        }
    }

    public void openCatalog(URL url){
        try {
            Document document = IOUtilities.readDocumentFromURL(url);
            DefaultListModel model = new DefaultListModel();
            List l = XPath.newInstance(selectionXpath).selectNodes(document);
            for (Object o : l){
                model.addElement(o);
            }
            resourcesList.setModel(model);
            if (model.size()>0){
                resourcesList.setSelectedIndex(0);
            }
        } catch (JDOMException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(rootPane, ex.getLocalizedMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(rootPane, ex.getLocalizedMessage());
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

        mainSplitPane = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        resourcesList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        resourceEditorPane = new javax.swing.JEditorPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        catalogURLTextField = new javax.swing.JTextField();
        openButton = new javax.swing.JButton();
        okCancelPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Catalog lookup");

        mainSplitPane.setDividerLocation(200);
        mainSplitPane.setPreferredSize(new java.awt.Dimension(800, 400));

        resourcesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        resourcesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        resourcesList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resourcesListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(resourcesList);

        mainSplitPane.setLeftComponent(jScrollPane1);

        resourceEditorPane.setBackground(new java.awt.Color(255, 255, 153));
        resourceEditorPane.setEditable(false);
        resourceEditorPane.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        jScrollPane2.setViewportView(resourceEditorPane);

        mainSplitPane.setRightComponent(jScrollPane2);

        getContentPane().add(mainSplitPane, java.awt.BorderLayout.CENTER);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Catalog URL: ");
        jPanel1.add(jLabel1);

        catalogURLTextField.setText("http://www.exmaralda.org/xml/EXMARaLDACorpusCatalog.xml");
        catalogURLTextField.setPreferredSize(new java.awt.Dimension(400, 30));
        catalogURLTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                catalogURLTextFieldActionPerformed(evt);
            }
        });
        jPanel1.add(catalogURLTextField);

        openButton.setText("Open");
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });
        jPanel1.add(openButton);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        okCancelPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        okCancelPanel.add(cancelButton);

        getContentPane().add(okCancelPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void catalogURLTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_catalogURLTextFieldActionPerformed
        doOpen();
    }//GEN-LAST:event_catalogURLTextFieldActionPerformed

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
       doOpen();
    }//GEN-LAST:event_openButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        approveAndQuit();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void resourcesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resourcesListMouseClicked
        if (evt.getClickCount()==2){
            approveAndQuit();
        }
    }//GEN-LAST:event_resourcesListMouseClicked

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CatalogLookupDialog dialog = new CatalogLookupDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField catalogURLTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel okCancelPanel;
    private javax.swing.JButton openButton;
    private javax.swing.JEditorPane resourceEditorPane;
    private javax.swing.JList resourcesList;
    // End of variables declaration//GEN-END:variables

    public void valueChanged(ListSelectionEvent e) {
        int i = resourcesList.getSelectedIndex();
        if (i>=0){
            Object o = resourcesList.getSelectedValue();
            Element element = (Element)o;
            String elementString =  IOUtilities.elementToString(element);
            String html;
            try {
                html = new StylesheetFactory().applyInternalStylesheetToString(HTML_STYLESHEET, elementString);
                //System.out.println(elementString);
                //System.out.println(html);
                resourceEditorPane.setText(html);
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (TransformerConfigurationException ex) {
                ex.printStackTrace();
            } catch (TransformerException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            String url = e.getURL().toString();
            try {
                BrowserLauncher.openURL(url);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(rootPane, ex.getLocalizedMessage());
            }
        }
    }

    void approveAndQuit(){
        if (parentFrame instanceof ExmaraldaApplication){
            ExmaraldaApplication ea = (ExmaraldaApplication)parentFrame;
            Preferences prefs = java.util.prefs.Preferences.userRoot().node(ea.getPreferencesNode());
            prefs.put("Catalog-URL", catalogURLTextField.getText());
        }
        approved=true;
        dispose();


    }

}
