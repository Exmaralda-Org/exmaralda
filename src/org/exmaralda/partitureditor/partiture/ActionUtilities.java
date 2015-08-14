/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.partiture;

import java.awt.Container;
import javax.swing.filechooser.FileFilter;
import org.exmaralda.common.ExmaraldaApplication;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.AbstractFileFilterDialog;

/**
 *
 * @author thomas
 */
public class ActionUtilities {


    public static void setFileFilter(String propName, Container topLevelAncestor, AbstractFileFilterDialog dialog) {
        // try to find out what the last selected filter was and set it
        if (topLevelAncestor instanceof ExmaraldaApplication){
            ExmaraldaApplication ea = (ExmaraldaApplication)(topLevelAncestor);
            String userNode = ea.getPreferencesNode();
            java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(userNode);
            String def = "Praat";
            if (propName.indexOf("output")>=0){
                def = "HTMLPartitur";
            }
            String lastFileFilter = settings.get(propName, def);
            if (lastFileFilter.equals("AG")) {dialog.setFileFilter(dialog.AGFileFilter);}
            else if (lastFileFilter.equals("EAF")) {dialog.setFileFilter(dialog.EAFFileFilter);}
            else if (lastFileFilter.equals("HIAT-DOS")) {dialog.setFileFilter(dialog.HIATDOSFileFilter);}
            else if (lastFileFilter.equals("Praat")) {dialog.setFileFilter(dialog.PraatFileFilter);}
            else if (lastFileFilter.equals("SimpleExmaralda")) {dialog.setFileFilter(dialog.SimpleExmaraldaFileFilter);}
            else if (lastFileFilter.equals("RioDeJaneiro")) {dialog.setFileFilter(dialog.RioDeJaneiroFileFilter);}
            else if (lastFileFilter.equals("TASX")) {dialog.setFileFilter(dialog.TASXFileFilter);}
            else if (lastFileFilter.equals("ANVIL")) {dialog.setFileFilter(dialog.AnvilFileFilter);}
            else if (lastFileFilter.equals("TEI")) {dialog.setFileFilter(dialog.TEIFileFilter);}
            else if (lastFileFilter.equals("TCF")) {dialog.setFileFilter(dialog.TCFFileFilter);}
            //else if (lastFileFilter.equals("TEIModena")) {dialog.setFileFilter(dialog.TEIModenaFileFilter);}
            else if (lastFileFilter.equals("TEIModena")) {dialog.setFileFilter(dialog.TEIFileFilter);}
            else if (lastFileFilter.equals("Text")) {dialog.setFileFilter(dialog.TextFileFilter);}
            else if (lastFileFilter.equals("exSync")) {dialog.setFileFilter(dialog.exSyncFileFilter);}
            else if (lastFileFilter.equals("HTMLPartitur")) {dialog.setFileFilter(dialog.HTMLPartiturFileFilter);}
            else if (lastFileFilter.equals("RTFPartitur")) {dialog.setFileFilter(dialog.RTFPartiturFileFilter);}
            else if (lastFileFilter.equals("SVGPartitur")) {dialog.setFileFilter(dialog.SVGPartiturFileFilter);}
            else if (lastFileFilter.equals("XMLPartitur")) {dialog.setFileFilter(dialog.XMLPartiturFileFilter);}
            else if (lastFileFilter.equals("HTMLSegmentChain")) {dialog.setFileFilter(dialog.HTMLSegmentChainFileFilter);}
            else if (lastFileFilter.equals("FreeStylesheet")) {dialog.setFileFilter(dialog.FreeStylesheetFileFilter);}
            else if (lastFileFilter.equals("HTMLPartiturFlash")) {dialog.setFileFilter(dialog.HTMLPartiturWithFlashFileFilter);}
            else if (lastFileFilter.equals("HTML5Partitur")) {dialog.setFileFilter(dialog.HTMLPartiturWithHTML5AudioFileFilter);}
            else if (lastFileFilter.equals("HTMLListFlash")) {dialog.setFileFilter(dialog.HTMLSegmentChainWithFlashFileFilter);}
            else if (lastFileFilter.equals("HTML5List")) {dialog.setFileFilter(dialog.HTMLSegmentChainWithHTML5AudioFileFilter);}
            else if (lastFileFilter.equals("ExmaraldaSegmented")) {dialog.setFileFilter(dialog.ExmaraldaSegmentedTranscriptionFileFilter);}
            else if (lastFileFilter.equals("GATTranscript")) {dialog.setFileFilter(dialog.GATTranscriptFileFilter);}
            else if (lastFileFilter.equals("SimpleTextTranscript")) {dialog.setFileFilter(dialog.SimpleTextTranscriptFileFilter);}
            else if (lastFileFilter.equals("CHATTranscript")) {dialog.setFileFilter(dialog.CHATTranscriptFileFilter);}
            else if (lastFileFilter.equals("FOLKERTranscription")) {dialog.setFileFilter(dialog.FOLKERTranscriptionFileFilter);}
            else if (lastFileFilter.equals("TreeTagger")) {dialog.setFileFilter(dialog.TreeTaggerFilter);}
            else if (lastFileFilter.equals("XSLImport")) {dialog.setFileFilter(dialog.XSLStylesheetImportFilter);}
            else if (lastFileFilter.equals("Transcriber")) {dialog.setFileFilter(dialog.TranscriberFileFilter);}
            else if (lastFileFilter.equals("WinPitch")) {dialog.setFileFilter(dialog.WinPitchFileFilter);}
            else if (lastFileFilter.equals("Audacity")) {dialog.setFileFilter(dialog.AudacityLabelFileFilter);}
            else if (lastFileFilter.equals("Phon")) {dialog.setFileFilter(dialog.PhonFileFilter);}
            else if (lastFileFilter.equals("F4Text")) {dialog.setFileFilter(dialog.F4TextFileFilter);}
        }
    }

