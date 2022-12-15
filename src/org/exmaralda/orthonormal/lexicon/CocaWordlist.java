/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.lexicon;

import java.io.IOException;

/**
 *
 * @author Schmidt
 */
public class CocaWordlist extends AbstractSimpleWordlist {

    String COCA_PATH = "/org/exmaralda/orthonormal/lexicon/coca_wordlist.txt";
    
    public CocaWordlist() throws IOException {
        init(COCA_PATH);
    }
    
    
    
    
    
    
}
