/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.folk.normalconsistency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class RandomSample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new RandomSample().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(RandomSample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RandomSample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        HashSet<Integer> randomNumbers = new HashSet<Integer>();
        while (randomNumbers.size()<100){
            Random r = new java.util.Random();
            int i = r.nextInt(1842);
            randomNumbers.add(i);
        }
        
        Document d = FileIO.readDocumentFromLocalFile(new File(Find.RESULT_FILE));
        List l = d.getRootElement().removeContent();

        ArrayList<Element> sample = new ArrayList<Element>();
        
        Iterator<Integer> it = randomNumbers.iterator();
        while (it.hasNext()){
            Integer i = it.next();
            sample.add((Element)l.get(i));
        }
        
        Document sampleDoc = new Document(new Element("root"));
        sampleDoc.getRootElement().addContent(sample);
        FileIO.writeDocumentToLocalFile(new File("C:\\Users\\Schmidt\\Desktop\\FOLK_RELEASE\\Consistency\\Sie_Sample_1.xml"), sampleDoc);
    }
}
