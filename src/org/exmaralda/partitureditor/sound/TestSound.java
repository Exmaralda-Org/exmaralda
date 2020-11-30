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
    p.setSoundFile("https://s3-uhh.lzs.uni-hamburg.de/gwiss-exmaralda/remote/demokorpus-1.1/AnneWill/AnneWill.wav");
    System.exit(0);
    // open the sound file as a Java input stream
    /*InputStream in = new BufferedInputStream(new FileInputStream("D:\\Dropbox\\IDS\\FOLK\\FOLK_WGCA_01_A01\\FOLK_WGCA_01_A01b_mask.WAV"));
    final Clip clip = AudioSystem.getClip();
    clip.open(AudioSystem.getAudioInputStream(in));
    clip.loop(3);*/
  }
}