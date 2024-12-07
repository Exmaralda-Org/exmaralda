/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class FindAmbiguities {

    String PATH = "Z:\\TAGGING\\SAMPLE\\2\\Swantje\\Inter-Rater-Agreement\\TestSwantje";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new FindAmbiguities().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(FindAmbiguities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FindAmbiguities.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        File[] inputs = new File(PATH).listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".fln");
            }
            
        });
        
        HashMap<String, HashMap<String,Integer>> all = new HashMap<String, HashMap<String,Integer>>();
        for (File input : inputs){
            Document doc = FileIO.readDocumentFromLocalFile(input);
            List l = XPath.selectNodes(doc, "//w");
            for (Object o : l){
                Element e = (Element)o;
                String n = e.getText();
                if (e.getAttribute("n")!=null){
                    n = e.getAttributeValue("n");
                }
                String pos = e.getAttributeValue("pos");
                String[] nTokens = n.split(" ");
                String[] posTokens = pos.split(" ");
                if (nTokens.length!=posTokens.length){
                    System.out.println(n + " " + pos);
                    continue;
                }
                int i = 0;
                for (String nToken : nTokens){
                    if (!(all.containsKey(nToken))){
                        HashMap<String, Integer> newHash = new HashMap<String, Integer>();
                        all.put(nToken, newHash);
                    }
                    HashMap<String, Integer> thisHash = all.get(nToken);
                    String posToken = posTokens[i];
                    if (!thisHash.containsKey(posToken)){
                        thisHash.put(posToken, 1);
                    } else {
                        thisHash.put(posToken, thisHash.get(posToken)+1);
                    }
                    i++;
                }
            }
        }
        Element root = new Element("result");
        for (String n : all.keySet()){
           Element form = new Element("element");
           form.setAttribute("form", n);
           root.addContent(form);
           HashMap<String, Integer> thisHash = all.get(n); 
           for (String pos : thisHash.keySet()){
               Element posE = new Element("pos");
               posE.setAttribute("tag", pos);
               posE.setAttribute("count", Integer.toString(thisHash.get(pos)));
               form.addContent(posE);
           }
        }
        Document out = new Document(root);
        FileIO.writeDocumentToLocalFile(new File("Z:\\TAGGING\\SAMPLE\\2\\Swantje\\Inter-Rater-Agreement\\Forms2POS.xml"), out);
    }
}
