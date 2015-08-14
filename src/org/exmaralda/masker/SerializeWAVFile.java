/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.masker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 *
 * @author Schmidt
 */
public class SerializeWAVFile {
    
    public static void main(String[] args) throws Exception {

        new SerializeWAVFile().doit();
    }
    
    private void doit() throws IOException, WavFileException{
        // Serialize an int[]
        WavFile in = WavFile.openWavFile(new File("C:\\Users\\Schmidt\\Dropbox\\IDS\\FOLK\\BrownNoise\\Brownsches_Rauschen_9db.wav"));
        
        double[] samples = new double[100000];
        
        //double[] samplesRead = new double[10000];
        
        in.readFrames(samples, 50000);
        
        
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("C:\\EXMARaLDA_FRESHEST\\src\\org\\exmaralda\\masker\\ImmerzBrownNoise_9db.ser")));
        out.writeObject(samples);
        out.flush();
        out.close();

        // Deserialize the int[]
        //ObjectInputStream in = new ObjectInputStream(new FileInputStream("test.ser"));
        //int[] array = (int[]) in.readObject();
        //in.close();

        // Print out contents of deserialized int[]
        //System.out.println("It is " + (array instanceof Serializable) + " that int[] implements Serializable");
        //System.out.print("Deserialized array: " + array[0]);
        //for (int i=1; i<array.length; i++) {
        //    System.out.print(", " + array[i]);
        //}
        System.out.println();
    }

}
