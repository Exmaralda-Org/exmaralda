package org.exmaralda.partitureditor.jexmaralda;

import java.io.*;
import org.exmaralda.partitureditor.interlinearText.RTFUtilities;
/*
 * Speaker.java
 *
 * Created on 6. Februar 2001, 11:20
 */



/* Revision History
 *  0   06-Feb-2001 Creation according to revision 0 of 'exmaralda-time-transcription.dtd'
 *                  and 'exmaralda-segment-transcription.dtd'
 */

/**
 *
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de) 
 * @version 1
 */

public class Speaker extends Object {

    private String id;
    private String abbreviation;
    private char sex;
    private Languages languagesUsed;
    private Languages l1;
    private Languages l2;
    private UDInformationHashtable udSpeakerInformation;
    private String comment;
    
    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new Speaker */
    public Speaker() {
        id = new String();
        abbreviation = new String();
        sex = 'm';
        languagesUsed = new Languages();
        l1 = new Languages();
        l2 = new Languages();
        udSpeakerInformation = new UDInformationHashtable();
        comment = new String();
    }
    
    /** Creates new Speaker with id i, abbreviation a, sex s, languages used langU, l1 lang1,
    *   l2 lang2, udSpeakerInformation udi and comment c */
    public Speaker(String i, String a, char s, 
                   Languages langU, Languages lang1, Languages lang2, 
                   UDInformationHashtable udi, String c){
         id = new String(i);
         abbreviation = new String(a);
         sex = s;
         languagesUsed = langU;
         l1 = lang1;
         l2 = lang2;
         udSpeakerInformation = udi;
         comment = new String(c);
   }
    
    /** returns a copy of this speaker */
    public Speaker makeCopy(){
        return new Speaker(this.getID(), this.getAbbreviation(), this.getSex(), this.getLanguagesUsed(), 
                        this.getL1(), this.getL2(), this.getUDSpeakerInformation().makeCopy(), this.getComment());
    }

    // ********************************************
    // ********** GET AND SET METHODS *************
    // ********************************************

    /** returns the id of this speaker */
    public String getID(){
        return id;
    }
    
    /** sets the id of this speaker to the specified value */
    public void setID(String i){
        id = new String(i);
    }

    /** returns the abbreviation of this speaker */
    public String getAbbreviation(){
        return abbreviation;
    }
    
    /** sets the abbreviation of this speaker to the specified value */
    public void setAbbreviation(String abb){
        abbreviation = new String(abb);
    }

    /** returns the sex of this speaker */
    public char getSex(){
        return sex;
    }
    
    /** sets the sex of this speaker to the specified value */
    public void setSex(char s){
        sex = s;
    }
    
    /** returns the languages used by this speaker */
    public Languages getLanguagesUsed(){
        return languagesUsed;
    }
    
    /** sets the languages used by this speaker to the specified value */
    public void setLanguagesUsed(Languages l){
        languagesUsed = l;
    }
    
    /** returns the first languages of this speaker */
    public Languages getL1(){
        return l1;
    }
    
    /** sets the first languages of this speaker to the specified value */
    public void setL1(Languages l){
        l1 = l;
    }
    
    /** returns the second languages of this speaker */
    public Languages getL2(){
        return l2;
    }
    
    /** sets the second languages of this speaker to the specified value */
    public void setL2(Languages l){
        l2 = l;
    }

    /** returns the user defined information of this speaker */
    public UDInformationHashtable getUDSpeakerInformation(){
        return udSpeakerInformation;
    }
    
    /** sets the user definde information of this speaker to the specified value */
    public void setUDSpeakerInformation(UDInformationHashtable i){
        udSpeakerInformation = i;
    }
    
    /** returns the comment for this speaker */
    public String getComment(){
        return comment;
    }
    
    /** sets the comment for this speaker to the specified value */
    public void setComment(String c){
        comment = new String(c);
    }

    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************

    /** returns the speaker as an XML element &lt;speaker&gt; as
     *  specified in the corresponding dtds */
    public String toXML(){
        StringBuffer sb=new StringBuffer();
        sb.append("<speaker id=\"" + id + "\">\n");
        sb.append("<abbreviation>" + StringUtilities.checkCDATA(abbreviation) + "</abbreviation>\n");
        sb.append("<sex value=\"" + sex + "\"/>\n");
        sb.append("<languages-used>" + languagesUsed.toXML() + "</languages-used>\n");
        sb.append("<l1>" + l1.toXML() + "</l1>\n");
        sb.append("<l2>" + l2.toXML() + "</l2>\n");
        sb.append("<ud-speaker-information>\n" + udSpeakerInformation.toXML() + "</ud-speaker-information>\n");
        sb.append("<comment>" + StringUtilities.checkCDATA(comment) + "</comment>\n");
        sb.append("</speaker>\n");
        return sb.toString();
    }
    

