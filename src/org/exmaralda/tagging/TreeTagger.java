/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
    }

    public void tag(TreeTaggableDocument input, File outputFile) throws IOException{
         System.setProperty("treetagger.home", treeTaggerDirectory);
         TreeTaggerWrapper tt = new TreeTaggerWrapper<String>();
         tt.setProbabilityThreshold(0.999999);
         SextantTokenHandler tokenHandler = new SextantTokenHandler();
         tokenHandler.setIDList(input.getIDs());
         try {
             tt.setModel(parameterFile + ":" + parameterFileEncoding);
             tt.setArguments(options);
             tt.setHandler(tokenHandler);
             System.out.print("Tagger setup complete");
             
             //System.out.println("");
             //System.out.println(input.getNumberOfTaggableSegments() + " tokens to tag");
             
             for (int pos=0; pos<input.getNumberOfTaggableSegments(); pos++){
                 System.out.print("\rProcessing " + (pos+1) + " of " + input.getNumberOfTaggableSegments());
                 List tokens = input.getTokensAt(pos);
                 System.out.print(" (" + tokens.size() + " tokens to tag).                ");
                 //for (Object t: tokens){System.out.println(t.toString());}
                 tt.process(tokens);
             }
             System.out.println("Tagging complete.");
         } catch (TreeTaggerException ex) {
            ex.printStackTrace();
            throw new IOException(ex.getLocalizedMessage());
         } catch (Exception ex){
             ex.printStackTrace();
             throw new IOException(ex.getLocalizedMessage());
         } finally {
            /* <annotation xmlns:xlink="http://www.w3.org/1999/xlink"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:xml="http://www.w3.org/XML/1998/namespace"
            xsi:noNamespaceSchemaLocation="http://xml.exmaralda.org/sextant.xsd" xml:base="MiniTest.exs"
            target="MiniTest.exs" id="MiniTest.exs_pos" targetId="n/a"
            type="pos-annotation from sextant tagger"> */
            tokenHandler.sextantDocument.getRootElement().setAttribute("target", input.getBase());
            tokenHandler.sextantDocument.getRootElement().setAttribute("base", input.getBase());
            tokenHandler.sextantDocument.getRootElement().setAttribute("id", input.getBase() + "_pos");
            FileIO.writeDocumentToLocalFile(outputFile, tokenHandler.sextantDocument);
            tt.destroy();
         }

    }

}
