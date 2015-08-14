/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.folk;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.orthonormal.data.NormalizedFolkerTranscription;
import org.exmaralda.orthonormal.io.XMLReaderWriter;
import org.exmaralda.orthonormal.lexicon.AutoNormalizer;
import org.exmaralda.orthonormal.lexicon.LexiconException;
import org.exmaralda.orthonormal.lexicon.RDBLexicon;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class MergeNormalizations {

    // the aim is to transfer as many normalizations as reasonable from the normalized file to the other file
    // and write the result to the out file  
    // the other file can, but need not already have nromalizations
    static String NORMALIZED_FILE_PATH = "C:\\Users\\Schmidt\\Dropbox\\IDS\\FOLK\\Normalisierung\\Mischen_BERU\\FOLK_BERU_01_A06_Teil_2_SW_11082011.fln";
    static String OTHER_FILE_PATH = "C:\\Users\\Schmidt\\Dropbox\\IDS\\FOLK\\Normalisierung\\Mischen_BERU\\FOLK_BERU_01_A06_Teil2von2_überarbeitet.flk";
    static String OUT_FILE_PATH = "C:\\Users\\Schmidt\\Dropbox\\IDS\\FOLK\\Normalisierung\\Mischen_BERU\\FOLK_BERU_01_A06_Teil2von2_überarbeitet.fln";
    
    File normalizedFile;
    File otherFile;
    File outFile;

    private final AutoNormalizer autoNormalizer;

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            MergeNormalizations mn = new MergeNormalizations(NORMALIZED_FILE_PATH, OTHER_FILE_PATH, OUT_FILE_PATH);
            mn.doit();
        } catch (LexiconException ex) {
            Logger.getLogger(MergeNormalizations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(MergeNormalizations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MergeNormalizations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private MergeNormalizations(String NORMALIZED_FILE, String OTHER_FILE, String OUT_FILE) throws IOException {
        this.normalizedFile = new File(NORMALIZED_FILE);
        this.otherFile = new File(OTHER_FILE);
        this.outFile = new File(OUT_FILE);
        
        String RDB_URL = "jdbc:oracle:thin:@10.0.1.35:1521:orc11";
        String RDB_USERNAME = "orthonormal";
        String RDB_PASSWORD = "----";
        String[] CONNECTION_PARAMETERS = {RDB_URL, RDB_USERNAME, RDB_PASSWORD};
        RDBLexicon lexicon = new RDBLexicon();
        lexicon.read(CONNECTION_PARAMETERS);
        
        autoNormalizer = new AutoNormalizer(lexicon);
    }

    HashMap<String, Element> normalizedIndex = new HashMap<String,Element>();
    
    private void doit() throws JDOMException, IOException, LexiconException {
        NormalizedFolkerTranscription normalizedTranscription = null;
        normalizedTranscription = XMLReaderWriter.readNormalizedFolkerTranscription(normalizedFile);
        NormalizedFolkerTranscription otherTranscription = null;
        if (otherFile.getName().toUpperCase().endsWith(".FLN")){
            otherTranscription = XMLReaderWriter.readNormalizedFolkerTranscription(otherFile);
        } else {
            otherTranscription = XMLReaderWriter.readFolkerTranscription(otherFile);
        }
        Document normalizedDocument = normalizedTranscription.getDocument();
        Document otherDocument = otherTranscription.getDocument();
        XPath xp = XPath.newInstance("//contribution");
        List l1 = xp.selectNodes(normalizedDocument);
        for (Object o : l1){
            Element e = (Element)o;
            indexContribution(e);
        }
        
        List l2 = xp.selectNodes(otherDocument);
        for (Object o : l2){
            Element e = (Element)o;
            String key = "";
            List l = XPath.selectNodes(e, "descendant::w");
            for (Object o2 : l){
                Element w = (Element)o2;
                key+=WordUtilities.getWordText(w) + "_";
            }
            if (normalizedIndex.containsKey(key)){
                System.out.println("Have a normalization");
                Element normalizedContribution = normalizedIndex.get(key);
                List l3 = XPath.selectNodes(normalizedContribution, "descendant::w");
                int index = 0;
                for (Object o2 : l){
                    Element w = (Element)o2;
                    Element n = (Element)(l3.get(index));
                    String existingNormalization =  n.getAttributeValue("n");
                    if (existingNormalization!=null){
                        w.setAttribute("n",existingNormalization);
                    }
                    index++;
                }                
            } else {
                System.out.println("Auto normalize");
                autoNormalizer.normalize(e);
            }
        }
        FileIO.writeDocumentToLocalFile(outFile, otherDocument);
    }

    private void indexContribution(Element e) throws JDOMException {
        String key = "";
        List l = XPath.selectNodes(e, "descendant::w");
        for (Object o : l){
            Element w = (Element)o;
            key+=WordUtilities.getWordText(w) + "_";
        }
        if (l.size()>3 || key.length()>8){
            //System.out.println(key);
            normalizedIndex.put(key, e);
        }
    }
    
    
    
}
