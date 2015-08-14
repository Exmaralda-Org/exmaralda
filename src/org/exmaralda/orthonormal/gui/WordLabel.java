/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import org.exmaralda.orthonormal.application.ApplicationControl;
import org.exmaralda.orthonormal.lexicon.LexiconException;
import org.exmaralda.orthonormal.lexicon.SimpleXMLFileLexicon;
import org.jdom.Element;
import org.jdom.filter.AbstractFilter;

/**
 *
 * @author thomas
 */
public class WordLabel extends JLabel implements MouseListener {

    Element wordElement;
    ApplicationControl applicationControl;
    WordLabelPopupPanel popupPanel = new WordLabelPopupPanel();
    JDialog popupPanelDialog;
    
    AbstractFilter textFilter = new AbstractFilter(){
        public boolean matches(Object o) {
            return ((o instanceof org.jdom.Text) && ((org.jdom.Text)o).getTextTrim().length()>0);
        }
    };

    public WordLabel(Element wordElement, ApplicationControl ac) {
        applicationControl = ac;
        this.wordElement = wordElement;
        addMouseListener(this);
        setBackground(Color.WHITE);
        setOpaque(true);
        setFont(this.getFont().deriveFont(16.0f));
        setWord(false);
    }

    public Element getWordElement(){
        return wordElement;
    }

    void setWord(){
        setWord(true);
        //setWord(applicationControl.lexicon instanceof SimpleXMLFileLexicon);
    }

    String getWordText(Element wordElement){        
        Iterator i = wordElement.getDescendants(textFilter);
        StringBuilder result = new StringBuilder();
        while (i.hasNext()){
            result.append(((org.jdom.Text)(i.next())).getText());
        }
        return result.toString();
    }

