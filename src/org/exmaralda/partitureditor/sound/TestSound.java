import java.io.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import org.exmaralda.partitureditor.sound.AbstractPlayer;
import org.exmaralda.partitureditor.sound.BASAudioPlayer;
import org.exmaralda.partitureditor.sound.JDSPlayer;
import org.exmaralda.partitureditor.sound.JavaFXPlayer;

public class TestSound

{
  public static void main(String[] args)
  throws Exception
  { 
    AbstractPlayer p = new JDSPlayer();
    //p.setSoundFile("N:\\Workspace\\EXMARaLDA\\EXMARaLDA-Testbatterie\\Beckhams_Test_Batterie_01.mp4");
    System.out.println("Setting sound file");
    p.setSoundFile("C:\\Users\\thomas.schmidt\\Desktop\\DEBUG\\MARKERS\\SSI_196X_Lifestory_nar.wav");
    System.exit(0);
    // open the sound file as a Java input stream
    /*InputStream in = new BufferedInputStream(new FileInputStream("D:\\Dropbox\\IDS\\FOLK\\FOLK_WGCA_01_A01\\FOLK_WGCA_01_A01b_mask.WAV"));
    final Clip clip = AudioSystem.getClip();
    clip.open(AudioSystem.getAudioInputStream(in));
    clip.loop(3);*/
  }
}