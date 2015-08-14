/*
 * PraatUnicodeMapping.java
 *
 * Created on 6. Februar 2004, 15:46
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.util.*;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;

/**
 *
 * @author  thomas
 */
public class PraatUnicodeMapping {
    
    static String PATH_TO_MAPPING_FILE = "/org/exmaralda/partitureditor/jexmaralda/convert/PraatUnicodeMapping.xml";
    public Hashtable praatUnicode;
    public Hashtable unicodePraat;
    
    /** Creates a new instance of PraatUnicodeMapping */
    public PraatUnicodeMapping() {
        praatUnicode = new Hashtable();
        unicodePraat = new Hashtable();
    }
    
    public void instantiate() throws JexmaraldaException{
        try {
            java.io.InputStream is = getClass().getResourceAsStream(PATH_TO_MAPPING_FILE);
            PraatUnicodeMappingSaxReader pumsr = new PraatUnicodeMappingSaxReader();
            PraatUnicodeMapping pum = pumsr.readFromStream(is);
            this.praatUnicode = pum.praatUnicode;
            this.unicodePraat = pum.unicodePraat;
        } catch (Exception e){
            e.printStackTrace();
            throw new JexmaraldaException(e.getLocalizedMessage());
        }
    }
    
    String getUnicodeForPraat(String praatEscapeSequence){
        if (praatUnicode.containsKey(praatEscapeSequence)){
            return (String)(praatUnicode.get(praatEscapeSequence));
        } else {
            return "?";
        }
    }

    String getPraatForUnicode(String unicodeCharacter){
        if (unicodePraat.containsKey(unicodeCharacter)){
            return "\\" + (String)(unicodePraat.get(unicodeCharacter));
        } else {
            return "?";
        }
    }

}
