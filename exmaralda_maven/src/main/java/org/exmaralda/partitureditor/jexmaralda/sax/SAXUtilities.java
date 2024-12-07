/*
 * SAXUtilities.java
 *
 * Created on 25. Januar 2002, 16:26
 */

package org.exmaralda.partitureditor.jexmaralda.sax;

/**
 *
 * @author  Thomas
 * @version 
 */
public class SAXUtilities {

    // elements that appear in all transcriptions (head elements)
    static final int HEAD = 1;
    static final int META_INFORMATION = 21;
    static final int UD_META_INFORMATION = 2;
    static final int REFERENCED_FILE = 3;
    static final int UD_INFORMATION = 4;
    static final int COMMENT = 11;
    static final int TRANSCRIPTION_CONVENTION = 12;
    static final int ABBREVIATION = 13;
    static final int UD_SPEAKER_INFORMATION = 14;;
    static final int PROJECT_NAME = 16;
    static final int TRANSCRIPTION_NAME = 17;
    static final int SPEAKER = 5;
    static final int SEX = 6;
    static final int LANGUAGE = 7;
    static final int TLI = 8;
    static final int UNKNOWN = 100;
    static final int L1 = 18;
    static final int L2 = 19;
    static final int LANGUAGES_USED = 20;

    // elements that appear only in basic transcriptions
    static final int BASIC_TRANSCRIPTION = 0;
    static final int UD_TIER_INFORMATION = 15;
    static final int TIER = 9;
    static final int EVENT = 10;

    // elements that appear only in tier format tables
    static final int TIER_FORMAT_TABLE = 22;
    static final int TIER_FORMAT = 23;
    static final int PROPERTY = 24;
    static final int TIMELINE_ITEM_FORMAT = 25;
    
    // element names that appear only in a segmented transcription
    static final int SEGMENTED_TRANSCRIPTION = 30;
    static final int SEGMENTED_BODY = 31;
    static final int SEGMENTED_TIER = 32;
    static final int TIMELINE_FORK = 33;
    static final int SEGMENTATION = 34;
    static final int ANNOTATION = 35;
    static final int TS = 36;
    static final int ATS = 37;
    static final int NTS = 38;
    static final int TA = 39;
    static final int CONVERSION_INFO = 40;
    static final int BASIC_TRANSCRIPTION_CONVERSION_INFO = 41;
    static final int CONVERSION_TIMELINE = 42;
    static final int CONVERSION_TLI = 43;
    static final int CONVERSION_TIER = 44;
    
    // element names that appear in tasx data
    static final int TASX_SESSION = 100;
    static final int TASX_META = 101;
    static final int TASX_DESC = 102;
    static final int TASX_NAME = 103;
    static final int TASX_VAL = 104;
    static final int TASX_LAYER = 105;
    static final int TASX_EVENT = 10;
    
    
    
    /** Creates new SAXUtilities */
    public SAXUtilities() {
    }
    
    static int getIDForElementName(String elementName){
        if (elementName.equals("basic-transcription")){return BASIC_TRANSCRIPTION;}
        else if (elementName.equals("head")){return HEAD;}
        else if (elementName.equals("meta-information")){return META_INFORMATION;}
        else if (elementName.equals("ud-meta-information")){return UD_META_INFORMATION;}
        else if (elementName.equals("referenced-file")){return REFERENCED_FILE;}
        else if (elementName.equals("ud-information")){return UD_INFORMATION;}
        else if (elementName.equals("comment")){return COMMENT;}
        else if (elementName.equals("transcription-convention")){return TRANSCRIPTION_CONVENTION;}
        else if (elementName.equals("abbreviation")){return ABBREVIATION;}
        else if (elementName.equals("ud-speaker-information")){return UD_SPEAKER_INFORMATION;}
        else if (elementName.equals("ud-tier-information")){return UD_TIER_INFORMATION;}
        else if (elementName.equals("project-name")){return PROJECT_NAME;}
        else if (elementName.equals("transcription-name")){return TRANSCRIPTION_NAME;}
        else if (elementName.equals("speaker")){return SPEAKER;}
        else if (elementName.equals("sex")){return SEX;}
        else if (elementName.equals("language")){return LANGUAGE;}
        else if (elementName.equals("tli")){return TLI;}
        else if (elementName.equals("tier")){return TIER;}
        else if (elementName.equals("event")){return EVENT;}
        else if (elementName.equals("l1")){return L1;}
        else if (elementName.equals("l2")){return L2;}
        else if (elementName.equals("languages-used")){return LANGUAGES_USED;}
        else if (elementName.equals("tierformat-table")){return TIER_FORMAT_TABLE;}
        else if (elementName.equals("tier-format")){return TIER_FORMAT;}
        else if (elementName.equals("property")){return PROPERTY;}
        else if (elementName.equals("timeline-item-format")){return TIMELINE_ITEM_FORMAT;}
        else if (elementName.equals("session")){return TASX_SESSION;}
        else if (elementName.equals("desc")){return TASX_DESC;}
        else if (elementName.equals("meta")){return TASX_META;}
        else if (elementName.equals("name")){return TASX_NAME;}
        else if (elementName.equals("val")){return TASX_VAL;}
        else if (elementName.equals("layer")){return TASX_LAYER;}

        else if (elementName.equals("segmented-transcription")) {return SEGMENTED_TRANSCRIPTION;}
        else if (elementName.equals("segmented-body")) {return SEGMENTED_BODY;}
        else if (elementName.equals("segmented-tier")) {return SEGMENTED_TIER;}
        else if (elementName.equals("timeline-fork")) {return TIMELINE_FORK;}
        else if (elementName.equals("segmentation")) {return SEGMENTATION;}
        else if (elementName.equals("annotation")) {return ANNOTATION;}
        else if (elementName.equals("ts")) {return TS;}
        else if (elementName.equals("ats")) {return ATS;}
        else if (elementName.equals("nts")) {return NTS;}
        else if (elementName.equals("ta")) {return TA;}
        else if (elementName.equals("conversion-info")) {return CONVERSION_INFO;}
        else if (elementName.equals("basic-transcription-conversion-info")) {return BASIC_TRANSCRIPTION_CONVERSION_INFO;}
        else if (elementName.equals("conversion-timeline")) {return CONVERSION_TIMELINE;}
        else if (elementName.equals("conversion-tli")) {return CONVERSION_TLI;}
        else if (elementName.equals("conversion-tier")) {return CONVERSION_TIER;}

        return UNKNOWN;
    }
            

}
