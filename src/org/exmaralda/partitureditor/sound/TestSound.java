import java.io.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import org.exmaralda.partitureditor.sound.BASAudioPlayer;

public class TestSound

{
  public static void main(String[] args)
  throws Exception
  { 
    BASAudioPlayer p = new BASAudioPlayer();
    p.setSoundFile("D:\\Dropbox\\IDS\\FOLK\\FOLK_WGCA_01_A01\\FOLK_WGCA_01_A01b_mask.WAV");
    // open the sound file as a Java input stream
    /*InputStream in = new BufferedInputStream(new FileInputStream("D:\\Dropbox\\IDS\\FOLK\\FOLK_WGCA_01_A01\\FOLK_WGCA_01_A01b_mask.WAV"));
    final Clip clip = AudioSystem.getClip();
    clip.open(AudioSystem.getAudioInputStream(in));
    clip.loop(3);*/
  }
}