/*
 * Contribution.java
 *
 * Created on 23. Juni 2008, 13:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class Contribution implements TimeAssigned, SpeakerAssigned {
    
    public Eventlist eventlist;
    
    /** Creates a new instance of Contribution */
    public Contribution(EventListTranscription elt) {
        eventlist = new Eventlist(elt);
    }
    
    /** called from OrthoNormal to edit transcriptions */
    public static EventListTranscription getTranscriptionForContributionFromOrthoNormalDocument(Document orthoNormalDocument, int index) throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException, JexmaraldaException{
        /*<contribution speaker-reference="HG" start-reference="TLI_479" end-reference="TLI_481" parse-level="2" speaker-dgd-id="FOLK_S_00096">
            <w id="w2521" i="y">schon</w>
            <w id="w2522" i="y">gesagt</w>
            <w id="w2523" i="y">habe</w>
            <time timepoint-reference="TLI_480"/>
            <breathe type="in" length="2"/>
            <pause duration="micro"/>
            <w id="w2524" n="äh" i="n">ähm</w>
        </contribution>*/
        Element root = new Element("folker-transcription");
        root.addContent(new Element("head"));
        Element speakers = root.addContent(new Element("speakers"));
        speakers.addContent(orthoNormalDocument.getRootElement().getChild("speakers").cloneContent());
        Element recording = root.addContent(new Element("recording"));
        recording.setAttribute("path", orthoNormalDocument.getRootElement().getChild("recording").getAttributeValue("path"));
        Element timeline = root.addContent(new Element("timeline"));
        timeline.addContent(orthoNormalDocument.getRootElement().getChild("timeline").cloneContent());
        Element contribution = ((Element) orthoNormalDocument.getRootElement().getChildren("contribution").get(index));
        root.addContent((Element) contribution.clone());
        Document doc = new Document(root);        
        return EventListTranscriptionXMLReaderWriter.readXML(doc, 1);          
    }

    
    public void addEvent(Event e){
        eventlist.addEvent(e);
    }
    
    @Override
    public void setSpeaker(Speaker s) {
        for (Event event : eventlist.getEvents()){
            event.setSpeaker(s);
        }
    }

    @Override
    public Speaker getSpeaker() {
        if (eventlist.getEvents().size()>0){
            return eventlist.getEventAt(0).getSpeaker();
        }
        return null;
    }
    
    @Override
    public void setStartpoint(Timepoint startpoint) {
        if (eventlist.getEvents().size()>0){
            eventlist.getEventAt(0).setStartpoint(startpoint);
        }
    }

    @Override
    public void setEndpoint(Timepoint endpoint) {
        if (eventlist.getEvents().size()>0){
            eventlist.getEventAt(eventlist.getEvents().size()-1).setEndpoint(endpoint);
        }
    }


    @Override
    public Timepoint getStartpoint() {
        if (eventlist.getEvents().size()>0){
            return eventlist.getEventAt(0).getStartpoint();
        }
        return null;
    }


    @Override
    public Timepoint getEndpoint() {
        if (eventlist.getEvents().size()>0){
            return eventlist.getEventAt(eventlist.getEvents().size()-1).getEndpoint();
        }
        return null;
    }
    
    public String getText(){
        StringBuffer sb = new StringBuffer();
        for (Event e : eventlist.getEvents()){
            sb.append(e.getText());
        }
        return sb.toString();
    }
    
    public boolean isOrdered(){
        double lastEndpoint = -1;
        for (Event e : eventlist.getEvents()){
            double startpoint = e.getStartpoint().getTime();
            if ((lastEndpoint>=0) && (startpoint!=lastEndpoint)){
                return false;
            }
            lastEndpoint = e.getEndpoint().getTime();;
        }
        return true;
    }

    public int offsetToCaretPosition(int offset){
        int offsetSum = 0;
        int caretPosition = 1;
        for (Event e : eventlist.getEvents()){
            int l = e.getText().length();
            if (offsetSum + l >offset){
                return caretPosition + (offset - offsetSum);
            }
            caretPosition+=(l+1);
            offsetSum+=l;
        }
        return caretPosition;
    }

    public Element toJDOMElement(Timeline timeline){
        Element contributionElement = new Element("contribution");
        if (getSpeaker()!=null){
            contributionElement.setAttribute("speaker-reference", getSpeaker().getIdentifier());
        }
        contributionElement.setAttribute("start-reference", "TLI_" + Integer.toString(timeline.getTimepoints().indexOf(getStartpoint())));
        contributionElement.setAttribute("end-reference", "TLI_" + Integer.toString(timeline.getTimepoints().indexOf(getEndpoint())));
        //contributionElement.setAttribute("start-reference", "T" + Integer.toString(timeline.getTimepoints().indexOf(getStartpoint())));
        //contributionElement.setAttribute("end-reference", "T" + Integer.toString(timeline.getTimepoints().indexOf(getEndpoint())));
        contributionElement.setAttribute("parse-level", "0");
        for (Event e : eventlist.getEvents()){
            contributionElement.addContent(e.toJDOMElement(timeline));
        }

        return contributionElement;
    }
    
}
