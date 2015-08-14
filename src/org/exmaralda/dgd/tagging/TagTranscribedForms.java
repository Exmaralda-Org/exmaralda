/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.tagging;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class TagTranscribedForms {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String[] theArgs = {"Z:\\FOLK-Tagging\\transcripts\\0-Originale", "Z:\\FOLK-Tagging\\transcripts\\0a-Tagged-Transcribed"};
            new TagDirectory().doit(theArgs);
        } catch (IOException ex) {
            Logger.getLogger(TagTranscribedForms.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(TagTranscribedForms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
