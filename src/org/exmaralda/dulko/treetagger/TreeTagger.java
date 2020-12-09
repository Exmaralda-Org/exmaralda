// TreeTagger.java -- A class for tagging tokens by TreeTagger
// Version 2.3
// Andreas Nolda 2020-12-08

// cf. TreeTaggerWrapper.java at https://github.com/reckart/tt4j
// and TreeTagger.java at https://github.com/Camille31/Swip

package org.exmaralda.dulko.treetagger;

import java.io.File;
import java.io.IOException;
import static java.io.File.separator;
import java.util.List;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerWrapper;
import org.exmaralda.tagging.TaggingProfiles;

public class TreeTagger {
  List<String> posList = null;
  List<String> lemmaList = null;

  String TT_HOME_FALLBACK_PATH;
  String TT_HOME_PATH;
  String TT_MODEL_FALLBACK_PATH;
  String TT_MODEL_PATH;
  String TT_MODEL_ENCODING;

  public TreeTagger() throws IOException {
    System.out.println("Initialising TreeTagger");

    TT_HOME_FALLBACK_PATH = System.getenv("TREETAGGER_HOME");
    TT_HOME_PATH = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("directory", TT_HOME_FALLBACK_PATH);

    if (TT_HOME_PATH == null) {
      throw new IOException("TreeTagger directory is unset.");
    }
    File f1 = new File(TT_HOME_PATH);
    if (!f1.exists()) {
      throw new IOException("TreeTagger directory " + TT_HOME_PATH + " does not exist.");
    } else if (!f1.canRead()) {
      throw new IOException("TreeTagger directory " + TT_HOME_PATH + " cannot be read.");
    }

    System.setProperty("treetagger.home", TT_HOME_PATH);

    TT_MODEL_FALLBACK_PATH = TT_HOME_FALLBACK_PATH + separator + "lib" + separator + "german.par";
    TT_MODEL_PATH = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("parameter-file", TT_MODEL_FALLBACK_PATH);
    TT_MODEL_ENCODING = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("parameter-file-encoding", "UTF-8");

    if (TT_MODEL_PATH == null) {
      throw new IOException("TreeTagger parameter file is unset.");
    }
    File f2 = new File(TT_MODEL_PATH);
    if (!f2.exists()) {
      throw new IOException("TreeTagger parameter file " + TT_MODEL_PATH + " does not exist.");
    } else if (!f2.canRead()) {
      throw new IOException("TreeTagger parameter file " + TT_MODEL_PATH + " cannot be read.");
    }

  }

  public String getHome() {
    System.out.println("Getting TreeTagger home directory");
    return TT_HOME_PATH;
  }

  public String getModel() {
    System.out.println("Getting TreeTagger parameter file");
    return TT_MODEL_PATH;
  }

  public void tag(String[] tokenArray) throws Exception {
    posList = new ArrayList<String>();
    lemmaList = new ArrayList<String>();

    TreeTaggerWrapper tt = new TreeTaggerWrapper<String>();
    tt.setModel(TT_MODEL_PATH + ":" + TT_MODEL_ENCODING);
    tt.setHandler(new TokenHandler<String>() {
      @Override
      public void token(String token, String pos, String lemma) {
        posList.add(pos);
        lemmaList.add(lemma);
      }
    });
    try {
      System.out.println("Running TreeTagger");
      tt.process(tokenArray);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      tt.destroy();
    }
  }

  public String[] pos(String[] tokenArray) throws Exception {
    String[] posArray = null;
    try {
      System.out.println("Seeking POS tags");
      tag(tokenArray);
      posArray = posList.toArray(new String[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return posArray;
  }

  public String[] lemma(String[] tokenArray) throws Exception {
    String[] lemmaArray = null;
    try {
      System.out.println("Seeking lemmas");
      tag(tokenArray);
      lemmaArray = lemmaList.toArray(new String[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return lemmaArray;
  }
}
