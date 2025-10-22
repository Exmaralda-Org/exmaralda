/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.tei;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author bernd
 */
public class TEINamespaceCleanser {
    
    public static void cleanFile(File file) throws IOException{
        System.out.println("[TEINamespaceCleanser] Cleaning " + file.getAbsolutePath());
        StringBuilder result = new StringBuilder();
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String nextLine="";
        while ((nextLine = br.readLine()) != null){
            String cleanLine = nextLine
                    .replaceAll("tei:", "")
                    .replaceAll("(?<!TEI )xmlns:tei=\"http://www.tei-c.org/ns/1.0\" ?", "")
                    .replaceAll("xmlns:saxon=\"http://saxon.sf.net/\" ?", "");
            result.append(cleanLine);
        }
        br.close();
        System.out.println("[TEINamespaceCleanser] Cleaned " + file.getAbsolutePath());
        
        Path path = file.toPath();

        System.out.println("[TEINamespaceCleanser] Writing " + file.getAbsolutePath());
        BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        writer.write(result.toString());                
        writer.flush();
        writer.close();
        System.out.println("[TEINamespaceCleanser] Written " + file.getAbsolutePath());
    }
    
}
