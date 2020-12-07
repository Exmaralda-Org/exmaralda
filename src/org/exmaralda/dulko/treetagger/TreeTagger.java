// TreeTagger.java -- A class for tagging tokens by TreeTagger
// Version 2.1
// Andreas Nolda, Thomas Schmidt 2020-12-06

// cf. TreeTaggerWrapper.java at https://github.com/reckart/tt4j
// and TreeTagger.java at https://github.com/Camille31/Swip

package org.exmaralda.dulko.treetagger;

import java.io.File;
import static java.io.File.separator;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerWrapper;
import org.exmaralda.tagging.TaggingProfiles;

public class TreeTagger {
  TreeTaggerWrapper tt = null;
  List<String> tagList = null;

  String ENVIRONMENT_TREETAGGER_HOME = System.getenv("TREETAGGER_HOME");
  String TREETAGER_HOME_PATH = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("directory", ENVIRONMENT_TREETAGGER_HOME);
  String MODEL_FALLBACK_PATH = ENVIRONMENT_TREETAGGER_HOME + separator + "lib" + separator + "german.par";
  String MODEL_PATH = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("parameter-file", MODEL_FALLBACK_PATH);
  String MODEL_ENCODING = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("parameter-file-encoding", "UTF-8");

  public TreeTagger() throws IOException {
    tt = new TreeTaggerWrapper<String>();

    File f1 = new File(TREETAGER_HOME_PATH);
    if (!f1.exists()){throw new IOException("TreeTagger home directory " + TREETAGER_HOME_PATH + " does not exist.");}
    if (!f1.canRead()){throw new IOException("TreeTagger home directory " + TREETAGER_HOME_PATH + " cannot be read.");}
    File f2 = new File(MODEL_PATH);
    if (!f2.exists()){throw new IOException("TreeTagger parameter file " + MODEL_PATH + " does not exist.");}
    if (!f2.canRead()){throw new IOException("TreeTagger parameter file " + MODEL_PATH + " cannot be read.");}

    System.setProperty("treetagger.home", TREETAGER_HOME_PATH);
    tt.setModel(MODEL_PATH + ":" + MODEL_ENCODING);
  }

  public String home() {
    return TREETAGER_HOME_PATH;
  }

  public String model() {
    return MODEL_PATH;
  }

  public String[] pos(String[] tokenArray) throws Exception {
    tagList = new ArrayList<String>();
    tt.setHandler(new TokenHandler<String>() {
      @Override
      public void token(String token, String pos, String lemma) {
        tagList.add(pos);
      }
    });
    try {
      tt.process(tokenArray);
    }
    finally {
      tt.destroy();
    }
    String[] tagArray = tagList.toArray(new String[0]);
    return tagArray;
  }

  public String[] lemma(String[] tokenArray) throws Exception {
    tagList = new ArrayList<String>();
    tt.setHandler(new TokenHandler<String>() {
      @Override
      public void token(String token, String pos, String lemma) {
        tagList.add(lemma);
      }
    });
    try {
      tt.process(tokenArray);
    }
    finally {
      tt.destroy();
    }
    String[] tagArray = tagList.toArray(new String[0]);
    return tagArray;
  }
}
