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
public class NeighborhoodGrimm {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            /*if (args.length!=4){
                System.out.println("Usage: GlobalNeighborhood in.txt combinations.txt lexicon.xml out.xml");
                System.out.println("   in.txt - the path to the input file with child forms");
                System.out.println("   combinations.txt - the path to the input file with the compound phonemes");
                System.out.println("   lexicon.xml - the path to the phonetic lexicon");
                System.out.println("   out.xml - the output file to be written");
                System.exit(1);
            }*/
            NeighborhoodGrimm l = new NeighborhoodGrimm();
            String[] myArgs = {FILENAME, COMBINATIONS_NAME, LEXICON_NAME, XML_OUT};
            l.doit(myArgs);
            //l.doit(args);
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // the input file with word forms for the child
    static String FILENAME = "C:\\Users\\Schmidt\\Dropbox\\IDS\\Allerlei\\KUNSTWÖRTER_NEIGHBORHOOD\\Kunstwörter in Sampa (1).txt";
    // the output file
    static String XML_OUT = "C:\\Users\\Schmidt\\Dropbox\\IDS\\Allerlei\\KUNSTWÖRTER_NEIGHBORHOOD\\Kunstwörter_Nachbarn_aus_HADI-BOMP_2.xml";
    // the phonetic lexicon
    //String LEXICON_NAME = "S:\\TP-Z2\\DATEN\\E3\\Zaba_PBU\\lexicon\\German-UTF8-Final.xml";
    static String LEXICON_NAME = "C:\\Users\\Schmidt\\Dropbox\\IDS\\Allerlei\\KUNSTWÖRTER_NEIGHBORHOOD\\nur die zweisilbigen W_rter aus HADI-BOMP.txt";
    // a list of compound phonemes to be replaced with a single symbols
    static String COMBINATIONS_NAME = "C:\\Users\\Schmidt\\Dropbox\\IDS\\Allerlei\\KUNSTWÖRTER_NEIGHBORHOOD\\mehrteilige Phoneme.txt";

