/*
 * AbstractIUProcessor.java
 *
 * Created on 7. Juni 2007, 11:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.convertStep1;

import java.io.*;
import java.util.*;
import org.jdom.*;
import org.exmaralda.sbcsae.utilities.*;

/**
 *
 * @author thomas
 */
public abstract class AbstractIUProcessor {
    
    static String BASE_DIRECTORY = "T:\\TP-Z2\\DATEN\\SBCSAE\\";
    String INPUT_DIRECTORY = "1";
    String OUTPUT_DIRECTORY = "X";
    String STYLESHEET_PATH = "";
    boolean writeBack;
    String currentFilename;
    int lineCount;
    StringBuffer out = new StringBuffer();
    
    /** Creates a new instance of AbstractIUProcessor */
    public AbstractIUProcessor(boolean wb) {
        writeBack = wb;
    }
    
    public void doIt() throws JDOMException, IOException {
        for (int i=1; i<=60; i++){
            String filename = "SBC0";
            if (i<10) filename+="0";
            filename+=Integer.toString(i);
            currentFilename = filename;
            System.out.println("Processing " + filename);
            String inputPath = BASE_DIRECTORY + "\\" + INPUT_DIRECTORY + "\\" + filename + ".xml";
            File f = new File(inputPath);
            Document d = FileIO.readDocumentFromLocalFile(f);
            beginDocument(d);
            Element rootElement = d.getRootElement();
            List l = rootElement.getChildren("intonation-unit");
            lineCount = 1;
            for (Object o : l){
                Element iu = (Element)o;
                processIntonationUnit(iu);
                lineCount++;
            }
            endDocument(d);
            if (writeBack){
                String outputPath = BASE_DIRECTORY + "\\" + OUTPUT_DIRECTORY + "\\" + filename + ".xml";
                if (STYLESHEET_PATH.length()>0){
                    FileIO.assignStylesheet(STYLESHEET_PATH,d);
                }
                FileIO.writeDocumentToLocalFile(outputPath, d);
            }
        }                
    }
    
    public void outappend(String what){
        out.append(what + "\n");
    }
    
    public void beginDocument(Document d){}
    public void endDocument(Document d){}
    
    void output(String filename) throws FileNotFoundException, IOException{
        System.out.println("started writing document...");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        fos.write(out.toString().getBytes());
        fos.close();
    }
    
    
    public abstract void processIntonationUnit(Element iu);
    
}
