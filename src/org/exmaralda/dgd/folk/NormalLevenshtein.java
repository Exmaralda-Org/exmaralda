/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.folk;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.helpers.LevenshteinDistance;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class NormalLevenshtein {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new NormalLevenshtein().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(NormalLevenshtein.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NormalLevenshtein.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        Document d = FileIO.readDocumentFromLocalFile(new File("C:\\Users\\Schmidt\\Desktop\\OrthoNormalDump.xml"));
        for (Object o : d.getRootElement().getChildren()){
            Element e = (Element)o;
            String form = e.getAttributeValue("form");
            String lemma = e.getAttributeValue("lemma");
            double ld = new LevenshteinDistance().modifiedLevenshteinDistance(form, lemma);
            if (ld>=2 && form.length()>3){
                System.out.println(form + "\t" + lemma);
            }
        }
    }
}
