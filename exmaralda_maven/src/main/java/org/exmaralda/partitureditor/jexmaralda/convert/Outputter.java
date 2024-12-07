/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.interlinearText.HTMLParameters;
import org.exmaralda.partitureditor.interlinearText.InterlinearText;
import org.exmaralda.partitureditor.interlinearText.ItBundle;
import org.exmaralda.partitureditor.interlinearText.RTFParameters;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Head;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class Outputter {
    
    public static enum OUTPUT_FORMAT {
        HTML, RTF, TXT, SVG
    };
    
    public static enum OUTPUT_LAYOUT {
        PARTITUR, LIST_SEGMENT_CHAINS, LIST_GAT_INTONATION_UNITS, LIST_HIAT_UTTERANCES, LIST_CHAT_UTTERANCES
    };
    
    public static enum OUTPUT_VARIANT {
        HTML_PARTITUR,
        HTML_PARTITUR_WITH_AUDIO,
        RTF_PARTITUR
    }
    
    public static OUTPUT_FORMAT getOutputFormatForVariant(OUTPUT_VARIANT variant){
        switch (variant){
            case HTML_PARTITUR :
            case HTML_PARTITUR_WITH_AUDIO : 
                return OUTPUT_FORMAT.HTML;
            case RTF_PARTITUR : 
                return OUTPUT_FORMAT.RTF;
        }
        return OUTPUT_FORMAT.HTML;
    }
    
    public static OUTPUT_LAYOUT getOutputLayoutForVariant(OUTPUT_VARIANT variant){
        switch (variant){
            case HTML_PARTITUR :
            case HTML_PARTITUR_WITH_AUDIO : 
            case RTF_PARTITUR :
                return OUTPUT_LAYOUT.PARTITUR;
        }
        return OUTPUT_LAYOUT.PARTITUR;
    }
    
    public static String HTML_PARTITUR_WITH_AUDIO_XSL = "/org/exmaralda/partitureditor/jexmaralda/xsl/Partitur2HTML5.xsl";

    
    BasicTranscription basicTranscription;
    

    public Outputter(BasicTranscription basicTranscription) {
        this.basicTranscription = basicTranscription;
    }
    
    /**
     * @param outputFile
     * @param thisVariant
     * @param parameters
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws javax.xml.transform.TransformerException
     * @throws javax.xml.transform.TransformerConfigurationException
     * @throws org.jdom.JDOMException
     **************************************/

    public void output(File outputFile, OUTPUT_VARIANT thisVariant, Map<String,Object> parameters) 
            throws IOException, SAXException, ParserConfigurationException, TransformerException, TransformerConfigurationException, JDOMException
    {
        switch (getOutputLayoutForVariant(thisVariant)){
            case PARTITUR :
                // convert the transcription to interlinear text
                InterlinearText interlinearText = ItConverter.BasicTranscriptionToInterlinearText(basicTranscription, basicTranscription.getTierFormatTable());
                
                // set the frame end position if present in the parameters
                int frameEndPosition = (int) parameters.getOrDefault("FrameEndPosition", -1);
                if (frameEndPosition>=0){
                    ((ItBundle)interlinearText.getItElementAt(0)).frameEndPosition = frameEndPosition;
                }
                switch (thisVariant){
                    case HTML_PARTITUR :
                        exportHTMLPartitur(interlinearText, outputFile, parameters);
                    case HTML_PARTITUR_WITH_AUDIO :
                        transformPartiturViaStylesheet(interlinearText, outputFile, HTML_PARTITUR_WITH_AUDIO_XSL, parameters);
                        break;
                    case RTF_PARTITUR :
                        exportRTFPartitur(interlinearText, outputFile, parameters);
                }
                break;
            case LIST_SEGMENT_CHAINS :
                break;
            case LIST_GAT_INTONATION_UNITS :
                break;
            case LIST_HIAT_UTTERANCES :
                break;
            case LIST_CHAT_UTTERANCES :
                break;
        }
    }
    
    /****************************************/

    void exportHTMLPartitur(InterlinearText it, File file, Map<String,Object> parameters) throws IOException{
        HTMLParameters htmlParameters = (HTMLParameters)parameters.get("HTMLParameters");       
        String head2HTMLStylesheet = (String)parameters.getOrDefault("Head2HTMLStylesheet", "");       
        boolean useFrames = (Boolean)parameters.getOrDefault("UseFrames", false);       
        if (htmlParameters.prependAdditionalInformation){
            Head head = basicTranscription.getHead();
            if (head2HTMLStylesheet.length()>0){
                try{
                    HTMLConverter converter = new HTMLConverter();
                    htmlParameters.additionalStuff = converter.HeadToHTML(head, head2HTMLStylesheet);
                } catch (IOException | ParserConfigurationException | TransformerException | JexmaraldaException | SAXException e){
                    String text = "There was a problem with " + System.getProperty("line.separator");
                    text+=head2HTMLStylesheet + " : " + System.getProperty("line.separator");
                    text+=e.getLocalizedMessage() + System.getProperty("line.separator");
                    text+="Using internal stylesheet instead.";                    
                    System.out.println(text);
                    htmlParameters.additionalStuff = head.toHTML();
                }                
            } else {
                // if no custom stylesheet is specified,
                // simply apply the built in stylesheet
                htmlParameters.additionalStuff = head.toHTML();
            }
        }
        if (htmlParameters.getWidth()>0) {
            it.trim(htmlParameters);
        }
        if (!useFrames){
            it.writeHTMLToFile(file.getAbsolutePath(),htmlParameters);
        } else {
            // write frame html

            // write IT HTML
            String nakedFileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
            String outputFileName = nakedFileName + "_p.html";
            htmlParameters.linkTarget="LinkFrame";
            it.writeHTMLToFile(outputFileName,htmlParameters);                                

            System.out.println("started writing document...");
            FileOutputStream fos = new FileOutputStream(file);

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
    }
    
    /****************************************/
    
    void exportRTFPartitur(InterlinearText it, File file, Map<String,Object> parameters) throws IOException{
        RTFParameters rtfParameters = (RTFParameters)parameters.get("RTFParameters");       
        if (rtfParameters.prependAdditionalInformation){
            rtfParameters.additionalStuff = basicTranscription.getHead().toRTF();
        }
        
        it.trim(rtfParameters);
        if (rtfParameters.makePageBreaks){it.calculatePageBreaks(rtfParameters);}
        if (rtfParameters.glueAdjacent){
            it.glueAdjacentItChunks(rtfParameters.glueEmpty, rtfParameters.criticalSizePercentage);
        }
        it.writeTestRTF(file.getAbsolutePath(),rtfParameters);
    }
    
    /****************************************/
       
    void transformPartiturViaStylesheet(InterlinearText it, File file, String stylesheetPath, Map<String,Object> parameters) 
            throws SAXException, ParserConfigurationException, IOException, TransformerConfigurationException, TransformerException, JDOMException
    {
        //HTMLParameters param = table.htmlParameters;                        
        HTMLParameters param = (HTMLParameters)parameters.get("HTMLParameters");
        it.trim(param);
        File tempFile = File.createTempFile("itxml", "xml");
        tempFile.deleteOnExit();
        String xmlFilename = tempFile.getAbsolutePath();
        it.writeXMLToFile(xmlFilename);


        Document itDocument = org.exmaralda.common.corpusbuild.FileIO.readDocumentFromLocalFile(xmlFilename);
        Document btDocument = org.exmaralda.common.corpusbuild.FileIO.readDocumentFromString(basicTranscription.toXML());

        // remove "line" elements (die stoeren nur)
        Iterator<Element> i = itDocument.getRootElement().getDescendants(new ElementFilter("line"));
        List<Element> toBeRemoved = new ArrayList<>();
        while (i.hasNext()){
            toBeRemoved.add(i.next());
        }
        for (int pos=0; pos<toBeRemoved.size(); pos++){
            Element e = (Element)(toBeRemoved.get(pos));
            e.detach();
        }

        XPath xpath1 = XPath.newInstance("//common-timeline");
        Element timeline = (Element)(xpath1.selectSingleNode(btDocument));
        timeline.detach();

        XPath xpath2 = XPath.newInstance("//head");
        Element head = (Element)(xpath2.selectSingleNode(btDocument));
        head.detach();

        XPath xpath3 = XPath.newInstance("//tier");
        List tiers = xpath3.selectNodes(btDocument);
        Element tiersElement = new Element("tiers");
        for (Object o : tiers){
            Element t = (Element)o;
            t.detach();
            t.removeContent();
            tiersElement.addContent(t);
        }

        Element nameElement = new Element("name");
        nameElement.setAttribute("name", file.getAbsolutePath());

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

        org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf =
            new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory(true);
        String htmlPartiturString =
                sf.applyInternalStylesheetToString(stylesheetPath,
                org.exmaralda.common.jdomutilities.IOUtilities.documentToString(itDocument));
        Document htmlPartiturDoc = org.exmaralda.common.corpusbuild.FileIO.readDocumentFromString(htmlPartiturString);
        
        XPath xpath4 = XPath.newInstance("//style");
        Element style = (Element)(xpath4.selectSingleNode(htmlPartiturDoc));
        style.setText(basicTranscription.getTierFormatTable().toTDCSS()+ "\n" +style.getText() );
        
        FileIO.writeDocumentToLocalFile(file, htmlPartiturDoc);
        
    }
    
    
    
    
    
}
