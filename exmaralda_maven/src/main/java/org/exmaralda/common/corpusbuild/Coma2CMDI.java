/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.common.corpusbuild;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.jdom.*;
import org.jdom.xpath.*;
import org.exmaralda.coma.helpers.ISOLanguageCodeHelper;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;

/**
 *
 * @author Z2
 */
public class Coma2CMDI {

    private String comaFile;
    private String cmdiFile;
    private String xsltFile;
    private Document cmdi;

    public Coma2CMDI(String comaFile, String cmdiFile, String xsltFile) {

        this.comaFile = comaFile;
        this.cmdiFile = cmdiFile;
        this.xsltFile = xsltFile;

    }

    void transform() throws JDOMException, IOException {

        //EXMARaLDAs "transformer class" - true für 2.0
        StylesheetFactory ssf = new StylesheetFactory(true);
        try {
            //the first transformation, resulting in a String...
            String firstCMDI = ssf.applyExternalStylesheetToExternalXMLFile(xsltFile, comaFile);
            //...from which we build a Document
            SAXBuilder builder = new SAXBuilder();
            StringReader in = new StringReader(firstCMDI);
            cmdi = builder.build(in);
            //set the namespace
            Namespace ns = Namespace.getNamespace("clarin", "http://www.clarin.eu/cmd/");


            //Replace variable CORPUS_NAME
            //in Header (MdSelfLink, ResourceRef), HZSKCorpusMetadata (Name, ID))
            XPath xURLs = XPath.newInstance("//clarin:MdSelfLink | //clarin:ResourceRef");
            xURLs.addNamespace(ns);

            List allURLs = xURLs.selectNodes(cmdi);

            String[] splitFile = comaFile.split("\\\\");
            String comaFileName = splitFile[splitFile.length - 1].replace(".coma", "");

            for (Object o : allURLs) {

                Element url = (Element) o;
                String selfLink = url.getText();
                url.setText(selfLink.replace("CORPUS_NAME", comaFileName));

            }

            //Replace language codes with names
            //in
            XPath xLanguageNames = XPath.newInstance("");
            xLanguageNames.addNamespace(ns);
            List allLanguageNames = xLanguageNames.selectNodes(cmdi);

            for (Object o : allLanguageNames) {

                Element languageName = (Element) o;
                languageName.setText(ISOLanguageCodeHelper.getLanguageName(languageName.getText()));
            }

            //Replace language names with codes
            //in DocumentationLanguages
            XPath xLanguageCodes = XPath.newInstance("//clarin:DocumentationLanguages/descendant::clarin:iso-639-3-code");
            xLanguageCodes.addNamespace(ns);
            List allLanguageCodes = xLanguageCodes.selectNodes(cmdi);

            for (Object o : allLanguageCodes) {

                Element languageCode = (Element) o;
                languageCode.setText(ISOLanguageCodeHelper.getLanguageCode(languageCode.getText()));
            }


            //Resources
            //add value of mimetype attribute to resourceType elements
            XPath xResourceTypes = XPath.newInstance("//clarin:ResourceType");
            xResourceTypes.addNamespace(ns);

            List allResourceTypes = xResourceTypes.selectNodes(cmdi);

            for (Object o : allResourceTypes) {

                Element resourceType = (Element) o;
                FileNameMap fileNameMap = URLConnection.getFileNameMap();
                String filename = resourceType.getAttributeValue("mimetype").toLowerCase();
                String mimetype = fileNameMap.getContentTypeFor(filename);
                if (mimetype != null) {
                    resourceType.setAttribute("mimetype", mimetype);
                } else if (filename.endsWith("exb") || filename.endsWith("exs")) {
                    resourceType.setAttribute("mimetype", "text/xml");
                } else {
                    resourceType.setAttribute("mimetype", "unknown");
                }
            }

         
            //Language


            //hzsk-corpus-materials
            //file size for the Size-elements
            XPath xMediaFiles = XPath.newInstance("//clarin:hzsk-corpus-materials/descendant::clarin:MediaFile");
            xMediaFiles.addNamespace(ns);
            List allFiles = xMediaFiles.selectNodes(cmdi);

            for (Object o : allFiles) {

                Element mediafile = (Element) o;

                File asocFile = new File(mediafile.getChild("ResourceLink", ns).getText());
                //es wird jetzt nicht sehr genau, aber das taugt...
                Element size = mediafile.getChild("Size", ns);
                size.addContent(String.valueOf(asocFile.length()));
            }

          
            


            //write CMDI-file
            Writer fileWriter = new FileWriter(cmdiFile);

            XMLOutputter serializer = new XMLOutputter();
            serializer.output(cmdi, fileWriter);


        } catch (SAXException ex) {
            Logger.getLogger(Coma2CMDI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Coma2CMDI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(Coma2CMDI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(Coma2CMDI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        try {

            if (args.length != 3) {
                System.out.println("Usage: Coma-file CMDI-file XSLT-file");
            }
            Coma2CMDI transformer = new Coma2CMDI(args[0], args[1], args[2]);
            transformer.transform();
            System.exit(0);

        } catch (JDOMException ex) {
            Logger.getLogger(AddRecordingDurationsInComa.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AddRecordingDurationsInComa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
