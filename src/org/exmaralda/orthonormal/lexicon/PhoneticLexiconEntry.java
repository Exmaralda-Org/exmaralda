/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.lexicon;

import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class PhoneticLexiconEntry {

    public String lemma;
    public String pos;
    public String ph;

    public PhoneticLexiconEntry(Element element){
        // <lexicon>
        //<entry lemma="A" pos="ADD:NOM" ph="?a?"/>
        lemma = element.getAttributeValue("lemma");
        pos = element.getAttributeValue("pos");
        ph = element.getAttributeValue("ph");
    }

    @Override
    public String toString() {
        return ("[" + lemma + ": " + pos + " / " + ph + "]");
    }



}
// :) Wer war das?
