/*
 * NewAction.java
 *
 * Created on 16. Juni 2003, 16:23
 */

package org.exmaralda.partitureditor.partiture.clarinActions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.jexmaralda.convert.TCFConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.TEITCFMerger;
import org.exmaralda.partitureditor.partiture.BrowserLauncher;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;
import org.exmaralda.webservices.WebLichtConnector;
import org.exmaralda.webservices.swing.CLARINProgressDialog;
import org.exmaralda.webservices.swing.WebLichtParameterDialog;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;



/**
 * Creates a new transcription
 * Menu: File --> New
 * @author  thomas
 */
public class WebLichtAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    CLARINProgressDialog pbd;
    
    static String XSL = "/org/exmaralda/partitureditor/jexmaralda/xsl/MergedTEI2HTML.xsl";

    /** Creates a new instance of NewAction */
    public WebLichtAction(PartitureTableWithActions t) {
        super("WebLicht...", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        try {
            table.commitEdit(true);
            System.out.println("WebLichtAction!");
            webLicht();
            table.transcriptionChanged = false;
            table.clearUndo();
            table.clearSearchResult();
            table.setFrameEndPosition(-2);
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(table.getParent(), ex.getLocalizedMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(table.getParent(), ex.getLocalizedMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        } catch (JDOMException ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(table.getParent(), ex.getLocalizedMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        } catch (SAXException ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(table.getParent(), ex.getLocalizedMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        } catch (FSMException ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(table.getParent(), ex.getLocalizedMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void webLicht() throws JexmaraldaException, IOException, JDOMException, SAXException, FSMException{
        
        // let the user define parameters
        WebLichtParameterDialog webLichtParameterDialog = new WebLichtParameterDialog(table.parent, true);
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.PartiturEditor");
        
        // retrieve values from preferences
        String chainFile = settings.get("WEBLICHT-CHAIN-FILE", "");
        String apiKey = settings.get("WEBLICHT-API-KEY", "");
        String dir = settings.get("WEBLICHT-OUTPUT-DIR", table.homeDirectory);

        webLichtParameterDialog.setParameters(chainFile, apiKey, dir);
        
        webLichtParameterDialog.setLocationRelativeTo(table);
        webLichtParameterDialog.setVisible(true);
        if (!webLichtParameterDialog.approved) return;
        final HashMap<String, Object> webLichtParameters = webLichtParameterDialog.getWebLichtParameters();
        
        // write the parameters to the preferences
        settings.put("WEBLICHT-CHAIN-FILE", (String) webLichtParameters.get("CHAIN"));
        settings.put("WEBLICHT-API-KEY", (String) webLichtParameters.get("API-KEY"));
        if (((String) webLichtParameters.get("TCF")!=null) && ((String) webLichtParameters.get("TCF")).length()>0){
            settings.put("WEBLICHT-OUTPUT-DIR", (String) webLichtParameters.get("TCF"));
        }
        if (((String) webLichtParameters.get("HTML")!=null) && ((String) webLichtParameters.get("HTML")).length()>0){
            settings.put("WEBLICHT-OUTPUT-DIR", (String) webLichtParameters.get("HTML"));
        }
        if (((String) webLichtParameters.get("TEI")!=null) && ((String) webLichtParameters.get("TEI")).length()>0){
            settings.put("WEBLICHT-OUTPUT-DIR", (String) webLichtParameters.get("TEI"));
        }
        
                       
        // get the transcription and the current selection
        final BasicTranscription bt = table.getModel().getTranscription();
        

        pbd = new CLARINProgressDialog(table.parent, false);
        pbd.setLocationRelativeTo(table.parent);
        pbd.setTitle("CLARIN-D & WebLicht... ");
        //pbd.setAlwaysOnTop(true);
        pbd.setVisible(true);

        // do this in a thread so we can report progress
        Thread webLichtThread = new Thread(){
            @Override
            public void run() {
                try {
                    pbd.addText("Creating TCF file...");                    
                    TCFConverter converter = new TCFConverter();
                    File tcfInputFile = File.createTempFile("TCF", ".tcf");
                    tcfInputFile.deleteOnExit();
                    
                    //converter.writeHIATTCFToFile(bt, tcfInputFile.getAbsolutePath(), (String) webLichtParameters.get("LANG"));
                    converter.writeTCFToFile(bt, 
                            tcfInputFile.getAbsolutePath(), 
                            (String) webLichtParameters.get("LANG"),
                            (String) webLichtParameters.get("SEGMENTATION"));
                    
                    pbd.addText("Creating TEI file...");                    
                    TEIConverter converter2 = new TEIConverter();
                    File teiInputFile = File.createTempFile("TEI", ".tei");
                    tcfInputFile.deleteOnExit();
                    String segName = (String) webLichtParameters.get("SEGMENTATION");
                    if ("HIAT".equals(segName)){
                        converter2.writeHIATISOTEIToFile(bt, teiInputFile.getAbsolutePath());
                    } else if ("cGAT Minimal".equals(segName)){
                        converter2.writeCGATMINIMALISOTEIToFile(bt, teiInputFile.getAbsolutePath(), "");                        
                    } else if ("GENERIC".equals(segName)){
                        converter2.writeGenericISOTEIToFile(bt, teiInputFile.getAbsolutePath());                                                
                    }
                    
                    // call WebLicht with the files and write the result to a temporary text grid file
                    pbd.addText("Chain file: " + webLichtParameters.get("CHAIN"));
                    pbd.addText("TCF file: " + tcfInputFile.getAbsolutePath());
                    pbd.addText("API Key: " + webLichtParameters.get("API-KEY"));
                    
                    WebLichtConnector wc = new WebLichtConnector();
                    pbd.addText("Calling WebLicht at " + wc.webLichtURL + ".....");
                    String result = wc.callWebLicht(tcfInputFile, new File((String) webLichtParameters.get("CHAIN")), (String) webLichtParameters.get("API-KEY"));
                    File tcfResultFile = File.createTempFile("TCF", ".tcf");
                    tcfResultFile.deleteOnExit();
                    FileIO.writeDocumentToLocalFile(tcfResultFile, IOUtilities.readDocumentFromString(result));
                    pbd.addText("TCF result written to " + tcfResultFile.getAbsolutePath() + ".");
                    
                    
                    
                    pbd.addText("Merging results... ");
                    // changed 20-04-2015 because Thorsten's Mudder is stateless
                    TEITCFMerger merger = new TEITCFMerger(teiInputFile, tcfResultFile);    
                    // rollback owing to shittiness of WebLicht which loses the textSource element on its way
                    // TEITCFMerger merger = new TEITCFMerger(tcfResultFile);
                    merger.merge();
                    Document mergedDocument = merger.getMergedDocument();

                    String tcf = (String) webLichtParameters.get("TCF");
                    if (tcf!=null && tcf.length()>0){
                        Document tcfDocument = IOUtilities.readDocumentFromString(result);
                        FileIO.writeDocumentToLocalFile(new File(tcf), tcfDocument);
                        pbd.addText("TCF written to " + tcf);
                    }

                    String tei = (String) webLichtParameters.get("TEI");
                    if (tei!=null && tei.length()>0){
                        FileIO.writeDocumentToLocalFile(new File(tei), mergedDocument);
                        pbd.addText("TEI written to " + tei);
                    }

                    String html = (String) webLichtParameters.get("HTML");
                    if (html!=null && html.length()>0){
                        File htmlFile = new File(html);
                        StylesheetFactory ssf = new StylesheetFactory(true);
                        String transformed = ssf.applyInternalStylesheetToString(XSL, IOUtilities.documentToString(mergedDocument));
                        
                        FileOutputStream fos = new FileOutputStream(htmlFile);
                        fos.write(transformed.getBytes("UTF-8"));
                        fos.close();    
                        pbd.addText("HTML written to " + html);
                        BrowserLauncher.openURL(htmlFile.toURI().toURL().toString());                        
                    }
                    
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                pbd.addText("Done.");
                                success();
                            } catch (IOException ex) {
                                pbd.addText("Error: " + ex.getLocalizedMessage());
                                JOptionPane.showMessageDialog(pbd, ex);
                            } catch (JexmaraldaException ex) {
                                pbd.addText("Error: " + ex.getLocalizedMessage());
                                JOptionPane.showMessageDialog(pbd, ex);
                            }
                        }
                    });


                } catch (Exception ex) {
                    pbd.addText("Error: " + ex.getLocalizedMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(pbd, ex);
                } 
            }
            
        };
        webLichtThread.start();

        
    }
    
    public void success() throws IOException, JexmaraldaException{        

        
    }
}



