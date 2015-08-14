/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.lexicon;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public class SimpleXMLFileLexicon extends AbstractNormalizationLexicon {

    Hashtable<String,HashSet<String>> mappings = new Hashtable<String,HashSet<String>>();

    public void put(String form, String lemma, String transcriptionID, String wordID) {
        if (form!=null || form.equals(lemma)) return;
        if (!mappings.containsKey(form)){
            mappings.put(form, new HashSet<String>());
        }
        mappings.get(form).add(lemma);
    }

    @Override
    public void read(Object source) throws IOException {
        File f = (File)source;
        try {
            Document d = IOUtilities.readDocumentFromLocalFile(f.getAbsolutePath());
            for (Object o : d.getRootElement().getChildren()){
                Element e = (Element)o;
                put(e.getAttributeValue("lemma"),e.getAttributeValue("form"), "", "");
            }
        } catch (JDOMException ex) {
            throw new IOException(ex);
        }
    } 

    @Override
    public void write(Object target) throws IOException {
        File f = (File)target;
        Document d = new Document();
        Element root = new Element("lexicon");
        d.setRootElement(root);
        for (String lemma : mappings.keySet()){
            for (String form : mappings.get(lemma)){
                Element e = new Element("entry");
                e.setAttribute("lemma", lemma);
                e.setAttribute("form", form);
                root.addContent(e);
            }
        }
        IOUtilities.writeDocumentToLocalFile(f.getAbsolutePath(), d);
    }

    @Override
    public List<String> getCandidateForms(String form) throws LexiconException {
        HashSet<String> result = mappings.get(form);
        Vector<String> resultVector = new Vector<String>();
        if (result!=null){
            for (String e : result){
                resultVector.addElement(e);
            }
        }
        resultVector.addAll(super.getCandidateForms(form));
        return resultVector;
    }

    public int getFrequency(String form, String correspondingForm) {
        return -1;
    }

    public boolean hasFrequencyInformation() {
        return false;
    }

    public boolean isCapitalOnly(String form) {
        return false;
    }

    public boolean hasCapitalInformation() {
        return false;
    }

    public void update(File[] files) throws JDOMException, IOException, LexiconException, SQLException{
        for (File f : files){
            update(f);
        }
    }
    
    public void update(File file) throws JDOMException, IOException, LexiconException, SQLException{
        System.out.println("Processing " + file.getName());
        Document d = FileIO.readDocumentFromLocalFile(file);
        update(d);
    }
    
    public void update(Document d) throws SQLException, JDOMException, LexiconException{
        String transcriptionID = d.getRootElement().getAttributeValue("id");
        List l = XPath.newInstance("//w").selectNodes(d);
        for (Object o : l){
            Element w = (Element)o;
            // <w id="w67" n="frage">frag</w>
            String wordID = w.getAttributeValue("id");
            String form = WordUtilities.getWordText(w);
            String lemma = form;
            if (w.getAttribute("n")!=null){
                  lemma = w.getAttributeValue("n");
            }
            put(form, lemma, transcriptionID, wordID);
        }                
    }
    


}
