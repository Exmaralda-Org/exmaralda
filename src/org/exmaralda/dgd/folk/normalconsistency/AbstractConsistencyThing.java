/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.folk.normalconsistency;

import java.io.File;
import java.io.IOException;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public abstract class AbstractConsistencyThing {
    
    String DIRECTORY = "L:\\FOLK_DGD_2\\transcripts\\FOLK_NORMALISIERUNG";
    Document resultDocument;
    Element resultRoot;
    File currentFile;
    Document currentDoc;
    
    
    public void doit() throws JDOMException, IOException, Exception{
        resultRoot = new Element("root");
        resultDocument = new Document(resultRoot);
        
        File dir = new File(DIRECTORY);
        File[] files = dir.listFiles();
        for (File f : files){
            currentFile = f;
            System.out.println("Processing " + f.getName());
            Document doc = FileIO.readDocumentFromLocalFile(f);
            currentDoc = doc;
            processDocument(doc);
        }
    }

    public abstract void processDocument(Document doc) throws Exception;

    public void writeResultDocument(File file) throws IOException{
        FileIO.writeDocumentToLocalFile(file, resultDocument);
    }
    
    public void writeDocument() throws IOException {
        FileIO.writeDocumentToLocalFile(currentFile, currentDoc);
    }
    

}
