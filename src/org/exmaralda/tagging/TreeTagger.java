/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.annolab.tt4j.ProbabilityHandler;
import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;
import org.exmaralda.exakt.utilities.FileIO;

/**
 *
 * @author thomas
 */
public class TreeTagger {

    public String treeTaggerDirectory = "S:\\TP-Z2\\TAGGING\\TreeTagger\\bin\\tree-tagger.exe";
    public String parameterFile = "S:\\TP-Z2\\TAGGING\\TreeTagger\\lib\\german.par";
    public String[] options = {"-token","-lemma","-sgml","-no-unknown"};
    String parameterFileEncoding = "iso8859-1"; 

    public TreeTagger(String treeTaggerDirectory, String parameterFile, String parameterFileEncoding, String[] options) throws IOException{
        System.out.println("Initialising tagger");
        File f1 = new File(treeTaggerDirectory);
        if (!f1.exists()){throw new IOException("Command file " + treeTaggerDirectory + " does not exist.");}
        if (!f1.canExecute()){throw new IOException("Command file " + treeTaggerDirectory + " is not exectuable.");}
        File f2 = new File(parameterFile);
        if (!f2.exists()){throw new IOException("Parameter file " + parameterFile + " does not exist.");}
        if (!f2.canRead()){throw new IOException("Parameter file " + parameterFile + " cannot be read.");}

        this.treeTaggerDirectory = treeTaggerDirectory;
        this.parameterFile = parameterFile;
        this.parameterFileEncoding = parameterFileEncoding;
        this.options = options;
        System.out.println("Tagger initialised");
    }

    public void tag(TreeTaggableDocument input, File outputFile) throws IOException{
         SextantTokenHandler tokenHandler = new SextantTokenHandler();
         tokenHandler.setIDList(input.getIDs());        
         tag(input, outputFile, tokenHandler);
    }
    
    
    public void tag(TreeTaggableDocument input, File outputFile, ProbabilityHandler tokenHandler) throws IOException{
         System.out.println("Setting up tagger");
         System.setProperty("treetagger.home", treeTaggerDirectory);
         TreeTaggerWrapper tt = new TreeTaggerWrapper<String>();
         //uncomment next line to make TreeTaggerWrapper verbose
         //tt.TRACE = true;
         tt.setProbabilityThreshold(0.999999);
         try {
             System.out.println("   Setting model:  " + parameterFile + ":" + parameterFileEncoding);
             tt.setModel(parameterFile + ":" + parameterFileEncoding);
             System.out.println("   Setting arguments");
             tt.setArguments(options);
             System.out.println("   Setting handler");
             tt.setHandler(tokenHandler);
             System.out.println("Tagger setup complete");
             
             //System.out.println("");
             //System.out.println(input.getNumberOfTaggableSegments() + " tokens to tag");
             
             for (int pos=0; pos<input.getNumberOfTaggableSegments(); pos++){
                 System.out.print("\rProcessing " + (pos+1) + " of " + input.getNumberOfTaggableSegments());
                 List tokens = input.getTokensAt(pos);
                 //for (int i=0; i<100; i++){tokens.add("ja");}
                 System.out.println(" (" + tokens.size() + " tokens to tag).                ");
                 //for (Object t: tokens){System.out.println(t.toString());}
                 try {
                     tt.process(tokens);
                 } catch (TreeTaggerException ex) {
                     System.out.println("PROBLEM WITH: "  + String.join(" ", tokens));
                     Logger.getLogger(TreeTagger.class.getName()).log(Level.SEVERE, null, ex);
                 }
             }
             System.out.println("Tagging complete.");
         } catch (IOException ex) {
             System.out.println(ex.getLocalizedMessage());
            throw new IOException(ex.getLocalizedMessage());
         } finally {
            /* <annotation xmlns:xlink="http://www.w3.org/1999/xlink"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:xml="http://www.w3.org/XML/1998/namespace"
            xsi:noNamespaceSchemaLocation="http://xml.exmaralda.org/sextant.xsd" xml:base="MiniTest.exs"
            target="MiniTest.exs" id="MiniTest.exs_pos" targetId="n/a"
            type="pos-annotation from sextant tagger"> */
            if (tokenHandler instanceof SextantTokenHandler carsten){
                carsten.sextantDocument.getRootElement().setAttribute("target", input.getBase());
                carsten.sextantDocument.getRootElement().setAttribute("base", input.getBase());
                carsten.sextantDocument.getRootElement().setAttribute("id", input.getBase() + "_pos");
                FileIO.writeDocumentToLocalFile(outputFile, carsten.sextantDocument);
                tt.destroy();
            }
         }

    }

}
