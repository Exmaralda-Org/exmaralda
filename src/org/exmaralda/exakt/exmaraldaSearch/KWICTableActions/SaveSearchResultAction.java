/*
 * SaveSearchResultAction.java
 *
 * Created on 19. Juni 2007, 16:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.*;
import java.util.*;
import org.jdom.*;
import org.jdom.transform.*;
import java.util.prefs.Preferences;
import org.exmaralda.exakt.search.SearchResultList;
import org.exmaralda.exakt.exmaraldaSearch.*;


/**
 *
 * @author thomas
 */

public class SaveSearchResultAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    public static final String PATH_TO_INTERNAL_STYLESHEET = "/org/exmaralda/exakt/resources/SearchResult2HTML.xsl";
    
    /** Creates a new instance of SaveSearchResultAction */
    public SaveSearchResultAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }
    
    public void actionPerformed(ActionEvent e) {   
        File file = exaktFrame.getActiveSearchPanel().getCurrentSearchResultFile();
        if (file==null){
            exaktFrame.saveSearchResultAsAction.actionPerformed(e);
            return;
        }
        String type = exaktFrame.getActiveSearchPanel().getCurrentSearchResultFileType();
        save(file,type);
    }

    public void save(File file, String type){
        if (file!=null){
            
            SearchResultList list = exaktFrame.getActiveSearchPanel().getSearchResultList();
            COMACorpusInterface corpus = exaktFrame.getActiveSearchPanel().getCorpus();
            Vector<String[]> meta = exaktFrame.getActiveSearchPanel().getMeta();
            Document doc = org.exmaralda.exakt.utilities.FileIO.COMASearchResultListToXML(list, corpus, meta, exaktFrame.getActiveSearchPanel().getCorpus().getCorpusPath());            

            if (type.startsWith("HTML")){
                // prepare the transformation                
                Preferences prefs = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.EXAKT");
                String pathToXSL = prefs.get("xsl-concordance-output", "");

                XSLTransformer transformer = null;
                boolean failed = false;
                if (pathToXSL.length()>0){
                    // try to use external stylesheet
                    try {
                        org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory ssf =
                                new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory(true);
                        String result = 
                                ssf.applyExternalStylesheetToString(pathToXSL, org.exmaralda.exakt.utilities.FileIO.getDocumentAsString(doc));
                        doc = org.exmaralda.exakt.utilities.FileIO.readDocumentFromString(result);
                        //transformer = new XSLTransformer(pathToXSL);
                    } catch (Exception ex) {
                        String message = "There is a problem with " + pathToXSL + ": \n";
                        message += ex.getMessage() + "\n";
                        message += "Using default stylesheet instead.";
                        failed = true;
                        javax.swing.JOptionPane.showMessageDialog(exaktFrame, message);
                    } 
                }
                try {
                    if ((pathToXSL.length()==0) || failed) {
                        // use internal stylesheet
                        java.io.InputStream is = getClass().getResourceAsStream(PATH_TO_INTERNAL_STYLESHEET);
                        transformer = new XSLTransformer(is);
                        doc = transformer.transform(doc);
                    }                    
                } catch (XSLTransformException ex) {
                    String message = "Stylesheet transformation failed:";
                    message += ex.getMessage() + "\n";
                    javax.swing.JOptionPane.showMessageDialog(exaktFrame, message);
                    ex.printStackTrace();
                    return;
                }
            }
            try {
                //list.writeXML(file);
                org.exmaralda.exakt.utilities.FileIO.writeDocumentToLocalFile(file,doc);
                exaktFrame.setLastSearchResultPath(file);
                exaktFrame.getActiveSearchPanel().setCurrentSearchResultFile(file);
                exaktFrame.getActiveSearchPanel().setCurrentSearchResultFileType(type);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(exaktFrame, ex.getMessage());
                ex.printStackTrace();
            }
        
        }        
    }
    
}
