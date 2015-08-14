/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.jdom.Element;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class FixAudio {

    
    StringBuffer recordingErrors = new StringBuffer();
    StringBuffer assignSuggestions = new StringBuffer();
    
    File folkerFile;
    File audioDirectory;
    File[] audioFiles;
    
    StylesheetFactory ssf = new StylesheetFactory(true);

    public FixAudio(String folkerPath, String audioPath) {
        folkerFile = new File(folkerPath);
        audioDirectory = new File(audioPath);
        audioFiles = audioDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".wav");
            }            
        });
    }
    
   
    
    private void write(String string, File file) throws FileNotFoundException, IOException {
        System.out.println("started writing document " + file.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(string.getBytes("UTF-8"));
        fos.close();
        System.out.println(file.getAbsolutePath() + " written.");
    }
    
    public static void main(String[] args){
        if ((args.length<2) || (args.length>3)){
            System.out.println("Usage: FixAudio transcriptDirectory audioDirectory [suggestionsFile]");
            System.exit(0);
        }
        try {
            File[] transcriptFiles = new File(args[0]).listFiles(new FilenameFilter(){
                @Override
                public boolean accept(File dir, String name) {
                    return (name.toLowerCase().endsWith(".flk") || name.toLowerCase().endsWith(".fln"));
                }               
            });
            System.out.println("Found " + transcriptFiles.length + " FOLKER transcripts. ");
            
            Hashtable<String,String> suggestions = new Hashtable<String,String>();
            if (args.length==3){
               FileInputStream fis = new FileInputStream(args[2]);
               InputStreamReader isr = new InputStreamReader(fis, "UTF-8");       
               BufferedReader myInput = new BufferedReader(isr);
               String nextLine = new String();

               System.out.println("Started reading document...");
               // read in the document line for line
               while ((nextLine = myInput.readLine()) != null) {
                   int index = nextLine.indexOf("\t");
                   suggestions.put(nextLine.substring(0, index), nextLine.substring(index+1));
               }    
               fis.close();
               System.out.println("Read " + suggestions.size() + " mappings from " + args[2]);
            }

            
            StringBuffer allRecErrs = new StringBuffer();
            StringBuffer allSuggestions = new StringBuffer();
            
            for (File tf : transcriptFiles){
                String folker = tf.getAbsolutePath();
                System.out.println("Processing " + folker);
                if (suggestions.containsKey(tf.getName())){
                    Document d = IOUtilities.readDocumentFromLocalFile(tf.getAbsolutePath());
                    ((Element) XPath.newInstance("//recording").selectSingleNode(d)).setAttribute("path", "../../media/audio/FOLK/" + suggestions.get(tf.getName()));       
                    IOUtilities.writeDocumentToLocalFile(tf.getAbsolutePath(), d);
                    System.out.println("Mapped " + tf.getName() + " to " + suggestions.get(tf.getName()));
                } else {
                    FixAudio f = new FixAudio(folker, args[1]);
                    f.fixAudio();
                    allRecErrs.append(f.recordingErrors);      
                    allSuggestions.append(f.assignSuggestions);
                }
            }

            if (allRecErrs.length()>0){
                File recErrFile = new File(transcriptFiles[0].getParentFile(), "RecordingErrors.txt");
                FileOutputStream fos = new FileOutputStream(recErrFile);
                fos.write(allRecErrs.toString().getBytes("UTF-8"));
                fos.close();              

                File suggFile = new File(transcriptFiles[0].getParentFile(), "Suggestions.txt");
                FileOutputStream fos2 = new FileOutputStream(suggFile);
                fos2.write(allSuggestions.toString().getBytes("UTF-8"));
                fos2.close();              
                
                System.out.println("********************************");
                System.out.println("THERE WERE RECORDING ERRORS");
                System.out.println("SEE: " + recErrFile.getAbsolutePath());
                System.out.println("EDIT AND USE " + suggFile.getAbsolutePath());
                System.out.println("********************************");
            } else {
                System.out.println("********************************");
                System.out.println("THERE WERE NO RECORDING ERRORS");
                System.out.println("********************************");
                
            }
        } catch (JDOMException ex) {
            Logger.getLogger(FixAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FixAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(FixAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FixAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(FixAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(FixAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(FixAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void fixAudio() throws IOException, JDOMException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException {
        // FOLK_E_00001
        String e_id = folkerFile.getName().substring(0,12);
        Vector<File> matches = new Vector<File>();
        for (File af : audioFiles){
           if (af.getName().startsWith(e_id)){
               matches.addElement(af);
           }        
        }
        Document d = IOUtilities.readDocumentFromLocalFile(folkerFile.getAbsolutePath());
        String path = "";
        if (matches.size()==0){
            //throw new IOException("No recording for " + e_id);
            recordingErrors.append(folkerFile.getName() + ": No recording for " + e_id + "\n");
            //return;
        } else if (matches.size()>1){
            //throw new IOException("More than one recording for " + e_id);
            path = new File(((Element) XPath.newInstance("//recording").selectSingleNode(d)).getAttributeValue("path")).getName();
            recordingErrors.append(folkerFile.getName() + ": More than one recording for " + e_id + " - current recording: " + path + "\n");
            assignSuggestions.append(folkerFile.getName() + "\t" + matches.elementAt(0).getName() +"\n");
            //path = matches.elementAt(0).getName();
            //return;
        } else {
            path = matches.elementAt(0).getName();
        }
        ((Element) XPath.newInstance("//recording").selectSingleNode(d)).setAttribute("path", "../../media/audio/FOLK/" + path);
        IOUtilities.writeDocumentToLocalFile(folkerFile.getAbsolutePath(), d);
    }
    
    
    
}