    // ********************************************
    // ********** HTML OUTPUT **********************
    // ********************************************
/*    public String toHTML(){
        StringBuffer sb = new StringBuffer();
        sb.append("<tr>");
        sb.append("<td bgcolor=\"#c0c0c0\" valign=\"top\">");
        sb.append("<a name=\"" + getID() + "\"/><big><b>" + StringUtilities.checkHTML(getAbbreviation()) + "</b></big>");
        sb.append("</td>");
        sb.append("<td>");
        sb.append("<b>Sex:&nbsp;</b>" + sex + "<br/>");
        if (languagesUsed.getNumberOfLanguages()>0){
            sb.append("<b>Languages used:&nbsp;</b>" + StringUtilities.checkHTML(languagesUsed.getLanguagesString()) + "<br/>");
        }
        if (l1.getNumberOfLanguages()>0){
            sb.append("<b>L1:&nbsp;</b>" + StringUtilities.checkHTML(l1.getLanguagesString()) + "<br/>");
        }
        if (l2.getNumberOfLanguages()>0){
            sb.append("<b>L2:&nbsp;</b>" + StringUtilities.checkHTML(l2.getLanguagesString()) + "<br/>");
        }
        if (comment.length()>0){
            sb.append("<b>Comment:&nbsp;</b>" + StringUtilities.checkHTML(comment,false) + "<br/>");
        }
        if (getUDSpeakerInformation().size()>0){
            sb.append("<p><u>User defined attributes</u><br/>");
            sb.append(getUDSpeakerInformation().toHTML());
            sb.append("</p>");
        }        
        sb.append("</td>");
        sb.append("</tr>");
        return sb.toString();
    }*/
   
    // ********************************************
    // ********** RTF OUTPUT **********************
    // ********************************************
    public String toRTF(){
        StringBuffer sb = new StringBuffer();
        sb.append("\\par");
        //sb.append("{\\fs24\\b\\ul " + StringUtilities.toANSI(getAbbreviation()) + "}\\par");
        sb.append("{\\fs24\\b\\ul " + RTFUtilities.toEscapedRTFString(getAbbreviation()) + "}\\par");
        sb.append("{\\fs20\\b \\tab Sex: }");
        sb.append("{\\plain\\fs20 " + sex + "}\\par");
        if (languagesUsed.getNumberOfLanguages()>0){
            sb.append("{\\fs20\\b \\tab Languages used: }");
            //sb.append("{\\plain\\fs20 " + StringUtilities.toANSI(languagesUsed.getLanguagesString()) + "}\\par");
            sb.append("{\\plain\\fs20 " + RTFUtilities.toEscapedRTFString(languagesUsed.getLanguagesString()) + "}\\par");
        }
        if (l1.getNumberOfLanguages()>0){
            sb.append("{\\fs20\\b \\tab L1: }");
            //sb.append("{\\plain\\fs20 " + StringUtilities.toANSI(l1.getLanguagesString()) + "}\\par");
            sb.append("{\\plain\\fs20 " + RTFUtilities.toEscapedRTFString(l1.getLanguagesString()) + "}\\par");
        }
        if (l2.getNumberOfLanguages()>0){
            sb.append("{\\fs20\\b \\tab L2: }");
            //sb.append("{\\plain\\fs20 " + StringUtilities.toANSI(l2.getLanguagesString()) + "}\\par");
            sb.append("{\\plain\\fs20 " + RTFUtilities.toEscapedRTFString(l2.getLanguagesString()) + "}\\par");
        }
        if (comment.length()>0){
            sb.append("{\\fs20\\b \\tab Comment: }");
            //sb.append("{\\plain\\fs20 " + StringUtilities.toANSI(getComment()) + "}\\par");
            sb.append("{\\plain\\fs20 " + RTFUtilities.toEscapedRTFString(getComment()) + "}\\par");
        }
        if (getUDSpeakerInformation().size()>0){
            sb.append("{\\plain\\par\\tab\\fs20\\ul User defined attributes: }\\par");
            sb.append(getUDSpeakerInformation().toRTF());
            sb.append("\\par");
        }        
        return sb.toString();
    }

    
}