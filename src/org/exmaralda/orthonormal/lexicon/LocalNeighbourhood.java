/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.lexicon;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import org.exmaralda.common.helpers.LevenshteinComparator;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
public class LocalNeighbourhood {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            LocalNeighbourhood l = new LocalNeighbourhood();
            l.doit();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    String FILENAME = "S:\\TP-Z2\\DATEN\\E3\\Zaba_PBU\\LexiconProfiles\\PAIDUS_Bernd_phon_Nachbarschaftsdichte_Aug_16.txt";
    String OUTPUT = "S:\\TP-Z2\\DATEN\\E3\\Zaba_PBU\\LexiconProfiles\\PAIDUS_Bernd_phon_Nachbarschaftsdichte_Aug_16_Phon.txt";
    String XML_OUT = "S:\\TP-Z2\\DATEN\\E3\\Zaba_PBU\\LexiconProfiles\\PAIDUS_Bernd_phon_Nachbarschaftsdichte_Aug_16_Phon.xml";
    String LEXICON_NAME = "S:\\TP-Z2\\DATEN\\E3\\Zaba_PBU\\lexicon\\German-UTF8-Final.xml";

    private void doit() throws JDOMException, IOException {
       Document d = IOUtilities.readDocumentFromLocalFile(LEXICON_NAME);
       PhoneticLexicon lexicon = new PhoneticLexicon(d);
       System.out.println("Lexicon read.");

       Vector<String> orthoList = new Vector<String>();
       Vector<String> phonList = new Vector<String>();
       Vector<String> lines = new Vector<String>();

       FileInputStream fis = new FileInputStream(FILENAME);
       InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
       BufferedReader myInput = new BufferedReader(isr);
       String nextLine = new String();
       StringBuffer output = new StringBuffer();
       while ((nextLine = myInput.readLine()) != null) {
           lines.add(nextLine);
           String[] items = nextLine.split("\\t");
           String orthoWord = items[2];
           PhoneticLexiconEntry entry = lexicon.getEntry(orthoWord);
           if (entry==null){
               String capitalizedOrthoWord = orthoWord.substring(0,1).toUpperCase() + orthoWord.substring(1);
               entry = lexicon.getEntry(capitalizedOrthoWord);
           }
           String ph = "";
           if (entry!=null){
               ph = entry.ph;
           }
           output.append(nextLine + "\t" + ph + "\n");
           orthoList.addElement(orthoWord);
           phonList.addElement(ph);
       }

       Document doc = new Document();
       Element root = new Element("neighbourhood-profile");
       doc.setRootElement(root);

       int count = 0;
       for (String pho : phonList){
           String orth = orthoList.elementAt(count);
           Element entry = new Element("entry");
           entry.setAttribute("orth", orth);
           entry.setAttribute("phon", pho);
           List<String> neighbours = null;
           HashSet<String> done = new HashSet<String>();

           if (pho.length()>0){
               System.out.println("=================");
               System.out.println("****" + pho + "****");
               neighbours = LevenshteinComparator.getNeighbours(pho, phonList, 1);
               for (String nb : neighbours){
                   if (nb.length()==0) continue;
                   int index = phonList.indexOf(nb);
                   String ortho = orthoList.elementAt(index);
                   // avoid duplicates
                   if (done.contains(ortho)) continue;
                   System.out.println(ortho);
                   Element neighbour = new Element("neighbour");
                   neighbour.setAttribute("orth", ortho);
                   neighbour.setAttribute("phon", nb);
                   entry.addContent(neighbour);
                   done.add(ortho);
               }
           }
           root.addContent(entry);
           count++;
       }

       IOUtilities.writeDocumentToLocalFile(XML_OUT, doc);

       

    }

}
