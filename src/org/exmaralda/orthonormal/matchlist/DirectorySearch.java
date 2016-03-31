/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.matchlist;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class DirectorySearch {
    
    
    
    File directory;

    public DirectorySearch(File directory) {
        this.directory = directory;
    }
    
    public MatchList searchDirectory(String transcribedRegex, 
            String normalizedRegex, 
            String lemmaRegex, 
            String posRegex) throws JDOMException, IOException{
        File[] transcripts = directory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith("FLN");                 
            }           
        });
        
        ArrayList<Element> result = new ArrayList<Element>();
        
        for (File t : transcripts){
            System.out.println("Reading " + t.getName());
            Document tDoc = FileIO.readDocumentFromLocalFile(t);
            List l = XPath.selectNodes(tDoc, "//w");
            for (Object o : l){
                Element w = (Element)o;

                String transcribed = WordUtilities.getWordText(w);
                boolean transcribedMatches = transcribedRegex==null || transcribedRegex.length()==0 || transcribed.matches(transcribedRegex);
                                
                boolean normalizedMatches = false;
                if (normalizedRegex!=null && normalizedRegex.length()>0){
                    String normalized = w.getAttributeValue("n");
                    if (normalized==null){
                        normalized = transcribed;
                    }
                    String[] nTokens = normalized.split(" ");
                    for (String nToken : nTokens){
                        normalizedMatches = normalizedMatches || nToken.matches(normalizedRegex);
                    }
                } else {
                    normalizedMatches = true;
                }
                
                boolean lemmaMatches = false;
                if (lemmaRegex!=null && lemmaRegex.length()>0){
                    String lemma = w.getAttributeValue("lemma");
                    String[] lTokens = lemma.split(" ");
                    for (String lToken : lTokens){
                        lemmaMatches = lemmaMatches || lToken.matches(lemmaRegex);
                    }
                } else {
                    lemmaMatches = true;
                }
                
                boolean posMatches = false;
                if (posRegex!=null && posRegex.length()>0){
                    String pos = w.getAttributeValue("pos");
                    String[] pTokens = pos.split(" ");
                    for (String pToken : pTokens){
                        posMatches = posMatches || pToken.matches(posRegex);
                    }
                } else {
                    posMatches = true;
                }
                
                if (transcribedMatches && normalizedMatches && lemmaMatches && posMatches){
                    Element contribution = (Element) XPath.selectSingleNode(w, "ancestor::contribution");
                    Element clonedContribution = (Element) contribution.clone();
                    clonedContribution.setAttribute("match", w.getAttributeValue("id"));
                    clonedContribution.setAttribute("transcript", t.getName());
                    clonedContribution.detach();                            
                    result.add(clonedContribution);                    
                }

            }
        }
        
        Document resultDoc = new Document(new Element("match-list"));
        for (Element e : result){
            resultDoc.getRootElement().addContent(e);
        }
        return new MatchList(resultDoc);
    }
    
    public static void main(String[] args){
        DirectorySearch ds = new DirectorySearch(new File("Z:\\TAGGING\\SAMPLE\\4\\Doubletten"));
        try {
            ds.searchDirectory("was", null, null, null);
        } catch (JDOMException ex) {
            Logger.getLogger(DirectorySearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DirectorySearch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
 