/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaraldaswing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
//import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class WordListTableModel extends javax.swing.table.AbstractTableModel {

    List words;
    SegmentedTranscription transcription;

    public WordListTableModel(SegmentedTranscription st) {
        try {
            transcription = st;
            /*StylesheetFactory sf = new StylesheetFactory();
            String docString = sf.applyInternalStylesheetToString("/org/exmaralda/partitureditor/jexmaralda/xsl/Segmented2Wordlist.xsl", st.toXML());
            words = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString(docString).getRootElement().getChildren();*/
            // changed 25-05-2009: generating the wordlist with a stylesheet is very slow
            // use a custom method instead
            words = st.getBody().getWordList();
        } catch (Exception ex) {
            ex.printStackTrace();
            // do nothing
        }
    }

    public int getRowCount() {
        return words.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Element word = (Element)(words.get(rowIndex));
        switch (columnIndex){
            case 0 :
                return word.getText();
            case 1 :
                try {
                    return transcription.getHead().getSpeakertable().getSpeakerWithID(word.getAttributeValue("speaker")).getAbbreviation();
                } catch (JexmaraldaException ex) {
                    return "";
                }
            default : return "";
        }

    }

    Element getWordElement(int index) {
        return (Element)(words.get(index));
    }

    public int getTokenCount(){
        return this.getRowCount();
    }

    public int getTypeCount(){
        HashSet<String> types = new HashSet<String>();
        for (int pos=0; pos<getTokenCount(); pos++){
            types.add((String)(getValueAt(pos, 0)));
        }
        return types.size();
    }

}
