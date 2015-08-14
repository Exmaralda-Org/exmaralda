/*
 * ExportAGAction.java
 *
 * Created on 17. Juni 2003, 11:00
 */

package org.exmaralda.partitureditor.partiture.fileActions;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.OutputFileDialog;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.convert.*;
import java.io.*;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.partitureditor.interlinearText.*;
import org.exmaralda.partitureditor.interlinearText.swing.ChooseSettingsForXMLExportPanel;
import org.exmaralda.partitureditor.jexmaralda.segment.GATSegmentation;
import org.xml.sax.SAXException;
import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.SelectionAccessory;
import org.jdom.*;
import org.jdom.xpath.*;

/**
 *
 * exports the current transcription to some 3rd party format
 * Menu: File --> Export... 
 * @author  thomas
 */
public class OutputAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of ExportAGAction */
    public OutputAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Output...", icon, t);  
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("outputAction!");
        table.commitEdit(true);
        try {
            output();
        } catch (Exception ex) {
            String message = "Output failed:\n" + ex.getLocalizedMessage();
            javax.swing.JOptionPane.showMessageDialog(table, message);
            ex.printStackTrace();
        }
    }
    
    private void output() throws IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JDOMException, JexmaraldaException, FSMException {
        OutputFileDialog dialog = new OutputFileDialog(table.homeDirectory);
        ActionUtilities.setFileFilter("last-output-filter", table.getTopLevelAncestor(), dialog);

        int retValue = dialog.showSaveDialog(table.parent);
        if (retValue!=javax.swing.JFileChooser.APPROVE_OPTION) return;
        ParameterFileFilter selectedFileFilter = (ParameterFileFilter)(dialog.getFileFilter());
        File selectedFile = dialog.getSelectedFile();
        String filename = selectedFile.getAbsolutePath();
        
        //check whether or not the selected file has an extension
        if (!(selectedFile.getName().indexOf(".")>=0)){
            filename+="." + selectedFileFilter.getSuffix();
        }
        
        File exportFile = new File(filename);
        //check whether the export file already exists
        if (exportFile.exists()){
            int confirm = 
                javax.swing.JOptionPane.showConfirmDialog(table, exportFile.getAbsolutePath() + "\nalready exists. Overwrite?");
            if (confirm==javax.swing.JOptionPane.CANCEL_OPTION) return;
            if (confirm==javax.swing.JOptionPane.NO_OPTION) output();
        }
        
        // now do the real output
        BasicTranscription trans = null;

        // CHANGE 12-04-2010
        // respect choices made in the selectionPanel
        switch (dialog.getSelectionChoice()){
            case SelectionAccessory.ALL :
                trans = table.getModel().getTranscription().makeCopy();
                break;
            case SelectionAccessory.VISIBLE :
                trans = table.getVisibleTiersAsNewTranscription();
                break;
            case SelectionAccessory.SELECTION :
                trans = table.getCurrentSelectionAsNewTranscription();
                break;
        }
        
        // partitur output methods
        if ((selectedFileFilter==dialog.HTMLPartiturFileFilter) 
                || (selectedFileFilter==dialog.HTMLPartiturWithHTML5AudioFileFilter) 
                || (selectedFileFilter==dialog.HTMLPartiturWithFlashFileFilter) 
                || (selectedFileFilter==dialog.RTFPartiturFileFilter) 
                || (selectedFileFilter==dialog.SVGPartiturFileFilter)
                || (selectedFileFilter==dialog.XMLPartiturFileFilter)) {
                
            InterlinearText it = null;
            // changed 02-08-2010
            if (dialog.getSelectionChoice()!=SelectionAccessory.SELECTION){
                    it = ItConverter.BasicTranscriptionToInterlinearText(trans, table.getModel().getTierFormatTable());
            } else {
                int timelineStart = table.selectionStartCol;
                it = ItConverter.BasicTranscriptionToInterlinearText(trans, table.getModel().getTierFormatTable(), timelineStart);
            }
            System.out.println("Transcript converted to interlinear text.");
            if (table.getFrameEndPosition()>=0){
                ((ItBundle)it.getItElementAt(0)).frameEndPosition
                        = table.getFrameEndPosition();
            }
            
            if (selectedFileFilter==dialog.HTMLPartiturFileFilter){
                // HTML partitur
                exportHTMLPartitur(it, filename, dialog.framesAccessory.useFrames());                                
            } else if (selectedFileFilter==dialog.RTFPartiturFileFilter){
                // RTF partitur
                exportRTFPartitur(it, filename);                                
            } else if (selectedFileFilter==dialog.SVGPartiturFileFilter){
                // SVG partitur
                exportSVGPartitur(it, filename, dialog.svgAccessory.getSubdirectory(), dialog.svgAccessory.getBasename());                                
            } else if (selectedFileFilter==dialog.XMLPartiturFileFilter){
                // XML partitur
                exportXMLPartitur(it, filename, dialog.chooseSettingsForXMLExportPanel.getSelection());                                
            } else if (selectedFileFilter==dialog.HTMLPartiturWithFlashFileFilter){
                exportHTMLPartiturWithFlash(it, filename);
            } else if (selectedFileFilter==dialog.HTMLPartiturWithHTML5AudioFileFilter){
                exportHTMLPartiturWithHTML5Audio(it, filename);
            }
        } else if (selectedFileFilter==dialog.FreeStylesheetFileFilter){
            exportFreeStylesheet(filename, dialog.encodings[dialog.encodingComboBox.getSelectedIndex()]);
        } else if (selectedFileFilter==dialog.HTMLSegmentChainFileFilter){
            exportHTMLSegmentChainList(trans, filename, table.getModel().getTierFormatTable());
        } else if (selectedFileFilter==dialog.HTMLSegmentChainWithFlashFileFilter){
            exportHTMLSegmentChainListWithFlash(trans, filename);
        } else if (selectedFileFilter==dialog.HTMLSegmentChainWithHTML5AudioFileFilter){
            exportHTMLSegmentChainListWithHTML5Audio(trans, filename);
        } else if (selectedFileFilter==dialog.GATTranscriptFileFilter){
            exportGATTranscript(trans, filename, dialog.encodings[dialog.encodingComboBox.getSelectedIndex()]);
        } else if (selectedFileFilter==dialog.SimpleTextTranscriptFileFilter){
            exportSimpleTextTranscript(trans, filename);
        }
        ActionUtilities.memorizeFileFilter("last-output-filter", table.getTopLevelAncestor(), dialog);
        
        if (filename.endsWith("html") || filename.endsWith("htm")){
            BrowserLauncher.openURL(new File(filename).toURI().toURL().toString());
        }

        table.status("Transcription output as " + filename);


    }
    
    void exportHTMLPartitur(InterlinearText it, String filename, boolean useFrames) throws IOException{
        Head head = table.getModel().getTranscription().getHead();
        if (table.htmlParameters.prependAdditionalInformation){
            if (table.head2HTMLStylesheet.length()>0){
                try{
                    org.exmaralda.partitureditor.jexmaralda.convert.HTMLConverter converter = new org.exmaralda.partitureditor.jexmaralda.convert.HTMLConverter();
                    table.htmlParameters.additionalStuff = converter.HeadToHTML(head, table.head2HTMLStylesheet);
                } catch (Exception e){
                    String text = new String("There was a problem with " + System.getProperty("line.separator"));
                    text+=table.head2HTMLStylesheet + " : " + System.getProperty("line.separator");
                    text+=e.getLocalizedMessage() + System.getProperty("line.separator");
                    text+="Using internal stylesheet instead.";                    
                    javax.swing.JOptionPane.showMessageDialog(table.getParent(), text);
                    table.htmlParameters.additionalStuff = head.toHTML();
                }                
            } else {
                // if no custom stylesheet is specified,
                // simply apply the built in stylesheet
                table.htmlParameters.additionalStuff = head.toHTML();
            }
        }
        HTMLParameters param = table.htmlParameters;
        if (param.getWidth()>0) {
            it.trim(param);
        }
        if (!useFrames){
            it.writeHTMLToFile(filename,param);
        } else {
            // write frame html

            // write IT HTML
            String nakedFileName = filename.substring(0, filename.lastIndexOf('.'));
            String outputFileName = nakedFileName + "_p.html";
            param.linkTarget="LinkFrame";
            it.writeHTMLToFile(outputFileName,param);                                

            System.out.println("started writing document...");
            FileOutputStream fos = new FileOutputStream(filename);

            fos.write("<html><head></head>".getBytes());
            fos.write("<frameset rows=\"70%,30%\">".getBytes());
            fos.write("<frame src=\"".getBytes());
            fos.write(outputFileName.substring(outputFileName.lastIndexOf(System.getProperty("file.separator"))+1).getBytes());
            fos.write("\" name=\"IT\">".getBytes());
            fos.write("<frame name=\"LinkFrame\">".getBytes());
            fos.write("<noframes></noframes></frameset></html>".getBytes());

            fos.close();
            System.out.println("document written.");
        }
        table.htmlDirectory = filename;
        table.htmlParameters.additionalStuff = "";                
    }
    
    void exportRTFPartitur(InterlinearText it, String filename) throws IOException{
        if (table.rtfParameters.prependAdditionalInformation){
            table.rtfParameters.additionalStuff = table.getModel().getTranscription().getHead().toRTF();
        }
        RTFParameters param = table.rtfParameters;
        
        it.trim(param);
        if (param.makePageBreaks){it.calculatePageBreaks(param);}
        if (param.glueAdjacent){
            it.glueAdjacentItChunks(param.glueEmpty, param.criticalSizePercentage);
        }
        it.writeTestRTF(filename,param);

        table.rtfDirectory = filename;
        table.rtfParameters.additionalStuff = "";
        table.rtfParameters.clearMappings();        
    }
    
    void exportSVGPartitur(InterlinearText it, String filename, String subdirectory, String basename) throws IOException{
        SVGParameters param = table.svgParameters;
        it.trim(param);
        it.writeSVGToFile(param, filename, subdirectory, basename);        
    }
    
    void exportXMLPartitur(InterlinearText it, String filename, short parameterSelection) throws IOException{
        switch (parameterSelection){
            case ChooseSettingsForXMLExportPanel.USE_PRINT_SETTINGS :
                it.trim(table.printParameters);
                break;
            case ChooseSettingsForXMLExportPanel.USE_RTF_SETTINGS :
                it.trim(table.rtfParameters);
                break;
            case ChooseSettingsForXMLExportPanel.USE_HTML_SETTINGS :
                if (table.htmlParameters.getWidth()>0){
                    it.trim(table.htmlParameters);
                } else {
                    it.calculateOffsets();
                }
                break;
        }
        it.writeXMLToFile(filename);
    }
    
    void exportFreeStylesheet(String filename, String encoding) throws SAXException, IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException{
        if (table.freeStylesheetVisualisationStylesheet.length()==0){
            javax.swing.JOptionPane.showMessageDialog(  table.parent,
                                            "No stylesheet defined in preferences.\n"
                                            + "Please define a stylesheet under\n"
                                            + "Edit > Preferences...",
                                            "Error",
                                            javax.swing.JOptionPane.ERROR_MESSAGE);
            return;            
        }
        
        StylesheetFactory sf = new StylesheetFactory();
        String resultText = sf.applyExternalStylesheetToString( table.freeStylesheetVisualisationStylesheet, 
                                                                table.getModel().getTranscription().toXML());
        System.out.println("started writing document...");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        if (encoding.length()==0){
            fos.write(resultText.getBytes());
        } else {
            fos.write(resultText.getBytes(encoding));
        }
        fos.close();
        System.out.println("document written.");                       
                
    }
    
    void exportHTMLSegmentChainList(BasicTranscription bt, String filename, TierFormatTable tft) throws IOException{
         SegmentedTranscription st = bt.toSegmentedTranscription();
         SegmentedToListInfo info = new SegmentedToListInfo(st, SegmentedToListInfo.TURN_SEGMENTATION);
         ListTranscription lt = st.toListTranscription(info);
         lt.getBody().sort();
         lt.writeHTMLToFile(filename,tft);
         table.htmlDirectory = filename;        
    }
    
    void exportHTMLSegmentChainListWithHTML5Audio(BasicTranscription bt, String filename) throws SAXException, ParserConfigurationException, IOException, TransformerConfigurationException, TransformerException, JDOMException {
         SegmentedTranscription st = bt.toSegmentedTranscription();
         SegmentedToListInfo info = new SegmentedToListInfo(st, SegmentedToListInfo.TURN_SEGMENTATION);
         ListTranscription lt = st.toListTranscription(info);
         // added 24-11-2009
         lt.getHead().getMetaInformation().relativizeReferencedFile(filename);
         lt.getBody().sort();

         StylesheetFactory sf = new StylesheetFactory(true);
         String xslString = "/org/exmaralda/partitureditor/jexmaralda/xsl/List2HTML5.xsl";
         String result = sf.applyInternalStylesheetToString(xslString, lt.toXML());

         String s1 = "HTML";
         String s2 = "-//W3C//DTD HTML 4.01//EN";
         Document resultDoc = FileIO.readDocumentFromString(result);
         FileIO.writeDocumentToLocalFile(filename,resultDoc, true, s1, s2);
         table.htmlDirectory = filename;

    }

    void exportHTMLSegmentChainListWithFlash(BasicTranscription bt, String filename) throws IOException, SAXException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JDOMException{
         if (table.getModel().getTranscription().getHead().getMetaInformation().getReferencedFile("mp3")==null){
             throw new IOException("Transcription does not reference an MP3 file.\nPlease add an MP3 file under \nTranscription > Recordings...");
         }
         SegmentedTranscription st = bt.toSegmentedTranscription();
         SegmentedToListInfo info = new SegmentedToListInfo(st, SegmentedToListInfo.TURN_SEGMENTATION);
         ListTranscription lt = st.toListTranscription(info);
         // added 24-11-2009
         lt.getHead().getMetaInformation().relativizeReferencedFile(filename);
         lt.getBody().sort();

         StylesheetFactory sf = new StylesheetFactory(true);
         String xslString = "/org/exmaralda/partitureditor/jexmaralda/xsl/List2FlashHTML.xsl";
         String result = sf.applyInternalStylesheetToString(xslString, lt.toXML());

         String s1 = "HTML";
         String s2 = "-//W3C//DTD HTML 4.01//EN";
         Document resultDoc = FileIO.readDocumentFromString(result);
         FileIO.writeDocumentToLocalFile(filename,resultDoc, true, s1, s2);
         //lt.writeHTMLToFile(filename, filename);
         //lt.writeHTMLToFile(filename);
         table.htmlDirectory = filename;
         copy("/org/exmaralda/common/flashplayer/player.swf", filename);
         copy("/org/exmaralda/common/flashplayer/seeker.swf", filename);
         copy("/org/exmaralda/common/flashplayer/seeker.html", filename);
    }

    void transformPartiturViaStylesheet(InterlinearText it, String filename, String stylesheetPath) throws SAXException, ParserConfigurationException, IOException, TransformerConfigurationException, TransformerException, JDOMException{
        HTMLParameters param = table.htmlParameters;                        
        it.trim(param);
        File tempFile = File.createTempFile("itxml", "xml");
        tempFile.deleteOnExit();
        String xmlFilename = tempFile.getAbsolutePath();
        it.writeXMLToFile(xmlFilename);


        Document itDocument = org.exmaralda.common.corpusbuild.FileIO.readDocumentFromLocalFile(xmlFilename);
        Document btDocument = org.exmaralda.common.corpusbuild.FileIO.readDocumentFromString(table.getModel().getTranscription().toXML());

        // remove "line" elements (die stören nur)
        java.util.Iterator i = itDocument.getRootElement().getDescendants(new org.jdom.filter.ElementFilter("line"));
        java.util.Vector toBeRemoved = new java.util.Vector();
        while (i.hasNext()){
            toBeRemoved.addElement(i.next());
        }
        for (int pos=0; pos<toBeRemoved.size(); pos++){
            Element e = (Element)(toBeRemoved.elementAt(pos));
            e.detach();
        }

        XPath xpath1 = XPath.newInstance("//common-timeline");
        Element timeline = (Element)(xpath1.selectSingleNode(btDocument));
        timeline.detach();

        XPath xpath2 = XPath.newInstance("//head");
        Element head = (Element)(xpath2.selectSingleNode(btDocument));
        head.detach();

        XPath xpath3 = XPath.newInstance("//tier");
        java.util.List tiers = xpath3.selectNodes(btDocument);
        Element tiersElement = new Element("tiers");
        for (int pos=0; pos<tiers.size(); pos++){
            Element t = (Element)(tiers.get(pos));
            t.detach();
            t.removeContent();
            tiersElement.addContent(t);
        }

        Element nameElement = new Element("name");
        nameElement.setAttribute("name", filename);

        Element tableWidthElement = new Element("table-width");
        tableWidthElement.setAttribute("table-width", Long.toString(Math.round(param.getWidth())));

        Element idElement = new Element("unique-id");
        idElement.setAttribute("id", Long.toString(System.currentTimeMillis()));

        Element btElement = new Element("basic-transcription");
        btElement.addContent(nameElement);
        btElement.addContent(tableWidthElement);
        btElement.addContent(head);
        btElement.addContent(timeline);
        btElement.addContent(tiersElement);
        btElement.addContent(idElement);

        itDocument.getRootElement().addContent(btElement);

        org.exmaralda.common.corpusbuild.FileIO.writeDocumentToLocalFile(xmlFilename,itDocument);

        System.out.println("Transforming...");

        //java.io.InputStream is = getClass().getResourceAsStream("/org/exmaralda/partitureditor/jexmaralda/xsl/Partitur2FlashHTML.xsl");
        //org.jdom.Document transformerDoc = org.exmaralda.common.corpusbuild.FileIO.readDocumentFromInputStream(is);
        //org.jdom.transform.XSLTransformer transformer = new org.jdom.transform.XSLTransformer(transformerDoc);
        
        org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf =
            new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory(true);
        String htmlPartiturString =
                sf.applyInternalStylesheetToString(stylesheetPath,
                org.exmaralda.common.jdomutilities.IOUtilities.documentToString(itDocument));
        //Document htmlPartiturDoc = transformer.transform(itDocument);
        Document htmlPartiturDoc = org.exmaralda.common.corpusbuild.FileIO.readDocumentFromString(htmlPartiturString);
        
        XPath xpath4 = XPath.newInstance("//style");
        Element style = (Element)(xpath4.selectSingleNode(htmlPartiturDoc));
        style.setText(table.getModel().getTierFormatTable().toTDCSS()+ "\n" +style.getText() );
        
        org.exmaralda.common.corpusbuild.FileIO.writeDocumentToLocalFile(filename,htmlPartiturDoc, true);
        
    }
    
    void exportHTMLPartiturWithHTML5Audio(InterlinearText it, String filename) throws IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JDOMException{
        String xslString = "/org/exmaralda/partitureditor/jexmaralda/xsl/Partitur2HTML5.xsl";        
        transformPartiturViaStylesheet(it, filename, xslString);
        table.htmlDirectory = filename;
        table.htmlParameters.additionalStuff = "";                
    }

    void exportHTMLPartiturWithFlash(InterlinearText it, String filename) throws IOException, JDOMException, SAXException, ParserConfigurationException, TransformerException{
            
        if (table.getModel().getTranscription().getHead().getMetaInformation().getReferencedFile("mp3")==null){
             throw new IOException("Transcription does not reference an MP3 file.\nPlease add an MP3 file under \nTranscription > Recordings...");
        }

        String xslString = "/org/exmaralda/partitureditor/jexmaralda/xsl/Partitur2FlashHTML.xsl";        
        transformPartiturViaStylesheet(it, filename, xslString);
        
        copy("/org/exmaralda/common/flashplayer/player.swf", filename);
        copy("/org/exmaralda/common/flashplayer/seeker.swf", filename);
        copy("/org/exmaralda/common/flashplayer/seeker.html", filename);

        table.htmlDirectory = filename;
        table.htmlParameters.additionalStuff = "";                
        
    }
    
    void exportGATTranscript(BasicTranscription bt, String filename, String encoding) throws JexmaraldaException, FSMException, SAXException, FileNotFoundException, IOException{
         // segment the basic transcription and transform it into a list transcription
         GATSegmentation segmenter = new org.exmaralda.partitureditor.jexmaralda.segment.GATSegmentation(table.gatFSM);
         ListTranscription lt = segmenter.BasicToIntonationUnitList(bt);
             
         String text = GATSegmentation.toText(lt);
         
         System.out.println("started writing document...");
         FileOutputStream fos = new FileOutputStream(new File(filename));
         if (encoding.length()==0){
             fos.write(text.getBytes());
         } else {
             fos.write(text.getBytes(encoding));
         }
         fos.close();
         System.out.println("document written.");                               
    }
    
    void exportSimpleTextTranscript(BasicTranscription bt, String filename) throws IOException{
         SegmentedTranscription st = bt.toSegmentedTranscription();
         SegmentedToListInfo info = new SegmentedToListInfo(st, SegmentedToListInfo.TURN_SEGMENTATION);
         ListTranscription lt = st.toListTranscription(info);
         lt.getBody().sort();
         lt.writeSimpleTextToFile(filename);
    }

    


    
    private void copy(String pathToInternalResource, String filename) throws FileNotFoundException, IOException{
    	 BufferedInputStream bis = new BufferedInputStream(getClass().getResourceAsStream(pathToInternalResource), 4096);
         String theName = pathToInternalResource.substring(pathToInternalResource.lastIndexOf("/")+1);
         File targetFile = new File(new File(filename).getParent() + System.getProperty("file.separator") + theName); // destination
         BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile), 4096);
         int theChar;
         while ((theChar = bis.read()) != -1) {
            bos.write(theChar);
         }
         bos.close();
         bis.close();
        
    }
    
    
}
