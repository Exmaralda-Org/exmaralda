/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.alignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.sound.AudioProcessor;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class Test {

//    static String PRAATCON_PATH = "C:\\Program Files\\Praat\\praatcon.exe";
//    static String PRAAT_SCRIPT_PATH = "C:\\Users\\Schmidt\\Desktop\\Alignment\\script\\praatMono2Stereo.praatScript";
    
//    static String CURL_PATH = "C:\\Program Files\\curl\\curl";
//    static String MAUS_WEB_SERVICE = "http://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runMAUSBasicGerman";
    
      static String XSL = "C:\\DGD2Web\\src\\java\\dgd2web\\xsl\\Transcript2HTML.xsl";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            try {
                try {
                    new Test().doit();
                } catch (SAXException ex) {
                    Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                } catch (TransformerConfigurationException ex) {
                    Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                } catch (TransformerException ex) {
                    Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (JexmaraldaException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (JDOMException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException, JexmaraldaException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        File transcriptFile = new File("Y:\\thomas\\DS2FLK\\14_Manual_Alignment_fertig\\DS--_E_00002_SE_01_T_01_DF_01.fln");
        File transcriptOutFile = new File("Y:\\thomas\\DS2FLK\\15_Automatic_Alignment\\DS--_E_00002_SE_01_T_01_DF_01.fln");
        File htmlOutFile = new File("Y:\\thomas\\DS2FLK\\15_Automatic_Alignment_HTML\\DS--_E_00002_SE_01_T_01_DF_01.html");
        //File transcriptFile = new File("Y:\\transcripts\\FOLK\\FOLK_E_00097_SE_01_T_01_DF_01.fln");
        File wavFile = new File("Y:\\media\\audio\\DS\\DS--_E_00002_SE_01_A_01_DF_01.WAV");
        /*if (!AudioProcessor.isCuttable(wavFile.getAbsolutePath())){
            System.out.println("SCHEISSE!");
            return;
        };*/
        File wavTargetDirectory = new File("C:\\Users\\Schmidt\\Desktop\\Alignment\\WAV");
        File flnTargetDirectory = new File("C:\\Users\\Schmidt\\Desktop\\Alignment\\TXT");
        File praatTargetDirectory = new File("C:\\Users\\Schmidt\\Desktop\\Alignment\\PRAAT");
        
        for (File f : wavTargetDirectory.listFiles()){
            f.delete();
        }
        for (File f : flnTargetDirectory.listFiles()){
            f.delete();
        }
        for (File f : praatTargetDirectory.listFiles()){
            f.delete();
        }
        
        Aligner aligner = new Aligner(transcriptFile, wavFile);
        ArrayList<File> result = aligner.cut(wavTargetDirectory, flnTargetDirectory);
        aligner.convertStereoToMono(wavTargetDirectory);
        aligner.doAlignment(wavTargetDirectory, flnTargetDirectory, praatTargetDirectory);
        Document document = aligner.mixResult(praatTargetDirectory);
        ((Element)XPath.newInstance("//recording").selectSingleNode(document)).setAttribute("path", wavFile.toURI().toString());
        FileIO.writeDocumentToLocalFile(transcriptOutFile, document);
        
        StylesheetFactory ssf = new StylesheetFactory(true);
        String[][] parameters = {{"output-type", "HTML-STANDALONE"}};
        String html = ssf.applyExternalStylesheetToExternalXMLFile(XSL, transcriptOutFile.getAbsolutePath(), parameters);
        FileOutputStream fos = new FileOutputStream(htmlOutFile);
        fos.write(html.getBytes("UTF-8"));
        fos.close();                    

        
        
        /*EventListTranscription elt = org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.readXML(transcriptOutFile);
        Document d = EventListTranscriptionXMLReaderWriter.toJDOMDocument(elt, wavFile);
        ((Element)XPath.newInstance("//recording").selectSingleNode(d)).setAttribute("path", wavFile.toURI().toString());
        StylesheetFactory ssf = new StylesheetFactory(true);
        String html = ssf.applyInternalStylesheetToString(XSL, IOUtilities.documentToString(d));
        FileOutputStream fos = new FileOutputStream(htmlOutFile);
        fos.write(html.getBytes("UTF-8"));
        fos.close();                    
        */
    }
}
