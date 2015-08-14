/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;

/**
 *
 * @author Schmidt
 */
public class CompareZW {

    String META_PATH = "Y:\\thomas\\ZW_HE\\Meta\\ZW_HE_Metadata.xml";
    String DGD1_WAV_PATH = "Y:\\media\\audio\\ZW_DGD_1.0\\umbenannt";
    String TRANSCRIPT_PATH = "Y:\\thomas\\ZW_HE";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new CompareZW().doit();
    }

    private void doit() {
        File[] wavFiles = new File(DGD1_WAV_PATH).listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".wav");
            }       
        });
        File[] flnFiles = new File(TRANSCRIPT_PATH).listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".fln");
            }       
        });
        
        HashSet<String> wavs = new HashSet<String>();
        for (File wav : wavFiles){
            wavs.add(wav.getName().substring(0,12));
        }
        // ZW--_E_00043_SE_01_T_01_DF_01
        HashSet<String> flns = new HashSet<String>();
        for (File fln : flnFiles){
            flns.add(fln.getName().substring(0,12));
        }
        
        HashSet<String> WAVBUTNOTFLN = new HashSet<String>();
        for (String kennung : wavs){
            System.out.print("Checking WAV " + kennung);
            if (flns.contains(kennung)){
                System.out.println(": FLN  exists.");
            } else {
                System.out.println(": NO FLN!!!!!!!!!!!");                
                WAVBUTNOTFLN.add(kennung);
            }
        }
        
        HashSet<String> FLNBUTNOTWAV = new HashSet<String>();
        for (String kennung : flns){
            System.out.print("Checking FLN " + kennung);
            if (wavs.contains(kennung)){
                System.out.println(": WAV  exists.");
            } else {
                System.out.println(": NO WAV!!!!!!!!!!!");                
                FLNBUTNOTWAV.add(kennung);
            }
        }
        
        System.out.println("===============");
        System.out.println("WAV existiert, FLN existiert nicht");
        for (String kennung : WAVBUTNOTFLN){
            System.out.println(kennung);
        }
        System.out.println("===============");
        System.out.println("FLN existiert, WAV existiert nicht");
        for (String kennung : FLNBUTNOTWAV){
            System.out.println(kennung);
        }
        
        
    }
}
