/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.folk;

//import dgd2web.query.Metadata;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author Schmidt
 */
public abstract class AbstractFOLKAnalyzer {
    
    public String FOLK_DIRECTORY = "U:\\FOLK_DGD_2\\transcripts\\FOLK_MINIMAL_REFERENZ";
    public String[] FOLK_FILES;
    
    public Element resultElement = new Element("result");
    public Document resultDocument = new Document(resultElement);
    
    //public Metadata metadata;
    

    public AbstractFOLKAnalyzer(String FOLK_DIRECTORY, String[] FOLK_FILES, String session_id) {
        this.FOLK_DIRECTORY = FOLK_DIRECTORY;
        this.FOLK_FILES = FOLK_FILES;
        //metadata = new Metadata(session_id);
    }
    
    public AbstractFOLKAnalyzer(String FOLK_DIRECTORY, final String suffix, String session_id) {
        this.FOLK_DIRECTORY = FOLK_DIRECTORY;
        this.FOLK_FILES = new File(FOLK_DIRECTORY).list(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(suffix);
            }
            
        });
        //metadata = new Metadata(session_id);
    }

    public void go() throws IOException{
        for (String filename : FOLK_FILES){
            File f = new File(new File(FOLK_DIRECTORY), filename);
            processFile(f);
        }
    }

    public abstract void processFile(File f) throws IOException;    
    
    public void writeResult(String filename) throws IOException {
        FileIO.writeDocumentToLocalFile(new File(filename), resultDocument);                    
    }
    
    
}
