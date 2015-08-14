/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.lexicon;

import java.util.List;
import java.util.Vector;

/**
 *
 * @author thomas
 */
public abstract class AbstractNormalizationLexicon implements LexiconInterface {

    public List<String> getCandidateForms(String form) throws LexiconException {
        Vector<String> result = new Vector<String>();
        result.add(form);
        // Took this out: IDS does not want to normalize capitalization
        /* if (Character.isLowerCase(form.charAt(0))){
            String capitalizedWord = Character.toString(Character.toUpperCase(form.charAt(0)));
            if (form.length()>1){
                capitalizedWord+=form.substring(1);
            }
            result.add(capitalizedWord);
        } */
        return result;
    }

}
