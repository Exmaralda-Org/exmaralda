/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Schmidt
 */
public class Compare {

    String DIR1 = "C:\\Users\\Schmidt\\Desktop\\FOLK_final\\FLK_CATALOG.txt";
    String DIR2 = "C:\\Users\\Schmidt\\Desktop\\FOLK-Beta\\FLK_CATALOG.txt";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new Compare().doit();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Compare.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Compare.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws FileNotFoundException, IOException {
        HashSet<String> dir1flks = read(DIR1);
        HashSet<String> dir2flks = read(DIR2);        
        dir1flks.removeAll(dir2flks);
        System.out.println("In " + DIR1 + " but not in "+ DIR2);
        for (String s : dir1flks){
            System.out.println(s);
        }
        
        dir1flks = read(DIR1);
        dir2flks.removeAll(dir1flks);
        System.out.println("In " + DIR2 + " but not in "+ DIR1);
        for (String s : dir2flks){
            System.out.println(s);
        }

    }

    private HashSet<String> read(String path) throws FileNotFoundException, IOException {
        HashSet<String> result = new HashSet<String>();
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String nextLine = new String();
        System.out.println("Started reading document");
        while ((nextLine = br.readLine()) != null){
            result.add(nextLine);
        }
        br.close();
        System.out.println("Document read.");        
        return result;
        
    }
}
