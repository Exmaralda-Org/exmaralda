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
            if (propName.contains("output")){
                def = "HTMLPartitur";
            }
            String lastFileFilter = settings.get(propName, def);
            switch (lastFileFilter) {
                case "AG":
                    dialog.setFileFilter(dialog.AGFileFilter);
                    break;
                case "EAF":
                    dialog.setFileFilter(dialog.EAFFileFilter);
                    break;
                case "HIAT-DOS":
                    dialog.setFileFilter(dialog.HIATDOSFileFilter);
                    break;
                case "Praat":
                    dialog.setFileFilter(dialog.PraatFileFilter);
                    break;
                case "SimpleExmaralda":
                    dialog.setFileFilter(dialog.SimpleExmaraldaFileFilter);
                    break;
                case "RioDeJaneiro":
                    dialog.setFileFilter(dialog.RioDeJaneiroFileFilter);
                    break;
                case "TASX":
                    dialog.setFileFilter(dialog.TASXFileFilter);
                    break;
                case "ANVIL":
                    dialog.setFileFilter(dialog.AnvilFileFilter);
                    break;
                case "TEI":
                    dialog.setFileFilter(dialog.TEIFileFilter);
                    break;
                case "TCF":
                    dialog.setFileFilter(dialog.TCFFileFilter);
                    break;
                case "TEIModena":
                    dialog.setFileFilter(dialog.TEIFileFilter);
                    break;
                case "Text":
                    dialog.setFileFilter(dialog.TextFileFilter);
                    break;
                case "exSync":
                    dialog.setFileFilter(dialog.exSyncFileFilter);
                    break;
                case "HTMLPartitur":
                    dialog.setFileFilter(dialog.HTMLPartiturFileFilter);
                    break;
                case "HTMLPartiturCompact":
                    dialog.setFileFilter(dialog.HTMLPartiturCompactFilter);
                    break;
                case "RTFPartitur":
                    dialog.setFileFilter(dialog.RTFPartiturFileFilter);
                    break;
                case "SVGPartitur":
                    dialog.setFileFilter(dialog.SVGPartiturFileFilter);
                    break;
                case "XMLPartitur":
                    dialog.setFileFilter(dialog.XMLPartiturFileFilter);
                    break;
                case "HTMLSegmentChain":
                    dialog.setFileFilter(dialog.HTMLSegmentChainFileFilter);
                    break;
                case "FreeStylesheet":
                    dialog.setFileFilter(dialog.FreeStylesheetFileFilter);
                    break;
                case "HTMLPartiturFlash":
                    dialog.setFileFilter(dialog.HTMLPartiturWithFlashFileFilter);
                    break;
                case "HTML5Partitur":
                    dialog.setFileFilter(dialog.HTMLPartiturWithHTML5AudioFileFilter);
                    break;
                case "HTML5PartiturSVG":
                    dialog.setFileFilter(dialog.HTMLPartiturWithSVGFileFilter);
                    break;
                case "HTMLListFlash":
                    dialog.setFileFilter(dialog.HTMLSegmentChainWithFlashFileFilter);
                    break;
                case "HTML5List":
                    dialog.setFileFilter(dialog.HTMLSegmentChainWithHTML5AudioFileFilter);
                    break;
                case "ExmaraldaSegmented":
                    dialog.setFileFilter(dialog.ExmaraldaSegmentedTranscriptionFileFilter);
                    break;
                case "GATTranscript":
                    dialog.setFileFilter(dialog.GATTranscriptFileFilter);
                    break;
                case "GATHTML5":
                    dialog.setFileFilter(dialog.GATWithHTML5AudioFileFilter);
                    break;
                case "SimpleTextTranscript":
                    dialog.setFileFilter(dialog.SimpleTextTranscriptFileFilter);
                    break;
                case "CHATTranscript":
                    dialog.setFileFilter(dialog.CHATTranscriptFileFilter);
                    break;
                case "FOLKERTranscription":
                    dialog.setFileFilter(dialog.FOLKERTranscriptionFileFilter);
                    break;
                case "FLKTranscription":
                    dialog.setFileFilter(dialog.FLKTranscriptionFileFilter);
                    break;
                case "FLNTranscription":
                    dialog.setFileFilter(dialog.FLNTranscriptionFileFilter);
                    break;
                case "TreeTagger":
                    dialog.setFileFilter(dialog.TreeTaggerFilter);
                    break;
                case "XSLImport":
                    dialog.setFileFilter(dialog.XSLStylesheetImportFilter);
                    break;
                case "Transcriber":
                    dialog.setFileFilter(dialog.TranscriberFileFilter);
                    break;
                case "WinPitch":
                    dialog.setFileFilter(dialog.WinPitchFileFilter);
                    break;
                case "Audacity":
                    dialog.setFileFilter(dialog.AudacityLabelFileFilter);
                    break;
                case "Phon":
                    dialog.setFileFilter(dialog.PhonFileFilter);
                    break;
                case "TransanaXML":
                    dialog.setFileFilter(dialog.TransanaXMLFileFilter);
                    break;
                case "F4Text":
                    dialog.setFileFilter(dialog.F4TextFileFilter);
                    break;
                case "SRT":
                    dialog.setFileFilter(dialog.SRTFileFilter);
                    break;
                case "VTT":
                    dialog.setFileFilter(dialog.VTTFileFilter);
                    break;
                case "Flextext":
                    dialog.setFileFilter(dialog.FlexTextXMLFileFilter);
                    break;
                case "FrazierADC":
                    dialog.setFileFilter(dialog.FrazierADCFileFilter);
                    break;
                case "WhisperJSON":
                    dialog.setFileFilter(dialog.WhisperJSONFileFilter);
                    break;
                case "AmberscriptJSON":
                    dialog.setFileFilter(dialog.AmberscriptJSONFileFilter);
                    break;
                case "AdobePremiereCSV":
                    dialog.setFileFilter(dialog.AdobePremiereCSVFilter);
                    break;
                default:
                    break;
            }
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
            else if (selectedFileFilter==dialog.HTMLPartiturCompactFilter) {lastFileFilter = "HTMLPartiturCompact";}
            else if (selectedFileFilter==dialog.HTMLPartiturWithSVGFileFilter) {lastFileFilter = "HTMLPartiturSVG";}
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
            else if (selectedFileFilter==dialog.GATWithHTML5AudioFileFilter) {lastFileFilter = "GATHTML5";}
            else if (selectedFileFilter==dialog.SimpleTextTranscriptFileFilter) {lastFileFilter = "SimpleTextTranscript";}            
            else if (selectedFileFilter==dialog.CHATTranscriptFileFilter) {lastFileFilter = "CHATTranscript";}
            
            else if (selectedFileFilter==dialog.FOLKERTranscriptionFileFilter) {lastFileFilter = "FOLKERTranscription";}
            else if (selectedFileFilter==dialog.FLKTranscriptionFileFilter) {lastFileFilter = "FLKTranscription";}
            else if (selectedFileFilter==dialog.FLNTranscriptionFileFilter) {lastFileFilter = "FLNTranscription";}
            
            else if (selectedFileFilter==dialog.TreeTaggerFilter) {lastFileFilter = "TreeTagger";}
            else if (selectedFileFilter==dialog.XSLStylesheetImportFilter) {lastFileFilter = "XSLImport";}
            else if (selectedFileFilter==dialog.TranscriberFileFilter) {lastFileFilter = "Transcriber";}
            else if (selectedFileFilter==dialog.WinPitchFileFilter) {lastFileFilter = "WinPitch";}
            else if (selectedFileFilter==dialog.AudacityLabelFileFilter) {lastFileFilter = "Audacity";}
            else if (selectedFileFilter==dialog.PhonFileFilter) {lastFileFilter = "Phon";}
            else if (selectedFileFilter==dialog.TransanaXMLFileFilter) {lastFileFilter = "TransanaXML";}
            else if (selectedFileFilter==dialog.F4TextFileFilter) {lastFileFilter = "F4Text";}
            else if (selectedFileFilter==dialog.SRTFileFilter) {lastFileFilter = "SRT";}
            else if (selectedFileFilter==dialog.VTTFileFilter) {lastFileFilter = "VTT";}
            else if (selectedFileFilter==dialog.FlexTextXMLFileFilter) {lastFileFilter = "Flextext";}
            else if (selectedFileFilter==dialog.FrazierADCFileFilter) {lastFileFilter = "FrazierADC";}
            else if (selectedFileFilter==dialog.WhisperJSONFileFilter) {lastFileFilter = "WhisperJSON";}
            else if (selectedFileFilter==dialog.AmberscriptJSONFileFilter) {lastFileFilter = "AmberscriptJSON";}
            else if (selectedFileFilter==dialog.AdobePremiereCSVFilter) {lastFileFilter = "AdobePremiereCSV";}


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
