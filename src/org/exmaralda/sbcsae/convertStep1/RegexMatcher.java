/*
 * RegexMatcher.java
 *
 * Created on 7. Juni 2007, 12:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.convertStep1;

import java.io.FileNotFoundException;
import java.io.*;
import org.jdom.Element;
import java.util.regex.*;
import org.jdom.JDOMException;
import java.util.*;
/**
 *
 * @author thomas
 */
public class RegexMatcher extends AbstractIUProcessor {    
    
    Pattern pattern;
    HashSet<String> types = new HashSet<String>();
    
    /** Creates a new instance of RegexMatcher */
    public RegexMatcher(String regex) {
        super(false);
        pattern = Pattern.compile(regex);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String regex = "(?<=<)[A-Z]+";
        String fn = "Segmental_Phonetics.txt";
        String outFile = BASE_DIRECTORY + "\\EXAMPLES\\"  + fn;
        String outFile2 = BASE_DIRECTORY + "\\TYPES\\" + fn;
        RegexMatcher rm = new RegexMatcher(regex);
        try {
            rm.doIt();
            rm.output(outFile);
            rm.outputTypes(outFile2);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void processIntonationUnit(Element iu) {
        String text = iu.getText();
        Matcher m = pattern.matcher(text);
        if (m.find()){
            outappend(currentFilename + "\t" + iu.getAttributeValue("line") + "\t" + text);
            m.reset();
            while(m.find()){
                types.add(m.group());
            }
        }
    }
    
    void outputTypes(String filename) throws FileNotFoundException, IOException{
        System.out.println("started writing document...");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        for (String s : types){
            fos.write(s.getBytes("UTF-8"));            
            fos.write("\n".getBytes("UTF-8"));
        }
        fos.close();
    }
    
    
}
