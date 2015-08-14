/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.exmaralda.common.corpusbuild.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class NormalizeFiles extends AbstractSchneiderProcessor {

    
    Document normalizations;
    HashSet<String> filesWithNormalizations = new HashSet<String>();
    
    
    static String XML_STRING="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    static String NBSP_STRING = "<!DOCTYPE stylesheet [<!ENTITY nbsp  \" \" ><!ENTITY copy  \" \" >]>";
    
    public NormalizeFiles(String[] args) throws JDOMException, IOException{
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];
        outputDirectory = new File(args[2]);
        outputDirectory.mkdir();
        for (File f : outputDirectory.listFiles()){
            f.delete();
        }
        outputSuffix = args[3];
        
        logFile = new File(args[4]);
        
        if (args.length==6){
            setupCustomNormalization(args[5]);
        }
        
        inputFiles = inputDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(inputSuffix.toUpperCase());
            }            
        });
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            NormalizeFiles nf = new NormalizeFiles(args);
            nf.processFiles();
        } catch (Exception ex) {
            Logger.getLogger(NormalizeFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException, JDOMException {
        StringBuffer log = new StringBuffer();
        for (File inputFile : inputFiles){
            System.out.println("Reading " + inputFile.getName());
            StringBuffer result = new StringBuffer();
            result.append(XML_STRING + "\n");
            result.append(NBSP_STRING + "\n");
            FileInputStream fis = new FileInputStream(inputFile);
            InputStreamReader isr = new InputStreamReader(fis, "ISO-8859-1");
            BufferedReader br = new BufferedReader(isr);
            String nextLine="";
            while ((nextLine = br.readLine()) != null){
                if ((nextLine.trim().length()>0) 
                        && (!(nextLine.trim().startsWith("<!DOCTYPE")))
                        && (!(nextLine.trim().startsWith("<div class=\"Menue\">")))                        
                        ){
                    String beforeReplacement0 = nextLine;
                    if (filesWithNormalizations.contains(inputFile.getName())){
                        String xpath = "//problem[@file='" + inputFile.getName() + "']";
                        List l = XPath.newInstance(xpath).selectNodes(normalizations);
                        for (Object o : l){
                            Element e = (Element)o;
                            String replace = e.getChildText("replace");
                            String replacement = e.getChildText("replacement");
                            nextLine = nextLine.replace(replace, replacement);
                            if (!(beforeReplacement0.equals(nextLine))){
                                log.append(inputFile.getName() + "\tcustom replacement" + "\t" + replace + "\t" + replacement + System.getProperty("line.separator"));
                            }
                           
                        }
                    }
                    if (inputFile.getName().startsWith("ZW")){
                        if (nextLine.endsWith("</td></tr>")){
                            int openingSpan = StringUtils.countMatches(nextLine, "<span");
                            int closingSpan = StringUtils.countMatches(nextLine, "</span>");
                            if ((openingSpan==closingSpan+1)){
                                nextLine = nextLine.substring(0, nextLine.length()-10) + "</span></td></tr>";
                                log.append(inputFile.getName() + "\t" + "span added" + System.getProperty("line.separator"));                            
                            }
                        }
                    }
                    
                    if (inputFile.getName().startsWith("OS")){
                        if (nextLine.endsWith("Sp: = S2</td></tr>")){
                            nextLine = nextLine.replace("Sp: = S2</td></tr>", "</td></tr>");
                            log.append(inputFile.getName() + "\t" + "removed Sp: = S2" + System.getProperty("line.separator"));                            
                        }                        
                    }
                                        
                    
                    String beforeReplacement = nextLine;
                    if (inputFile.getName().contains("OS") || inputFile.getName().contains("ZW")){
                        String regex = " ?<TA[^/]+?/>";
                        Vector<String> allMatches = new Vector<String>();
                        Matcher m = Pattern.compile(regex).matcher(nextLine);
                        while (m.find()){
                            String replaced = nextLine.substring(m.start(), m.end());
                            allMatches.add(replaced);
                            System.out.println("*********" + replaced);
                        }
                        for (String s : allMatches){
                            nextLine = nextLine.replace(s, calculateTAReplacement(s));
                        }
                        //nextLine = nextLine.replaceAll(regex, "");
                        if (!(beforeReplacement.equals(nextLine))){
                            //int diff = beforeReplacement.length() - nextLine.length();
                            for (String s : allMatches){
                                log.append(inputFile.getName() + "\tgeneric replacement" + "\t" + s + "\t" + calculateTAReplacement(s) + System.getProperty("line.separator"));
                            }
                        }
                    }
                    result.append(nextLine);
                }
            }
            br.close();            
            //System.out.println(result.toString());
            try {
                Document xmlDocument = FileIO.readDocumentFromString(result.toString());
                FileIO.writeDocumentToLocalFile(makeOutputPath(inputFile), xmlDocument);
            } catch (JDOMException jde){
                log.append(inputFile.getName() + "\t" + jde.getMessage() + System.getProperty("line.separator"));                
            }
            
            
            
        }
        
        super.writeLogToTextFile(log);
        
    }


    private void setupCustomNormalization(String normFilePath) throws JDOMException, IOException {
        /*<problem file="OS165TRA.HTM">
            <replace><![CDATA[<TA Korpus='..' Annotation='(UNDEUTLICH) [Kommentar des Ertsttranskribenten: gibt außer den Bezeichnungen noch folgende Erläuterungen zu 'Anemonen':]' /></span>]]></replace>
            <replacement><![CDATA[]]></replacement>
        </problem>*/
        normalizations = FileIO.readDocumentFromLocalFile(normFilePath);
        for (Object o : normalizations.getRootElement().getChildren("problem")){
            Element e = (Element)o;
            filesWithNormalizations.add(e.getAttributeValue("file"));
        }        
    }
    
    private String calculateTAReplacement(String original){
        //<TA Korpus='ZW' Annotation='(#um das 'Schmateres' zu lecken)' />        
        int i1 = original.indexOf("Annotation='") + "Annotation='".length();
        int i2 = original.lastIndexOf("' />");
        if (i1<0 || i2<0 || i2<=i1) return "";
        //<span class="TA">(PAUSE)</span>
        String result = "<span class=\"TA\">";
        result+=original.substring(i1, i2);
        result+="</span>";
        System.out.println(result);
        return result;
    }
}
