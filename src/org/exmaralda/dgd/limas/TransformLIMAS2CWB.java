/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.limas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class TransformLIMAS2CWB {

    String LIMAS = "U:\\LIMAS\\lim.xces";
    String STRAT = "U:\\LIMAS\\Stratifikation_LIMAS_Korrigiert.txt";
    String PARA = "U:\\LIMAS\\german.par";
    static String treeTaggerDirectory = "c:\\TreeTagger";
    
    Document limas_doc;
    ArrayList<String[]> categories = new ArrayList<String[]>();
    ArrayList<String[]> subcategories = new ArrayList<String[]>();
    
    TreeTaggerWrapper tt = new TreeTaggerWrapper<String>();
    
    HashSet<String> UMLAUTS = new HashSet<String>();

    public TransformLIMAS2CWB() throws IOException {
        tt.setModel(PARA + ":iso8859-1");                   
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            /*try {
                try {
                    System.setProperty("treetagger.home", treeTaggerDirectory);
                    TransformLIMAS2CWB tl = new TransformLIMAS2CWB();
                    tl.doit();
                    System.out.println("=============");
                    for (String umlaut : tl.UMLAUTS){
                        System.out.println(umlaut);
                    }
                } catch (TreeTaggerException ex) {
                    Logger.getLogger(TransformLIMAS2CWB.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(TransformLIMAS2CWB.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JDOMException ex) {
                Logger.getLogger(TransformLIMAS2CWB.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            //new TransformLIMAS2CWB().groupAll();
            new TransformLIMAS2CWB().groupDirectory("U:\\LIMAS2\\AGGREGAT");
        } catch (IOException ex) {
            Logger.getLogger(TransformLIMAS2CWB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException, JDOMException, TreeTaggerException {
        limas_doc = FileIO.readDocumentFromLocalFile(new File(LIMAS));
        FileInputStream fis = new FileInputStream(new File(STRAT));
        InputStreamReader isr = new InputStreamReader(fis, "ISO-8859-1");
        BufferedReader br = new BufferedReader(isr);
        String nextLine="";
        while ((nextLine = br.readLine()) != null){
            // 465	
            // KULTUR- UND SITTENGESCHICHTE | HANSFERDINAND DOEBLER | KULTUR- UND SITTENGESCHICHTE DER WELT | (EROS - SEXUS - SITTE) | BERTELSMANN KUNSTVERLAG, GUETERSLOH 1971, S. 258-	
            // 13	
            // GESCHICHTE	
            // 13.03	
            // TEILGEBIETE
            String[] items = nextLine.split("\\t");
            String[] c = {items[2], items[3]};
            String[] s = {items[4], items[5]};
            categories.add(c);
            subcategories.add(s);
        }
        List texts = XPath.newInstance("//text/body/div").selectNodes(limas_doc);
        for (Object o : texts){
            Element thisText = (Element)o;
            // <textSigle>LIM/LI2.00270</textSigle>
            Element thisNumberElement = (Element) XPath.selectSingleNode(thisText, "ancestor::idsText/descendant::textSigle");
            int thisNumber = Integer.parseInt(thisNumberElement.getText().substring(8));
            Element root = new Element("text");
            //<text    id="001"    class="Belletristik" subclass="Heftromane">
            root.setAttribute("id", thisNumberElement.getText().substring(10));
            root.setAttribute("class", categories.get(thisNumber-1)[1]);
            root.setAttribute("class-no", categories.get(thisNumber-1)[0]);
            root.setAttribute("subclass", subcategories.get(thisNumber-1)[1]);
            root.setAttribute("subclass-no", subcategories.get(thisNumber-1)[0]);
            List sentences = XPath.newInstance("descendant::s").selectNodes(thisText);
            for (Object o2: sentences){
                Element sentence = (Element)o2;
                boolean isInsideHead = sentence.getParentElement().getName().equals("head");
                sentence.detach();
                tokenize(sentence, isInsideHead);
                tag(sentence);
                root.addContent(sentence);
            }
            Document resultDoc = new Document(root);
            String path = "U:\\LIMAS2\\" + categories.get(thisNumber-1)[1] + "\\" + subcategories.get(thisNumber-1)[1];
            File dir = new File(path);
            dir.mkdirs();
            File f = new File(dir, thisNumberElement.getText().substring(10) + ".xces");
            writeDocument(f, resultDoc);
            System.out.println(f.getAbsolutePath() + " written.");
        }
    }

    private void tokenize(Element sentence, boolean addSpace) throws JDOMException {
        List l = XPath.selectNodes(sentence, "descendant::text()");
        String sentenceText = "";
        for (Object o : l){
            Text t = (Text)o;
            sentenceText+=t.getText();
        }
        // new 29-04-2013
        if (addSpace){
            sentenceText+=" ";
        }
        // end new
        Pattern p = Pattern.compile("( ?\\p{Punct}+ ?| )");
        Matcher m = p.matcher(sentenceText);
        int index = 0;
        sentence.removeContent();
        while (m.find()){
            String word = sentenceText.substring(index, m.start());
            String punc = sentenceText.substring(m.start(), m.end()).trim();
            index = m.end();
            //System.out.println(word);
            //System.out.println(punc);
            Element w1 = new Element("t");
            w1.setText(word);
            sentence.addContent(w1);
            if (punc.length()>0){
                for (int i=0; i<punc.length(); i++){
                    Element w2 = new Element("t");
                    w2.setText(punc.substring(i,i+1));
                    sentence.addContent(w2);                
                }
            }
        }
    }

    private void tag(final Element sentence) throws IOException, TreeTaggerException {
        List l = sentence.removeContent();
        ArrayList<String> tokens = new ArrayList<String>();
        for (Object o : l){
            tokens.add(((Element)o).getText());
        }
        //tt.setModel(PARA + ":UTF-8");           
        try {
           tt.setHandler(new TokenHandler<String>() {
                @Override
                public void token(String token, String pos, String lemma) {
                        Element t = new Element("t");
                        t.setText(token);
                        t.setAttribute("pos", pos);
                        t.setAttribute("lemma", lemma);
                        if (lemma.contains("\uFFFD")){
                            System.out.println(lemma);
                            UMLAUTS.add(lemma);
                        }
                        sentence.addContent(t);
                }
            });
            tt.process(tokens);
        }
        finally {
            //tt.destroy();
        }        
    }

    private void writeDocument(File f, Document resultDoc) throws FileNotFoundException, IOException, JDOMException {
        StringBuilder text = new StringBuilder();
        Element x = (Element)resultDoc.getRootElement().clone();
        x.removeContent();
        text.append(IOUtilities.elementToString(x, false));
        text.append(System.getProperty("line.separator"));
        List l = XPath.selectNodes(resultDoc, "//s");
        for (Object o : l){
            text.append("<s>");
            text.append(System.getProperty("line.separator"));
            for (Object o2:((Element)o).getChildren("t")){
                Element t = (Element)o2;
                text.append(t.getText());
                text.append("\t");
                text.append(t.getAttributeValue("pos"));
                text.append("\t");
                text.append(t.getAttributeValue("lemma"));
                text.append(System.getProperty("line.separator"));
            }            
            text.append("</s>");
            text.append(System.getProperty("line.separator"));
        }
        text.append("</text>");
        text.append(System.getProperty("line.separator"));
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(text.toString().getBytes());
        fos.close();
        System.out.println("document written.");        
        
    }
    
    void groupDirectory(String path) throws IOException{
        final File directory = new File(path);        
        ArrayList<File> allFiles = getFiles(path);
        /*File[] allFiles = directory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return ((!name.equals(directory.getName()+ ".xces")) && name.endsWith(".xces"));                
            }           
        });*/        
        System.out.println("GOT EM!");
        File outFile = new File(directory.getParentFile(), directory.getName() + ".xces");
        FileOutputStream fos = new FileOutputStream(outFile);
        
        for (File thisFile : allFiles){
            System.out.println(thisFile.getAbsolutePath());
            FileInputStream fis = new FileInputStream(thisFile);
            InputStreamReader isr = new InputStreamReader(fis, "ISO-8859-1");
            BufferedReader br = new BufferedReader(isr);
            String nextLine="";
            while ((nextLine = br.readLine()) != null){
                fos.write(nextLine.toString().getBytes());
                fos.write(System.getProperty("line.separator").getBytes());
            }
            fis.close();
        }
        
        fos.close();
        System.out.println(outFile.getAbsolutePath() + " written.");        
        
    }

    private void groupAll() throws IOException {
        File[] directories = new File("U:\\LIMAS2").listFiles(new FileFilter(){

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
            
        });
        
        for (File dir : directories){
            groupDirectory(dir.getAbsolutePath());
        }
        
    }

    private ArrayList<File> getFiles(String path) {
        ArrayList<File> result = new ArrayList<File>();
        final File directory = new File(path);
        File[] allFiles = directory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return ((!name.equals(directory.getName()+ ".xces")) && name.endsWith(".xces")) || new File(dir,name).isDirectory();                
            }           
        });        
        for (File f : allFiles){
            System.out.println(f.getAbsolutePath());
            if (f.isDirectory()){
                result.addAll(getFiles(f.getAbsolutePath()));
            } else {
                result.add(f);
            }
        }
        return result;

    }
}