    public static void memorizeFileFilter(String propName, Container topLevelAncestor, AbstractFileFilterDialog dialog){
        if (topLevelAncestor instanceof ExmaraldaApplication){
            ExmaraldaApplication ea = (ExmaraldaApplication)(topLevelAncestor);
            String userNode = ea.getPreferencesNode();
            java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(userNode);
            String lastFileFilter = "Praat";
            FileFilter selectedFileFilter = dialog.getFileFilter();

            if (selectedFileFilter==dialog.AGFileFilter) {lastFileFilter = "AG";}
            else if (selectedFileFilter==dialog.EAFFileFilter) {lastFileFilter = "EAF";}
            else if (selectedFileFilter==dialog.HIATDOSFileFilter) {lastFileFilter = "HIAT-DOS";}
            else if (selectedFileFilter==dialog.PraatFileFilter) {lastFileFilter = "Praat";}
            else if (selectedFileFilter==dialog.SimpleExmaraldaFileFilter) {lastFileFilter = "SimpleExmaralda";}
            else if (selectedFileFilter==dialog.RioDeJaneiroFileFilter) {lastFileFilter = "RioDeJaneiro";}
            else if (selectedFileFilter==dialog.TASXFileFilter) {lastFileFilter = "TASX";}
            else if (selectedFileFilter==dialog.TASXFileFilter) {lastFileFilter = "ANVIL";}
            else if (selectedFileFilter==dialog.TEIFileFilter) {lastFileFilter = "TEI";}
            else if (selectedFileFilter==dialog.TCFFileFilter) {lastFileFilter = "TCF";}
            else if (selectedFileFilter==dialog.TEIModenaFileFilter) {lastFileFilter = "TEIModena";}
            else if (selectedFileFilter==dialog.TextFileFilter) {lastFileFilter = "Text";}
            else if (selectedFileFilter==dialog.exSyncFileFilter) {lastFileFilter = "exSync";}
            else if (selectedFileFilter==dialog.HTMLPartiturFileFilter) {lastFileFilter = "HTMLPartitur";}
            else if (selectedFileFilter==dialog.RTFPartiturFileFilter) {lastFileFilter = "RTFPartitur";}
            else if (selectedFileFilter==dialog.SVGPartiturFileFilter) {lastFileFilter = "SVGPartitur";}
            else if (selectedFileFilter==dialog.XMLPartiturFileFilter) {lastFileFilter = "XMLPartitur";}
            else if (selectedFileFilter==dialog.HTMLSegmentChainFileFilter) {lastFileFilter = "HTMLSegmentChain";}
            else if (selectedFileFilter==dialog.FreeStylesheetFileFilter) {lastFileFilter = "FreeStylesheet";}
            else if (selectedFileFilter==dialog.HTMLPartiturWithFlashFileFilter) {lastFileFilter = "HTMLPartiturFlash";}
            else if (selectedFileFilter==dialog.HTMLPartiturWithHTML5AudioFileFilter) {lastFileFilter = "HTML5Partitur";}
            else if (selectedFileFilter==dialog.HTMLSegmentChainWithFlashFileFilter) {lastFileFilter = "HTMLListFlash";}
            else if (selectedFileFilter==dialog.HTMLSegmentChainWithHTML5AudioFileFilter) {lastFileFilter = "HTML5List";}
            else if (selectedFileFilter==dialog.ExmaraldaSegmentedTranscriptionFileFilter) {lastFileFilter = "ExmaraldaSegmented";}
            else if (selectedFileFilter==dialog.GATTranscriptFileFilter) {lastFileFilter = "GATTranscript";}
            else if (selectedFileFilter==dialog.SimpleTextTranscriptFileFilter) {lastFileFilter = "SimpleTextTranscript";}            
            else if (selectedFileFilter==dialog.CHATTranscriptFileFilter) {lastFileFilter = "CHATTranscript";}
            else if (selectedFileFilter==dialog.FOLKERTranscriptionFileFilter) {lastFileFilter = "FOLKERTranscription";}
            else if (selectedFileFilter==dialog.TreeTaggerFilter) {lastFileFilter = "TreeTagger";}
            else if (selectedFileFilter==dialog.XSLStylesheetImportFilter) {lastFileFilter = "XSLImport";}
            else if (selectedFileFilter==dialog.TranscriberFileFilter) {lastFileFilter = "Transcriber";}
            else if (selectedFileFilter==dialog.WinPitchFileFilter) {lastFileFilter = "WinPitch";}
            else if (selectedFileFilter==dialog.AudacityLabelFileFilter) {lastFileFilter = "Audacity";}
            else if (selectedFileFilter==dialog.PhonFileFilter) {lastFileFilter = "Phon";}
            else if (selectedFileFilter==dialog.F4TextFileFilter) {lastFileFilter = "F4Text";}


            settings.put(propName, lastFileFilter);
        }
    }

