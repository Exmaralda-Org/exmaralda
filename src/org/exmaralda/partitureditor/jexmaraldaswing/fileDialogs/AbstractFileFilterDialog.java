/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs;

import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;

/**
 *
 * @author thomas
 */
public class AbstractFileFilterDialog extends javax.swing.JFileChooser {
    public ParameterFileFilter AudacityLabelFileFilter = new ParameterFileFilter("txt", "Audacity Label File (*.txt)");
    public ParameterFileFilter AGFileFilter = new ParameterFileFilter("xml", "Annotation Graph File (*.xml)");
    public ParameterFileFilter EAFFileFilter = new ParameterFileFilter("eaf", "ELAN Annotation File (*.eaf)");
    public ParameterFileFilter HIATDOSFileFilter = new ParameterFileFilter("dat", "HIAT-DOS file (*.dat)");
    public ParameterFileFilter PraatFileFilter = new ParameterFileFilter("textGrid", "PRAAT Textgrid (*.textGrid)");
    public ParameterFileFilter SimpleExmaraldaFileFilter = new ParameterFileFilter("txt", "Simple EXMARaLDA text file (*.txt)");
    public ParameterFileFilter RioDeJaneiroFileFilter = new ParameterFileFilter("txt", "Rio de Janeiro style text transcription (*.txt)");
    public ParameterFileFilter TASXFileFilter = new ParameterFileFilter("xml", "TASX Annotation File (*.xml)");
    public ParameterFileFilter TEIFileFilter = new ParameterFileFilter("xml", "TEI file (*.xml)");
    static String[] tcf_suff = {"tcf", "xml"};
    public ParameterFileFilter TCFFileFilter = new ParameterFileFilter(tcf_suff, "TCF file (*.tcf, *.xml)");
    public ParameterFileFilter TEIModenaFileFilter = new ParameterFileFilter("xml", "Modena TEI file (*.xml)");
    public ParameterFileFilter TextFileFilter = new ParameterFileFilter("txt", "Plain text file (*.txt)");
    public ParameterFileFilter exSyncFileFilter = new ParameterFileFilter("xml", "ExSync file (*.xml)");
    public ParameterFileFilter HTMLPartiturFileFilter = new ParameterFileFilter("html", "HTML Partitur (*.html)");
    public ParameterFileFilter RTFPartiturFileFilter = new ParameterFileFilter("rtf", "RTF Partitur (*.rtf)");
    public ParameterFileFilter SVGPartiturFileFilter = new ParameterFileFilter("html", "SVG Partitur (*.html)");
    public ParameterFileFilter XMLPartiturFileFilter = new ParameterFileFilter("xml", "XML Partitur (*.xml)");
    public ParameterFileFilter HTMLSegmentChainFileFilter = new ParameterFileFilter("html", "HTML Segment Chain List (*.html)");
    public ParameterFileFilter FreeStylesheetFileFilter = new ParameterFileFilter("html", "Free Stylesheet Transformation (*.html)");
    public ParameterFileFilter HTMLPartiturWithFlashFileFilter = new ParameterFileFilter("html", "HTML Partitur + Flash Player (*.html)");
    public ParameterFileFilter HTMLPartiturWithHTML5AudioFileFilter = new ParameterFileFilter("html", "HTML Partitur + HTML5 Video/Audio (*.html)");
    public ParameterFileFilter HTMLSegmentChainWithFlashFileFilter = new ParameterFileFilter("html", "HTML Segment Chain List + Flash Player (*.html)");
    public ParameterFileFilter HTMLSegmentChainWithHTML5AudioFileFilter = new ParameterFileFilter("html", "HTML Segment Chain List + HTML5 Audio (*.html)");
    static String[] seg_suff = {"exs", "xml"};
    public ParameterFileFilter ExmaraldaSegmentedTranscriptionFileFilter = new ParameterFileFilter(seg_suff, "EXMARaLDA Segmented Transcription (*.exs)");
    public ParameterFileFilter GATTranscriptFileFilter = new ParameterFileFilter("txt", "GAT transcript (*.txt)");
    static String[] f4_suff = {"txt", "rtf"};
    public ParameterFileFilter F4TextFileFilter = new ParameterFileFilter(f4_suff, "F4 transcript (*.txt, *.rtf)");
    public ParameterFileFilter SimpleTextTranscriptFileFilter = new ParameterFileFilter("txt", "Simple text output (*.txt)");
    public ParameterFileFilter CHATTranscriptFileFilter = new ParameterFileFilter("cha", "CHAT transcript (*.cha)");
    static String[] fl_suff = {"flk", "fln"};
    public ParameterFileFilter FOLKERTranscriptionFileFilter = new ParameterFileFilter(fl_suff, "FOLKER transcription (*.flk, *.fln)");
    public ParameterFileFilter TreeTaggerFilter = new ParameterFileFilter("txt", "Tree Tagger Output (*.txt)");
    public ParameterFileFilter XSLStylesheetImportFilter = new ParameterFileFilter("xml", "Import via XSL Stylesheet (*.xml)");
    public ParameterFileFilter TranscriberFileFilter = new ParameterFileFilter("trs", "Transcriber file (*.trs)");
    static String[] winpitch_suff = {"alg", "xml"};
    public ParameterFileFilter WinPitchFileFilter = new ParameterFileFilter(winpitch_suff, "Winpitch file (*.alg, *.xml)");
    public ParameterFileFilter AnvilFileFilter = new ParameterFileFilter("anvil", "Anvil annotation file (*.anvil)");
    public ParameterFileFilter PhonFileFilter = new ParameterFileFilter("xml", "Phon transcription (*.xml)");


}
