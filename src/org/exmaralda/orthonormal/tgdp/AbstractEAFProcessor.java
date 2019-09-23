/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.tgdp;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Thomas_Schmidt
 */
public abstract class AbstractEAFProcessor {

    String IN_DIR = "D:\\Dropbox\\work\\WERKVERTRAEGE\\2019_AUSTIN\\2019_05_03_Pilot_Sample\\0";

    public void doit() throws IOException {
        /*ArrayList<File> allEAFFiles = new ArrayList<File>();
        File[] subDirs = new File(IN_DIR).listFiles(new FileFilter(){
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
                  
        });
        for (File subDir : subDirs){
            File[] eafFiles = subDir.listFiles(new FilenameFilter(){
                @Override
                public boolean accept(File dir, String name) {
                    return name.toUpperCase().endsWith(".EAF");
                }            
            });
            allEAFFiles.addAll(Arrays.asList(eafFiles));
        }*/
        File[] allEAFFiles = new File(IN_DIR).listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(".EAF");
            }            
        });
        for (File eafFile : allEAFFiles){
            System.out.println("\n---------------------------");
            System.out.println("Processing " + eafFile.getAbsolutePath());
            processFile(eafFile);
        }
        
    }
    
    public abstract void processFile(File eafFile) throws IOException;
    
}