    /*public ParameterFileFilter AGFileFilter = new ParameterFileFilter("xml", "Annotation Graph File (*.xml)");
    public ParameterFileFilter EAFFileFilter = new ParameterFileFilter("eaf", "ELAN Annotation File (*.eaf)");
    public ParameterFileFilter HIATDOSFileFilter = new ParameterFileFilter("dat", "HIAT-DOS file (*.dat)");
    public ParameterFileFilter PraatFileFilter = new ParameterFileFilter("textGrid", "PRAAT Textgrid (*.textGrid)");
    public ParameterFileFilter SimpleExmaraldaFileFilter = new ParameterFileFilter("txt", "Simple EXMARaLDA text file (*.txt)");
    public ParameterFileFilter TASXFileFilter = new ParameterFileFilter("xml", "TASX Annotation File (*.xml)");
    public ParameterFileFilter TEIFileFilter = new ParameterFileFilter("xml", "TEI file (*.xml)");
    public ParameterFileFilter TextFileFilter = new ParameterFileFilter("txt", "Plain text file (*.txt)");
    public ParameterFileFilter exSyncFileFilter = new ParameterFileFilter("xml", "ExSync file (*.xml)");
    public ParameterFileFilter HTMLPartiturFileFilter = new ParameterFileFilter("html", "HTML Partitur (*.html)");
    public ParameterFileFilter RTFPartiturFileFilter = new ParameterFileFilter("rtf", "RTF Partitur (*.rtf)");
    public ParameterFileFilter SVGPartiturFileFilter = new ParameterFileFilter("html", "SVG Partitur (*.html)");
    public ParameterFileFilter XMLPartiturFileFilter = new ParameterFileFilter("xml", "XML Partitur (*.xml)");
    public ParameterFileFilter HTMLSegmentChainFileFilter = new ParameterFileFilter("html", "HTML Segment Chain List (*.html)");
    public ParameterFileFilter FreeStylesheetFileFilter = new ParameterFileFilter("html", "Free Stylesheet Transformation (*.html)");
    public ParameterFileFilter HTMLPartiturWithFlashFileFilter = new ParameterFileFilter("html", "HTML Partitur + Flash Player (*.html)");*/


}
