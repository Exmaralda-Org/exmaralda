/*
 * SpeakerContribution.java
 *
 * Created on 27. August 2002, 14:46
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;
/**
 *
 * @author  Thomas
 */
public class SpeakerContribution implements XMLable {
    
    
    String tierReference;
    private String speaker;
    private TimedSegment main;
    private Vector dependents;
    private Vector annotations;
    
    /** Creates a new instance of SpeakerContribution */
    public SpeakerContribution() {
        super();
        dependents = new Vector();
        annotations = new Vector();
    }
        
    /** returns speakerID */
    public String getSpeaker(){
        return speaker;
    }

    /** sets speaker id to value of s */
    public void setSpeaker(String s){
        speaker=s;
    }
    
    public String getTierReference(){
        return tierReference;
    }
    
    public void setTierReference(String t){
        tierReference = t;
    }

    public TimedSegment getMain(){
        return main;
    }
    
    public void setMain(TimedSegment ts){
        main = ts;
    }
    
    public AbstractSegmentVector getDependentAt(int pos){
        return (AbstractSegmentVector)(dependents.elementAt(pos));
    }
    
    public AbstractSegmentVector getAnnotationAt(int pos){
        return (AbstractSegmentVector)(annotations.elementAt(pos));
    }
    
    public int getNumberOfDependents(){
        return dependents.size();
    }
    
    public int getNumberOfAnnotations(){
        return annotations.size();
    }

    public void addDependent(AbstractSegmentVector asv){
        dependents.add(asv);
    }
    
    public void addAnnotation(AbstractSegmentVector asv){
        annotations.add(asv);
    }
    
    public String toXML(){
        StringBuffer sb = new StringBuffer();
        String [][] atts = {{"tierref", getTierReference()}};
        if (getSpeaker()!=null){
            String [][] atts2 = {{"speaker", getSpeaker()}, {"tierref", getTierReference()}};
            atts = atts2;
        }
        sb.append(StringUtilities.makeXMLOpenElement("speaker-contribution", atts));        
        sb.append(StringUtilities.makeXMLOpenElement("main",null));
        sb.append(main.toXML());
        sb.append(StringUtilities.makeXMLCloseElement("main"));
        for (int pos=0; pos<dependents.size(); pos++){
            AbstractSegmentVector asv = (AbstractSegmentVector)(dependents.elementAt(pos));
            String [][] atts2 = {{"name", asv.getName()}, {"tierref", asv.getTierReference()}};
            sb.append(StringUtilities.makeXMLOpenElement("dependent",atts2));
            for (int pos2=0; pos2<asv.size(); pos2++){
                sb.append(((XMLable)(asv.elementAt(pos2))).toXML());
            }
            sb.append(StringUtilities.makeXMLCloseElement("dependent"));
        }
        for (int pos=0; pos<annotations.size(); pos++){
            AbstractSegmentVector asv = (AbstractSegmentVector)(annotations.elementAt(pos));
            String [][] atts2 = {{"name", asv.getName()}, {"tierref", asv.getTierReference()}};
            sb.append(StringUtilities.makeXMLOpenElement("annotation",atts2));
            for (int pos2=0; pos2<asv.size(); pos2++){
                sb.append(((XMLable)(asv.elementAt(pos2))).toXML());
            }
            sb.append(StringUtilities.makeXMLCloseElement("annotation"));
        }
        sb.append(StringUtilities.makeXMLCloseElement("speaker-contribution"));        
        return sb.toString();
    }
    
    public String toHTML(Speakertable st){
        StringBuffer sb = new StringBuffer();
        sb.append("<tr>");
        // speaker abbrev
        sb.append("<a name=\"" + main.getStart() + "\"/>");
        sb.append("<td valign=\"top\">");
        if (main!=null) {sb.append("<a name=\"" + main.getStart() + "\"/>");}
        sb.append("<span ");
        sb.append("class=\"ROW-LABEL\">");
        if (getSpeaker()!=null){
            try {sb.append(st.getSpeakerWithID(getSpeaker()).getAbbreviation());} catch (JexmaraldaException je){}

        }
        sb.append("</span>");
        sb.append("</td>");
        // text
        sb.append("<td valign=\"top\">");

        // dependents?
        if (dependents.size()>0){sb.append("[");}
        for (int pos=0; pos<dependents.size(); pos++){
            AbstractSegmentVector asv = (AbstractSegmentVector)(dependents.elementAt(pos));
            for (int pos2=0; pos2<asv.size(); pos2++){
                Describable d = (Describable)(asv.elementAt(pos2));
                sb.append("<span class=\"" + asv.getTierReference() + "\">");
                sb.append(StringUtilities.checkHTML(d.getDescription(), false));
                if (pos2<asv.size()-1) {sb.append(" | ");}
                sb.append("</span>");
            }
            if (pos<dependents.size()-1) {sb.append("<br/>");}
        }
        if (dependents.size()>0){sb.append("]<br/>");}
        
        // main
        sb.append("<span class=\"" + getTierReference() + "\">");
        sb.append(StringUtilities.checkHTML(main.getDescription(), false));
        sb.append("</span>");
        
        //annotations??
        if (annotations.size()>0){sb.append("<br/>{");}
        for (int pos=0; pos<annotations.size(); pos++){
            AbstractSegmentVector asv = (AbstractSegmentVector)(annotations.elementAt(pos));
            for (int pos2=0; pos2<asv.size(); pos2++){
                Describable d = (Describable)(asv.elementAt(pos2));
                sb.append("<span class=\"" + asv.getTierReference() + "\">");
                sb.append(StringUtilities.checkHTML(d.getDescription(), false));
                if (pos2<asv.size()-1) {sb.append(" | ");}
                sb.append("</span>");
            }
            if (pos<annotations.size()-1) {sb.append("<br/>");}
        }
        if (annotations.size()>0){sb.append("}");}

        sb.append("</td>");
        sb.append("</tr>");
        return sb.toString();
    }
    
}
