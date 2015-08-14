/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.folk;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Schmidt
 */
public class NormalizeFOLKNames {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new NormalizeFOLKNames().doit();
    }

    private void doit() {
        File[] files = new File("C:\\Users\\Schmidt\\Desktop\\FOLK_RELEASE\\0").listFiles();
        int count = 0;
        for (File f : files){            
            String newName = f.getName();
            
            // FOLK_EKSP_01_A01a_Teil_3.fln
            Pattern p = Pattern.compile("FOLK_[A-ZÄÖÜ]{4}_\\d{2}_A\\d{2}[ab]?(_Teil_\\d{1,2})?\\.fln");
            Matcher m = p.matcher(newName);
            if(!(m.matches())){
                System.out.println(count + " " + newName);
                count++;
            }
            
            /*newName = newName.replaceAll("Teil4", "Teil_4");
            newName = newName.replaceAll("Teil5", "Teil_5");
            newName = newName.replaceAll("Teil6", "Teil_6");
            newName = newName.replaceAll("Teil 4", "Teil_4");
            newName = newName.replaceAll("Teil 5", "Teil_5");
            newName = newName.replaceAll("Teil 6", "Teil_6");
            newName = newName.replaceAll(" Teil", "_Teil");
            File newFile = new File(f.getParentFile(), newName);
            System.out.println("Renaming " + f.getName() + " to " + newName);
            f.renameTo(newFile);*/
        }
    }
}
