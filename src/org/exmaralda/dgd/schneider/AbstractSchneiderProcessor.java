/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.common.helpers.RelativePath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public abstract class AbstractSchneiderProcessor {
    File logFile;

    protected void getMappings(String path) throws JDOMException, IOException {
        Document eventMetadataDocument = FileIO.readDocumentFromLocalFile(path);
        List l = eventMetadataDocument.getRootElement().getChildren("event");
        for (Object o : l) {
            //<event event-id="ZW--_E_00001" file="ZW--_E_00001.xml">
            //    <Sonstige_Bezeichnungen>ZW001 ; I/1</Sonstige_Bezeichnungen>
            Element e = (Element) o;
            String id = e.getAttributeValue("event-id");
            String sb = e.getChildText("Sonstige_Bezeichnungen");
            if (sb.contains(";")) {
                int i = sb.indexOf(";");
                if (id.startsWith("KN")) {
                    sb = sb.substring(i + 1).trim();
                } else if (sb.contains("; HE")){
                    i = sb.lastIndexOf(";");
                    sb = sb.substring(i + 1).trim();                    
                } else {
                    sb = sb.substring(0, i).trim();
                } 
            }
            if (!(old2new.containsKey(sb))){
                old2new.put(sb, id);
            }
            System.out.println("Putting " + sb + " ---> " + id);
            // ZW7I8 entspricht ZW--_E_04477
            old2new.put("ZW7I8", "ZW--_E_04477");
            old2new.put("HE134", "ZW--_E_05787");
            old2new.put("ZW8V7", "ZW--_E_05787");

            old2new.put("HLD03", "HL--_E_00008");
            
            // falsch zugeordnete (siehe Mail UMS vom 24.09.2012)
            /* Transkript  ZW--_E_01298 (=ZWC98) ? 01294
            Transkript  ZW--_E_03552 (=ZWZ52) ? 03559
            Transkript  ZW--_E_03879 (=ZW7C9) ? 03479
            Transkript  ZW--_E_04723 (=ZW2L3) ? 02123
            Transkript  ZW--_E_05246 (=ZW4Q6) ? 02646
            Transkript  ZW--_E_04262 (=ZW6G2) ? 04263
            Transkript  ZW--_E_04637 (=ZW3K7) /04638 (Dublette?) ? 04537 */
            old2new.put("ZWC98", "ZW--_E_01294");
            old2new.put("ZWZ52", "ZW--_E_03559");
            old2new.put("ZW7C9", "ZW--_E_03479");
            old2new.put("ZW2L3", "ZW--_E_02123");
            old2new.put("ZW4Q6", "ZW--_E_02646");
            old2new.put("ZW6G2", "ZW--_E_04263");
            old2new.put("ZW3K7", "ZW--_E_04537");
            
        }
    }

    public File inputDirectory;
    public File[] inputFiles;
    public String inputSuffix;

    protected String makeOutputPath(File inputFile) {
        File f = new File(outputDirectory, inputFile.getName().replaceAll("\\." + inputSuffix, "." + outputSuffix));
        return f.getAbsolutePath();
    }

    
    void writeLogToTextFile(StringBuffer log) throws FileNotFoundException, IOException {
        System.out.println("started writing log file...");
        FileOutputStream fos = new FileOutputStream(logFile);
        fos.write(log.toString().getBytes());
        fos.close();
        System.out.println("document written.");        
    }
    
    
    public Hashtable<String, String> old2new = new Hashtable<String, String>();
    public File outputDirectory;
    public String outputSuffix;

    void writeLogToXMLFile(Document logDocument) throws IOException {
        System.out.println("started writing log file...");
        FileIO.writeDocumentToLocalFile(logFile.getAbsolutePath(), logDocument);
        System.out.println("document written.");
    }

    public String determineAudio(Hashtable<String, String> old2new, File inputFile) {
        if (inputFile.getName().startsWith("PF")) {
            //PF001TRA
            //PF--_E_00157_A_01_DF_01.WAV
            String old = inputFile.getName().substring(0, inputFile.getName().indexOf("TRA."));
            System.out.println("Getting " + old);
            String newKennung = old2new.get(old);
            String path = "../../../media/audio/PF/";
            path += newKennung;
            path += "_SE_01_A_01_DF_01.WAV";
            return path;
        } else if (inputFile.getName().startsWith("OS")) {
            //PF001TRA
            //PF--_E_00157_A_01_DF_01.WAV
            String eNumber = inputFile.getName().substring(2, 5);
            String path = "../../../media/audio/OS_2/OS--_E_00";
            path += eNumber;
            path += "_SE_01_A_01_DF_01.WAV";
            return path;
        } else if (inputFile.getName().startsWith("ZW") || inputFile.getName().startsWith("HE")) {
            String old = inputFile.getName().substring(0, inputFile.getName().indexOf("TRA."));
            System.out.println("Getting " + old);
            String newKennung = old2new.get(old);
            String path = "../../../media/audio/ZW_2/";
            path += newKennung;
            if (new File(RelativePath.resolveRelativePath(path + "_SE_01_A_02_DF_01.WAV", inputFile.getAbsolutePath())).exists()) {
                path += "_SE_01_A_02_DF_01.WAV";
            } else {
                path += "_SE_01_A_01_DF_01.WAV";
            }
            //path += "_A_01_DF_01.WAV";
            return path;
        } else if (inputFile.getName().startsWith("KO")) {
            String old = inputFile.getName().substring(0, inputFile.getName().indexOf("TRA."));
            System.out.println("Getting " + old);
            String newKennung = old2new.get(old);
            String path = "../../../media/audio/KN/";
            path += newKennung;
            //if (new File(RelativePath.resolveRelativePath(path + "_SE_01_A_02_DF_01.WAV", inputFile.getAbsolutePath())).exists()) {
            //    path += "_SE_01_A_02_DF_01.WAV";
            //} else {
                path += "_SE_01_A_01_DF_01.WAV";
            //}
            return path;
        } else if (inputFile.getName().startsWith("HL")) {
            String old = "";
            if (inputFile.getName().endsWith(".WAV")){
                old = inputFile.getName().substring(0, inputFile.getName().indexOf("W2.")-1);
            } else {
                old = inputFile.getName().substring(0, inputFile.getName().indexOf("TRA."));
            }
            System.out.println("Getting " + old);
            String newKennung = old2new.get(old);
            String path = "../../../media/audio/" + inputFile.getName().substring(0, 2) + "/";
            path += newKennung;
            if (new File(RelativePath.resolveRelativePath(path + "_SE_01_A_02_DF_01.WAV", inputFile.getAbsolutePath())).exists()) {
                path += "_SE_01_A_02_DF_01.WAV";
            } else {
                path += "_SE_01_A_01_DF_01.WAV";
            }
            return path;
        } else if (inputFile.getName().startsWith("DS")) {
            String old = inputFile.getName().substring(0, inputFile.getName().indexOf("TRA."));
            System.out.println("Getting " + old);
            String newKennung = old2new.get(old);
            String path = "../../../media/audio/DS/";
            path += newKennung;
            path += "_SE_01_A_01_DF_01.WAV";
            return path;
        } else if (inputFile.getName().startsWith("FR")) {
            String old = inputFile.getName().substring(0, inputFile.getName().indexOf("TRA."));
            System.out.println("Getting " + old);
            String newKennung = old2new.get(old);
            String path = "../../../media/audio/FR/";
            path += newKennung;
            path += "_SE_01_A_01_DF_01.WAV";
            return path;
        } else if (inputFile.getName().startsWith("IS")) {
            String old = inputFile.getName().substring(0, inputFile.getName().indexOf("TRA."));
            System.out.println("Getting " + old);
            String newKennung = old2new.get(old);
            String path = "../../../media/audio/IS/";
            path += newKennung;
            path += "_SE_01_A_01_DF_01.WAV";
            return path;
        }
        return "xxxx";
    }
    
    public String determineTranscriptName(Hashtable<String, String> old2new, File inputFile) {
        if (inputFile.getName().startsWith("PF")) {
            //PF001TRA
            //PF--_E_00157_A_01_DF_01.WAV
            String old = inputFile.getName().substring(0, inputFile.getName().indexOf("TRA."));
            System.out.println("Getting " + old);
            String newKennung = old2new.get(old);
            String tKennung = newKennung + "_SE_01_T_01_DF_01";
            return tKennung;
        } else if (inputFile.getName().startsWith("ZW") 
                || inputFile.getName().startsWith("HE") 
                || inputFile.getName().startsWith("FR") 
                || inputFile.getName().startsWith("DS") 
                || inputFile.getName().startsWith("KO") 
                || inputFile.getName().startsWith("HL")) {
            String old = inputFile.getName().substring(0, inputFile.getName().indexOf("TRA."));
            System.out.println("Getting " + old);
            String newKennung = old2new.get(old);
            String tKennung = newKennung + "_SE_01_T_01_DF_01";
            return tKennung;
        } else if (inputFile.getName().startsWith("OS")) {
            String eNumber = inputFile.getName().substring(2, 5);
            String path = "OS--_E_00";
            path += eNumber;
            path += "_SE_01_T_01_DF_01";
            return path;
        } else if (inputFile.getName().startsWith("IS")) {
            String eNumber = inputFile.getName().substring(2, 5);
            String path = "IS--_E_00";
            path += eNumber;
            path += "_SE_01_T_01_DF_01";
            return path;
        }         
        return "";
    }
    
    
}
