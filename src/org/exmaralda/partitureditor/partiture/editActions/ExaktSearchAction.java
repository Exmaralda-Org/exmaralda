/*
 * SearchInEventsAction.java
 *
 * Created on 17. Juni 2003, 12:55
 */

package org.exmaralda.partitureditor.partiture.editActions;


import org.jdom.*;
import org.jdom.xpath.*;
import org.exmaralda.partitureditor.partiture.*;
import java.util.*;
import java.io.*;
import javax.swing.*;

/**
 *
 * @author  thomas
 */
public class ExaktSearchAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    
    /** Creates a new instance of SearchInEventsAction */
    public ExaktSearchAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("EXAKT search...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("exaktSearch!");
        table.commitEdit(true);
        exaktSearch();        
    }
    
    private void exaktSearch(){
        try {            
            // create segmented transcription and write it to temp file
            File segFile = File.createTempFile("EXMARaLDA_s", ".xml");
            segFile.deleteOnExit();
            table.getModel().getTranscription().toSegmentedTranscription().writeXMLToFile(segFile.getAbsolutePath(), "none");
            
            // create COMA corpus and write it to temp file
            File corpusFile = File.createTempFile("EXMARaLDA_corpus", ".xml");
            corpusFile.deleteOnExit();
            org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory();
            String comaString = sf.applyInternalStylesheetToString("/org/exmaralda/partitureditor/jexmaralda/xsl/BasicTranscription2Coma.xsl", table.getModel().getTranscription().toXML());
            Document comaDocument = org.exmaralda.exakt.utilities.FileIO.readDocumentFromString(comaString);
            List l = XPath.newInstance("//*[@id='fillinfilename']").selectNodes(comaDocument);
            for (Object o : l){
                Element e = (Element)o;
                e.setText(segFile.getName());
                e.removeAttribute("id");                
            }
            org.exmaralda.exakt.utilities.FileIO.writeDocumentToLocalFile(corpusFile, comaDocument);           
            
            
            // setup the EXAKT panel
            final org.exmaralda.exakt.exmaraldaSearch.COMACorpus comaCorpus = new org.exmaralda.exakt.exmaraldaSearch.COMACorpus();            
            comaCorpus.readCorpus(corpusFile);
            
            javax.swing.SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    displayKwicPanel(comaCorpus);
                }
            });
            
        } catch (Exception ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(table.getParent(), ex.getMessage());
        }
        
    }
    
    private void displayKwicPanel(org.exmaralda.exakt.exmaraldaSearch.COMACorpus comaCorpus){
        org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICSearchPanel kwicPanel =
                new org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICSearchPanel(comaCorpus);
        kwicPanel.setPreferredSize(new java.awt.Dimension(800,600));
        kwicPanel.setSize(new java.awt.Dimension(800,600));        
        kwicPanel.splitPane.setDividerLocation((int)Math.round(0.8*600.0));

        kwicPanel.addKWICTableListener((PartiturEditor)(table.getTopLevelAncestor()));

        // put the EXAKT panel in a dialog and display it
        JDialog dialog = new JDialog((JFrame)(table.getTopLevelAncestor()),false);
        dialog.setTitle("EXAKT search");
        dialog.getContentPane().add(kwicPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(table);
        dialog.setVisible(true);
    }
    
    
}