    void setWord(boolean lexiconUpdateNecessary){
        //String form = wordElement.getText();
        String form = getWordText(wordElement);
        String normalizedForm = form;
        if (wordElement.getAttribute("n")!=null){
            normalizedForm = wordElement.getAttributeValue("n");
        }
        boolean isTagged = wordElement.getAttribute("lemma")!=null;
        String text = "<html>";
        if (form.equals(normalizedForm)){
            //this.setText(wordElement.getText());
            //this.setText(getWordText(wordElement));
            text+=getWordText(wordElement);
            this.setForeground(Color.BLACK);
            this.setFont(this.getFont().deriveFont(Font.PLAIN));
        } else {
            this.setForeground(Color.RED);
            if (!isTagged){
                this.setFont(this.getFont().deriveFont(Font.BOLD));
            }
            if ((normalizedForm.startsWith(form))){
                // lemma starts with form
                text+="<font color='blue'>"
                        + form
                        + "</font>"
                        + "["
                        + "<font color='red'>"
                        //+ lemma.substring(wordElement.getText().length())
                        + normalizedForm.substring(form.length())
                        + "</font>"
                        + "]";
            } else if (normalizedForm.endsWith(form)){
                // lemma ends with form
                text+="[<font color='red'>"
                        + normalizedForm.substring(0, normalizedForm.length() - form.length())
                        + "</font>"
                        + "]"
                        + "<font color='blue'>"
                        + form
                        + "</font>";
            } else if ((normalizedForm.substring(0,1).equalsIgnoreCase(form.substring(0,1))) &&
                    (normalizedForm.substring(1).equals(form.substring(1)))){
                // lemma is capitalized form
                text+="[<font color='red'>"
                        + normalizedForm.substring(0,1)
                        + "</font>"
                        + "]"
                        + "<font color='blue'>"
                        + form.substring(1)
                        + "</font>";                        
            } else {
                // it is more complex
                text+="<font color='blue'>"
                        + form
                        + "</font>"
                        + " ["
                        + "<font color='red'>"
                        + normalizedForm
                        + "</font>"
                        + "]";
            }
        }
        if (isTagged && applicationControl.getMode()==ApplicationControl.TAGGING_MODE){
            text+="<small>";
            if (wordElement.getAttribute("p-pos")==null){
                text+="<font color='gray'>";
            } else {
                String pPos = wordElement.getAttributeValue("p-pos");
                double p = 1.0;
                if (!(pPos.contains(" "))){
                    p = Double.parseDouble(pPos);
                } else {
                    String[] ps = pPos.split(" ");
                    for (String thisP : ps){
                        p = Math.min(p, Double.parseDouble(thisP));
                    }
                }
                if (p>applicationControl.maximumTagProbability){
                    text+="<font color='gray'>";                        
                } else if (p<=applicationControl.maximumTagProbability && p>applicationControl.criticalTagProbability){
                    text+="<font color='black'>";
                } else {
                    text+="<font color='red'>";                        
                }
            }
            text+=" {";
            text+=wordElement.getAttributeValue("lemma");
            text+=" / ";
            text+=wordElement.getAttributeValue("pos");
            text+="}</font></small>";
        }            
        text+="</html>";
 
        this.setText(text);
        try {
            if (lexiconUpdateNecessary){
                applicationControl.lexicon.put(form, normalizedForm, applicationControl.getTranscription().getID(), wordElement.getAttributeValue("id"));
            }
        } catch (LexiconException ex) {
            ex.printStackTrace();
            applicationControl.displayException(ex);
        }
        applicationControl.wordListTableModel.updateWord(wordElement);
        if (wordElement.getParentElement().getName().equals("alternative")){
            //this.setBorder(javax.swing.border.LineBorder.createGrayLineBorder());
            this.setBorder(javax.swing.BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        }
    }

    void setupPopup(){
        popupPanel.setData(this.getWordElement());
        popupPanelDialog = new JDialog(applicationControl.getFrame(),false);
        popupPanelDialog.setUndecorated(true);
        popupPanelDialog.add(popupPanel);
        popupPanelDialog.pack();
        popupPanelDialog.setLocationRelativeTo(this);
        popupPanelDialog.setVisible(true);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e==null || (e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==1)){
            // programatic or
            // single left click
            editWord();
            applicationControl.updateContribution();
        } else if (e.getButton()==MouseEvent.BUTTON3 && e.getClickCount()==1){
            if (!(e.isControlDown())){
                // single right click
                if (applicationControl.getMode()==ApplicationControl.TAGGING_MODE){
                    //setupPopup();
                } else {
                    capitalizeWord();
                    applicationControl.updateContribution();
                }
            } else {
                // single right click and control
                removeLemma();
            }
        } else if (e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==2){
            // double left click
            // does not work - the first of the double click is processed before
            autoTagWord();
            applicationControl.updateContribution();   
        } 
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton()==MouseEvent.BUTTON3){
            if (!(e.isControlDown())){
                // single right click
                if (applicationControl.getMode()==ApplicationControl.TAGGING_MODE){
                    setupPopup();
                }
            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton()==MouseEvent.BUTTON3){
            if (!(e.isControlDown())){
                if (applicationControl.getMode()==ApplicationControl.TAGGING_MODE){
                    if (popupPanelDialog!=null){
                        popupPanelDialog.setVisible(false);
                    }
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.setBackground(Color.YELLOW);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.setBackground(Color.WHITE);
    }

    void editWord(){
        if (applicationControl.getMode()==ApplicationControl.NORMALIZATION_MODE){
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            WordNormalizationDialog dialog = new WordNormalizationDialog((JFrame)(getTopLevelAncestor()), true, wordElement, applicationControl.queryLexicon(wordElement.getText()));
            Point p = this.getLocationOnScreen();
            p.translate(0, -dialog.getHeight());
            dialog.setLocation(p);
            //dialog.setLocation(this.getPopupLocation(null));
            //dialog.setLocationRelativeTo(this);
            //AWTUtilities.setWindowOpacity(dialog, 0.6f);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            dialog.setVisible(true);
            if (!dialog.escaped){
                if (!(dialog.getNormalizedForm().equals(wordElement.getText()))){
                    wordElement.setAttribute("n", dialog.getNormalizedForm());
                } else {
                    wordElement.removeAttribute("n");
                }
                setWord();
                applicationControl.DOCUMENT_CHANGED = true;
                applicationControl.nextWord(this);
            }
        } else if (applicationControl.getMode()==ApplicationControl.TAGGING_MODE){
            TaggingDialog dialog = new TaggingDialog((JFrame)(getTopLevelAncestor()), true, wordElement);
            Point p = this.getLocationOnScreen();
            p.translate(0, -dialog.getHeight());
            dialog.setLocation(p);
            //dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (!dialog.escaped){
                wordElement.setAttribute("pos", dialog.getPOS());
                wordElement.setAttribute("lemma", dialog.getLemma());
                setWord();
                applicationControl.DOCUMENT_CHANGED = true;
                applicationControl.nextWord(this);
            }            
        }
    }

    void removeLemma(){
        wordElement.removeAttribute("n");
        setWord();
        applicationControl.DOCUMENT_CHANGED = true;
    }

    void autoTagWord(){
        String word = wordElement.getText();
        String lemma = applicationControl.queryLexicon(word).get(0);
        if (!(word.equals(lemma))){
            wordElement.setAttribute("n", lemma);
            setWord();
            applicationControl.DOCUMENT_CHANGED = true;
        }
    }

    void capitalizeWord(){
        String word = wordElement.getText();
        if (Character.isLowerCase(word.charAt(0))){
            String capitalizedWord = Character.toString(Character.toUpperCase(word.charAt(0)));
            if (word.length()>1){
                capitalizedWord+=word.substring(1);
            }
            wordElement.setAttribute("n", capitalizedWord);
            setWord();
            applicationControl.DOCUMENT_CHANGED = true;
        }
    }

    public void highlight() {
        this.setBackground(Color.GREEN);
    }

}
