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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.*;
import org.jdom.transform.*;
import java.util.prefs.Preferences;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.exmaralda.common.helpers.XMLFormatter;
import org.exmaralda.exakt.search.SearchResultList;
import org.exmaralda.exakt.exmaraldaSearch.*;


/**
 *
 * @author thomas
 */

public class SaveSearchResultAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {
    
    public static final String PATH_TO_INTERNAL_STYLESHEET = "/org/exmaralda/exakt/resources/SearchResult2HTML.xsl";
    
    /** Creates a new instance of SaveSearchResultAction
     * @param ef
     * @param title
     * @param icon */
    public SaveSearchResultAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }
    
    @Override
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
            List<String[]> meta = exaktFrame.getActiveSearchPanel().getMeta();
            
            
            //get the current KWIC results
            Document searchResultDoc = null;
            StreamSource searchResultSource = null;
            String searchResultString = null;
            try{
                searchResultDoc = org.exmaralda.exakt.utilities.FileIO.COMASearchResultListToXML(list, corpus, meta, exaktFrame.getActiveSearchPanel().getCorpus().getCorpusPath());
                searchResultString = org.exmaralda.exakt.utilities.FileIO.getDocumentAsString(searchResultDoc);
                searchResultSource = new StreamSource(new StringReader(searchResultString));
            } catch(IOException ioE){
                String message = "Exception getting the KWIC result:";
                message += ioE.getMessage() + "\n";
                javax.swing.JOptionPane.showMessageDialog(exaktFrame, message);
                System.out.println(ioE.getLocalizedMessage());
                return;
            }
            
            
            //transform XML concordance into HTML
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
                        searchResultString = ssf.applyExternalStylesheetToString(pathToXSL, searchResultString);
                        
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
                        
                        java.io.InputStream is = getClass().getResourceAsStream(PATH_TO_INTERNAL_STYLESHEET);
                        StreamSource xsltSource = new StreamSource(is);

                        //create TransformerFactory and TransformerInstance
                        TransformerFactory tf = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
                        Transformer t = tf.newTransformer(xsltSource);


                        //transform and fetch result as string
                        StreamSource resultSource = new StreamSource(new StringReader(searchResultString));
                        StringWriter resultWriter = new StringWriter();
                        t.transform(searchResultSource, new StreamResult(resultWriter));

                        // result is not saved anywhere (could be done for debugging)
                        searchResultString = resultWriter.toString(); 
                        
                    }                    
                } catch (TransformerConfigurationException ex) {
                    String message = "There is a problem with the XSLT transformation (TransformerConfigurationException): \n";
                    message += ex.getMessage() + "\n";
                    javax.swing.JOptionPane.showMessageDialog(exaktFrame, message);
                } catch (TransformerException ex) {
                    String message = "There is a problem with the XSLT transformation (TransformerException): \n";
                    message += ex.getMessage() + "\n";
                    javax.swing.JOptionPane.showMessageDialog(exaktFrame, message);
                }
            }
            
            //if CSV is desired
            else if (type.startsWith("CSV")){
                // prepare the transformation  
                XSLTransformer transformer = null;
                boolean failed = false;
                
                
                // try to use external stylesheet
                try {
                    
                    java.io.InputStream is = getClass().getResourceAsStream("/org/exmaralda/exakt/resources/SearchResult2CSV.xsl");
                    StreamSource xsltSource = new StreamSource(is);

                    //create TransformerFactory and TransformerInstance
                    TransformerFactory tf = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
                    Transformer t = tf.newTransformer(xsltSource);
                

                    //transform and fetch result as string
                    StreamSource resultSource = new StreamSource(new StringReader(searchResultString));
                    StringWriter resultWriter = new StringWriter();
                    t.transform(searchResultSource, new StreamResult(resultWriter));
                
                    // result is not saved anywhere (could be done for debugging)
                    searchResultString = resultWriter.toString();  
                    
                }  catch (TransformerException ex) {
                    String message = """
                                     General problem with CSV export:: 
                                     """;
                    message += ex.getMessage() + "\n";
                    failed = true;
                    javax.swing.JOptionPane.showMessageDialog(exaktFrame, message);
                } 
                
            }
            
            // not HTML and not CSV, must be XML
            else {
                try {
                    searchResultString = XMLFormatter.formatXML(searchResultString, false);
                } catch (JDOMException | IOException ex) {
                    Logger.getLogger(SaveSearchResultAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            try {
                // issue #267?
                /*FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(searchResultString);
                fileWriter.flush();
                fileWriter.close();*/
                Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "UTF-8"));
                try {
                    out.write(searchResultString);
                } finally {
                    out.close();
                }                
                exaktFrame.setLastSearchResultPath(file);
                exaktFrame.getActiveSearchPanel().setCurrentSearchResultFile(file);
                exaktFrame.getActiveSearchPanel().setCurrentSearchResultFileType(type);                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(exaktFrame, ex.getMessage());
                System.out.println(ex.getLocalizedMessage());
            }
        
        }        
    }
    
}
