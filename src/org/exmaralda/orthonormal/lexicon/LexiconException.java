/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.lexicon;


/**
 *
 * @author thomas
 */
public class LexiconException extends Exception {

    public LexiconException(Exception ex) {
        super(ex.getCause());
    }

}
