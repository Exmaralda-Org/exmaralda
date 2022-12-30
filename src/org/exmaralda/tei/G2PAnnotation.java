/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.tei;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.exmaralda.webservices.G2PConnector;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

/**
 *
 * @author bernd
 */
public class G2PAnnotation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws JDOMException, IOException {
        new G2PAnnotation().doit();
    }

    String[] TEI_FILES = {
        //"C:\\Users\\bernd\\Dropbox\\work\\ZUMULT-COMA\\ESLO-DEMO\\ESLO_ENT_1005\\ESLO2_ENT_1005_C.xml",
        "C:\\Users\\bernd\\Dropbox\\work\\ZUMULT-COMA\\ESLO-DEMO\\ESLO_ENT_1013\\ESLO2_ENT_1013_C.xml",
    };

    static final Namespace teiNamespace = Namespace.getNamespace("tei", "http://www.tei-c.org/ns/1.0");
    
    
    private void doit() throws JDOMException, IOException {
        String[][] parameters = {
            {"lng","fra-FR"},
            {"iform","list"},
            {"oform","tab"},
            {"outsym","ipa"},
            {"syl","yes"},
            {"stress","no"}
        };
        HashMap<String, Object> otherParameters = new HashMap<>();
        for (String[] s : parameters){
            otherParameters.put(s[0], s[1]);
        }

        G2PConnector g2pConnector = new G2PConnector();

        for (String TEI_FILE : TEI_FILES){
            File file = new File(TEI_FILE);
            Document document = FileIO.readDocumentFromLocalFile(file);
            XPath xpath = XPath.newInstance("//tei:seg");
            xpath.addNamespace(teiNamespace);
            XPath xpath2 = XPath.newInstance("descendant::tei:w");
            xpath2.addNamespace(teiNamespace);

            List l = xpath.selectNodes(document);

            for (Object o : l){
                Element seg = (Element)o;
                List l2 = xpath2.selectNodes(seg);
                String originalText = "";
                for (Object o2 : l2){
                    Element wElement = (Element)o2;
                    originalText+=WordUtilities.getWordText(wElement) + "\n";
                }
                File tempInputFile = File.createTempFile("G2P", ".txt");
                Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(tempInputFile), "UTF-8"));
                try {
                    out.write(originalText);
                } finally {
                    out.close();
                }      
                String result = g2pConnector.callG2P(tempInputFile, otherParameters);
                String[] lines = result.split("\\n");
                int countLines = 0;
                for (Object o2 : l2){
                    Element wElement = (Element)o2;
                    originalText+=WordUtilities.getWordText(wElement) + "\n";
                    String line = lines[countLines];
                    String[] bits = line.split(";");
                    String pho = bits[1].replaceAll(" ", "");
                    wElement.setAttribute("phon", pho);
                    countLines++;
                }

            }
    
            FileIO.writeDocumentToLocalFile(file, document);
        }
        
        


    }
    
}