    private void doit(String[] args) throws JDOMException, IOException {
       FILENAME = args[0];
       COMBINATIONS_NAME = args[1];
       LEXICON_NAME = args[2];
       XML_OUT = args[3];
       
       // read the lexicon with the phonetic forms...
       Vector<String> phoneticForms = new Vector<String>();
       FileInputStream fis3 = new FileInputStream(LEXICON_NAME);
       InputStreamReader isr3 = new InputStreamReader(fis3, "UTF-8");
       BufferedReader myInput3 = new BufferedReader(isr3);
       String nextLine3 = new String();
       while ((nextLine3 = myInput3.readLine()) != null) {
           // ignore comment lines (start with //)
           if (nextLine3.length()>0){
               phoneticForms.add(nextLine3);
           }
       }
       System.out.println(phoneticForms.size() + " phonetic forms read.");

       // read the list with the phonetic forms...
       Vector<String> formsList = new Vector<String>();
       FileInputStream fis1 = new FileInputStream(FILENAME);
       InputStreamReader isr1 = new InputStreamReader(fis1, "UTF-8");
       BufferedReader myInput1 = new BufferedReader(isr1);
       String nextLine1 = new String();
       while ((nextLine1 = myInput1.readLine()) != null) {
           // ignore comment lines (start with //)
           if (nextLine1.length()>0){
               formsList.add(nextLine1);
           }
       }
       System.out.println(phoneticForms.size() + " phonetic forms read.");

       // read the text file with the compund phonemes
       Vector<String> combinations = new Vector<String>();
       if (COMBINATIONS_NAME.length()>0){
            FileInputStream fis2 = new FileInputStream(COMBINATIONS_NAME);
            InputStreamReader isr2 = new InputStreamReader(fis2, "UTF-8");
            BufferedReader myInput2 = new BufferedReader(isr2);
            String nextLine2 = new String();
            while ((nextLine2 = myInput2.readLine()) != null) {
                // ignore comment lines (start with //)
                if ((nextLine2.length()>0) && (!(nextLine2.startsWith("//")))){
                    combinations.add(nextLine2);
                }
            }
            System.out.println(combinations.size() + " combinations read.");
       }


       // a list with orthographic forms from the lexicon
       //Vector<String> orthoList = new Vector<String>();
       // a list with phonetic forms from the lexicon
       //Vector<String> phonList = new Vector<String>();
       // a list with orthographic forms for the child
       //Vector<String> localOrthoList = new Vector<String>();
       // a list with phonetic forms for the child
       //Vector<String> localPhonList = new Vector<String>();

       /*for (PhoneticLexiconEntry ple : lexicon.entries){
           orthoList.addElement(ple.lemma);
           phonList.addElement(ple.ph);
       }*/

       // read the input document
       /*FileInputStream fis = new FileInputStream(FILENAME);
       InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
       BufferedReader myInput = new BufferedReader(isr);
       String nextLine = new String();
       while ((nextLine = myInput.readLine()) != null) {
           System.out.println("Read line " + nextLine);
           // read a line and
           // split it at the tabulators
           String[] items = nextLine.split("\\t");
           // the 3rd field contains the normalised orthographic form
           // i.e. the word we want to work with
           String orthoWord = items[2];
           if (orthoWord.contains(" ")){
               orthoWord = orthoWord.substring(0, orthoWord.indexOf(" "));
           }
           System.out.println("Processing " + orthoWord);
           // find the entry in the phonetic lexicon for this word
           PhoneticLexiconEntry entry = lexicon.getEntry(orthoWord);
           if (entry==null){
               // if there is no entry, try a capitalized variant of the word, i.e.
               // for the word 'maus' try 'Maus'
               String capitalizedOrthoWord = orthoWord.substring(0,1).toUpperCase() + orthoWord.substring(1);
               entry = lexicon.getEntry(capitalizedOrthoWord);
           }
           String ph = "";
           if (entry!=null){
               ph = entry.ph;
           }
           // add the word to the local list of words
           localOrthoList.addElement(orthoWord);
           // add its phonetic form to the local list of phonetic forms
           localPhonList.addElement(ph);
       }*/

       // create a new XML document
       Document doc = new Document();
       Element root = new Element("neighbourhood-profile");
       doc.setRootElement(root);

       int count = 0;
       // go through all phonetic forms for the child
       for (String pho : formsList){
           // create an XML element <entry>
           Element entry = new Element("entry");
           // set its atributes orth and phon
           entry.setAttribute("phon", pho);

           // create a list for storing the neighbors
           List<String> neighbours = null;
           // create a set for memorizing neighbors
           HashSet<String> done = new HashSet<String>();

           // only do this if a phonetic form has been found in the lexicon
           if (pho.length()>0){
               // from the list of phonetic forms from the lexicon:
               // find all forms which have a Levenshtein distance of less or equal to 1
               // to the current phonetic form
               // replace compound phonemes with single symbols as specified in the 
               // combinations file
               int DISTANCE = 2;
               neighbours = LevenshteinComparator.getNeighbours(pho, phoneticForms, DISTANCE, combinations);
               // for all neighbours which were found:
               for (String nb : neighbours){
                   // ignore neighbours whose phonetic form is empty
                   if (nb.length()==0) continue;
                   // find the index of the phonetic form in the list...
                   // create an element <neighbour>
                   Element neighbour = new Element("neighbour");
                   // and set its attributes for phonetic and orthographic form
                   neighbour.setAttribute("phon", nb);
                   // add the neighbour element to the XML document
                   entry.addContent(neighbour);
                   // and make a note that this orthographic form is in the list
               }
           }
           root.addContent(entry);
           count++;
           //System.out.println(count + "/" + localPhonList.size() + ": " + orth);
       }

       // save the XML document under the specified name
       IOUtilities.writeDocumentToLocalFile(XML_OUT, doc);
       System.out.println("Output written to " + XML_OUT);

       

    }

}
