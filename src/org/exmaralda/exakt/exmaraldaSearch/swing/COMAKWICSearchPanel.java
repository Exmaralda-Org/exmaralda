/*
 * COMAKWICSearchPanel.java
 *
 * Created on 16. Januar 2007, 13:44
 */

package org.exmaralda.exakt.exmaraldaSearch.swing;

import java.util.*;
import java.util.regex.PatternSyntaxException;
import javax.swing.event.TableModelEvent;
import java.awt.event.*;
import javax.swing.*;
import org.jdom.*;
import java.util.prefs.Preferences;
import java.io.File;
import javax.swing.text.html.HTMLDocument;
import org.exmaralda.exakt.search.*;
import org.exmaralda.exakt.search.swing.*;
import org.exmaralda.exakt.exmaraldaSearch.*;



/**
 *
 * @author  thomas
 */
public class COMAKWICSearchPanel extends javax.swing.JPanel 
                                    implements SearchListenerInterface, KWICTableListener, 
                                    javax.swing.event.TableModelListener {
    
    private SearchHistory searchHistory = new SearchHistory();
    private COMACorpusInterface corpus = new COMACorpus();
    private SearchResultList searchResultList = new SearchResultList();
    private Vector<String[]> meta = new Vector<String[]>();
    COMASearchResultListTableModel tableModel;

    boolean isApplet = false;
    
    
    
    Thread searchThread;
    boolean isSearching = false;
    
    private File currentSearchResultFile = null;
    private String currentSearchResultFileType = "";
    
    Search search;
    String searchExpression = "";
    String[][] ADDITIONAL_DATA_LOCATORS = {{"tier-id", "../../@id"},{"speaker", "../../@speaker"},{"start", "@s"}};
    
    public final static int UPDATE_TIME = 100;
    javax.swing.Timer timer;
    
    int currentProgress = 0;
    String progressString = "";
    String currentFile = "";
    
    InputHelperDialog inputHelperDialog = new InputHelperDialog(this);

    ActionListener actionListener;
    ActionListener actionListener2;
    ActionListener actionListener3;
    ActionListener actionListener4;
    
    Vector<COMAKWICSearchPanelListener> listenerList = new Vector<COMAKWICSearchPanelListener>();

    
    /** Creates new form COMAKWICSearchPanel */
    public COMAKWICSearchPanel(COMACorpusInterface corpus) {
        this(corpus, false);
    }

    public COMAKWICSearchPanel(COMACorpusInterface c, boolean configureForApplet) {
        isApplet = configureForApplet;
        corpus = c;
        tableModel = new COMASearchResultListTableModel(getSearchResultList(), corpus, getMeta());
        getSearchHistory().addHistory("", new HashSet<String>());
        initComponents();
        ((COMAKWICTable)(kwicTable)).copyAction.setCorpus(c);
        ((COMAKWICTable)(kwicTable)).copyAction.setMeta(getMeta());
        ((COMAKWICTable)(kwicTable)).setWrappedModel(tableModel);
        ((COMAKWICTable)(kwicTable)).setAutoCreateColumnsFromModel(false);
        ((COMAKWICTable)(kwicTable)).addKWICTableListener(this);
        ((COMAKWICTable)(kwicTable)).getWrappedModel().addTableModelListener(this);
        //((COMAKWICTableSorter)(kwicTable.getModel())).setTable((COMAKWICTable)(kwicTable));
        rightSideButtonsPanel.add(javax.swing.Box.createVerticalGlue());
                
        timer = new javax.swing.Timer(UPDATE_TIME, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (!isSearching) return;
                if (getCorpus() instanceof COMADBCorpus){
                    String existingString = progressBar.getString();
                    if (existingString.length()>60){
                        progressBar.setString("Database query in progress");
                    } else {
                        progressBar.setString("." + existingString + ".");
                    }

                } else {
                    progressBar.setValue(currentProgress);
                    progressBar.setString(currentFile);
                    progressBarLabel.setText(progressString);                    
                }
            }
        });

        progressBar.setIndeterminate(c instanceof COMADBCorpus);
        progressBar.setString("Database query in progress");
        
        timer.start();
        
        // init the xpath combo box
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        for (String sn : corpus.getSegmentationNames()){
            comboBoxModel.addElement(sn);
        }
        segmentationComboBox.setModel(comboBoxModel);
        
        // init the annotation combo box
        DefaultComboBoxModel comboBoxModel2 = new DefaultComboBoxModel();
        for (String an : corpus.getAnnotationNames()){
            comboBoxModel2.addElement(an);
        }
        annotationComboBox.setModel(comboBoxModel2);

        // init the annotation combo box
        DefaultComboBoxModel comboBoxModel3 = new DefaultComboBoxModel();
        for (String dn : corpus.getDescriptionNames()){
            comboBoxModel3.addElement(dn);
        }
        descriptionComboBox.setModel(comboBoxModel3);

        progressBarPanel.setVisible(false);
        xpathExpressionPanel.setVisible(false);
        annotationExpressionPanel.setVisible(false);
        descriptionExpressionPanel.setVisible(false);
        xslPanel.setVisible(false);

        
        // actionListener for the regular search expression textfield
        actionListener = new ActionListener() {     
            public void actionPerformed(ActionEvent actionEvent) {
                 if (isSearching) return;
                 searchExpressionComboBox.setSelectedItem(searchExpressionComboBox.getEditor().getItem());
                 performSearch();                 
            }
        };
        searchExpressionComboBox.getEditor().addActionListener(actionListener);
        
        // actionListener for the xpath expression textfield
        actionListener2 = new ActionListener() {     
            public void actionPerformed(ActionEvent actionEvent) {
                 if (isSearching) return;
                 performSearch();                 
            }
        };
        xpathTextField.addActionListener(actionListener2);

        // actionListener for the xsl textfield
        actionListener3 = new ActionListener() {     
            public void actionPerformed(ActionEvent actionEvent) {
                 if (isSearching) return;
                 performSearch();                 
            }
        };
        xslTextField.addActionListener(actionListener2);
        
        // actionListener for the description expression textfield
        /*actionListener4 = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                 if (isSearching) return;
                 performSearch();                 
            }
        };
        descriptionRegExField.addActionListener(actionListener4);*/

        // this one is throwing a security exception in the applet
        if (!isApplet){
            try{
                Preferences prefs = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.EXAKT");
                String fontName = prefs.get("kwic-table-font-name","Times New Roman");
                int fontSize = prefs.getInt("kwic-table-font-size", 10);
                java.awt.Font font = new java.awt.Font(fontName, java.awt.Font.PLAIN, fontSize);
                kwicTable.setFont(font);
                // new 28-05-2014
                searchExpressionComboBox.setFont(font);
                annotationRegExField.setFont(font);
                descriptionRegExField.setFont(font);
                completeTextEditorPane.setFont(font);
                String bodyRule = "body { font-family: " + font.getFamily() + "; " +
                    "font-size: " + font.getSize() + "pt; }";
                ((HTMLDocument)completeTextEditorPane.getDocument()).getStyleSheet().addRule(bodyRule);                
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        
        MACOSHarmonizer.harmonize(this);
        
    }


    public void configureForApplet() {
        copyButton.setEnabled(false);
        splitPane.setDividerLocation(500);
    }
    
    public COMAKWICTable getKWICTable(){
        return ((COMAKWICTable)(kwicTable));
    }
    
    public void addCOMAKWICSearchPanelListener(COMAKWICSearchPanelListener l){
        listenerList.add(l);
    }
    
    public void addKWICTableListener(KWICTableListener ktl){
        ((COMAKWICTable)(kwicTable)).addKWICTableListener(ktl);
    }
    
    
    public void fireCOMAKWICSearchPanelEvent(COMAKWICSearchPanelEvent ev){
        for (COMAKWICSearchPanelListener l : listenerList){
            l.processEvent(ev);
        }
    }
    
    public void startBrowser(){
        if (getSearchResultList().size()<=0){
            javax.swing.JOptionPane.showMessageDialog(this, "The concordance is empty.");
            return;
        }
        int index = Math.max(0,kwicTable.getSelectedRow());        
        COMASearchResultListBrowserDialog dialog =
                new COMASearchResultListBrowserDialog((javax.swing.JFrame)(this.getTopLevelAncestor()), 
                                                        true, 
                                                        (COMAKWICTableSorter)(getKWICTable().getModel()),
                                                        //tableModel,
                                                        index);
        java.awt.Dimension dialogSize = dialog.getPreferredSize();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation(screenSize.width/2 - dialogSize.width/2, screenSize.height/2 - dialogSize.height/2);        
        //dialog.setLocationRelativeTo(this.searchExpressionPanel);
        dialog.setVisible(true);
        setCellEditors();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        upperPanel = new javax.swing.JPanel();
        topSidePanel = new javax.swing.JPanel();
        searchExpressionPanel = new javax.swing.JPanel();
        searchTypeComboBox = new javax.swing.JComboBox();
        regularExpressionPanel = new javax.swing.JPanel();
        searchExpressionButton = new javax.swing.JButton();
        searchExpressionComboBox = new org.exmaralda.exakt.search.swing.SearchHistoryComboBox(searchHistory);
        annotationExpressionPanel = new javax.swing.JPanel();
        searchAnnotationLabel1 = new javax.swing.JLabel();
        annotationComboBox = new javax.swing.JComboBox();
        searchAnnotationLabel2 = new javax.swing.JLabel();
        annotationRegExField = new org.exmaralda.exakt.search.swing.RegularExpressionTextField();
        descriptionExpressionPanel = new javax.swing.JPanel();
        searchDescriptionLabel1 = new javax.swing.JLabel();
        descriptionComboBox = new javax.swing.JComboBox();
        searchDescriptionLabel2 = new javax.swing.JLabel();
        descriptionRegExField = new org.exmaralda.exakt.search.swing.RegularExpressionTextField();
        xpathExpressionPanel = new javax.swing.JPanel();
        searchXPathLabel1 = new javax.swing.JLabel();
        segmentationComboBox = new javax.swing.JComboBox();
        searchXPathLabel2 = new javax.swing.JLabel();
        xpathTextField = new org.exmaralda.exakt.search.swing.XPathExpressionTextField();
        xslPanel = new javax.swing.JPanel();
        xslLabel = new javax.swing.JLabel();
        xslTextField = new javax.swing.JTextField();
        xslBrowseButton = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();
        progressBarPanel = new javax.swing.JPanel();
        searchDescriptionLabel = new javax.swing.JLabel();
        progressBarLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        cancelSearchButton = new javax.swing.JButton();
        kwicTablePanel = new javax.swing.JPanel();
        kwicTableScrollPane = new javax.swing.JScrollPane();
        kwicTable = new org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICTable();
        rightSidePanel = new javax.swing.JPanel();
        searchInfoPanel = new javax.swing.JPanel();
        types = new javax.swing.JLabel();
        typesLabel = new javax.swing.JLabel();
        tokens = new javax.swing.JLabel();
        tokensLabel = new javax.swing.JLabel();
        selected = new javax.swing.JLabel();
        selectedLabel = new javax.swing.JLabel();
        time = new javax.swing.JLabel();
        timeLabel = new javax.swing.JLabel();
        rightSideButtonsPanel = new javax.swing.JPanel();
        filterButton = new javax.swing.JButton();
        metaDataButton = new javax.swing.JButton();
        addAnalysisButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        copyButton = new javax.swing.JButton();
        removeUnselectedButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        showPartiturButton = new javax.swing.JButton();
        moreContextButton = new javax.swing.JButton();
        lessContextButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        bottomSidePanel = new javax.swing.JPanel();
        completeTextScrollPane = new javax.swing.JScrollPane();
        completeTextEditorPane = new javax.swing.JEditorPane();
        additionalDataScrollPane = new javax.swing.JScrollPane();
        additionalDataEditorPane = new javax.swing.JEditorPane();

        setLayout(new java.awt.BorderLayout());

        splitPane.setDividerSize(3);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        upperPanel.setLayout(new java.awt.BorderLayout());

        topSidePanel.setLayout(new javax.swing.BoxLayout(topSidePanel, javax.swing.BoxLayout.Y_AXIS));

        searchExpressionPanel.setLayout(new javax.swing.BoxLayout(searchExpressionPanel, javax.swing.BoxLayout.LINE_AXIS));

        searchTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "RegEx (T)", "RegEx (A)", "RegEx (D)", "XPath (T)", "XSL" }));
        searchTypeComboBox.setToolTipText("Search Type");
        searchTypeComboBox.setMaximumSize(new java.awt.Dimension(300, 31));
        searchTypeComboBox.setMinimumSize(new java.awt.Dimension(80, 31));
        searchTypeComboBox.setPreferredSize(new java.awt.Dimension(110, 31));
        searchTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchTypeComboBoxActionPerformed(evt);
            }
        });
        searchExpressionPanel.add(searchTypeComboBox);

        regularExpressionPanel.setLayout(new javax.swing.BoxLayout(regularExpressionPanel, javax.swing.BoxLayout.LINE_AXIS));

        searchExpressionButton.setText("Search: ");
        searchExpressionButton.setToolTipText("Click to open the input helper");
        searchExpressionButton.setMaximumSize(new java.awt.Dimension(80, 31));
        searchExpressionButton.setMinimumSize(new java.awt.Dimension(80, 31));
        searchExpressionButton.setPreferredSize(new java.awt.Dimension(80, 31));
        searchExpressionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchExpressionButtonActionPerformed(evt);
            }
        });
        regularExpressionPanel.add(searchExpressionButton);

        searchExpressionComboBox.setFont(new java.awt.Font("Tahoma", 0, 14));
        searchExpressionComboBox.setMaximumSize(new java.awt.Dimension(3000, 31));
        searchExpressionComboBox.setMinimumSize(new java.awt.Dimension(10, 31));
        searchExpressionComboBox.setPreferredSize(new java.awt.Dimension(300, 31));
        regularExpressionPanel.add(searchExpressionComboBox);

        searchExpressionPanel.add(regularExpressionPanel);

        annotationExpressionPanel.setLayout(new javax.swing.BoxLayout(annotationExpressionPanel, javax.swing.BoxLayout.LINE_AXIS));

        searchAnnotationLabel1.setText(" Annotation: ");
        annotationExpressionPanel.add(searchAnnotationLabel1);

        annotationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        annotationComboBox.setMaximumSize(new java.awt.Dimension(300, 31));
        annotationComboBox.setMinimumSize(new java.awt.Dimension(100, 31));
        annotationComboBox.setPreferredSize(new java.awt.Dimension(150, 31));
        annotationExpressionPanel.add(annotationComboBox);

        searchAnnotationLabel2.setText(" Regex: ");
        annotationExpressionPanel.add(searchAnnotationLabel2);

        annotationRegExField.setFont(new java.awt.Font("Tahoma", 0, 14));
        annotationRegExField.setMaximumSize(new java.awt.Dimension(3000, 31));
        annotationRegExField.setMinimumSize(new java.awt.Dimension(100, 31));
        annotationRegExField.setPreferredSize(new java.awt.Dimension(300, 31));
        annotationRegExField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annotationRegExFieldActionPerformed(evt);
            }
        });
        annotationExpressionPanel.add(annotationRegExField);

        searchExpressionPanel.add(annotationExpressionPanel);

        descriptionExpressionPanel.setLayout(new javax.swing.BoxLayout(descriptionExpressionPanel, javax.swing.BoxLayout.LINE_AXIS));

        searchDescriptionLabel1.setText(" Category: ");
        descriptionExpressionPanel.add(searchDescriptionLabel1);

        descriptionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        descriptionComboBox.setMaximumSize(new java.awt.Dimension(300, 31));
        descriptionComboBox.setMinimumSize(new java.awt.Dimension(100, 31));
        descriptionComboBox.setPreferredSize(new java.awt.Dimension(150, 31));
        descriptionExpressionPanel.add(descriptionComboBox);

        searchDescriptionLabel2.setText(" Regex: ");
        descriptionExpressionPanel.add(searchDescriptionLabel2);

        descriptionRegExField.setMaximumSize(new java.awt.Dimension(3000, 31));
        descriptionRegExField.setMinimumSize(new java.awt.Dimension(100, 31));
        descriptionRegExField.setPreferredSize(new java.awt.Dimension(300, 31));
        descriptionRegExField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descriptionRegExFieldActionPerformed(evt);
            }
        });
        descriptionExpressionPanel.add(descriptionRegExField);

        searchExpressionPanel.add(descriptionExpressionPanel);

        xpathExpressionPanel.setLayout(new javax.swing.BoxLayout(xpathExpressionPanel, javax.swing.BoxLayout.LINE_AXIS));

        searchXPathLabel1.setText(" Segmentation: ");
        xpathExpressionPanel.add(searchXPathLabel1);

        segmentationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        segmentationComboBox.setMaximumSize(new java.awt.Dimension(300, 31));
        segmentationComboBox.setMinimumSize(new java.awt.Dimension(100, 31));
        segmentationComboBox.setPreferredSize(new java.awt.Dimension(200, 31));
        xpathExpressionPanel.add(segmentationComboBox);

        searchXPathLabel2.setText(" XPath: ");
        xpathExpressionPanel.add(searchXPathLabel2);

        xpathTextField.setFont(new java.awt.Font("Tahoma", 0, 14));
        xpathTextField.setMaximumSize(new java.awt.Dimension(3000, 31));
        xpathTextField.setMinimumSize(new java.awt.Dimension(100, 31));
        xpathTextField.setPreferredSize(new java.awt.Dimension(300, 31));
        xpathExpressionPanel.add(xpathTextField);

        searchExpressionPanel.add(xpathExpressionPanel);

        xslPanel.setLayout(new javax.swing.BoxLayout(xslPanel, javax.swing.BoxLayout.LINE_AXIS));

        xslLabel.setText(" XSL Stylesheet: ");
        xslPanel.add(xslLabel);

        xslTextField.setMaximumSize(new java.awt.Dimension(3000, 31));
        xslTextField.setMinimumSize(new java.awt.Dimension(100, 31));
        xslTextField.setPreferredSize(new java.awt.Dimension(300, 31));
        xslPanel.add(xslTextField);

        xslBrowseButton.setText("Browse...");
        xslBrowseButton.setMaximumSize(new java.awt.Dimension(85, 31));
        xslBrowseButton.setMinimumSize(new java.awt.Dimension(85, 31));
        xslBrowseButton.setPreferredSize(new java.awt.Dimension(85, 31));
        xslBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xslBrowseButtonActionPerformed(evt);
            }
        });
        xslPanel.add(xslBrowseButton);

        searchExpressionPanel.add(xslPanel);

        searchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/Find24.gif"))); // NOI18N
        searchButton.setToolTipText("Perform search");
        searchButton.setMaximumSize(new java.awt.Dimension(55, 31));
        searchButton.setMinimumSize(new java.awt.Dimension(55, 31));
        searchButton.setPreferredSize(new java.awt.Dimension(55, 31));
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        searchExpressionPanel.add(searchButton);

        topSidePanel.add(searchExpressionPanel);

        progressBarPanel.setLayout(new javax.swing.BoxLayout(progressBarPanel, javax.swing.BoxLayout.LINE_AXIS));

        searchDescriptionLabel.setText("jLabel1");
        progressBarPanel.add(searchDescriptionLabel);

        progressBarLabel.setForeground(new java.awt.Color(255, 0, 102));
        progressBarLabel.setToolTipText("The number of search results found so far");
        progressBarLabel.setMaximumSize(new java.awt.Dimension(300, 20));
        progressBarLabel.setMinimumSize(new java.awt.Dimension(50, 20));
        progressBarLabel.setPreferredSize(new java.awt.Dimension(200, 20));
        progressBarPanel.add(progressBarLabel);

        progressBar.setMaximumSize(new java.awt.Dimension(3000, 31));
        progressBar.setMinimumSize(new java.awt.Dimension(100, 31));
        progressBar.setPreferredSize(new java.awt.Dimension(300, 31));
        progressBar.setStringPainted(true);
        progressBarPanel.add(progressBar);

        cancelSearchButton.setText("Cancel");
        cancelSearchButton.setMaximumSize(new java.awt.Dimension(65, 31));
        cancelSearchButton.setMinimumSize(new java.awt.Dimension(65, 31));
        cancelSearchButton.setPreferredSize(new java.awt.Dimension(65, 31));
        cancelSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelSearchButtonActionPerformed(evt);
            }
        });
        progressBarPanel.add(cancelSearchButton);

        topSidePanel.add(progressBarPanel);

        upperPanel.add(topSidePanel, java.awt.BorderLayout.NORTH);

        kwicTablePanel.setLayout(new javax.swing.BoxLayout(kwicTablePanel, javax.swing.BoxLayout.Y_AXIS));

        kwicTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ));
        kwicTable.setToolTipText("KWIC concordance");
        kwicTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        kwicTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        kwicTable.setMaximumSize(null);
        kwicTable.setMinimumSize(null);
        kwicTable.setPreferredSize(null);
        kwicTableScrollPane.setViewportView(kwicTable);

        kwicTablePanel.add(kwicTableScrollPane);

        upperPanel.add(kwicTablePanel, java.awt.BorderLayout.CENTER);

        rightSidePanel.setMinimumSize(new java.awt.Dimension(127, 10));
        rightSidePanel.setLayout(new java.awt.BorderLayout());

        searchInfoPanel.setBackground(new java.awt.Color(255, 255, 255));
        searchInfoPanel.setLayout(new javax.swing.BoxLayout(searchInfoPanel, javax.swing.BoxLayout.Y_AXIS));

        types.setText("Types:");
        searchInfoPanel.add(types);

        typesLabel.setForeground(new java.awt.Color(51, 102, 255));
        typesLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        typesLabel.setText("0");
        typesLabel.setMaximumSize(new java.awt.Dimension(57, 14));
        typesLabel.setMinimumSize(new java.awt.Dimension(57, 14));
        typesLabel.setPreferredSize(new java.awt.Dimension(57, 14));
        searchInfoPanel.add(typesLabel);

        tokens.setText("Tokens: ");
        searchInfoPanel.add(tokens);

        tokensLabel.setForeground(new java.awt.Color(51, 102, 255));
        tokensLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tokensLabel.setText("0");
        tokensLabel.setMaximumSize(new java.awt.Dimension(57, 14));
        tokensLabel.setMinimumSize(new java.awt.Dimension(57, 14));
        tokensLabel.setPreferredSize(new java.awt.Dimension(57, 14));
        searchInfoPanel.add(tokensLabel);

        selected.setText("Selected:");
        searchInfoPanel.add(selected);

        selectedLabel.setForeground(new java.awt.Color(51, 102, 255));
        selectedLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        selectedLabel.setText("0");
        selectedLabel.setMaximumSize(new java.awt.Dimension(57, 14));
        selectedLabel.setMinimumSize(new java.awt.Dimension(57, 14));
        selectedLabel.setPreferredSize(new java.awt.Dimension(57, 14));
        searchInfoPanel.add(selectedLabel);

        time.setText("Time:");
        searchInfoPanel.add(time);

        timeLabel.setForeground(new java.awt.Color(51, 102, 255));
        timeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        timeLabel.setText("0");
        timeLabel.setMaximumSize(new java.awt.Dimension(57, 14));
        timeLabel.setMinimumSize(new java.awt.Dimension(57, 14));
        timeLabel.setPreferredSize(new java.awt.Dimension(57, 14));
        searchInfoPanel.add(timeLabel);

        rightSidePanel.add(searchInfoPanel, java.awt.BorderLayout.SOUTH);

        rightSideButtonsPanel.setLayout(new javax.swing.BoxLayout(rightSideButtonsPanel, javax.swing.BoxLayout.Y_AXIS));

        filterButton.setAction(((COMAKWICTable)(kwicTable)).filterAction);
        filterButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/Filter.gif"))); // NOI18N
        filterButton.setText(null);
        filterButton.setToolTipText("Filter the search result");
        filterButton.setMaximumSize(new java.awt.Dimension(57, 33));
        filterButton.setMinimumSize(new java.awt.Dimension(57, 33));
        filterButton.setPreferredSize(new java.awt.Dimension(57, 33));
        filterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterButtonActionPerformed(evt);
            }
        });
        rightSideButtonsPanel.add(filterButton);

        metaDataButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/mail-attachment.png"))); // NOI18N
        metaDataButton.setToolTipText("Specify meta data");
        metaDataButton.setMaximumSize(new java.awt.Dimension(56, 31));
        metaDataButton.setMinimumSize(new java.awt.Dimension(56, 31));
        metaDataButton.setPreferredSize(new java.awt.Dimension(56, 31));
        metaDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                metaDataButtonActionPerformed(evt);
            }
        });
        rightSideButtonsPanel.add(metaDataButton);

        addAnalysisButton.setAction(((COMAKWICTable)(kwicTable)).addAnalysisAction);
        addAnalysisButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/analysis_22.png"))); // NOI18N
        addAnalysisButton.setText(null);
        addAnalysisButton.setToolTipText("Add an analysis");
        addAnalysisButton.setMaximumSize(new java.awt.Dimension(57, 33));
        addAnalysisButton.setMinimumSize(new java.awt.Dimension(57, 33));
        addAnalysisButton.setPreferredSize(new java.awt.Dimension(57, 33));
        rightSideButtonsPanel.add(addAnalysisButton);
        rightSideButtonsPanel.add(jSeparator1);

        copyButton.setAction(((COMAKWICTable)(kwicTable)).copyAction
        );
        copyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/Copy.gif"))); // NOI18N
        copyButton.setText(null);
        copyButton.setToolTipText("Copy the selected search result");
        copyButton.setMaximumSize(new java.awt.Dimension(57, 33));
        copyButton.setMinimumSize(new java.awt.Dimension(57, 33));
        copyButton.setPreferredSize(new java.awt.Dimension(57, 33));
        rightSideButtonsPanel.add(copyButton);

        removeUnselectedButton.setAction(((COMAKWICTable)(kwicTable)).removeUnselectedAction);
        removeUnselectedButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/Delete24.gif"))); // NOI18N
        removeUnselectedButton.setText(null);
        removeUnselectedButton.setToolTipText("Remove unselected search results");
        removeUnselectedButton.setMaximumSize(new java.awt.Dimension(57, 33));
        removeUnselectedButton.setMinimumSize(new java.awt.Dimension(57, 33));
        removeUnselectedButton.setPreferredSize(new java.awt.Dimension(57, 33));
        rightSideButtonsPanel.add(removeUnselectedButton);
        rightSideButtonsPanel.add(jSeparator2);

        showPartiturButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/view-refresh.png"))); // NOI18N
        showPartiturButton.setToolTipText("Update view");
        showPartiturButton.setMaximumSize(new java.awt.Dimension(57, 33));
        showPartiturButton.setMinimumSize(new java.awt.Dimension(57, 33));
        showPartiturButton.setPreferredSize(new java.awt.Dimension(57, 33));
        showPartiturButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPartiturButtonActionPerformed(evt);
            }
        });
        rightSideButtonsPanel.add(showPartiturButton);

        moreContextButton.setAction(((COMAKWICTable)(kwicTable)).moreContextAction);
        moreContextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/MoreContext.gif"))); // NOI18N
        moreContextButton.setText(null);
        moreContextButton.setToolTipText("Show more context");
        moreContextButton.setMaximumSize(new java.awt.Dimension(57, 33));
        moreContextButton.setMinimumSize(new java.awt.Dimension(57, 33));
        moreContextButton.setPreferredSize(new java.awt.Dimension(57, 33));
        rightSideButtonsPanel.add(moreContextButton);

        lessContextButton.setAction(((COMAKWICTable)(kwicTable)).lessContextAction);
        lessContextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/exakt/exmaraldaSearch/swing/resources/LessContext.gif"))); // NOI18N
        lessContextButton.setText(null);
        lessContextButton.setToolTipText("Show less context");
        lessContextButton.setMaximumSize(new java.awt.Dimension(57, 33));
        lessContextButton.setMinimumSize(new java.awt.Dimension(57, 33));
        lessContextButton.setPreferredSize(new java.awt.Dimension(57, 33));
        lessContextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lessContextButtonActionPerformed(evt);
            }
        });
        rightSideButtonsPanel.add(lessContextButton);
        rightSideButtonsPanel.add(jSeparator3);

        rightSidePanel.add(rightSideButtonsPanel, java.awt.BorderLayout.CENTER);

        upperPanel.add(rightSidePanel, java.awt.BorderLayout.EAST);

        splitPane.setLeftComponent(upperPanel);

        bottomSidePanel.setLayout(new javax.swing.BoxLayout(bottomSidePanel, javax.swing.BoxLayout.LINE_AXIS));

        completeTextScrollPane.setPreferredSize(new java.awt.Dimension(400, 150));

        completeTextEditorPane.setContentType("text/html");
        completeTextEditorPane.setEditable(false);
        completeTextEditorPane.setPreferredSize(new java.awt.Dimension(400, 100));
        completeTextScrollPane.setViewportView(completeTextEditorPane);

        bottomSidePanel.add(completeTextScrollPane);

        additionalDataEditorPane.setEditable(false);
        additionalDataEditorPane.setEditorKit(new javax.swing.text.html.HTMLEditorKit());
        additionalDataScrollPane.setViewportView(additionalDataEditorPane);

        bottomSidePanel.add(additionalDataScrollPane);

        splitPane.setRightComponent(bottomSidePanel);

        add(splitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void descriptionRegExFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descriptionRegExFieldActionPerformed
        performSearch();
    }//GEN-LAST:event_descriptionRegExFieldActionPerformed

    private void annotationRegExFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annotationRegExFieldActionPerformed
        performSearch();
    }//GEN-LAST:event_annotationRegExFieldActionPerformed

    private void lessContextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lessContextButtonActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_lessContextButtonActionPerformed

    private void xslBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xslBrowseButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        //fileChooser.setCurrentDirectory(new File(startDirectory));
        fileChooser.setDialogTitle("Choose a stylesheet");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                String name = f.getAbsolutePath();
                return (f.isDirectory() || name.substring(Math.max(0,name.length()-3)).equalsIgnoreCase("XSL"));
            }
            public String getDescription() {
                return "XSL files (*.xsl)";
            }
        });
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue==JFileChooser.APPROVE_OPTION){
            xslTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        } 
    }//GEN-LAST:event_xslBrowseButtonActionPerformed

    private void cancelSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelSearchButtonActionPerformed
        cancelSearch();
    }//GEN-LAST:event_cancelSearchButtonActionPerformed

    private void searchTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchTypeComboBoxActionPerformed
        int index = searchTypeComboBox.getSelectedIndex();
        regularExpressionPanel.setVisible(index==0);
        annotationExpressionPanel.setVisible(index==1);
        descriptionExpressionPanel.setVisible(index==2);
        xpathExpressionPanel.setVisible(index==3);
        xslPanel.setVisible(index==4);
    }//GEN-LAST:event_searchTypeComboBoxActionPerformed

    private void showPartiturButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPartiturButtonActionPerformed
        ((COMAKWICTable)(kwicTable)).fireSearchResultDoubleClicked();
    }//GEN-LAST:event_showPartiturButtonActionPerformed

    private void filterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterButtonActionPerformed
        //filter();
    }//GEN-LAST:event_filterButtonActionPerformed

    private void searchExpressionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchExpressionButtonActionPerformed
            RegularExpressionTextField textField 
                    = (RegularExpressionTextField)(searchExpressionComboBox.getEditor().getEditorComponent());
            textField.setBackground(java.awt.Color.LIGHT_GRAY);
            java.awt.Point p = searchExpressionComboBox.getLocationOnScreen();
            int h = searchExpressionComboBox.getHeight();
            int w = searchExpressionComboBox.getWidth();
            p.translate(0,h);
            inputHelperDialog.setLocation(p);
            inputHelperDialog.setAlwaysOnTop(true);
            inputHelperDialog.inputHelperPanel.setSearchExpression(textField.getText());
            inputHelperDialog.setSize(Math.max(500,w),inputHelperDialog.getHeight());
            inputHelperDialog.setVisible(true);
            if (inputHelperDialog.changed){
                textField.setText(inputHelperDialog.inputHelperPanel.getSearchExpression());
                textField.requestFocus();                
            }
            textField.setBackground(java.awt.Color.WHITE);
    }//GEN-LAST:event_searchExpressionButtonActionPerformed

    private void metaDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_metaDataButtonActionPerformed
        // can I really change this to null?
        specifyMetadata();
    }//GEN-LAST:event_metaDataButtonActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        performSearch();
    }//GEN-LAST:event_searchButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAnalysisButton;
    private javax.swing.JEditorPane additionalDataEditorPane;
    private javax.swing.JScrollPane additionalDataScrollPane;
    private javax.swing.JComboBox annotationComboBox;
    private javax.swing.JPanel annotationExpressionPanel;
    private javax.swing.JTextField annotationRegExField;
    private javax.swing.JPanel bottomSidePanel;
    private javax.swing.JButton cancelSearchButton;
    private javax.swing.JEditorPane completeTextEditorPane;
    private javax.swing.JScrollPane completeTextScrollPane;
    private javax.swing.JButton copyButton;
    private javax.swing.JComboBox descriptionComboBox;
    private javax.swing.JPanel descriptionExpressionPanel;
    private javax.swing.JTextField descriptionRegExField;
    private javax.swing.JButton filterButton;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTable kwicTable;
    private javax.swing.JPanel kwicTablePanel;
    private javax.swing.JScrollPane kwicTableScrollPane;
    private javax.swing.JButton lessContextButton;
    private javax.swing.JButton metaDataButton;
    private javax.swing.JButton moreContextButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel progressBarLabel;
    private javax.swing.JPanel progressBarPanel;
    private javax.swing.JPanel regularExpressionPanel;
    private javax.swing.JButton removeUnselectedButton;
    private javax.swing.JPanel rightSideButtonsPanel;
    private javax.swing.JPanel rightSidePanel;
    private javax.swing.JLabel searchAnnotationLabel1;
    private javax.swing.JLabel searchAnnotationLabel2;
    private javax.swing.JButton searchButton;
    private javax.swing.JLabel searchDescriptionLabel;
    private javax.swing.JLabel searchDescriptionLabel1;
    private javax.swing.JLabel searchDescriptionLabel2;
    private javax.swing.JButton searchExpressionButton;
    private javax.swing.JComboBox searchExpressionComboBox;
    private javax.swing.JPanel searchExpressionPanel;
    private javax.swing.JPanel searchInfoPanel;
    private javax.swing.JComboBox searchTypeComboBox;
    private javax.swing.JLabel searchXPathLabel1;
    private javax.swing.JLabel searchXPathLabel2;
    private javax.swing.JComboBox segmentationComboBox;
    private javax.swing.JLabel selected;
    private javax.swing.JLabel selectedLabel;
    private javax.swing.JButton showPartiturButton;
    public javax.swing.JSplitPane splitPane;
    private javax.swing.JLabel time;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JLabel tokens;
    private javax.swing.JLabel tokensLabel;
    private javax.swing.JPanel topSidePanel;
    private javax.swing.JLabel types;
    private javax.swing.JLabel typesLabel;
    private javax.swing.JPanel upperPanel;
    private javax.swing.JPanel xpathExpressionPanel;
    private javax.swing.JTextField xpathTextField;
    private javax.swing.JButton xslBrowseButton;
    private javax.swing.JLabel xslLabel;
    private javax.swing.JPanel xslPanel;
    private javax.swing.JTextField xslTextField;
    // End of variables declaration//GEN-END:variables

    public void specifyMetadata(){
        ChooseCOMAAttributesDialog d = new ChooseCOMAAttributesDialog(null, true,
                corpus.getSpeakerAttributes(),
                corpus.getCommunicationAttributes(),
                corpus.getTranscriptionAttributes(), getMeta());
        d.setVisible(true);
        d.setLocationRelativeTo(kwicTable);
        setMeta(d.getSelectedAttributes());              
    }

    private void filter(){
        FilterTargetSelectionDialog dialog = new FilterTargetSelectionDialog((JFrame)(getTopLevelAncestor()), true);
        dialog.setLocationRelativeTo(filterButton);
        dialog.setVisible(true);
        if (dialog.isApproved()){
            //String filterExpression = ((String[])(this.searchExpressionComboBox.getSelectedItem()))[0];
            searchExpression = ((String[])(this.searchExpressionComboBox.getSelectedItem()))[0];
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(searchExpression);
            int selection = dialog.getSelection();            
            switch (selection){
                case FilterTargetSelectionDialog.LEFT_CONTEXT :
                     getSearchResultList().filterLeftContext(pattern, dialog.getInversion());
                     break;
                case FilterTargetSelectionDialog.MATCH_TEXT :
                     getSearchResultList().filterMatchText(pattern, dialog.getInversion());
                     break;
                case FilterTargetSelectionDialog.RIGHT_CONTEXT :
                     getSearchResultList().filterRightContext(pattern, dialog.getInversion());
                     break;
            }
        }        
        tableModel.fireTableDataChanged();
        //updateSearchResult();
    }
    
    private void performSearch(){
        
        if (getSearchResultList().getAnalyses().size()>0){
            String message = "You have " + getSearchResultList().getAnalyses().size() + " analysis columns for this concordance.\n";
            message+="These will be deleted for the new search.\n";
            message+="Do you want to continue?";
            int ret = javax.swing.JOptionPane.showConfirmDialog(this, message, "Warning", javax.swing.JOptionPane.YES_NO_OPTION);
            if (ret==javax.swing.JOptionPane.NO_OPTION) return;
        }

        
        int index = searchTypeComboBox.getSelectedIndex();

        SearchParametersInterface parameters = null;     
        String segmentationName = null;
        String xpath = null;
        
        switch(index){
            case 0 : // Regular Expression
                    // TODO: this is an unzulaessige Verallgemeinerung
                    // not every segmented transcription has a segmentation with this name
                    // I fucking need to change this, Hanna wants it so
                    segmentationName = "SpeakerContribution_Event";
                    xpath = "//segmentation[@name='" + segmentationName + "']/ts";
                    try {
                        searchExpression = ((String[])(this.searchExpressionComboBox.getSelectedItem()))[0];
                        parameters = new RegularExpressionSearchParameters(searchExpression, ADDITIONAL_DATA_LOCATORS);
                    } catch (PatternSyntaxException ex) {
                        javax.swing.JOptionPane.showMessageDialog(this, "Error in expression: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        return;
                    } catch (JDOMException ex) {
                        javax.swing.JOptionPane.showMessageDialog(this, "Error in expression: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;
            case 1 : // Annotation
                    segmentationName = (String)(annotationComboBox.getSelectedItem());
                    xpath = "//annotation[@name='" + segmentationName + "']/ta";
                    //System.out.println(xpath);
                    try {
                        searchExpression = annotationRegExField.getText();
                        //System.out.println(searchExpression);
                        parameters = new RegularExpressionSearchParameters(searchExpression, ADDITIONAL_DATA_LOCATORS, SearchParametersInterface.ANNOTATION_SEARCH, segmentationName);
                    } catch (PatternSyntaxException ex) {
                        //System.out.println("Pattern");
                        javax.swing.JOptionPane.showMessageDialog(this, "Error in expression: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        return;
                    } catch (JDOMException ex) {
                        //System.out.println("JDOM");
                        javax.swing.JOptionPane.showMessageDialog(this, "Error in expression: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;
            case 2 : // Description
                    segmentationName = (String)(descriptionComboBox.getSelectedItem());
                    xpath = "//segmented-tier[@type='d' and @category='" + segmentationName + "']/segmentation/ats";
                    //System.out.println(xpath);
                    try {
                        searchExpression = descriptionRegExField.getText();
                        //System.out.println(searchExpression);
                        parameters = new RegularExpressionSearchParameters(searchExpression, ADDITIONAL_DATA_LOCATORS, SearchParametersInterface.DEFAULT_SEARCH, segmentationName);
                    } catch (PatternSyntaxException ex) {
                        //System.out.println("Pattern");
                        javax.swing.JOptionPane.showMessageDialog(this, "Error in expression: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        return;
                    } catch (JDOMException ex) {
                        //System.out.println("JDOM");
                        javax.swing.JOptionPane.showMessageDialog(this, "Error in expression: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;
            case 3 : // XPath expression
                    segmentationName = (String)(segmentationComboBox.getSelectedItem());
                    xpath = "//segmentation[@name='" + segmentationName + "']/ts";
                    //getCorpus().setXPathToSearchableSegment(xpath);                    
                    try {
                        searchExpression = xpathTextField.getText();
                        parameters = new XPathSearchParameters(searchExpression, ADDITIONAL_DATA_LOCATORS);
                    } catch (JDOMException ex) {
                        javax.swing.JOptionPane.showMessageDialog(this, "Error in expression: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;
            case 4 : // XSL stylesheet
                     String xslPath = xslTextField.getText();
                     File xslFile = new File(xslPath);
                     xpath = "/*";
                     try {
                        parameters = new XSLSearchParameters(xslFile);
                     } catch (JDOMException ex){
                        javax.swing.JOptionPane.showMessageDialog(this, "Error in stylesheet: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        return;                         
                     }
                     break;
        }
        

        // DO SEARCH
        try {
            // this one is throwing a security exception when used in an applet
            int cl = -1;
            if (!isApplet){
                cl = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.EXAKT").getInt("kwic-context-limit", -1);
            }
            parameters.setContextLimit(cl);
        } catch (Exception e){e.printStackTrace();}
        if (getCorpus() instanceof COMACorpus){
            ((COMACorpus)getCorpus()).setXPathToSearchableSegment(xpath);
        } else if (getCorpus() instanceof COMARemoteCorpus){
            ((COMARemoteCorpus)getCorpus()).setXPathToSearchableSegment(xpath);
        } else if (getCorpus() instanceof COMADBCorpus){
            //TODO: maybe nothing
        }
        search = new Search(corpus,parameters);
        try {
            // this one is throwing a security exception when used in an applet
            int mnrs = 10000;
            if (!isApplet){
                mnrs = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.EXAKT").getInt("max-search-results", 10000);
            }
            search.setMaxNumberOfSearchResults(mnrs);
        } catch (Exception e){e.printStackTrace();}
        search.addSearchListener(this);
        isSearching = true;
        searchExpressionPanel.setVisible(false);
        progressBarPanel.setVisible(true);
        searchDescriptionLabel.setText("   Searching for " + searchExpression.substring(0,Math.min(50, searchExpression.length())) + "   ");
        final Runnable doUpdateSearchResult = new Runnable() {
             public void run() {
                 updateSearchResult();
             }
         };        
        /*Thread*/ searchThread = new Thread(){
            @Override
            public void run(){
                search.doSearch();
                javax.swing.SwingUtilities.invokeLater(doUpdateSearchResult);
                progressBarPanel.setVisible(false);      
                searchExpressionPanel.setVisible(true);
                isSearching = false;
            }
        };
        //SwingUtilities.invokeLater(searchThread);
        searchThread.start();
    }
    
    private void cancelSearch(){
        //searchThread.interrupt();
    }
    
    private void updateSearchResult(){
        setSearchResultList(search.getSearchResult());
        tableModel = new COMASearchResultListTableModel(getSearchResultList(), corpus, getMeta());            
        ((COMAKWICTable)(kwicTable)).setWrappedModel(tableModel);
        if (searchTypeComboBox.getSelectedIndex()==1){
            // annotation search
            tableModel.addAnalysis(new org.exmaralda.exakt.search.analyses.FreeAnalysis((String)(annotationComboBox.getSelectedItem())));
        }
        tableModel.addTableModelListener(this);
                
        HashSet<String> types = getSearchResultList().getTypes();
        inputHelperDialog.inputHelperPanel.setTypes(types);

        if (searchTypeComboBox.getSelectedIndex()==0){
            if (searchHistory.addHistory(searchExpression, types)){
                searchExpressionComboBox.setModel(new javax.swing.DefaultComboBoxModel(searchHistory));
            }
        }
        
        updateLabels();
        
        COMAKWICSearchPanelEvent ev = new COMAKWICSearchPanelEvent(
                COMAKWICSearchPanelEvent.NEW_SEARCH_RESULT,
                this, getSearchResultList()
                );
        this.fireCOMAKWICSearchPanelEvent(ev);
        
    }

    public SearchHistory getSearchHistory() {
        return searchHistory;
    }

    public void setSearchHistory(SearchHistory searchHistory) {
        this.searchHistory = searchHistory;
    }

    public COMACorpusInterface getCorpus() {
        return corpus;
    }

    public void setCorpus(COMACorpusInterface corpus) {
        this.corpus = corpus;
    }
    

    public void processSearchEvent(SearchEvent se) {
        short type = se.getType();
        if (type==SearchEvent.SEARCH_PROGRESS_CHANGED){
            progressString = ((String[])(se.getData()))[0];
            currentFile = ((String[])(se.getData()))[1];
            currentProgress = (int)Math.round(se.getProgress()*100);
        } else if (type==SearchEvent.SEARCH_STOPPED){
            progressString = (String)(se.getData());
            currentProgress = (int)Math.round(se.getProgress()*100);
        }
    }

    public void processEvent(KWICTableEvent ev) {
        if (ev.getType()==KWICTableEvent.SELECTION){
            updateSelectionLabel();
            SearchResultInterface r = ev.getSelectedSearchResult();
            completeTextEditorPane.setText(r.getKWICAsHTML());
            completeTextEditorPane.setCaretPosition(r.getLeftContextAsString().length());
            
            StringBuffer sb2 = new StringBuffer();
            sb2.append("<html><head><style type=\"text/css\">");
            sb2.append("td.evenRow {background-color:rgb(210,210,210);font-family:Arial,sans-serif;font-size:10pt}");
            sb2.append("td.oddRow  {background-color:rgb(255,255,255);font-family:Arial,sans-serif;font-size:10pt}");
            sb2.append("</style></head><body><table rules=\"rows\" border=\"1\">");
            int row = kwicTable.getSelectedRow();
            // changed (increment 1) for row numbering in version 0.4, 22-Jan-2008
            //for (int col=6; col<kwicTable.getModel().getColumnCount(); col++){
            for (int col=7; col<kwicTable.getModel().getColumnCount(); col++){
                sb2.append("<tr><td bgcolor=\"");
                if (col%2==0) {sb2.append("#DDDDDD");} else {sb2.append("#FFFFFF");}
                String colName = "";
                if (kwicTable.getColumnName(col)!=null) {colName = kwicTable.getColumnName(col);}
                sb2.append("\"><b>" + colName + "</b></td>");
                sb2.append("<td bgcolor=\"");
                if (col%2==0) {sb2.append("#DDDDDD");} else {sb2.append("#FFFFFF");}
                String colValue = null;
                if (kwicTable.getValueAt(row,col)!=null) {colValue = kwicTable.getValueAt(row,col).toString();}
                sb2.append("\">" + colValue + "</td></tr>");                                
            }            
            sb2.append("</table></body></html>");
            additionalDataEditorPane.setText(sb2.toString());
            additionalDataEditorPane.setCaretPosition(0);
        } 
    }

    public SearchResultList getSearchResultList() {
        return searchResultList;
    }

    public void setSearchResultList(SearchResultList srl) {
        this.searchResultList = srl;
        tableModel.setData(searchResultList);
        this.inputHelperDialog.inputHelperPanel.setTypes(searchResultList.getTypes());
        //((COMAKWICTable)(kwicTable)).setWrappedModel(tableModel);
        COMAKWICSearchPanelEvent ev = new COMAKWICSearchPanelEvent(
                COMAKWICSearchPanelEvent.NEW_SEARCH_RESULT,
                this, getSearchResultList()
                );
        this.fireCOMAKWICSearchPanelEvent(ev);
        
    }

    public void setCellEditors(){
        ((COMAKWICTable)(kwicTable)).setCellEditors();
    }
    
    public Vector<String[]> getMeta() {
        return meta;
    }

    public void setMeta(Vector<String[]> meta) {
        this.meta = meta;
        ((COMAKWICTable)(kwicTable)).copyAction.setMeta(meta);
        tableModel.setMetaIdentifiers(getMeta());        
    }

    /**
     * This fine grain notification tells listeners the exact range
     * of cells, rows, or columns that changed.
     */
    public void tableChanged(TableModelEvent e) {
        updateLabels();
    }

    private void updateSelectionLabel(){
        String selectedLabelString = Integer.toString(getSearchResultList().getSelectedCount())                                        
                                        + " (" + Integer.toString(kwicTable.getSelectedRowCount()) + ")";
        selectedLabel.setText(selectedLabelString);        
    }
    
    private void updateLabels(){
        updateSelectionLabel();
        tokensLabel.setText(Integer.toString(getSearchResultList().size()));
        typesLabel.setText(Integer.toString(getSearchResultList().getTypes().size()));
        if (search!=null){
            timeLabel.setText(Float.toString(Math.round(search.getTimeForLastSearch()*100)/100f) + " s");   
        } else {
            timeLabel.setText("-");
        }
    }

    public File getCurrentSearchResultFile() {
        return currentSearchResultFile;
    }

    public void setCurrentSearchResultFile(File currentSearchResultFile) {
        this.currentSearchResultFile = currentSearchResultFile;
    }

    public String getCurrentSearchResultFileType() {
        return currentSearchResultFileType;
    }

    public void setCurrentSearchResultFileType(String currentSearchResultFileType) {
        this.currentSearchResultFileType = currentSearchResultFileType;
    }

    public void pasteRegex(String text) {
        switch (this.searchTypeComboBox.getSelectedIndex()){
            case 0 : //Transcription
                JTextField t = (JTextField)(searchExpressionComboBox.getEditor().getEditorComponent());
                t.replaceSelection(text);
                break;
            case 1 : //Annotation
                annotationRegExField.replaceSelection(text);
                break;
            case 2 : //Description
                descriptionRegExField.replaceSelection(text);
                break;
        }
    }

    public Element getRegexLibraryEntry(){
        /* <entry name="Demonstratives">
                <regex>\b[Tt]h(is|at|ose|ese)\b</regex>
                <description>Matches English demonstrative pronouns in small and capital variants</description>
                <explanation>A word boundary, followed by a capital or small <i>T</i>, followed by an <i>h</i>, followed by one of the strings <i>is</i>, <i>at</i>, <i>ose</i> or <i>ese</i>, followed by a word boundary.</explanation>
                <examples>
                    <example>This</example>
                    <example>this</example>
                    <example>Those</example>
                    <example>those</example>
                </examples>
        </entry> */
        Element entry = new Element("entry");
        entry.setAttribute("name", "--- new entry ---");
        Element regex = new Element("regex");
        switch (this.searchTypeComboBox.getSelectedIndex()){
            case 0 : //Transcription
                JTextField t = (JTextField)(searchExpressionComboBox.getEditor().getEditorComponent());
                regex.setText(t.getText());
                break;
            case 1 : //Annotation
                regex.setText(annotationRegExField.getText());
                break;
            case 2 : //Description
                regex.setText(descriptionRegExField.getText());
                break;
        }
        entry.addContent(regex);
        entry.addContent(new Element("description"));
        entry.addContent(new Element("explanation"));
        Element examples = new Element("examples");
        for (Object type : getSearchResultList().getTypes()){
            Element example = new Element("example");
            example.setText((String)type);
            examples.addContent(example);
        }
        entry.addContent(examples);
        return entry;
    }

    
        
}
