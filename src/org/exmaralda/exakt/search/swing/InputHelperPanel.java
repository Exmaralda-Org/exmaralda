/*
 * InputHelperPanel.java
 *
 * Created on 19. Januar 2007, 14:42
 */

package org.exmaralda.exakt.search.swing;

import org.exmaralda.partitureditor.unicodeKeyboard.*;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author  thomas
 */
public class InputHelperPanel extends javax.swing.JPanel implements UnicodeKeyboardListener {
    
    private static final String[] ANY_CHARACTER = {
        "[A-Za-z]", // 0 : DEFAULT
        "[A-Za-zÄÖÜäöüß]", // 1 : GERMAN
        "[A-Za-z\u00C2\u00C7\u011E\u0130\u00CE\u00D6\u015E\u0160\u00DB\u00DC\u00E2\u00E7\u011F\u00EE\u0131\u00F6\u015F\u0161\u00FB\u00FC]", // 2 : TURKISH
        "[A-Za-z\u00C6\u00E6\u00C5\u00E5\u00C4\u00E4\u00D0\u00F0\u00D8\u00F8\u00D6\u00F6\u00DE\u00FE]", // 3 : SCANDINAVIAN
        "[A-Za-zÀÁÂÃÇÉÊÍÓÔÕÚàáâãçéêíóôõú]", // 4 : PORTUGUESE
        "[A-Za-zÀÈÉÌÍÎÓÒÚÙàèéìíîóòúù]", // 5 : ITALIAN
        "[A-Za-zÑñ]", // 6 : SPANISH
        "[A-Za-zÀÂÇÉÈÊËÎÏÔŒÙÛàâçéèêëïîôœûù]", // 7 : FRENCH
        "[\\p{L}]" // 8 : ANY KIND OF ALPHABET
        
    };
    
    private static final String[] UPPER_CASE_CHARACTER = {
        "[A-Z]", // 0 : DEFAULT
        "[A-ZÄÖÜ]", // 1 : GERMAN
        "[A-Z\u00C2\u00C7\u011E\u0130\u00CE\u00D6\u015E\u0160\u00DB\u00DC]", // 2 : TURKISH
        "[A-Z\u00C6\u00C5\u00C4\u00D0\u00D8\u00D6\u00DE]", // 3 : SCANDINAVIAN
        "[A-ZÀÁÂÃÇÉÊÍÓÔÕÚ]", // 4 : PORTUGUESE
        "[A-ZÀÈÉÌÍÎÓÒÚÙ]", // 5 : ITALIAN
        "[A-ZÑ]", // 6 : SPANISH
        "[A-ZÀÂÇÉÈÊËÎÏÔŒÙÛ]", // 7 : FRENCH
        "[\\p{Lu}]" // 8 : ANY KIND OF ALPHABET
    };

    private static final String[] LOWER_CASE_CHARACTER = {
        "[a-z]", // 0 : DEFAULT
        "[a-zäöüß]", // 1 : GERMAN
        "[a-z\u00E2\u00E7\u011F\u00EE\u0131\u00F6\u015F\u0161\u00FB\u00FC]", // 2 : TURKISH
        "[a-z\u00E6\u00E5\u00E4\u00F0\u00F8\u00F6\u00FE]", // 3 : SCANDINAVIAN
        "[a-zàáâãçéêíóôõú]", // 4 : PORTUGUESE
        "[a-zàèéìíîóòúù]", // 5 : ITALIAN
        "[a-záéíñóúü]", // 6 : SPANISH
        "[a-zàâçéèêëïîôœûù]", // 7 : FRENCH
        "[\\p{Ll}]" // 8 : ANY KIND OF ALPHABET
    };
    
    Comparator<String> caseIgnoringComparator = new java.util.Comparator<>() {
        @Override
        public int compare(String s1, String s2) {
            return s1.toUpperCase().compareTo(s2.toUpperCase());
        }
    };
    
    DefaultListModel unselectedListModel = new DefaultListModel();;
    DefaultListModel selectedListModel = new DefaultListModel();;
    
    private final UnicodeKeyboardPanel keyboardPanel;
    //private SearchHistoryComboBox searchHistoryComboBox;
    RegularExpressionTextField textField;
            
