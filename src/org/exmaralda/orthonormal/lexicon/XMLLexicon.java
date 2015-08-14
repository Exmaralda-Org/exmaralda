/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.lexicon;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */ 

public class XMLLexicon extends AbstractNormalizationLexicon {

    
    HashMap<String, NormalisationInfo> map = new HashMap<String, NormalisationInfo>();
    String DEFAULT_LEXICON = "/org/exmaralda/orthonormal/lexicon/FOLK_Normalization_Lexicon_FEB_2015.xml";
    String CAPITAL_ONLY_LIST = "/org/exmaralda/orthonormal/lexicon/dereko_capital_only.txt";
    HashSet<String> capitalOnly = new HashSet<String>(240000, 1.0f);
    
    @Override
    public void put(String form, String correspondingForm, String transcriptionID, String wordID) throws LexiconException {
        if (!map.containsKey(form)){
            map.put(form, new NormalisationInfo(form));
        }
        NormalisationInfo info = map.get(form);
        info.put(correspondingForm);
        
    }

    @Override
    public void read(Object source) throws IOException {
        Document d;
        if (source!=null){
            try {
                File f = (File)source;
                d = IOUtilities.readDocumentFromLocalFile(f.getAbsolutePath());
            } catch (JDOMException ex) {
                throw new IOException(ex);
            }
        } else {
            try {
                d = new IOUtilities().readDocumentFromResource(DEFAULT_LEXICON);
            } catch (JDOMException ex) {
                throw new IOException(ex);
            }
        }
        for (Object o : d.getRootElement().getChildren()){
            Element e = (Element)o;
            NormalisationInfo info = new NormalisationInfo(e);
            map.put(info.form, info);
        }
        
        InputStream in = getClass().getResourceAsStream(CAPITAL_ONLY_LIST);
        BufferedReader input = new BufferedReader(new InputStreamReader(in));        
        String nextLine;
        while ((nextLine = input.readLine()) != null){
            capitalOnly.add(nextLine);
        }
        
    }

    @Override
    public void write(Object target) throws IOException {
        File f = (File)target;
        Document d = new Document();
        Element root = new Element("lexicon");
        d.setRootElement(root);
        for (String form : map.keySet()){
            NormalisationInfo info = map.get(form);
            Element infoElement = info.toJDOMElement();
            root.addContent(infoElement);
        }
        IOUtilities.writeDocumentToLocalFile(f.getAbsolutePath(), d);
        
    }

    @Override
    public int getFrequency(String form, String correspondingForm) {
        NormalisationInfo info = map.get(form);
        return info.getFrequency(correspondingForm);
    }

    @Override
    public boolean hasFrequencyInformation() {
        return true;
    }

    @Override
    public boolean hasCapitalInformation() {
        return true;
    }

    @Override
    public boolean isCapitalOnly(String form) throws LexiconException {
        return capitalOnly.contains(form);
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
    
    @Override
    public List<String> getCandidateForms(String form) throws /*up*/ LexiconException {
        if (!map.containsKey(form)){
            return super.getCandidateForms(form);
        }
        NormalisationInfo info = map.get(form);
        return info.getCandidateForms();
        
    }
    
    
}
