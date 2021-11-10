/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.util.Arrays;
import java.util.List;
import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;

/**
 *
 * @author thomas.schmidt
 */
public class TestTT4J {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new TestTT4J().doit();
    }

    // 04-11-2021, issue #286 : this one works
    //public static String TTC = "D:\\Dropbox\\TreeTagger";
    // 04-11-2021, issue #286 : and this one doesn't
    public static String TTC = "c:\\TreeTagger";
    public String parameterFile = "C:\\TreeTagger\\lib\\italian.par";
    public String[] options = {"-token","-lemma","-sgml","-no-unknown"};
    String parameterFileEncoding = "UTF-8"; 


    private void doit() {
         System.out.println("Setting up tagger");
         System.setProperty("treetagger.home", TTC);
         TreeTaggerWrapper tt = new TreeTaggerWrapper<String>();
         //uncomment next line to make TreeTaggerWrapper verbose
         tt.TRACE = true;
         tt.setProbabilityThreshold(0.999999);
         TokenHandler tokenHandler = new TokenHandler(){
            public void token(Object token, String pos, String lemma) {
                // do nothing
            }             
         };
         try {
             System.out.println("   Setting model");
             tt.setModel(parameterFile + ":" + parameterFileEncoding);
             System.out.println("   Setting arguments");
             tt.setArguments(options);
             System.out.println("   Setting handler");
             tt.setHandler(tokenHandler);
             System.out.println("Tagger setup complete");
             
             System.out.println("*** FlushSequence: " + tt.getModel().getFlushSequence());
             
             String[] tokenArray = {"uno", "due", "tre", 
                 "uno", "due", "tre", 
                 "uno", "due", "tre", 
                 "uno", "due", "tre", 
                 "uno", "due", "tre", 
                 "uno", "due", "tre", 
                 "uno", "due", "tre", 
                 "uno", "due", "tre", 
                 "uno", "due", "tre", 
                 "uno", "due", "tre", 
                 "uno", "due", "tre", 
                 "uno", "due", "tre", 
                 "uno", "due", "tre", 
                 "uno", "due", "tre", 
                 "uno", "due", "tre", 
                 "uno", "due", "tre", 
                 "uno", "due", "tre"};
             List tokens = Arrays.asList(tokenArray);
             tt.process(tokens);
             System.out.println("Tagging complete.");
         } catch (TreeTaggerException ex) {
            ex.printStackTrace();
         } catch (Exception ex){
             ex.printStackTrace();
         }
    }
    
}