    /** Creates new form InputHelperPanel */
    public InputHelperPanel() {
        initComponents();
        keyboardPanel = new UnicodeKeyboardPanel(new String[0], "Arial Unicode MS");
        tabbedPane.addTab("Keyboard", null, keyboardPanel, "Virtual keyboard");        
        keyboardPanel.addListener(this);
        typeHelpersPanel.setVisible(false);
        textField = new RegularExpressionTextField();
        searchExpressionTextFieldPanel.add(textField,0);
        alphabetPanel.add(javax.swing.Box.createHorizontalGlue(),2);
        MACOSHarmonizer.harmonize(this);
    }

    
    private String getAnyCharacter(){
        int index = alphabetComboBox.getSelectedIndex();
        return ANY_CHARACTER[index];
    }

    private String getLowerCaseCharacter(){
        int index = alphabetComboBox.getSelectedIndex();
        return LOWER_CASE_CHARACTER[index];
    }
    
    private String getUpperCaseCharacter(){
        int index = alphabetComboBox.getSelectedIndex();
        return UPPER_CASE_CHARACTER[index];
    }
    
    public void setTypes(HashSet<String> types){
        Vector<String> v = new Vector<>();
        v.addAll(types);
        Collections.sort(v, caseIgnoringComparator);
        unselectedListModel = new DefaultListModel();
        for (String type : v){
            unselectedListModel.addElement(type);
        }
        unselectedTypesList.setModel(unselectedListModel);
        
        selectedListModel = new DefaultListModel();
        selectedTypesList.setModel(selectedListModel);
    }

    @Override
    public void performUnicodeKeyboardAction(UnicodeKeyboardEvent event) {
        insertText(event.getText());
    }

/*    public SearchHistoryComboBox getSearchHistoryComboBox() {
        return searchHistoryComboBox;
    }

    public void setSearchHistoryComboBox(SearchHistoryComboBox searchHistoryComboBox) {
        this.searchHistoryComboBox = searchHistoryComboBox;
    }*/
    
    private void insertText(String text){
        textField.replaceSelection(text);
        textField.requestFocus();
    }
    
    public String getSearchExpression(){
        return textField.getText();
    }

