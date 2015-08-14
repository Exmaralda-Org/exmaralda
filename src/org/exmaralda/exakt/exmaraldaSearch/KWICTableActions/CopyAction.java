/*
 * WordWiseReversedSortAction.java
 *
 * Created on 19. Februar 2007, 13:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.prefs.Preferences;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.jdom.*;
import org.jdom.transform.*;
import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import org.xml.sax.SAXException;
import org.exmaralda.exakt.search.SearchResultInterface;
import org.exmaralda.exakt.search.SearchResultList;
import org.exmaralda.exakt.exmaraldaSearch.*;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;

/**
 *
 * @author thomas
 */
public class CopyAction extends AbstractKWICTableAction {
    
    private COMACorpusInterface corpus;
    private Vector<String[]> meta;
    
    
    /** Creates a new instance of WordWiseReversedSortAction */
    public CopyAction(COMAKWICTable t, String title) {
        super(t,title);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));                        
    }

    public void actionPerformed(ActionEvent e) {
        COMAKWICTable t = this.table;
        int[] viewSelections = t.getSelectedRows();
        if (viewSelections.length<=0) return;
        SearchResultList selected = new SearchResultList();
        for (int viewSelection : viewSelections){
            int selectedRow = ((COMAKWICTableSorter)(t.getModel())).modelIndex(viewSelection);
            SearchResultInterface sr = t.getWrappedModel().getSearchResultAt(selectedRow);
            selected.addSearchResult(sr);
        }
        Document doc = org.exmaralda.exakt.utilities.FileIO.COMASearchResultListToXML(selected, corpus, meta, t.getWrappedModel().getCorpus().getCorpusPath());

        // prepare the transformation                
        Preferences prefs = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.EXAKT");
        String pathToXSL = prefs.get("xsl-concordance-output", "");

        XSLTransformer transformer = null;
        boolean failed = false;
        if (pathToXSL.length()>0){
            try {
                // try to use external stylesheet
                org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory ssf =
                        new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory(true);
                String result = 
                        ssf.applyExternalStylesheetToString(pathToXSL, org.exmaralda.exakt.utilities.FileIO.getDocumentAsString(doc));
                doc = org.exmaralda.exakt.utilities.FileIO.readDocumentFromString(result);
            } catch (Exception ex) {
                ex.printStackTrace();
                String message = "There is a problem with " + pathToXSL + ": \n";
                message += ex.getMessage() + "\n";
                message += "Using default stylesheet instead.";
                failed = true;
                javax.swing.JOptionPane.showMessageDialog(table, message);                
            }
        //transformer = new XSLTransformer(pathToXSL);
        }
        if ((pathToXSL.length()==0) || failed) {
            try {
                // use internal stylesheet
                java.io.InputStream is = getClass().getResourceAsStream(org.exmaralda.exakt.exmaraldaSearch.KWICTableActions.SaveSearchResultAsAction.PATH_TO_INTERNAL_STYLESHEET);
                transformer = new XSLTransformer(is);
                doc = transformer.transform(doc);
            } catch (XSLTransformException ex) {
                String message = "Stylesheet transformation failed:";
                message += ex.getMessage() + "\n";
                javax.swing.JOptionPane.showMessageDialog(table, message);
                ex.printStackTrace();
                return;
            }
        }                    
        try {
            String text = org.exmaralda.exakt.utilities.FileIO.getDocumentAsString(doc);
            org.exmaralda.exakt.utilities.HTMLSelection html = new org.exmaralda.exakt.utilities.HTMLSelection(text);
            table.getToolkit().getSystemClipboard().setContents(html,null);
        } catch (HeadlessException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setCorpus(COMACorpusInterface corpus) {
        this.corpus = corpus;
    }

    public void setMeta(Vector<String[]> meta) {
        this.meta = meta;
    }
    
}
