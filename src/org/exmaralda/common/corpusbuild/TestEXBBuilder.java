/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.common.corpusbuild;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class TestEXBBuilder {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new TestEXBBuilder().doit();
    }

    private void doit() {
        try {
            EXBBuilder exbBuilder = new EXBBuilder("MANV", new File("C:\\UDE\\PILOT_MANV\\ZUMULT-CORPUS\\MANV"), "descendant::ud-information[@attribute-name='uniqueID']", "default");
            Set<String> dmk = new HashSet<>();
            dmk.add("ELAN-Media-File");
            dmk.add("ELAN-Mime-Type");
            exbBuilder.setDeleteMetaKeys(dmk);
            exbBuilder.build();
        } catch (IOException | SAXException | JexmaraldaException | JDOMException | FSMException ex) {
            Logger.getLogger(TestEXBBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