    public void setSearchExpression(String text){
        textField.setText(text);
        textField.selectAll();
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        searchExpressionPanel = new javax.swing.JPanel();
        standardRegexPanel = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        ZeroOrOneButton = new javax.swing.JButton();
        ZeroOrMoreButton = new javax.swing.JButton();
        OneOrMoreButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        AnyAlphabeticButton = new javax.swing.JButton();
        UpperAlphabeticButton = new javax.swing.JButton();
        LowerAlphabeticButton = new javax.swing.JButton();
        AnyDigitButton = new javax.swing.JButton();
        AnyCharacterButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        OrButton = new javax.swing.JButton();
        NotButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        wordHelpersPanel = new javax.swing.JPanel();
        wordPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        wordTextField = new javax.swing.JTextField();
        wordButton = new javax.swing.JButton();
        wordStartsWithPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        wordStartsWithTextField = new javax.swing.JTextField();
        wordStartsWithButton = new javax.swing.JButton();
        wordEndsWithPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        wordEndsWithTextField = new javax.swing.JTextField();
        wordEndsWithButton = new javax.swing.JButton();
        wordContainsPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        wordContainsTextField = new javax.swing.JTextField();
        wordContainsButton = new javax.swing.JButton();
        typeHelpersPanel = new javax.swing.JPanel();
        typeRightSidePanel = new javax.swing.JPanel();
        addTypeButton = new javax.swing.JButton();
        copyButton = new javax.swing.JButton();
        listsPanel = new javax.swing.JPanel();
        unselectedTypesScrollPane = new javax.swing.JScrollPane();
        unselectedTypesList = new javax.swing.JList();
        selectButtonsPanel = new javax.swing.JPanel();
        selectAllButton = new javax.swing.JButton();
        selectButton = new javax.swing.JButton();
        unselectButton = new javax.swing.JButton();
        unselectAllButton = new javax.swing.JButton();
        selectedTypesScrollPane = new javax.swing.JScrollPane();
        selectedTypesList = new javax.swing.JList();
        alphabetContainerPanel = new javax.swing.JPanel();
        okCancelPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        alphabetPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        alphabetComboBox = new javax.swing.JComboBox();
        searchExpressionTextFieldPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        tabbedPane.setBackground(new java.awt.Color(255, 255, 255));
        tabbedPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));
        tabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tabbedPane.setMinimumSize(new java.awt.Dimension(200, 200));
        tabbedPane.setOpaque(true);
        tabbedPane.setPreferredSize(new java.awt.Dimension(400, 400));

        searchExpressionPanel.setLayout(new java.awt.BorderLayout());

        standardRegexPanel.setPreferredSize(new java.awt.Dimension(541, 35));
        standardRegexPanel.setRequestFocusEnabled(false);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setMinimumSize(new java.awt.Dimension(2, 25));
        jSeparator3.setPreferredSize(new java.awt.Dimension(2, 25));
        standardRegexPanel.add(jSeparator3);

        ZeroOrOneButton.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ZeroOrOneButton.setText("0-1");
        ZeroOrOneButton.setToolTipText("Once or not at all");
        ZeroOrOneButton.setMaximumSize(new java.awt.Dimension(45, 25));
        ZeroOrOneButton.setMinimumSize(new java.awt.Dimension(45, 25));
        ZeroOrOneButton.setPreferredSize(new java.awt.Dimension(45, 25));
        ZeroOrOneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ZeroOrOneButtonActionPerformed(evt);
            }
        });
        standardRegexPanel.add(ZeroOrOneButton);

        ZeroOrMoreButton.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        ZeroOrMoreButton.setText("0-n");
        ZeroOrMoreButton.setToolTipText("Zero or more times");
        ZeroOrMoreButton.setMaximumSize(new java.awt.Dimension(45, 25));
        ZeroOrMoreButton.setMinimumSize(new java.awt.Dimension(45, 25));
        ZeroOrMoreButton.setPreferredSize(new java.awt.Dimension(45, 25));
        ZeroOrMoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ZeroOrMoreButtonActionPerformed(evt);
            }
        });
        standardRegexPanel.add(ZeroOrMoreButton);

        OneOrMoreButton.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        OneOrMoreButton.setText("1-n");
        OneOrMoreButton.setToolTipText("One or more times");
        OneOrMoreButton.setMaximumSize(new java.awt.Dimension(45, 25));
        OneOrMoreButton.setMinimumSize(new java.awt.Dimension(45, 25));
        OneOrMoreButton.setPreferredSize(new java.awt.Dimension(45, 25));
        OneOrMoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OneOrMoreButtonActionPerformed(evt);
            }
        });
        standardRegexPanel.add(OneOrMoreButton);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setMinimumSize(new java.awt.Dimension(2, 25));
        jSeparator1.setPreferredSize(new java.awt.Dimension(2, 25));
        standardRegexPanel.add(jSeparator1);

        AnyAlphabeticButton.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        AnyAlphabeticButton.setText("Aa");
        AnyAlphabeticButton.setToolTipText("Any alphabetic character (A,a,B,b,..)");
        AnyAlphabeticButton.setMaximumSize(new java.awt.Dimension(45, 25));
        AnyAlphabeticButton.setMinimumSize(new java.awt.Dimension(45, 25));
        AnyAlphabeticButton.setPreferredSize(new java.awt.Dimension(45, 25));
        AnyAlphabeticButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnyAlphabeticButtonActionPerformed(evt);
            }
        });
        standardRegexPanel.add(AnyAlphabeticButton);

        UpperAlphabeticButton.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        UpperAlphabeticButton.setText("AB");
        UpperAlphabeticButton.setToolTipText("Any uppercase alphabetic character");
        UpperAlphabeticButton.setMaximumSize(new java.awt.Dimension(45, 25));
        UpperAlphabeticButton.setMinimumSize(new java.awt.Dimension(45, 25));
        UpperAlphabeticButton.setPreferredSize(new java.awt.Dimension(45, 25));
        UpperAlphabeticButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpperAlphabeticButtonActionPerformed(evt);
            }
        });
        standardRegexPanel.add(UpperAlphabeticButton);

        LowerAlphabeticButton.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        LowerAlphabeticButton.setText("ab");
        LowerAlphabeticButton.setToolTipText("Any lowercase alphabetic character");
        LowerAlphabeticButton.setMaximumSize(new java.awt.Dimension(45, 25));
        LowerAlphabeticButton.setMinimumSize(new java.awt.Dimension(45, 25));
        LowerAlphabeticButton.setPreferredSize(new java.awt.Dimension(45, 25));
        LowerAlphabeticButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LowerAlphabeticButtonActionPerformed(evt);
            }
        });
        standardRegexPanel.add(LowerAlphabeticButton);

        AnyDigitButton.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        AnyDigitButton.setText("01");
        AnyDigitButton.setToolTipText("Any digit (0,1,...,9)");
        AnyDigitButton.setMaximumSize(new java.awt.Dimension(45, 25));
        AnyDigitButton.setMinimumSize(new java.awt.Dimension(45, 25));
        AnyDigitButton.setPreferredSize(new java.awt.Dimension(45, 25));
        AnyDigitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnyDigitButtonActionPerformed(evt);
            }
        });
        standardRegexPanel.add(AnyDigitButton);

        AnyCharacterButton.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        AnyCharacterButton.setText("Any");
        AnyCharacterButton.setToolTipText("Any character");
        AnyCharacterButton.setMaximumSize(new java.awt.Dimension(50, 25));
        AnyCharacterButton.setMinimumSize(new java.awt.Dimension(50, 25));
        AnyCharacterButton.setPreferredSize(new java.awt.Dimension(50, 25));
        AnyCharacterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnyCharacterButtonActionPerformed(evt);
            }
        });
        standardRegexPanel.add(AnyCharacterButton);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setMinimumSize(new java.awt.Dimension(2, 25));
        jSeparator2.setPreferredSize(new java.awt.Dimension(2, 25));
        standardRegexPanel.add(jSeparator2);

        OrButton.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        OrButton.setText("OR");
        OrButton.setToolTipText("Alternative");
        OrButton.setMaximumSize(new java.awt.Dimension(50, 25));
        OrButton.setMinimumSize(new java.awt.Dimension(50, 25));
        OrButton.setPreferredSize(new java.awt.Dimension(50, 25));
        OrButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OrButtonActionPerformed(evt);
            }
        });
        standardRegexPanel.add(OrButton);

        NotButton.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        NotButton.setText("Not");
        NotButton.setToolTipText("Alternative");
        NotButton.setMaximumSize(new java.awt.Dimension(50, 25));
        NotButton.setMinimumSize(new java.awt.Dimension(50, 25));
        NotButton.setPreferredSize(new java.awt.Dimension(50, 25));
        NotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NotButtonActionPerformed(evt);
            }
        });
        standardRegexPanel.add(NotButton);

        searchExpressionPanel.add(standardRegexPanel, java.awt.BorderLayout.NORTH);

        mainPanel.setLayout(new java.awt.BorderLayout());

        wordHelpersPanel.setLayout(new java.awt.GridLayout(6, 1));

        wordPanel.setLayout(new javax.swing.BoxLayout(wordPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel2.setText("Word:");
        jLabel2.setMaximumSize(new java.awt.Dimension(90, 14));
        jLabel2.setMinimumSize(new java.awt.Dimension(90, 14));
        jLabel2.setPreferredSize(new java.awt.Dimension(90, 14));
        wordPanel.add(jLabel2);

        wordTextField.setMaximumSize(new java.awt.Dimension(300, 23));
        wordTextField.setMinimumSize(new java.awt.Dimension(100, 23));
        wordTextField.setPreferredSize(new java.awt.Dimension(300, 23));
        wordTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wordTextFieldActionPerformed(evt);
                wordTextFieldjTextField1ActionPerformed(evt);
            }
        });
        wordPanel.add(wordTextField);

        wordButton.setText("!");
        wordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wordButtonActionPerformed(evt);
            }
        });
        wordPanel.add(wordButton);

        wordHelpersPanel.add(wordPanel);

        wordStartsWithPanel.setMaximumSize(new java.awt.Dimension(419, 23));
        wordStartsWithPanel.setPreferredSize(new java.awt.Dimension(419, 23));
        wordStartsWithPanel.setLayout(new javax.swing.BoxLayout(wordStartsWithPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setText("Word starts with: ");
        jLabel1.setMaximumSize(new java.awt.Dimension(90, 14));
        jLabel1.setMinimumSize(new java.awt.Dimension(90, 14));
        jLabel1.setPreferredSize(new java.awt.Dimension(90, 14));
        wordStartsWithPanel.add(jLabel1);

        wordStartsWithTextField.setMaximumSize(new java.awt.Dimension(300, 23));
        wordStartsWithTextField.setMinimumSize(new java.awt.Dimension(100, 23));
        wordStartsWithTextField.setPreferredSize(new java.awt.Dimension(300, 23));
        wordStartsWithTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wordStartsWithTextFieldActionPerformed(evt);
                jTextField1ActionPerformed(evt);
            }
        });
        wordStartsWithPanel.add(wordStartsWithTextField);

        wordStartsWithButton.setText("!");
        wordStartsWithButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wordStartsWithButtonActionPerformed(evt);
            }
        });
        wordStartsWithPanel.add(wordStartsWithButton);

        wordHelpersPanel.add(wordStartsWithPanel);

        wordEndsWithPanel.setLayout(new javax.swing.BoxLayout(wordEndsWithPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setText("Word ends with: ");
        jLabel3.setMaximumSize(new java.awt.Dimension(90, 14));
        jLabel3.setMinimumSize(new java.awt.Dimension(90, 14));
        jLabel3.setPreferredSize(new java.awt.Dimension(90, 14));
        wordEndsWithPanel.add(jLabel3);

        wordEndsWithTextField.setMaximumSize(new java.awt.Dimension(300, 23));
        wordEndsWithTextField.setMinimumSize(new java.awt.Dimension(100, 23));
        wordEndsWithTextField.setPreferredSize(new java.awt.Dimension(300, 23));
        wordEndsWithTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wordEndsWithTextFieldActionPerformed(evt);
                wordEndsWithTextFieldjTextField1ActionPerformed(evt);
            }
        });
        wordEndsWithPanel.add(wordEndsWithTextField);

        wordEndsWithButton.setText("!");
        wordEndsWithButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wordEndsWithButtonActionPerformed(evt);
            }
        });
        wordEndsWithPanel.add(wordEndsWithButton);

        wordHelpersPanel.add(wordEndsWithPanel);

        wordContainsPanel.setLayout(new javax.swing.BoxLayout(wordContainsPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel4.setText("Word contains: ");
        jLabel4.setMaximumSize(new java.awt.Dimension(90, 14));
        jLabel4.setMinimumSize(new java.awt.Dimension(90, 14));
        jLabel4.setPreferredSize(new java.awt.Dimension(90, 14));
        wordContainsPanel.add(jLabel4);

        wordContainsTextField.setMaximumSize(new java.awt.Dimension(300, 23));
        wordContainsTextField.setMinimumSize(new java.awt.Dimension(100, 23));
        wordContainsTextField.setPreferredSize(new java.awt.Dimension(300, 23));
        wordContainsTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wordContainsTextFieldActionPerformed(evt);
                wordContainsTextFieldjTextField1ActionPerformed(evt);
            }
        });
        wordContainsPanel.add(wordContainsTextField);

        wordContainsButton.setText("!");
        wordContainsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wordContainsButtonActionPerformed(evt);
            }
        });
        wordContainsPanel.add(wordContainsButton);

        wordHelpersPanel.add(wordContainsPanel);

        jTabbedPane1.addTab("Words", wordHelpersPanel);

        typeHelpersPanel.setLayout(new java.awt.BorderLayout());

        typeRightSidePanel.setLayout(new javax.swing.BoxLayout(typeRightSidePanel, javax.swing.BoxLayout.Y_AXIS));

        addTypeButton.setText("Add");
        addTypeButton.setToolTipText("Add the selected type");
        addTypeButton.setMaximumSize(new java.awt.Dimension(57, 23));
        addTypeButton.setMinimumSize(new java.awt.Dimension(57, 23));
        addTypeButton.setPreferredSize(new java.awt.Dimension(57, 23));
        addTypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTypeButtonActionPerformed(evt);
            }
        });
        typeRightSidePanel.add(addTypeButton);

        copyButton.setText("Copy");
        copyButton.setToolTipText("Copy the list to the clipboard");
        copyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyButtonActionPerformed(evt);
            }
        });
        typeRightSidePanel.add(copyButton);

        typeHelpersPanel.add(typeRightSidePanel, java.awt.BorderLayout.EAST);

        listsPanel.setLayout(new javax.swing.BoxLayout(listsPanel, javax.swing.BoxLayout.LINE_AXIS));

        unselectedTypesScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Unselected"));
        unselectedTypesScrollPane.setMinimumSize(new java.awt.Dimension(150, 50));

        unselectedTypesScrollPane.setViewportView(unselectedTypesList);

        listsPanel.add(unselectedTypesScrollPane);

        selectButtonsPanel.setLayout(new javax.swing.BoxLayout(selectButtonsPanel, javax.swing.BoxLayout.Y_AXIS));

        selectAllButton.setText(">>");
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });
        selectButtonsPanel.add(selectAllButton);

        selectButton.setText(">");
        selectButton.setMaximumSize(new java.awt.Dimension(49, 23));
        selectButton.setMinimumSize(new java.awt.Dimension(49, 23));
        selectButton.setPreferredSize(new java.awt.Dimension(49, 23));
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });
        selectButtonsPanel.add(selectButton);

        unselectButton.setText("<");
        unselectButton.setMaximumSize(new java.awt.Dimension(49, 23));
        unselectButton.setMinimumSize(new java.awt.Dimension(49, 23));
        unselectButton.setPreferredSize(new java.awt.Dimension(49, 23));
        unselectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unselectButtonActionPerformed(evt);
            }
        });
        selectButtonsPanel.add(unselectButton);

        unselectAllButton.setText("<<");
        unselectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unselectAllButtonActionPerformed(evt);
            }
        });
        selectButtonsPanel.add(unselectAllButton);

        listsPanel.add(selectButtonsPanel);

        selectedTypesScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected"));
        selectedTypesScrollPane.setMinimumSize(new java.awt.Dimension(150, 50));

        selectedTypesScrollPane.setViewportView(selectedTypesList);

        listsPanel.add(selectedTypesScrollPane);

        typeHelpersPanel.add(listsPanel, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Types", typeHelpersPanel);

        mainPanel.add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        searchExpressionPanel.add(mainPanel, java.awt.BorderLayout.CENTER);

        alphabetContainerPanel.setLayout(new java.awt.BorderLayout());

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        okCancelPanel.add(okButton);

        cancelButton.setText("Cancel");
        okCancelPanel.add(cancelButton);

        alphabetContainerPanel.add(okCancelPanel, java.awt.BorderLayout.EAST);

        jLabel5.setText("Alphabet: ");
        alphabetPanel.add(jLabel5);

        alphabetComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "German", "Turkish", "Scandinavian", "Portuguese", "Italian", "Spanish", "French", "Any character" }));
        alphabetComboBox.setMaximumSize(new java.awt.Dimension(55, 22));
        alphabetPanel.add(alphabetComboBox);

        alphabetContainerPanel.add(alphabetPanel, java.awt.BorderLayout.WEST);

        searchExpressionPanel.add(alphabetContainerPanel, java.awt.BorderLayout.SOUTH);

        tabbedPane.addTab("Search expression", null, searchExpressionPanel, "Helpers for inputting regular expression");

        add(tabbedPane, java.awt.BorderLayout.CENTER);

        searchExpressionTextFieldPanel.setLayout(new javax.swing.BoxLayout(searchExpressionTextFieldPanel, javax.swing.BoxLayout.LINE_AXIS));
        add(searchExpressionTextFieldPanel, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    private void unselectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unselectAllButtonActionPerformed
        Object[] toBeAdded = selectedListModel.toArray();        
        for (Object o : toBeAdded){
            unselectedListModel.addElement(o);
            selectedListModel.removeElement(o);
        }
    }//GEN-LAST:event_unselectAllButtonActionPerformed

    private void unselectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unselectButtonActionPerformed
        Object[] toBeAdded = selectedTypesList.getSelectedValues();
        for (Object o : toBeAdded){            
            unselectedListModel.addElement(o);
            selectedListModel.removeElement(o);
        }
    }//GEN-LAST:event_unselectButtonActionPerformed

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        Object[] toBeAdded = unselectedListModel.toArray();        
        for (Object o : toBeAdded){
            selectedListModel.addElement(o);
            unselectedListModel.removeElement(o);
        }
    }//GEN-LAST:event_selectAllButtonActionPerformed

    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        Object[] toBeAdded = unselectedTypesList.getSelectedValues();
        for (Object o : toBeAdded){
            selectedListModel.addElement(o);
            unselectedListModel.removeElement(o);
        }
    }//GEN-LAST:event_selectButtonActionPerformed

    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyButtonActionPerformed
        StringBuilder all = new StringBuilder();
        for (int pos=0; pos<selectedTypesList.getModel().getSize(); pos++){
            String type = (String)(selectedTypesList.getModel().getElementAt(pos));
            all.append(type + "\n");
        }
        java.awt.datatransfer.StringSelection ss = new java.awt.datatransfer.StringSelection(all.toString());
        this.getToolkit().getSystemClipboard().setContents(ss,ss);        
    }//GEN-LAST:event_copyButtonActionPerformed

    private void addTypeButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTypeButton1ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_addTypeButton1ActionPerformed

    private void addTypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTypeButtonActionPerformed
        //Object[] sel = selectedTypesList.getSelectedValues();
        Object[] sel = selectedListModel.toArray();
        String text = "";
        int pos=0;
        for (Object o : sel){
            String s = (String)(o);
            text+="\\Q" + s + "\\E";
            if (pos<sel.length-1) text+="|";
            pos++;
        }
        if (text.length()>0){
            insertText(text);
        }
    }//GEN-LAST:event_addTypeButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        
    }//GEN-LAST:event_okButtonActionPerformed

    private void NotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NotButtonActionPerformed
        insertText("^");
    }//GEN-LAST:event_NotButtonActionPerformed

    private void OrButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OrButtonActionPerformed
        insertText("|");
    }//GEN-LAST:event_OrButtonActionPerformed

    private void OneOrMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OneOrMoreButtonActionPerformed
        insertText("+");
    }//GEN-LAST:event_OneOrMoreButtonActionPerformed

    private void ZeroOrMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ZeroOrMoreButtonActionPerformed
        insertText("*");
    }//GEN-LAST:event_ZeroOrMoreButtonActionPerformed

    private void wordContainsTextFieldjTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordContainsTextFieldjTextField1ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_wordContainsTextFieldjTextField1ActionPerformed

    private void wordContainsTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordContainsTextFieldActionPerformed
        String text = "\\b" + getAnyCharacter() + "*" + wordContainsTextField.getText() + getAnyCharacter() + "*" + "\\b";
        insertText(text);
    }//GEN-LAST:event_wordContainsTextFieldActionPerformed

    private void wordEndsWithTextFieldjTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordEndsWithTextFieldjTextField1ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_wordEndsWithTextFieldjTextField1ActionPerformed

    private void wordEndsWithTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordEndsWithTextFieldActionPerformed
        String text = "\\b" + getAnyCharacter() + "*" + wordEndsWithTextField.getText() + "\\b";
        insertText(text);
    }//GEN-LAST:event_wordEndsWithTextFieldActionPerformed

    private void wordTextFieldjTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordTextFieldjTextField1ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_wordTextFieldjTextField1ActionPerformed

    private void wordTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordTextFieldActionPerformed
        String text = "\\b" + wordTextField.getText() + "\\b";
        insertText(text);
    }//GEN-LAST:event_wordTextFieldActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void wordStartsWithTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordStartsWithTextFieldActionPerformed
        String text = "\\b" + wordStartsWithTextField.getText() + getAnyCharacter() + "*\\b";
        insertText(text);
    }//GEN-LAST:event_wordStartsWithTextFieldActionPerformed

    private void AnyCharacterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnyCharacterButtonActionPerformed
        insertText(".");
    }//GEN-LAST:event_AnyCharacterButtonActionPerformed

    private void AnyDigitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnyDigitButtonActionPerformed
        insertText("\\d");
    }//GEN-LAST:event_AnyDigitButtonActionPerformed

    private void LowerAlphabeticButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LowerAlphabeticButtonActionPerformed
        insertText(getLowerCaseCharacter());
    }//GEN-LAST:event_LowerAlphabeticButtonActionPerformed

    private void UpperAlphabeticButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpperAlphabeticButtonActionPerformed
        insertText(getUpperCaseCharacter());
    }//GEN-LAST:event_UpperAlphabeticButtonActionPerformed

    private void AnyAlphabeticButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnyAlphabeticButtonActionPerformed
        insertText(getAnyCharacter());
    }//GEN-LAST:event_AnyAlphabeticButtonActionPerformed

    private void ZeroOrOneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ZeroOrOneButtonActionPerformed
        insertText("?");
    }//GEN-LAST:event_ZeroOrOneButtonActionPerformed

    private void wordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordButtonActionPerformed
        wordTextFieldActionPerformed(evt);
    }//GEN-LAST:event_wordButtonActionPerformed

    private void wordStartsWithButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordStartsWithButtonActionPerformed
        wordStartsWithTextFieldActionPerformed(evt);
    }//GEN-LAST:event_wordStartsWithButtonActionPerformed

    private void wordEndsWithButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordEndsWithButtonActionPerformed
        wordEndsWithTextFieldActionPerformed(evt);
    }//GEN-LAST:event_wordEndsWithButtonActionPerformed

    private void wordContainsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordContainsButtonActionPerformed
        wordContainsTextFieldActionPerformed(evt);
    }//GEN-LAST:event_wordContainsButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AnyAlphabeticButton;
    private javax.swing.JButton AnyCharacterButton;
    private javax.swing.JButton AnyDigitButton;
    private javax.swing.JButton LowerAlphabeticButton;
    private javax.swing.JButton NotButton;
    private javax.swing.JButton OneOrMoreButton;
    private javax.swing.JButton OrButton;
    private javax.swing.JButton UpperAlphabeticButton;
    private javax.swing.JButton ZeroOrMoreButton;
    private javax.swing.JButton ZeroOrOneButton;
    private javax.swing.JButton addTypeButton;
    private javax.swing.JComboBox alphabetComboBox;
    private javax.swing.JPanel alphabetContainerPanel;
    private javax.swing.JPanel alphabetPanel;
    javax.swing.JButton cancelButton;
    private javax.swing.JButton copyButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel listsPanel;
    private javax.swing.JPanel mainPanel;
    javax.swing.JButton okButton;
    private javax.swing.JPanel okCancelPanel;
    private javax.swing.JPanel searchExpressionPanel;
    private javax.swing.JPanel searchExpressionTextFieldPanel;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JButton selectButton;
    private javax.swing.JPanel selectButtonsPanel;
    private javax.swing.JList selectedTypesList;
    private javax.swing.JScrollPane selectedTypesScrollPane;
    private javax.swing.JPanel standardRegexPanel;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JPanel typeHelpersPanel;
    private javax.swing.JPanel typeRightSidePanel;
    private javax.swing.JButton unselectAllButton;
    private javax.swing.JButton unselectButton;
    private javax.swing.JList unselectedTypesList;
    private javax.swing.JScrollPane unselectedTypesScrollPane;
    private javax.swing.JButton wordButton;
    private javax.swing.JButton wordContainsButton;
    private javax.swing.JPanel wordContainsPanel;
    private javax.swing.JTextField wordContainsTextField;
    private javax.swing.JButton wordEndsWithButton;
    private javax.swing.JPanel wordEndsWithPanel;
    private javax.swing.JTextField wordEndsWithTextField;
    private javax.swing.JPanel wordHelpersPanel;
    private javax.swing.JPanel wordPanel;
    private javax.swing.JButton wordStartsWithButton;
    private javax.swing.JPanel wordStartsWithPanel;
    private javax.swing.JTextField wordStartsWithTextField;
    private javax.swing.JTextField wordTextField;
    // End of variables declaration//GEN-END:variables
    
}
