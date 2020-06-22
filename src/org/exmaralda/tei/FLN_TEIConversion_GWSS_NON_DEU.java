/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tei;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class FLN_TEIConversion_GWSS_NON_DEU {

    //String CORPUS ="FOLK";
    //String CORPUS ="FOLK-GOLD-SEGCOR";
    String CORPUS ="GWSS";
    //String CORPUS="ISW";
    //String CORPUS ="MEND";
    //String CORPUS ="DH";
    //String CORPUS ="DNAM";
    //String CORPUS ="ZW";
    String IN = "D:\\AGD-DATA\\dgd2_data\\transcripts\\" + CORPUS;
    //String IN = "D:\\Dropbox\\IDS\\AGD\\MEND-Mennonitendeutsch-Goez\\Transkripte\\5";
    String OUT = "D:\\AGD-DATA\\dgd2_data\\iso-transcripts\\" + CORPUS;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new FLN_TEIConversion_GWSS_NON_DEU().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(FLN_TEIConversion_GWSS_NON_DEU.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FLN_TEIConversion_GWSS_NON_DEU.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(FLN_TEIConversion_GWSS_NON_DEU.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FLN_TEIConversion_GWSS_NON_DEU.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(FLN_TEIConversion_GWSS_NON_DEU.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(FLN_TEIConversion_GWSS_NON_DEU.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        File[] files = new File(IN).listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                //System.out.println(name);
                int number = Integer.parseInt(name.substring(name.indexOf("_E_") + 3, name.indexOf("_SE_")));
                boolean isNonDeu = (number>=1000);
                return name.toUpperCase().endsWith(".FLN") && isNonDeu;
            }
            
        });
        StylesheetFactory sf = new StylesheetFactory(true);
        /*new File(OUT).mkdir();
        if (new File(OUT).listFiles()!=null){
            for (File existingFile : new File(OUT).listFiles()){
                System.out.println("Deleting " + existingFile.getAbsolutePath());
                //Files.delete(existingFile.toPath());
                existingFile.delete();
            }
        }*/
        for (File file : files){ 
            System.out.println(file.getName());
            File out = new File(new File(OUT), file.getName().replaceAll("\\.fln", ".xml"));
            String name = file.getName();
            int number = Integer.parseInt(name.substring(name.indexOf("_E_") + 3, name.indexOf("_SE_")));
            String lang = "en";
            if (number >=2000){
                lang = "pl";
            }
            if (number >=3000){
                lang = "it";
            }
            String[][] parameters = {
                {"LANGUAGE", lang},
                {"MAKE_INLINE_ATTRIBUTES", "TRUE"}
            };
            String result = sf.applyInternalStylesheetToExternalXMLFile("/org/exmaralda/tei/xml/folker2isotei.xsl", file.getAbsolutePath(), parameters);
            Document d = IOUtilities.readDocumentFromString(result);
            IOUtilities.writeDocumentToLocalFile(out.getAbsolutePath(), d);  
            System.out.println(out.getAbsolutePath() + " written.");
        }
    }
}
