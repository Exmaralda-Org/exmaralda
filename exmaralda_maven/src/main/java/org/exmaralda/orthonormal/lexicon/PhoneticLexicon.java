/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.lexicon;

import java.util.Vector;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class PhoneticLexicon {

    public Vector<PhoneticLexiconEntry> entries = new Vector<PhoneticLexiconEntry>();

    public PhoneticLexicon(Document document){
        entries.ensureCapacity(142000);
        for (Object o : document.getRootElement().getChildren("entry")){
            entries.add(new PhoneticLexiconEntry((Element)o));
        }
    }

    public PhoneticLexiconEntry getEntry(String lemma){
        int lower = 0;
        int upper = entries.size()-1;
        int index = (lower + upper) / 2;
        PhoneticLexiconEntry entry = entries.elementAt(index);
        while ((!(entry.lemma.equals(lemma)) && ((upper-lower)>1))){
            if (entry.lemma.compareTo(lemma)>0){
                upper = index;
            } else if (entry.lemma.compareTo(lemma)<0){
                lower = index;
            }
            index = (lower + upper) / 2;
            entry = entries.elementAt(index);
        }
        if (entry.lemma.equals(lemma)){
            return entry;
        }
        return null;
    }

}

