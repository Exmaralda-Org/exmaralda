/*
 * StringConstants.java
 *
 * Created on 7. Februar 2001, 17:57
 */



package org.exmaralda.partitureditor.jexmaralda;

/**
 *
 * @author  Thomas
 * @version 
 */
class StringConstants extends Object {

    static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    
    static String XML_DOCTYPE_AG = "<!DOCTYPE AGSet SYSTEM \"ag.dtd\">";

    static String XML_DOCTYPE_BASIC_TRANSCRIPTION = "<!DOCTYPE basic-transcription SYSTEM \"basic-transcription.dtd\">\n";
    static String XML_PUBLIC_DOCTYPE_BASIC_TRANSCRIPTION = "<!DOCTYPE basic-transcription PUBLIC \"-//thomas_schmidt//EXMARaLDA_Basic_Transcription//DE\" \"http://www.rrz.uni-hamburg.de/exmaralda/data/Exmaralda/basic-transcription.dtd\">\n";
    
    static String XML_DOCTYPE_PARTITUR_TABLE_TRANSCRIPTION = "<!DOCTYPE partitur-table-transcription SYSTEM \"partitur-table-transcription.dtd\">\n";
    
    static String XML_DOCTYPE_SEGMENTED_TRANSCRIPTION = "<!DOCTYPE segmented-transcription SYSTEM \"segmented-transcription.dtd\">\n";
    static String XML_PUBLIC_DOCTYPE_SEGMENTED_TRANSCRIPTION = "<!DOCTYPE segmented-transcription PUBLIC \"-//thomas_schmidt//EXMARaLDA_Segmented_Transcription//DE\" \"http://www.rrz.uni-hamburg.de/exmaralda/data/Exmaralda/segmented-transcription.dtd\">\n";
    
    static String XML_DOCTYPE_LIST_TRANSCRIPTION = "<!DOCTYPE list-transcription SYSTEM \"list-transcription.dtd\">\n";
    static String XML_PUBLIC_DOCTYPE_LIST_TRANSCRIPTION = "<!DOCTYPE list-transcription PUBLIC \"-//thomas_schmidt//EXMARaLDA_List_Transcription//DE\" \"http://www.rrz.uni-hamburg.de/exmaralda/data/Exmaralda/list-transcription.dtd\">\n";
    
    static String XML_DOCTYPE_TIERFORMAT_TABLE = "<!DOCTYPE tierformat-table SYSTEM \"tierformat-table.dtd\">\n";  
    static String XML_PUBLIC_DOCTYPE_TIERFORMAT_TABLE = "<!DOCTYPE tierformat-table PUBLIC \"-//thomas_schmidt//EXMARaLDA_tierformat_table//DE\" \"http://www.rrz.uni-hamburg.de/exmaralda/data/Exmaralda/tierformat-table.dtd\">\n";
    
    static String XML_DOCTYPE_WORD_UTTERANCE_SEGMENTATION_INFO_TABLE = "<!DOCTYPE word-utterance-segmentation-info-table SYSTEM \"word-utterance-segmentation-info-table.dtd\">\n";
    
    static String XML_DOCTYPE_SEGMENTED_TO_BASIC_CONVERSION_INFO = "<!DOCTYPE segmented-to-basic-conversion-info SYSTEM \"segmented-to-basic-conversion-info.dtd\">\n";
    
    static String XML_DOCTYPE_SEGMENTED_TO_LIST_CONVERSION_INFO = "<!DOCTYPE segmented-to-list-conversion-info SYSTEM \"segmented-to-list-conversion-info.dtd\">\n";
    
    static String XML_COPYRIGHT_COMMENT ="<!-- (c) http://www.rrz.uni-hamburg.de/exmaralda -->\n";
    
    static String HTML_HEADER = "<HTML>\n<HEAD>\n<META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n";
    
    static String RTF_HEADER = "{\\rtf1\\ansi\\ansicpg1252\\uc0";
    
    static String RTF_TABLE_ROW_DEFINITION = "\\trowd \\trgaph4\\trleft-70\\trkeep \\trbrdrt\\brdrs\\brdrw10\\trbrdrl\\brdrs\\brdrw10\\trbrdrb\\brdrs\\brdrw10\\trbrdrr\\brdrs\\brdrw10";

    /** Creates new StringConstants */
    StringConstants() {
    }


}