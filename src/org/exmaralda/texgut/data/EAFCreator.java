/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.texgut.data;

import java.io.IOException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 *
 * @author bernd
 */
public class EAFCreator {
    
    String[] speakerNumbers;
    String[] interviewerNumbers;
    String audioPath;

    public EAFCreator(String[] speakerNumbers, String[] interviewerNumbers, String audioPath) {
        this.speakerNumbers = speakerNumbers;
        this.interviewerNumbers = interviewerNumbers;
        this.audioPath = audioPath;
    }
    
    public Document createEAF() throws JDOMException, IOException{
        Document document = new IOUtilities().readDocumentFromResource("/org/exmaralda/texgut/data/EAFSkeleton.eaf");
        return document;
    }
    
    
    
}
