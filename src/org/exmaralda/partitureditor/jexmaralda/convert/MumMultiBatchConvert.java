/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class MumMultiBatchConvert {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new MumMultiBatchConvert().doit();
        } catch (SAXException ex) {
            Logger.getLogger(MumMultiBatchConvert.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(MumMultiBatchConvert.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MumMultiBatchConvert.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(MumMultiBatchConvert.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(MumMultiBatchConvert.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    String IN = "C:\\Users\\Schmidt\\Dropbox\\IDS\\HZSK\\WV_MuM-Multi\\XML-IN";
    String OUT = "C:\\Users\\Schmidt\\Dropbox\\IDS\\HZSK\\WV_MuM-Multi\\EXB";

    private void doit() throws SAXException, ParserConfigurationException, IOException, TransformerException, JexmaraldaException {
        File[] xmlFiles = new File(IN).listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(".XML");
            }

        });

        TransanaConverter tc = new TransanaConverter();
        for (File f : xmlFiles){
            BasicTranscription bt = tc.readTransanaFromXMLFile(f);
            File out = new File(OUT, f.getName().replaceAll("\\.xml", ".exb"));
            
            //ZF2-D-EK-150522-P1-V1-000448-F2A1.xml
            //ZF2-D-EK-150522_P1_V1.MOV
            String mediaName = f.getName().substring(0,21).replace("-P", "_P").replace("-V", "_V");
            final String findString = mediaName.substring(15);
            File[] findFiles = new File("C:\\Users\\Schmidt\\Dropbox\\IDS\\HZSK\\WV_MuM-Multi\\MPG-Video").listFiles(new FilenameFilter(){

                @Override
                public boolean accept(File dir, String name) {
                    return name.contains(findString);
                }
                
            });
            
            if (!(findFiles.length==1)){
                System.out.println("WRONG");
                System.exit(1);
            }
            
            mediaName = findFiles[0].getName().substring(0, findFiles[0].getName().indexOf("."));
            
            Vector<String> mediaFileList = new Vector();
            mediaFileList.add("C:\\Users\\Schmidt\\Dropbox\\IDS\\HZSK\\WV_MuM-Multi\\MPG-Video\\" + mediaName + ".mpg");
            mediaFileList.add("C:\\Users\\Schmidt\\Dropbox\\IDS\\HZSK\\WV_MuM-Multi\\WAV-Audio\\" + mediaName + ".wav");
            mediaFileList.add("C:\\Users\\Schmidt\\Dropbox\\IDS\\HZSK\\WV_MuM-Multi\\MOV-Video\\" + mediaName + ".MOV");
            
            
            bt.getHead().getMetaInformation().setReferencedFiles(mediaFileList);
            
            bt.writeXMLToFile(out.getAbsolutePath(), "none");
            System.out.println(out.getAbsolutePath() + " written.");
        }
    }
    
}
