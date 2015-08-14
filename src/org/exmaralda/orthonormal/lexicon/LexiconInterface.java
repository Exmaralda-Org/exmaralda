/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.lexicon;

import java.io.IOException;
import java.util.List;
import org.jdom.Document;

/**
 *
 * @author thomas
 */
public interface LexiconInterface {


    // returns a list of possibly corresponding strings from the lexicon
    public List<String> getCandidateForms(String form) throws LexiconException;

    public void put(String form, String correspondingForm, String transcriptionID, String wordID) throws LexiconException;

    public void read(Object source) throws IOException;

    public void write(Object target) throws IOException;

    public int getFrequency (String form, String correspondingForm);

    public boolean hasFrequencyInformation();
    
    public boolean hasCapitalInformation();
    
    public boolean isCapitalOnly(String form) throws LexiconException;

    public void update(Document d) throws Exception;


    
    
    

}
