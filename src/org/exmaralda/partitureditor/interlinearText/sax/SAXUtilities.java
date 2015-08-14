/*
 * SAXUtilities.java
 *
 * Created on 21. Maerz 2002, 16:33
 */

package org.exmaralda.partitureditor.interlinearText.sax;

/**
 *
 * @author  Thomas
 * @version 
 */
public class SAXUtilities {

    static final int INTERLINEAR_TEXT = 0;
    static final int LINE = 1;
    static final int IT_BUNDLE = 2;
    static final int IT_LINE = 3;
    static final int IT_LABEL = 4;
    static final int IT_CHUNK = 5;
    static final int RUN = 6;
    static final int SYNC_POINTS = 7;
    static final int SYNC_POINT = 8;
    static final int FORMATS = 9;
    static final int FORMAT = 10;
    static final int PROPERTY = 11;
    static final int UD_INFORMATION = 12;
    static final int UD_ATTRIBUTE = 13;
    static final int LINK = 14;
    static final int ANCHOR = 15;
    static final int PAGE_BREAK = 16;
    static final int FRAME_END = 17;
    
    

    /** Creates new SAXUtilities */
    public SAXUtilities() {
    }
    
    static int getIDForElementName(String elementName){
        if (elementName.equals("interlinear-text")){return INTERLINEAR_TEXT;}
        else if (elementName.equals("line")){return LINE;}
        else if (elementName.equals("it-bundle")){return IT_BUNDLE;}
        else if (elementName.equals("it-line")){return IT_LINE;}
        else if (elementName.equals("it-label")){return IT_LABEL;}
        else if (elementName.equals("it-chunk")){return IT_CHUNK;}
        else if (elementName.equals("run")){return RUN;}
        else if (elementName.equals("sync-points")){return SYNC_POINTS;}
        else if (elementName.equals("sync-point")){return SYNC_POINT;}
        else if (elementName.equals("formats")){return FORMATS;}
        else if (elementName.equals("format")){return FORMAT;}
        else if (elementName.equals("property")){return PROPERTY;}
        else if (elementName.equals("ud-information")){return UD_INFORMATION;}
        else if (elementName.equals("ud-attribute")){return UD_ATTRIBUTE;}
        else if (elementName.equals("link")){return LINK;}
        else if (elementName.equals("anchor")){return ANCHOR;}
        else if (elementName.equals("page-break")){return PAGE_BREAK;}
        else if (elementName.equals("frame-end")){return FRAME_END;}
        return -1;
    }
    

}
