/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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
public class SampleDoublettes {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new SampleDoublettes().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(SampleDoublettes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SampleDoublettes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        File in = new File("Z:\\TAGGING\\SAMPLE\\0");
        File[] transcriptFiles = in.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".fln"));
            }               
        });

       
        
        System.out.println("Found " + transcriptFiles.length + " OrthoNormal transcripts in " + in.getAbsolutePath() + ".");
        
        int count=0;
        int all = 0;
        int wCount = 0;
        int wCountAll = 0;
        HashSet<String> ids = new HashSet<String>();        
        HashSet<String> trs = new HashSet<String>();
        for (File transcript : transcriptFiles){
            System.out.println("=================================");
            Document trDoc = FileIO.readDocumentFromLocalFile(transcript);
            String transcriptID = trDoc.getRootElement().getAttributeValue("dgd-id");
            List w = XPath.selectNodes(trDoc, "//w");
            wCountAll+=w.size();
            List l = XPath.selectNodes(trDoc, "//contribution");
            for (Object o : l){
                Element c = (Element)o;
                all++;
                String contributionID = c.getAttributeValue("id");
                String combiID = transcriptID + "_" + contributionID;
                if (ids.contains(combiID)){
                    count++;
                    System.out.println(combiID + " already there.");
                    trs.add(transcriptID);
                    List w2 = XPath.selectNodes(c, "descendant::w");
                    wCount+=w2.size();
                }
                ids.add(combiID);                
            }
        }
        System.out.println("=============");
        System.out.println(count + " von " + all + " Beiträgen sind Doubletten");
        System.out.println(wCount + " von " + wCountAll + " Tokens sind Doubletten");
        for (String t : trs){
            System.out.println(t);
        }
    }
}
