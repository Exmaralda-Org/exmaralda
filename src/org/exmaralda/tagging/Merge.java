/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.orthonormal.lexicon.Tagset;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class Merge {

    String ORIGINAL = "Z:\\FOLK-Tagging\\transcripts\\Evaluation1\\FOLK_E_00076_SE_01_T_01_DF_01.fln";
    String CORRECTED = "Z:\\FOLK-Tagging\\transcripts\\Evaluation1\\FOLK_E_00076_SE_01_T_01_DF_01_korrigiert.fln";
    String OUT = "Z:\\FOLK-Tagging\\transcripts\\Evaluation1\\FOLK_E_00076_SE_01_T_01_DF_01_merged.fln";
    static Tagset STTS;
    
    static {
        try {
            STTS = new Tagset();
        } catch (JDOMException ex) {
            Logger.getLogger(Merge.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Merge.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new Merge().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(Merge.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Merge.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Document merge(Document originalDoc, Document correctedDoc) throws JDOMException{
        HashMap<String, String> ids2correctedPos = new HashMap<String, String>();
        HashMap<String, String> ids2correctedLemmas = new HashMap<String, String>();

        HashSet<String> identicalTokens = getIdenticalTokens(originalDoc, correctedDoc);
        
        List correctedWords = XPath.newInstance("//w").selectNodes(correctedDoc);
        for (Object o : correctedWords){
            Element correctedWord = (Element)o;
            String id = correctedWord.getAttributeValue("id");
            String pos = correctedWord.getAttributeValue("pos");
            String lemma = correctedWord.getAttributeValue("lemma");
            if (identicalTokens.contains(id)){
                ids2correctedPos.put(id, pos);
                ids2correctedLemmas.put(id,lemma);
            }
        }

        List originalWords = XPath.newInstance("//w").selectNodes(originalDoc);
        for (Object o : originalWords){
            Element originalWord = (Element)o;
            String id = originalWord.getAttributeValue("id");
            String correctedPos = ids2correctedPos.get(id);
            String correctedLemma = ids2correctedLemmas.get(id);
            if (!identicalTokens.contains(id)) {
                continue;
            }
            if (correctedPos==null){
                correctedPos = "???";
            }
            if (correctedLemma==null){
                correctedLemma = "???";
            }
            originalWord.setAttribute("pos_c", correctedPos);
            originalWord.setAttribute("lemma_c", correctedLemma);
            
            String superPos = STTS.getParentTag(originalWord.getAttributeValue("pos"));
            String correctedSuperPos = STTS.getParentTag(correctedPos);
            
            if (superPos==null){
                superPos = "???";
            }
            if (correctedSuperPos==null){
                correctedSuperPos = "???";
            }
            
            originalWord.setAttribute("super", superPos);
            originalWord.setAttribute("super_c", correctedSuperPos);
        }
        
        return originalDoc;
    }
    
    private void doit() throws JDOMException, IOException {
        Document originalDoc = FileIO.readDocumentFromLocalFile(ORIGINAL);
        Document correctedDoc = FileIO.readDocumentFromLocalFile(CORRECTED);
        
        originalDoc = merge(originalDoc, correctedDoc);
        
        FileIO.writeDocumentToLocalFile(OUT, originalDoc);
    }

    private HashSet<String> getIdenticalTokens(Document originalDoc, Document correctedDoc) throws JDOMException {
        List l1 = XPath.selectNodes(originalDoc, "//w");
        HashMap<String, Element> index1 = new HashMap<String, Element>();
        for (Object o : l1){
            Element e = (Element)o;
            index1.put(e.getAttributeValue("id"), e);
        }
        List l2 = XPath.selectNodes(correctedDoc, "//w");
        HashMap<String, Element> index2 = new HashMap<String, Element>();
        for (Object o : l2){
            Element e = (Element)o;
            index2.put(e.getAttributeValue("id"), e);
        }
        HashSet<String> result = new HashSet<String>();
        for (Object o : l1){
            Element e1 = (Element)o;
            String id = e1.getAttributeValue("id");
            Element e2 = index2.get(id);
            if (e2==null) continue;
            String w1 = WordUtilities.getWordText(e1);
            String w2 = WordUtilities.getWordText(e2);
            String n1 = e1.getAttributeValue("n");
            String n2 = e2.getAttributeValue("n");
            String lemma1 = e1.getAttributeValue("lemma");
            String lemma2 = e2.getAttributeValue("lemma");
            boolean isIdentical = 
                    w1.equals(w2)
                    || ((n1==null) && (n2==null) && lemma1.equals(lemma2))
                    || ((n1!=null) && (n2!=null) && n1.equals(n2));
            if (isIdentical){
                result.add(id);
            }
        }                        
        System.out.println(result.size() + " of " + l1.size() + " okay.");
        return result;
    }
}
