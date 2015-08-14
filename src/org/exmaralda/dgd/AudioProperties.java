/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.folker.utilities.TimeStringFormatter;
import org.exmaralda.partitureditor.sound.BASAudioPlayer;
import org.exmaralda.partitureditor.sound.JMFPlayer;
import org.exmaralda.partitureditor.sound.Playable;
import org.jdom.Document;
import org.jdom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class AudioProperties {

    
    File audioDirectory;
    Vector<File> audioFiles;
    //File[] audioFiles;
    StringBuffer properties = new StringBuffer();
    Playable player = new BASAudioPlayer();
    double totalLength = 0.0;
    Document resultDocument;
    String HTML_XSL = "/org/exmaralda/dgd/audioProperties2HTML.xsl";
    
    FilenameFilter wavFilter = new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".wav");
            }            
        }; 
    
    FileFilter dirFilter = new FileFilter(){

        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
        }; 

    public AudioProperties(String audioPath) {
        audioDirectory = new File(audioPath);
        //audioFiles = audioDirectory.listFiles(wavFilter);
        audioFiles = listFiles(audioDirectory);
        resultDocument = new Document(new Element("audio-files"));
    }
    
       
    public static void main(String[] args){
        try {
            if ((args.length!=2)){
                System.out.println("Usage: AudioProperties audioDirectory result.xml");
                System.exit(0);
            }

            AudioProperties f = new AudioProperties(args[0]);
            f.getProperties();
            
            //f.properties.append("-----------------------------------------------\n");
            //f.properties.append("TOTAL\t\t"+ Double.toString(f.totalLength)+ "\t" + TimeStringFormatter.formatMiliseconds(f.totalLength*1000.0, 2) + "\n");
            /*File recErrFile = new File(args[1]);
            FileOutputStream fos = new FileOutputStream(recErrFile);
            fos.write(f.properties.toString().getBytes("UTF-8"));
            fos.close();*/
            f.writeResultList(args[1]);
            System.exit(0);
        } catch (SAXException ex) {
            Logger.getLogger(AudioProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(AudioProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(AudioProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(AudioProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AudioProperties.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    void writeResultList(String xmlPath) throws IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        FileIO.writeDocumentToLocalFile(new File(xmlPath), resultDocument);
        String htmlPath = xmlPath.replaceAll("\\.xml", ".html");
        StylesheetFactory sf = new StylesheetFactory(true);
        String html = sf.applyInternalStylesheetToExternalXMLFile(HTML_XSL, xmlPath);
        FileOutputStream fos = new FileOutputStream(new File(htmlPath));
        fos.write(html.getBytes("UTF-8"));
        fos.close();
        System.out.println(htmlPath + " written.");
        
    }
    

    private void getProperties() throws IOException{
        for (File audioFile : audioFiles){
            try{
                //System.gc();
                //player = new BASAudioPlayer();
                //player = new JMFPlayer();
                player.setSoundFile(audioFile.getAbsolutePath());
                double lengthInSeconds = player.getTotalLength();
                totalLength+=lengthInSeconds;
                /*properties.append(audioFile.getName() + "\t");
                properties.append(audioFile.getAbsolutePath() + "\t");
                properties.append(Double.toString(lengthInSeconds) + "\t");
                properties.append(TimeStringFormatter.formatMiliseconds(lengthInSeconds*1000.0, 2) + "\n");            */
                Element audioFileElement = new Element("audio-file");
                resultDocument.getRootElement().addContent(audioFileElement);
                audioFileElement.setAttribute("name", audioFile.getName());
                audioFileElement.setAttribute("path", audioFile.getAbsolutePath());
                audioFileElement.setAttribute("seconds", Double.toString(lengthInSeconds));
                audioFileElement.setAttribute("length", TimeStringFormatter.formatMiliseconds(lengthInSeconds*1000.0, 2));
                audioFileElement.setAttribute("bytes", Long.toString(audioFile.length()));
            } catch (IOException ioe){
                Element audioFileElement = new Element("audio-file");
                resultDocument.getRootElement().addContent(audioFileElement);
                audioFileElement.setAttribute("name", audioFile.getName());
                audioFileElement.setAttribute("path", audioFile.getAbsolutePath());
                audioFileElement.setAttribute("seconds", "error");
                audioFileElement.setAttribute("length", "error");
                audioFileElement.setAttribute("bytes", Long.toString(audioFile.length()));
                
            }
        }
    }

    private Vector<File> listFiles(File audioDirectory) {
        Vector<File> result = new Vector<File>();
        File[] wavFiles = audioDirectory.listFiles(wavFilter);
        for (File wf : wavFiles){
            result.add(wf);
        }
        File[] subDirectories = audioDirectory.listFiles(dirFilter);
        if (subDirectories!=null){
            for (File dir : subDirectories){
                Vector<File> wavsInSub = listFiles(dir);
                result.addAll(wavsInSub);
            }
        }
        return result;
    }
    
    
    
}
