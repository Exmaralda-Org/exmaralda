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
public class FLN_TEIConversion {

    //String CORPUS ="FOLK";
    //String CORPUS ="FOLK-GOLD-SEGCOR";
    //String CORPUS ="GWSS";
    //String CORPUS="ISW";
    //String CORPUS ="MEND";
    //String CORPUS ="DH";
    String CORPUS ="DNAM";
    //String CORPUS = "ZW";
    //String CORPUS ="BETV";
    //String CORPUS ="FGOP";
    //String CORPUS ="DH";
    String IN = "D:\\AGD-DATA\\dgd2_data\\transcripts\\" + CORPUS;
    //String IN = "D:\\Dropbox\\IDS\\AGD\\MEND-Mennonitendeutsch-Goez\\Transkripte\\5";
    String OUT = "D:\\AGD-DATA\\dgd2_data\\iso-transcripts\\" + CORPUS;
    
    //String OUT = "N:\\Workspace\\FOLK\\ISO-VERSIONS\\0-FLN2TEI";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new FLN_TEIConversion().doit();
        } catch (JDOMException | IOException | SAXException | ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(FLN_TEIConversion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        File[] files = new File(IN).listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(".FLN");
            }
            
        });
        StylesheetFactory sf = new StylesheetFactory(true);
        new File(OUT).mkdir();
        if (new File(OUT).listFiles()!=null){
            for (File existingFile : new File(OUT).listFiles()){
                System.out.println("Deleting " + existingFile.getAbsolutePath());
                //Files.delete(existingFile.toPath());
                existingFile.delete();
            }
        }
        for (File file : files){ 
            File out = new File(new File(OUT), file.getName().replaceAll("\\.fln", ".xml"));
            String[][] parameters = {
                {"LANGUAGE", "de"},
                {"MAKE_INLINE_ATTRIBUTES", "TRUE"}
            };
            String result = sf.applyInternalStylesheetToExternalXMLFile("/org/exmaralda/tei/xml/folker2isotei.xsl", file.getAbsolutePath(), parameters);
            Document d = IOUtilities.readDocumentFromString(result);
            IOUtilities.writeDocumentToLocalFile(out.getAbsolutePath(), d);  
            System.out.println(out.getAbsolutePath() + " written.");
        }
    }
}
