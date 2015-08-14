/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
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
import org.jdom.Text;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class TransformZWMetaData extends AbstractSchneiderProcessor {
    
    private String STYLESHEET_PATH = "Y:\\thomas\\ZW_HE\\Meta\\Old2New.xsl";
    File transcriptDirectory;
    
    String[] PSEUDOS = {"ZWY62",
                        "ZWY27",
                        "ZWY28",
                        "ZWY73",
                        "ZWY52",
                        "ZWV13",
                        "ZW9O7",
                        "ZW9O8",
                        "ZW9O9",
                        "ZW9P7",
                        "ZWL60",
                        "ZWL59",
                        "ZW8O9",
                        "ZW9F3",
                        "ZW983",
                        "ZW7I8",
                        "ZW7J0",
                        "ZW7Q4",
                        "ZW8B0",
                        "ZW7B8",
                        "ZW7B9",
                        "ZW5B1",
                        "ZW5U3",
                        "ZW4L0",
                        "ZW514",
                        "ZWY34",
                        "ZWY33",
                        "ZW498",
                        "ZW2C2",
                        "ZWF82",
                        "ZW2S6",
                        "ZWY29",
                        "ZWY30",
                        "ZW0P0",
                        "ZW0L3",
                        "ZW0M6",
                        "ZWY95",
                        "ZWY97",
                        "ZW0L4",
                        "ZWY89",
                        "HE116"};

    HashSet<String> pseudoSet = new HashSet<String>();
    
    public TransformZWMetaData(String[] args){
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

        for (String p : PSEUDOS){
            pseudoSet.add(p);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String[] myArgs = {
                "Y:\\thomas\\ZW_HE\\Meta\\zw+he_2", "xml", 
                "Y:\\thomas\\ZW_HE\\Meta\\zw+he_3", "xml",
                "Y:\\thomas\\ZW_HE"
            };
            TransformZWMetaData aa = new TransformZWMetaData(myArgs);
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
            File tryTranscript = new File("Y:\\thomas\\ZW_HE\\" + inputFile.getName().substring(0, inputFile.getName().lastIndexOf(".xml")) + "_SE_01_T_01_DF_01.fln");
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

            List l = XPath.newInstance("//text()")
                    .selectNodes(transformedDocument);
            for (Object t : l){
                Text text = (Text)t;
                if (text.getTextNormalize().length()==0){
                    text.detach();
                }
            }
            FileIO.writeDocumentToLocalFile(makeOutputPath(inputFile), transformedDocument);
        }                
    }
    
}
