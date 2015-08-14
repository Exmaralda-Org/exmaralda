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
public class UpdateFileSizesAndCounts extends AbstractSchneiderProcessor {
    
    
    public UpdateFileSizesAndCounts(String[] args){
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];
        
        inputFiles = inputDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(inputSuffix.toUpperCase());
            }            
        });
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String[] myArgs = {
                "O:\\xml\\events\\Werkstatt\\DS", "xml"
            };
            UpdateFileSizesAndCounts aa = new UpdateFileSizesAndCounts(myArgs);
            aa.processFiles();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(FixNonAlignedTranscripts.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private void processFiles() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException{
        for (File inputFile : inputFiles){
            System.out.println("Processing " + inputFile.getName());                        
            File tryTranscript = new File("Y:\\thomas\\DS2FLK\\12\\" + inputFile.getName().substring(0, inputFile.getName().lastIndexOf(".xml")) + "_SE_01_T_01_DF_01.fln");
            System.out.println("Trying " + tryTranscript.getAbsolutePath());
            if (!(tryTranscript.exists())){
                System.out.println("==== No transcript");
                continue;
            }
            
            Document inputDocument = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
            Document transcript = FileIO.readDocumentFromLocalFile(tryTranscript.getAbsolutePath());

            double first = Double.parseDouble(((Attribute) (XPath.newInstance("//timepoint[1]/@absolute-time").selectSingleNode(transcript))).getValue());
            double last = Double.parseDouble(((Attribute) (XPath.newInstance("//timepoint[last()]/@absolute-time").selectSingleNode(transcript))).getValue());
            double duration = (last - first) * 1000.0;
            String dauer = "00:" + org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(duration,0);
            String zeitabschnitt = "00:" + org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(1000.0 * first ,0)
                                    + " - "
                                    + "00:" + org.exmaralda.folker.utilities.TimeStringFormatter.formatMiliseconds(1000.0 * last ,0);
            
            Object o = XPath.newInstance("//Transkript[1]//Dateiname").selectSingleNode(transcript);
            
            Element e = (Element) XPath.newInstance("//Transkript[1]//Dateigröße").selectSingleNode(inputDocument);
            e.setText(Long.toString(tryTranscript.length()));
            //System.out.println(e.getText());
            
            HashtableTokenList htl = new HashtableTokenList();
            File[] fs = {tryTranscript};
            htl.readWordsFromFolkerFiles(fs);
            int types = htl.getNumberOfTokens();
            int tokens = XPath.newInstance("//w").selectNodes(FileIO.readDocumentFromLocalFile(tryTranscript.getAbsolutePath())).size();
            
            if (duration > 60*60*1000){
                System.out.println("--------------------------");
                //System.out.println("Types: " + types);
                //System.out.println("Tokens: " + tokens);
                System.out.println("Dauer: " + dauer);
                System.out.println("Zeitabschnitt: " + zeitabschnitt);
                System.out.println("--------------------------");
            } 
            
            e = (Element) XPath.newInstance("//Transkript[1]//Types").selectSingleNode(inputDocument);
            e.setText(Integer.toString(types));

            e = (Element) XPath.newInstance("//Transkript[1]//Tokens").selectSingleNode(inputDocument);
            e.setText(Integer.toString(tokens));

            e = (Element) XPath.newInstance("//Transkript[1]//Dauer").selectSingleNode(inputDocument);
            e.setText(dauer);

            e = (Element) XPath.newInstance("//Transkript[1]//Zeitabschnitt").selectSingleNode(inputDocument);
            e.setText(zeitabschnitt);

            FileIO.writeDocumentToLocalFile(inputFile.getAbsolutePath(), inputDocument);
        }                
    }
    
}
