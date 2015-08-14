/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.exakt.tokenlist.HashtableTokenList;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class TransformOSMetaData extends AbstractSchneiderProcessor {
    
    private String STYLESHEET_PATH = "Y:\\thomas\\OS2FLK\\Meta\\Old2New.xsl";
    File transcriptDirectory;
    
    public TransformOSMetaData(String[] args){
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];
        outputDirectory = new File(args[2]);
        outputDirectory.mkdir();
        for (File f : outputDirectory.listFiles()){
            f.delete();
        }
        outputSuffix = args[3];        
        
        inputFiles = inputDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(inputSuffix.toUpperCase());
            }            
        });
        
        transcriptDirectory = new File(args[4]);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String[] myArgs = {
                "Y:\\thomas\\OS2FLK\\Meta\\os_2", "xml", 
                "Y:\\thomas\\OS2FLK\\Meta\\os_3", "xml",
                "Y:\\thomas\\OS2FLK\\11"
            };
            TransformOSMetaData aa = new TransformOSMetaData(myArgs);
            aa.processFiles();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(FixNonAlignedTranscripts.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void processFiles() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException{
        StylesheetFactory ssf = new StylesheetFactory(true);
        for (File inputFile : inputFiles){
            System.out.println("Processing " + inputFile.getName());                        
            /*Document xmlDocument = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
            if (XPath.newInstance("//Transkript").selectNodes(xmlDocument).size()<2){
                System.out.println("==== No transcript");
                continue;
            }*/
            File tryTranscript = new File("Y:\\thomas\\OS2FLK\\11\\" + inputFile.getName().substring(0, inputFile.getName().lastIndexOf(".xml")) + "_SE_01_T_01_DF_01.fln");
            System.out.println("Trying " + tryTranscript.getAbsolutePath());
            if (!(tryTranscript.exists())){
                System.out.println("==== No transcript");
                continue;
            }
            
            Document transcript = FileIO.readDocumentFromLocalFile(tryTranscript.getAbsolutePath());
            double first = Double.parseDouble(((Attribute) (XPath.newInstance("//timepoint[1]/@absolute-time").selectSingleNode(transcript))).getValue());
            double last = Double.parseDouble(((Attribute) (XPath.newInstance("//timepoint[last()]/@absolute-time").selectSingleNode(transcript))).getValue());
            double duration = (last - first) * 1000.0;
            String dauer = "00:" + org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(duration,0);
            String zeitabschnitt = "00:" + org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(1000.0 * first ,0)
                                    + " - "
                                    + "00:" + org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(1000.0 * last ,0);
            
            String transformed = ssf.applyExternalStylesheetToExternalXMLFile(STYLESHEET_PATH, inputFile.getAbsolutePath());
            Document transformedDocument = FileIO.readDocumentFromString(transformed);
            Object o = XPath.newInstance("//Transkript[1]//Dateiname").selectSingleNode(transformedDocument);
            String fln = ((Element)o).getText();
            File f = new File(transcriptDirectory, fln);
            Element e = (Element) XPath.newInstance("//Transkript[1]//Dateigröße").selectSingleNode(transformedDocument);
            e.setText(Long.toString(f.length()));
            
            HashtableTokenList htl = new HashtableTokenList();
            File[] fs = {f};
            htl.readWordsFromFolkerFiles(fs);
            int types = htl.getNumberOfTokens();
            int tokens = XPath.newInstance("//w").selectNodes(FileIO.readDocumentFromLocalFile(f.getAbsolutePath())).size();
            
            System.out.println("Types: " + types);
            System.out.println("Tokens: " + tokens);
            System.out.println("Dauer: " + dauer);
            System.out.println("Zeitabschnitt: " + zeitabschnitt);
            
            e = (Element) XPath.newInstance("//Transkript[1]//Types").selectSingleNode(transformedDocument);
            e.setText(Integer.toString(types));

            e = (Element) XPath.newInstance("//Transkript[1]//Tokens").selectSingleNode(transformedDocument);
            e.setText(Integer.toString(tokens));

            /*e = (Element) XPath.newInstance("//Transkript[1]//Dauer").selectSingleNode(transformedDocument);
            e.setText(dauer);

            e = (Element) XPath.newInstance("//Transkript[1]//Zeitabschnitt").selectSingleNode(transformedDocument);
            e.setText(zeitabschnitt);*/

            FileIO.writeDocumentToLocalFile(makeOutputPath(inputFile), transformedDocument);
        }                
    }
    
}
