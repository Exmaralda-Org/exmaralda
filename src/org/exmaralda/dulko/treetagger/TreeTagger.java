// TreeTagger.java -- A class for tagging tokens by TreeTagger
// Version 2.2
// Andreas Nolda, Thomas Schmidt 2020-12-07

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

  String ENVIRONMENT_TREETAGGER_HOME = System.getenv("TREETAGGER_HOME");
  String TREETAGER_HOME_PATH = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("directory", ENVIRONMENT_TREETAGGER_HOME);

  String MODEL_FALLBACK_PATH = ENVIRONMENT_TREETAGGER_HOME + separator + "lib" + separator + "german.par";
  String MODEL_PATH = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("parameter-file", MODEL_FALLBACK_PATH);
  String MODEL_ENCODING = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("parameter-file-encoding", "UTF-8");

  public TreeTagger() throws IOException {
    System.out.println("Initialising TreeTagger");

    File f1 = new File(TREETAGER_HOME_PATH);
    if (!f1.exists()){throw new IOException("TreeTagger home directory " + TREETAGER_HOME_PATH + " does not exist.");}
    if (!f1.canRead()){throw new IOException("TreeTagger home directory " + TREETAGER_HOME_PATH + " cannot be read.");}

    File f2 = new File(MODEL_PATH);
    if (!f2.exists()){throw new IOException("TreeTagger parameter file " + MODEL_PATH + " does not exist.");}
    if (!f2.canRead()){throw new IOException("TreeTagger parameter file " + MODEL_PATH + " cannot be read.");}

    System.setProperty("treetagger.home", TREETAGER_HOME_PATH);
  }

  public String getHome() {
    System.out.println("Getting TreeTagger home directory");
    return TREETAGER_HOME_PATH;
  }

  public String getModel() {
    System.out.println("Getting TreeTagger parameter file");
    return MODEL_PATH;
  }

  public void tag(String[] tokenArray) throws Exception {
    posList = new ArrayList<String>();
    lemmaList = new ArrayList<String>();

    TreeTaggerWrapper tt = new TreeTaggerWrapper<String>();
    tt.setModel(MODEL_PATH + ":" + MODEL_ENCODING);
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
