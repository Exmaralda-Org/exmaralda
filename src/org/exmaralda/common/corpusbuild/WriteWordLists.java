/*
 * MakeWordList.java
 *
 * Created on 22. November 2006, 15:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import org.exmaralda.partitureditor.jexmaralda.*;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;
import java.io.*;

/**
 *
 * @author thomas
 */
public class WriteWordLists extends AbstractSegmentedTranscriptionProcessor{
    
    String suffix = "_wlist";
    String segsuffix = "_s";
    String wordName = "HIAT:w";
    String linkTargetSuffix = "_partiture";
    
   /** Creates a new instance of MakeWordList */
    public WriteWordLists(String corpusName, String s) {
        super(corpusName);
        suffix = s;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            WriteWordLists mwl = new WriteWordLists(args[0], args[2]);
            if (args.length>3) {
                mwl.segsuffix = args[3];
                System.out.println("segsuffix set to " + mwl.segsuffix);
            }
            if (args.length>4){
                mwl.wordName = args[4];
            }
            if (args.length>5){
                mwl.linkTargetSuffix = args[5];
            }
            mwl.doIt();
            mwl.output(args[1]);
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void processTranscription(SegmentedTranscription st) {
        try {
            SegmentList segmentList = st.makeSegmentList(wordName);

            int index = getNakedFilenameWithoutSuffix().lastIndexOf(segsuffix);
            
            String wordlistFilename =    getCurrentDirectoryname() 
                                        + System.getProperty("file.separator")
                                        + "presentation"
                                        + System.getProperty("file.separator")
                                        + getNakedFilenameWithoutSuffix().substring(0,index)
                                        + suffix
                                        + ".html";                        

            String relativeWordlistFilename =  getNakedFilenameWithoutSuffix().substring(0,index)
                                               + suffix
                                               + ".html";                        

            
            String partiturFilename =   getNakedFilenameWithoutSuffix().substring(0,index)
                                        + this.linkTargetSuffix
                                        //+ "_partiture"
                                        + ".html";                        

            String frameFilename =      getCurrentDirectoryname() 
                                        + System.getProperty("file.separator")
                                        + "presentation"
                                        + System.getProperty("file.separator")
                                        + "wlistframe.html";

            segmentList.writeHTMLToFile(wordlistFilename,"IT", partiturFilename);
            
            FileOutputStream fos = new FileOutputStream(frameFilename);
            fos.write("<html><head></head>".getBytes());
            fos.write("<frameset cols=\"80%,20%\">".getBytes());
            fos.write("<frame src=\"".getBytes());
            fos.write(partiturFilename.getBytes());
            fos.write("\" name=\"IT\">".getBytes());
            fos.write("<frame src=\"".getBytes());
            fos.write(relativeWordlistFilename.getBytes());
            fos.write("\" name=\"WL\">".getBytes());
            fos.write("<noframes></noframes></frameset></html>".getBytes());
            fos.close();
            System.out.println("document written.");            
            
            outappend(wordlistFilename +  " written.\n");   
        } catch (IOException ex) {
            ex.printStackTrace();
        }   
    }
    
    
    
}
