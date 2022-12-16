/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.orthonormal.data;

import java.io.File;
import org.exmaralda.orthonormal.io.XMLReaderWriter;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;

/**
 *
 * @author bernd
 */
public class TestSegConversion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        new TestSegConversion().doit();
    }

    private void doit() throws Exception {
        NormalizedFolkerTranscription nft = XMLReaderWriter.readNormalizedFolkerTranscription(new File("C:\\Users\\bernd\\OneDrive\\Desktop\\FOLK_E_00056\\FOLK_E_00056_SE_01_T_01.fln"));
        SegmentedTranscription st = nft.toSegmentedTranscription();
        st.writeXMLToFile("C:\\Users\\bernd\\Dropbox\\work\\EXMARaLDA_Support\\2022_12_12_ISSUE_328\\out.xml", "none");
    }
    
    
    
}
