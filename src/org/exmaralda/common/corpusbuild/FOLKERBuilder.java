/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.helpers.BasicTranscription2COMA;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader;
import org.exmaralda.partitureditor.jexmaralda.segment.cGATMinimalSegmentation;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class FOLKERBuilder {

    File resultFile;
    Vector<File> folkerFiles;
    Hashtable<String,File[]> communicationsWithExmaraldaFiles = new Hashtable<String, File[]>();
    Hashtable<String,File> communicationsWithFOLKERFiles = new Hashtable<String, File>();
    Hashtable<String, Element> speakers = new Hashtable<String, Element>();
    String directory;
    boolean separateDirectory;
    boolean writeBasic;
    int countSegmentationErrors = 0;

    private Vector<SearchListenerInterface> listenerList = new Vector<SearchListenerInterface>();

    File directoryFile;

    public FOLKERBuilder(File resultFile, Vector<File> folkerFiles, String directory, boolean separateDirectory, boolean writeBasic) throws IOException {
        this.resultFile = resultFile;
        this.folkerFiles = folkerFiles;
        this.directory = directory;
        this.separateDirectory = separateDirectory;
        this.writeBasic = writeBasic;

        if (separateDirectory){
            directoryFile = new File(resultFile.getParentFile().getAbsolutePath() + File.separator + directory);
            if (!directoryFile.exists()){
                directoryFile.mkdir();
            } else if (!directoryFile.isDirectory()){
                throw new IOException(directory + " is not a directory.");
            }
        }

    }

    public int getCountSegmentationErrors() {
        return countSegmentationErrors;
    }
   
    

    public void addSearchListener(SearchListenerInterface sli) {
        listenerList.addElement(sli);
    }

    protected void fireCorpusInit(double progress, String message) {
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listenerList.size() - 1; i >= 0; i -= 1) {
            SearchEvent se = new SearchEvent(SearchEvent.CORPUS_INIT_PROGRESS, progress, message);
            listenerList.elementAt(i).processSearchEvent(se);
        }
    }

    public void doBuild() throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException, URISyntaxException{
        // write an empty COMA file
        Document comaDocument = new IOUtilities().readDocumentFromResource("/org/exmaralda/common/resources/EmptyComaDocument.coma");
        comaDocument.getRootElement().setAttribute("Name", resultFile.getName().substring(0,resultFile.getName().lastIndexOf(".")));
        IOUtilities.writeDocumentToLocalFile(resultFile.getAbsolutePath(), comaDocument);


        // convert
        int count = 0;
        for (File folkerFile : folkerFiles){
            // inform listeners of progress
            count++;
            double prog = (double)count/(double)(folkerFiles.size()*2);
            fireCorpusInit(prog, "Converting " + folkerFile.getName());

            String fileNameWithSuffix = folkerFile.getName();
            String fileNameWithoutSuffix = fileNameWithSuffix.substring(0, fileNameWithSuffix.lastIndexOf("."));

            // read the FOLKER file as a Basic Transcription
            BasicTranscription bt = org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.readXMLAsBasicTranscription(folkerFile);
            // added 09-05-2011
            bt.getBody().stratify(Tier.STRATIFY_BY_DISTRIBUTION);

            // find a suitable name for the transcription (will become the communication name)
            String transcriptionName = fileNameWithoutSuffix;
            int number = 2;
            while (communicationsWithExmaraldaFiles.containsKey(transcriptionName)){
                transcriptionName = fileNameWithoutSuffix + Integer.toString(number);
                number++;
            }
            bt.getHead().getMetaInformation().setTranscriptionName(transcriptionName);
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' hh:mm:ss z");
            bt.getHead().getMetaInformation().setComment("Imported from " + folkerFile.getAbsolutePath() + " on " + sdf.format(cal.getTime()));
            bt.getHead().getMetaInformation().getUDMetaInformation().setAttribute("FOLKER-Original", folkerFile.getAbsolutePath());
            
            // added 18-01-2012
            for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
                Tier tier = bt.getBody().getTierAt(pos);
                if (tier.getSpeaker()==null){
                    tier.setType("d");
                }
            }

            cGATMinimalSegmentation segmentor = new cGATMinimalSegmentation();
            SegmentedTranscription st = null;
            try {
                st = segmentor.BasicToSegmented(bt);
            } catch (FSMException ex) {
                System.out.println("Segmentation error");
                st = bt.toSegmentedTranscription();
                countSegmentationErrors++;
            }
            

            File thisDirectoryFile = folkerFile.getParentFile();
            if (separateDirectory){
                thisDirectoryFile = directoryFile;
            }

            String stName = new File(thisDirectoryFile, transcriptionName + ".exs").getAbsolutePath();
            System.out.println("+++ Writing segmented transcription to " + stName);

            if (this.writeBasic){
                String btName = new File(thisDirectoryFile, transcriptionName + ".exb").getAbsolutePath();
                System.out.println("+++ Writing basic transcription to " + btName);
                bt.writeXMLToFile(btName, "none");
                st.setEXBSource(btName);
                File[] files = new File[2];
                files[0] = new File(stName);
                files[1] = new File(btName);
                communicationsWithExmaraldaFiles.put(transcriptionName, files);
            } else {
                File[] files = new File[1];
                files[0] = new File(stName);
                communicationsWithExmaraldaFiles.put(transcriptionName, files);
            }
            st.writeXMLToFile(stName, "none");
            communicationsWithFOLKERFiles.put(transcriptionName, folkerFile);
        }

        // Now build the COMA Document
        Vector<Element> allElements = new Vector<Element>();
        for (String name : communicationsWithExmaraldaFiles.keySet()){
            count++;
            double prog = (double)count/(double)(folkerFiles.size()*2);
            fireCorpusInit(prog, "Processing " + name);

            File[] files = communicationsWithExmaraldaFiles.get(name);
            File btFile = null;
            if (writeBasic){
                btFile = files[1];
            } else {
                btFile = File.createTempFile("temp", "exb", directoryFile);
                new SegmentedTranscriptionSaxReader().readFromFile(files[0].getAbsolutePath()).toBasicTranscription().writeXMLToFile(btFile.getAbsolutePath(), "none");
            }
            Vector<Element> elements = BasicTranscription2COMA.generateComaElements(btFile, resultFile);
            Element communicationElement = elements.elementAt(0);
            Element transcriptionElement = communicationElement.getChild("Transcription");
            /* <Transcription Id="ID82BA18FA-0BA9-6D94-2063-30EFFD6431EB">
                <Name>Oettinger</Name>
                <Filename>temp25355exb</Filename>
                <NSLink>exmaralda/temp25355exb</NSLink>
                <Description>
                    <Key Name="segmented">false</Key>
                </Description>
                <Availability>
                    <Available>false</Available>
                    <ObtainingInformation/>
                </Availability>
            </Transcription> */
            if (writeBasic){
                // the existing one is OK, need to add the segmented one
                Element otherElement = (Element)(transcriptionElement.clone());
                otherElement.setAttribute("Id", new GUID().makeID());
                otherElement.getChild("Filename").setText(otherElement.getChild("Filename").getText().replace(".exb", ".exs"));
                otherElement.getChild("NSLink").setText(otherElement.getChild("NSLink").getText().replace(".exb", ".exs"));
                otherElement.getChild("Description").getChild("Key").setText("true");
                communicationElement.addContent(otherElement);
            } else {
                // replace the existing one with the segmented fucker
                transcriptionElement.getChild("Filename").setText(files[0].getName());
                String path = "";
                int index = transcriptionElement.getChild("NSLink").getText().lastIndexOf("/");
                if (index >= 0){
                    path = transcriptionElement.getChild("NSLink").getText().substring(0, index +1);
                }
                transcriptionElement.getChild("NSLink").setText(path + files[0].getName());
                transcriptionElement.getChild("Description").getChild("Key").setText("true");
            }

            //allElements.addAll(elements);

             /* <File Id="FIDA4AF0D8C-F3AB-38C2-E672-F423792E202D">
                <filename>Zeitplan.pdf</filename>
                <mimetype>application/pdf</mimetype>
                <relPath>file:/C:/Dokumente%20und%20Einstellungen/thomas/Desktop/Zeitplan.pdf</relPath>
                <absPath>C:\Dokumente und Einstellungen\thomas\Desktop\Zeitplan.pdf</absPath>
                <Description>
                  <Key Name="Wurst">Salami</Key>
                </Description>
              </File>            */

            Element attachedFileElement = new Element("File");
            attachedFileElement.setAttribute("Id", new GUID().makeID());
            File ff = communicationsWithFOLKERFiles.get(communicationElement.getAttributeValue("Name"));
            Element fn = new Element("filename");
            fn.setText(ff.getName());
            attachedFileElement.addContent(fn);
            Element mt = new Element("mimetype");
            mt.setText("application/xml");
            attachedFileElement.addContent(mt);
            Element rp = new Element("relPath");
            System.out.println("=== Relativizing " + ff.toURI() + " relative to " + resultFile.toURI());
            rp.setText((resultFile.getParentFile().toURI().relativize(ff.toURI())).toString());
            attachedFileElement.addContent(rp);
            Element ap = new Element("absPath");
            ap.setText(ff.getAbsolutePath());
            attachedFileElement.addContent(ap);
            communicationElement.addContent(attachedFileElement);


            allElements.add(communicationElement);
            for (int i=1; i<elements.size(); i++){
                Element speakerElement = elements.elementAt(i);
                String sigle = speakerElement.getChildText("Sigle");
                String id = speakerElement.getAttributeValue("Id");
                if (speakers.containsKey(sigle)) {
                        // i.e. there has already been a speaker with this sigle
                        Element existingSpeaker = speakers.get(sigle);
                        String hisID = existingSpeaker.getAttributeValue("Id");
                        String xp2 = "//Person[text()='" + id + "']";
                        Element personElement = (Element) XPath.newInstance(xp2)
                                        .selectSingleNode(communicationElement);
                        personElement.setText(hisID);
                } else {
                        speakers.put(sigle, speakerElement);
                        speakerElement.detach();
                        allElements.add(speakerElement);
                }

            }
            if (!writeBasic){
                btFile.delete();
            }
        }

        for (Element e : allElements){
            e.detach();
            comaDocument.getRootElement().getChild("CorpusData").addContent(e);
        }
        IOUtilities.writeDocumentToLocalFile(resultFile.getAbsolutePath(), comaDocument);


    }



}
