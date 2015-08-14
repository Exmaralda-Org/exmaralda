/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.folk;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.orthonormal.data.NormalizedFolkerTranscription;
import org.exmaralda.orthonormal.io.XMLReaderWriter;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class AssignAudio {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new AssignAudio().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(AssignAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AssignAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        File[] files = new File("L:\\FOLK_DGD_2\\transcripts\\FOLK_NORMALISIERUNG").listFiles();
        for (File f : files){            
            System.out.println("--------------- Reading " + f.getAbsolutePath());
            NormalizedFolkerTranscription nft = XMLReaderWriter.readNormalizedFolkerTranscription(f);
            File referenceFile = new File("C:\\Users\\Schmidt\\Desktop\\DGD-RELEASE\\transcripts\\FOLK", f.getName());
            System.out.println("--------------- Reading " + referenceFile.getAbsolutePath());
            NormalizedFolkerTranscription nft2 = XMLReaderWriter.readNormalizedFolkerTranscription(referenceFile);
            String referenceMediaPath = nft2.getMediaPath();
            //System.out.println(referenceMediaPath);
            String newMediaPath = "L:\\FOLK_DGD_2\\media\\audio\\FOLK\\" + referenceMediaPath.substring(referenceMediaPath.lastIndexOf("/")+1);
            System.out.println("+++++++++++++++++++ Setting " + newMediaPath);
            nft.setMediaPath(newMediaPath);
            FileIO.writeDocumentToLocalFile(f.getAbsolutePath(), nft.getDocument());
        }
    }
}
