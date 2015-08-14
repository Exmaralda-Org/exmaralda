/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.alignment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class AlignDS {

    static String XSL = "C:\\DGD2Web\\src\\java\\dgd2web\\xsl\\Transcript2HTML.xsl";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new AlignDS().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(AlignDS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AlignDS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(AlignDS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(AlignDS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(AlignDS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(AlignDS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(AlignDS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException, JexmaraldaException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        File wavTargetDirectory = new File("C:\\Users\\Schmidt\\Desktop\\Alignment\\WAV");
        File flnTargetDirectory = new File("C:\\Users\\Schmidt\\Desktop\\Alignment\\TXT");
        File praatTargetDirectory = new File("C:\\Users\\Schmidt\\Desktop\\Alignment\\PRAAT");
               
        File flnDirectory = new File("Y:\\thomas\\DS2FLK\\14_Manual_Alignment_fertig");
        File flnResultDirectory = new File("Y:\\thomas\\DS2FLK\\15_Automatic_Alignment");
        File htmlDirectory  = new File("Y:\\thomas\\DS2FLK\\15_Automatic_Alignment_HTML");
        File wavDirectory = new File("Y:\\media\\audio\\DS");
        
        File[] flnFiles = flnDirectory.listFiles();
        Arrays.sort(flnFiles, new Comparator<File>(){

            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
            
        });
        
        for (File flnFile : flnFiles){
            // DS--_E_00009_SE_01_T_01_DF_01
            int no = Integer.parseInt(flnFile.getName().substring(8,12));
            System.out.println("#" + no);
            if (no!=64) continue;
            
            for (File f : wavTargetDirectory.listFiles()){f.delete();}
            for (File f : flnTargetDirectory.listFiles()){f.delete();}
            for (File f : praatTargetDirectory.listFiles()){f.delete();}
            
            File wavFile = new File(wavDirectory, flnFile.getName().replaceAll("_T_", "_A_").replaceAll("\\.fln", ".WAV"));
            File transcriptOutFile = new File(flnResultDirectory, flnFile.getName());
            File htmlOutFile = new File(htmlDirectory, flnFile.getName().replaceAll("\\.fln", ".html"));

            Aligner aligner = new Aligner(flnFile, wavFile);
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

        }
                
    }
}
