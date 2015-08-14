/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.folk;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class CalculatePOSFrequencies extends AbstractFOLKAnalyzer {

    String[][] ZUORDNUNGEN =
{
{"FOLK_E_00001","B6_Unterrichtsstunde_in_der_Berufsschule","B"},
{"FOLK_E_00002","A9_Vorlesen_für_Kinder","A"},
{"FOLK_E_00003","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00004","B6_Unterrichtsstunde_in_der_Berufsschule","B"},
{"FOLK_E_00005","B6_Unterrichtsstunde_in_der_Berufsschule","B"},
{"FOLK_E_00006","B6_Unterrichtsstunde_in_der_Berufsschule","B"},
{"FOLK_E_00007","B6_Unterrichtsstunde_in_der_Berufsschule","B"},
{"FOLK_E_00008","B6_Unterrichtsstunde_in_der_Berufsschule","B"},
{"FOLK_E_00009","B6_Unterrichtsstunde_in_der_Berufsschule","B"},
{"FOLK_E_00010","A5_Spielinteraktion_mit_Kindern","A"},
{"FOLK_E_00011","A5_Spielinteraktion_mit_Kindern","A"},
{"FOLK_E_00012","A5_Spielinteraktion_mit_Kindern","A"},
{"FOLK_E_00013","A9_Vorlesen_für_Kinder","A"},
{"FOLK_E_00014","A9_Vorlesen_für_Kinder","A"},
{"FOLK_E_00015","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00016","A9_Vorlesen_für_Kinder","A"},
{"FOLK_E_00017","A9_Vorlesen_für_Kinder","A"},
{"FOLK_E_00020","A8_Tischgespräch","A"},
{"FOLK_E_00021","A6_Spielinteraktion_zwischen_Erwachsenen","A"},
{"FOLK_E_00022","B1_Meeting_in_einer_sozialen_Einrichtung","B"},
{"FOLK_E_00024","B1_Meeting_in_einer_sozialen_Einrichtung","B"},
{"FOLK_E_00026","B1_Meeting_in_einer_sozialen_Einrichtung","B"},
{"FOLK_E_00027","A8_Tischgespräch","A"},
{"FOLK_E_00028","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00029","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00030","A4_Paargespräch","A"},
{"FOLK_E_00031","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00032","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00033","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00034","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00035","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00036","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00037","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00038","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00039","A4_Paargespräch","A"},
{"FOLK_E_00040","A6_Spielinteraktion_zwischen_Erwachsenen","A"},
{"FOLK_E_00042","A7_Studentisches_Alltagsgespräch","A"},
{"FOLK_E_00043","A4_Paargespräch","A"},
{"FOLK_E_00046","A7_Studentisches_Alltagsgespräch","A"},
{"FOLK_E_00047","A8_Tischgespräch","A"},
{"FOLK_E_00049","A7_Studentisches_Alltagsgespräch","A"},
{"FOLK_E_00050","A1_Gespräch_auf_der_Urlaubsreise","A"},
{"FOLK_E_00052","A8_Tischgespräch","A"},
{"FOLK_E_00053","A1_Gespräch_auf_der_Urlaubsreise","A"},
{"FOLK_E_00055","A8_Tischgespräch","A"},
{"FOLK_E_00056","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00057","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00058","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00059","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00060","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00061","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00062","B2_Prüfungsgespräch_in_der_Hochschule","B"},
{"FOLK_E_00066","A3_Gespräch_unter_Freunden","A"},
{"FOLK_E_00076","A9_Vorlesen_für_Kinder","A"},
{"FOLK_E_00086","C_Maptask","C"},
{"FOLK_E_00087","C_Maptask","C"},
{"FOLK_E_00089","C_Maptask","C"},
{"FOLK_E_00090","C_Maptask","C"},
{"FOLK_E_00091","C_Maptask","C"},
{"FOLK_E_00093","C_Maptask","C"},
{"FOLK_E_00094","C_Maptask","C"},
{"FOLK_E_00095","C_Maptask","C"},
{"FOLK_E_00096","C_Maptask","C"},
{"FOLK_E_00097","C_Maptask","C"},
{"FOLK_E_00098","C_Maptask","C"},
{"FOLK_E_00099","C_Maptask","C"},
{"FOLK_E_00100","C_Maptask","C"},
{"FOLK_E_00101","C_Maptask","C"},
{"FOLK_E_00102","C_Maptask","C"},
{"FOLK_E_00103","C_Maptask","C"},
{"FOLK_E_00104","C_Maptask","C"},
{"FOLK_E_00106","C_Maptask","C"},
{"FOLK_E_00107","C_Maptask","C"},
{"FOLK_E_00108","C_Maptask","C"},
{"FOLK_E_00109","C_Maptask","C"},
{"FOLK_E_00110","C_Maptask","C"},
{"FOLK_E_00111","B3_Schichtübergabe_in_einem_Krankenhaus","B"},
{"FOLK_E_00112","B3_Schichtübergabe_in_einem_Krankenhaus","B"},
{"FOLK_E_00113","B3_Schichtübergabe_in_einem_Krankenhaus","B"},
{"FOLK_E_00114","B3_Schichtübergabe_in_einem_Krankenhaus","B"},
{"FOLK_E_00115","B3_Schichtübergabe_in_einem_Krankenhaus","B"},
{"FOLK_E_00116","B3_Schichtübergabe_in_einem_Krankenhaus","B"},
{"FOLK_E_00117","B3_Schichtübergabe_in_einem_Krankenhaus","B"},
{"FOLK_E_00118","B3_Schichtübergabe_in_einem_Krankenhaus","B"},
{"FOLK_E_00119","A8_Tischgespräch","A"},
{"FOLK_E_00120","B5_Unterrichtsstunde_im_Wirtschaftsgymnasium","B"},
{"FOLK_E_00121","B5_Unterrichtsstunde_im_Wirtschaftsgymnasium","B"},
{"FOLK_E_00123","B5_Unterrichtsstunde_im_Wirtschaftsgymnasium","B"},
{"FOLK_E_00124","B5_Unterrichtsstunde_im_Wirtschaftsgymnasium","B"},
{"FOLK_E_00125","B5_Unterrichtsstunde_im_Wirtschaftsgymnasium","B"},
{"FOLK_E_00127","B5_Unterrichtsstunde_im_Wirtschaftsgymnasium","B"},
{"FOLK_E_00133","A2_Gespräch_beim_Umräumen","A"},
{"FOLK_E_00135","B4_Training_in_einer_Hilfsorganisation","B"},
{"FOLK_E_00136","B4_Training_in_einer_Hilfsorganisation","B"},
{"FOLK_E_00138","B4_Training_in_einer_Hilfsorganisation","B"}
};
            
    
    StylesheetFactory ssf = new StylesheetFactory(true);
    HashMap<String,String> Z1 = new HashMap<String, String>();
    HashMap<String,String> Z2 = new HashMap<String, String>();
    
    public CalculatePOSFrequencies(String FOLK_DIRECTORY, String suffix, String session_id) {
        super(FOLK_DIRECTORY, suffix, session_id);
        for (String[] x : ZUORDNUNGEN){
            Z1.put(x[0],x[1]);
            Z2.put(x[0],x[2]);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            CalculatePOSFrequencies cpf = new CalculatePOSFrequencies("U:\\FOLK_DGD_2\\transcripts\\FOLK_MINIMAL_REFERENZ", ".fln", "");
            cpf.go();
            cpf.writeResult("C:\\Users\\Schmidt\\Desktop\\FOLK_QUANT\\POS_FREQUENCIES.xml");
            
        } catch (IOException ex) {
            Logger.getLogger(CalculatePOSFrequencies.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processFile(File f) throws IOException {
        try {
            System.out.println("Processing " + f.getAbsolutePath());
            String countElement = ssf.applyExternalStylesheetToExternalXMLFile("C:\\Users\\Schmidt\\Desktop\\FOLK_QUANT\\CountPOSTags.xsl", f.getAbsolutePath());
            Document doc = IOUtilities.readDocumentFromString(countElement);
            Element e = doc.detachRootElement();
            e.setAttribute("Gesprächssubtyp", Z1.get(e.getAttributeValue("document").substring(0,12)));
            e.setAttribute("Subkorpus", Z2.get(e.getAttributeValue("document").substring(0,12)));
            resultDocument.getRootElement().addContent(e);
            
        } catch (JDOMException ex) {
            Logger.getLogger(CalculatePOSFrequencies.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(CalculatePOSFrequencies.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CalculatePOSFrequencies.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(CalculatePOSFrequencies.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(CalculatePOSFrequencies.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
