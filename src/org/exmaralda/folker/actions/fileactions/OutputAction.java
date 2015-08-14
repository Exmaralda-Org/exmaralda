/*
 * OpenAction.java
 *
 * Created on 14. Mai 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.actions.fileactions;

import org.exmaralda.folker.actions.AbstractApplicationAction;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.*;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.folker.application.ApplicationControl;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.GATParser;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.folker.utilities.Constants;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;
import org.exmaralda.folker.utilities.PreferencesUtilities;
import org.exmaralda.partitureditor.interlinearText.HTMLParameters;
import org.exmaralda.partitureditor.interlinearText.InterlinearText;
import org.exmaralda.partitureditor.interlinearText.ItBundle;
import org.exmaralda.partitureditor.jexmaralda.AbstractEventTier;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class OutputAction extends AbstractApplicationAction {
    
    ParameterFileFilter htmlContributionListFilter = new ParameterFileFilter("html", FOLKERInternationalizer.getString("misc.contributionList"));
    ParameterFileFilter htmlContributionListAudioFilter = new ParameterFileFilter("html", FOLKERInternationalizer.getString("misc.contributionListAudio"));
    ParameterFileFilter htmlSegmentListFilter = new ParameterFileFilter("html", FOLKERInternationalizer.getString("misc.segmentList"));
    ParameterFileFilter htmlPartiturFilter = new ParameterFileFilter("html", FOLKERInternationalizer.getString("misc.partitur"));
    ParameterFileFilter htmlCompactPartiturAudioFilter = new ParameterFileFilter("html", FOLKERInternationalizer.getString("misc.compactPartiturAudio"));
    ParameterFileFilter gatBasicTranscriptFilter = new ParameterFileFilter("html", FOLKERInternationalizer.getString("misc.gatBasicTranscript"));
    ParameterFileFilter htmlQuantifyFilter = new ParameterFileFilter("html", FOLKERInternationalizer.getString("misc.quantification"));
    
    public static String CONTRIBUTIONS2HTML_STYLESHEET = "/org/exmaralda/folker/data/unparsedFolker2HTMLContributionList.xsl";
    public static String CONTRIBUTIONS2HTML_AUDIO_STYLESHEET = "/org/exmaralda/folker/data/unparsedFolker2HTMLContributionListAudio.xsl";
    public static String COMPACT_PARTITUR_AUDIO_STYLESHEET = "/org/exmaralda/folker/data/compactPartitur2HTMLAudio.xsl";
    public static String SEGMENTS2HTML_STYLESHEET = "/org/exmaralda/folker/data/unparsedFolker2HTMLSegmentList.xsl";    
    public static String QUANTIFY_STYLESHEET = "/org/exmaralda/folker/data/folkerquantification.xsl";
    public static String QUANTIFY_STYLESHEET_EN = "/org/exmaralda/folker/data/folkerquantification_en.xsl";
    public static String QUANTIFY_STYLESHEET_FR = "/org/exmaralda/folker/data/folkerquantification_fr.xsl";
    public static String BASIC_TRANSCRIPTION2HTML_STYLESHEET = "/org/exmaralda/folker/data/basicFolker2HTML.xsl";    
    
    /** Creates a new instance of OpenAction */
    public OutputAction(ApplicationControl ac, String name, Icon icon) {
        super(ac, name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[*** OutputAction ***]");
        ApplicationControl ac = (ApplicationControl)applicationControl;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(FOLKERInternationalizer.getString("dialog.output"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(htmlSegmentListFilter);
        fileChooser.addChoosableFileFilter(htmlPartiturFilter);
        fileChooser.addChoosableFileFilter(htmlCompactPartiturAudioFilter);
        fileChooser.addChoosableFileFilter(htmlContributionListFilter);
        fileChooser.addChoosableFileFilter(htmlContributionListAudioFilter);
        fileChooser.addChoosableFileFilter(gatBasicTranscriptFilter);
        fileChooser.addChoosableFileFilter(htmlQuantifyFilter);
        fileChooser.setFileFilter(htmlSegmentListFilter);
        fileChooser.setCurrentDirectory(new File(PreferencesUtilities.getProperty("workingDirectory", "")));        

        int retValue = fileChooser.showSaveDialog(ac.getFrame());
        if (retValue==JFileChooser.CANCEL_OPTION) return;
        
        File f = fileChooser.getSelectedFile();
        if (!(f.getName().indexOf(".")>=0)){
            f = new File(f.getAbsolutePath() + "." + ((ParameterFileFilter)(fileChooser.getFileFilter())).getSuffix());
        }
        if (f.exists()){
            int confirm = JOptionPane.showConfirmDialog(ac.getFrame(), FOLKERInternationalizer.getString("option.fileexists"));
            if (confirm!=JOptionPane.OK_OPTION){
                actionPerformed(e);
            }
        } // remotely commented
        try {
            if ((fileChooser.getFileFilter()==htmlContributionListFilter) 
                    || (fileChooser.getFileFilter()==htmlContributionListAudioFilter)
                    || (fileChooser.getFileFilter()==htmlSegmentListFilter)
                    || (fileChooser.getFileFilter()==gatBasicTranscriptFilter)
                    || (fileChooser.getFileFilter()==htmlQuantifyFilter)){
                // List based output
                String STYLESHEET = "";
                if (fileChooser.getFileFilter()==htmlContributionListFilter) {
                    System.out.println("[*** ContributionListOutput ***]");
                    STYLESHEET = CONTRIBUTIONS2HTML_STYLESHEET;
                } else if (fileChooser.getFileFilter()==htmlContributionListAudioFilter) {
                    System.out.println("[*** SegmentListAudioOutput ***]");
                    STYLESHEET = CONTRIBUTIONS2HTML_AUDIO_STYLESHEET;
                } else if (fileChooser.getFileFilter()==htmlSegmentListFilter) {
                    System.out.println("[*** SegmentListOutput ***]");
                    STYLESHEET = SEGMENTS2HTML_STYLESHEET;
                } else if (fileChooser.getFileFilter()==gatBasicTranscriptFilter) {
                    System.out.println("[*** GAT output ***]");
                    STYLESHEET = BASIC_TRANSCRIPTION2HTML_STYLESHEET;
                } else {
                    System.out.println("[*** QuantificationOutput ***]");
                    String lang = PreferencesUtilities.getProperty("language", "en");
                    if (lang.equals("de")){
                        STYLESHEET = QUANTIFY_STYLESHEET;
                    } else if (lang.equals("fr")){
                        STYLESHEET = QUANTIFY_STYLESHEET_FR;
                    } else {
                        STYLESHEET = QUANTIFY_STYLESHEET_EN;
                    }
                }
                EventListTranscription elt = ac.getTranscription();
                Document transcriptionDoc = EventListTranscriptionXMLReaderWriter.toJDOMDocument(elt, fileChooser.getSelectedFile());
                if (fileChooser.getFileFilter()==htmlQuantifyFilter){
                    GATParser ap = new GATParser(Constants.getAlphabetLanguage());
                    if (((ApplicationControl)applicationControl).PARSE_LEVEL!=3){
                        for (int level=1; level<=2; level++){
                            ap.parseDocument(transcriptionDoc, level);
                        }
                    } else {
                        // added 08-05-2014
                        ap.parseDocument(transcriptionDoc, 1);
                        ap.parseDocument(transcriptionDoc, 3);                        
                    }
                } else if (fileChooser.getFileFilter()==gatBasicTranscriptFilter) {
                    GATParser ap = new GATParser(Constants.getAlphabetLanguage());
                    ap.parseDocument(transcriptionDoc, 1);
                    ap.parseDocument(transcriptionDoc, 3);
                    if (!(ap.isFullyParsedOnLevel(transcriptionDoc, 3))){
                        String text = "Die Transkription konnte nicht vollständig\n"
                                + "als GAT-Basistranskript geparst werden.\n"
                                + "Die Ausgabe ist daher unvollständig.";
                        JOptionPane.showMessageDialog(fileChooser, text, "Parse-Fehler", JOptionPane.WARNING_MESSAGE);
                    }
                }
                String docString = org.exmaralda.common.jdomutilities.IOUtilities.documentToString(transcriptionDoc);
                StylesheetFactory sf = new StylesheetFactory(true);
                String resultString = sf.applyInternalStylesheetToString(STYLESHEET, docString);
                if (fileChooser.getFileFilter()==htmlContributionListAudioFilter){
                    FileOutputStream fos = new FileOutputStream(new File(f.getAbsolutePath()));
                    fos.write(resultString.getBytes("UTF-8"));
                    fos.close();                    
                } else {
                    Document resultDocument = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString(resultString);
                    org.exmaralda.common.jdomutilities.IOUtilities.writeDocumentToLocalFile(
                            f.getAbsolutePath(), resultDocument, true);
                }
            } else if ((fileChooser.getFileFilter()==htmlPartiturFilter)
                    || (fileChooser.getFileFilter()==htmlCompactPartiturAudioFilter)){
                
                System.out.println("[*** PartiturOutput ***]");
                // Partitur output
                BasicTranscription bt = ac.getBasicTranscription().makeCopy();
                //TierFormatTable tft = new TierFormatTable(bt);
                bt.getBody().stratify(AbstractEventTier.STRATIFY_BY_DISTRIBUTION);
                TierFormatTable tft = TierFormatTable.makeTierFormatTableForFolker(bt);
                InterlinearText it = ItConverter.BasicTranscriptionToInterlinearText(bt, tft);

                it.markOverlaps("[", "]");
                
                int frameEndPosition = -1;
                for (int pos=0; pos<bt.getBody().getNumberOfTiers();pos++){
                    if (bt.getBody().getTierAt(pos).getSpeaker()==null){
                        frameEndPosition = pos-1;
                        break;
                    }
                }
                if (frameEndPosition>=0){
                    ((ItBundle)it.getItElementAt(0)).frameEndPosition=frameEndPosition;
                }

                HTMLParameters parameters = new HTMLParameters();
                if (fileChooser.getFileFilter()==htmlPartiturFilter){
                    parameters.setWidth(600.0);
                    parameters.smoothRightBoundary = true;
                    parameters.includeSyncPoints = true;
                    parameters.putSyncPointsOutside = true;

                    it.trim(parameters);
                    it.writeHTMLToFile(f.getAbsolutePath(), parameters);
                } else if (fileChooser.getFileFilter()==htmlCompactPartiturAudioFilter){
                    parameters.setWidth(540.0);
                    parameters.smoothRightBoundary = true;
                    parameters.includeSyncPoints = false;
                    parameters.putSyncPointsOutside = false;

                    it.trim(parameters);

                    it.reorder();
                    Document itDocument = IOUtilities.readDocumentFromString(it.toXML());
                    Document btDocument = IOUtilities.readDocumentFromString(bt.toXML(tft));
                    Element btRootElement = btDocument.getRootElement();
                    btRootElement.detach();
                    itDocument.getRootElement().addContent(btRootElement);
                    
                    StylesheetFactory sf = new StylesheetFactory(true);
                    String resultString = sf.applyInternalStylesheetToString(COMPACT_PARTITUR_AUDIO_STYLESHEET, IOUtilities.documentToString(itDocument));
                    FileOutputStream fos = new FileOutputStream(new File(f.getAbsolutePath()));
                    fos.write(resultString.getBytes("UTF-8"));
                    fos.close();                    
                    
                    //FileIO.writeDocumentToLocalFile("S:\\TP-Z2\\GAT\\it-test.xml", itDocument);
                }
            }
        } catch (Exception ex) {
            applicationControl.displayException(ex);
            return;
        }
        
        PreferencesUtilities.setProperty("workingDirectory", f.getParent());        
        ac.status(FOLKERInternationalizer.getString("status.output1") + f.getAbsolutePath() +   FOLKERInternationalizer.getString("status.output2"));

        
    }
    
}
