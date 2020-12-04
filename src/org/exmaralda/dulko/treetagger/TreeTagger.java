// TreeTagger.java -- A class for tagging tokens by TreeTagger
// Version 1.2
// Andreas Nolda 2020-12-01

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

  public TreeTagger(String lang) throws IOException {
    tt = new TreeTaggerWrapper<String>();        

    // changed 04-12-2020, for issue #228      
    String ENVIRONMENT_TREE_TAGGER_HOME = System.getenv("TREETAGGER_HOME");
    String MODEL_FALLBACK_PATH = ENVIRONMENT_TREE_TAGGER_HOME + separator + "lib" + separator + "german-utf8.par";        

    String TREETAGER_EXECUTABLE_PATH = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("directory", ENVIRONMENT_TREE_TAGGER_HOME);    
    String MODEL_PATH = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("parameter-file", MODEL_FALLBACK_PATH);  
    String MODEL_ENCODING = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("parameter-file-encoding", "UTF-8");  
    
    // do some checks
    System.out.println("Initialising tagger");
    File f1 = new File(TREETAGER_EXECUTABLE_PATH);
    if (!f1.exists()){throw new IOException("Command file " + TREETAGER_EXECUTABLE_PATH + " does not exist.");}
    if (!f1.canExecute()){throw new IOException("Command file " + TREETAGER_EXECUTABLE_PATH + " is not exectuable.");}
    File f2 = new File(MODEL_PATH);
    if (!f2.exists()){throw new IOException("Parameter file " + MODEL_PATH + " does not exist.");}
    if (!f2.canRead()){throw new IOException("Parameter file " + MODEL_PATH + " cannot be read.");}
    
    
    System.setProperty("treetagger.home", TREETAGER_EXECUTABLE_PATH);
    //tt.setModel(MODEL_PATH);
    tt.setModel(MODEL_PATH + ":" + MODEL_ENCODING);

    
    
    
    
    // do we still need a distinction by lang if the user can set the language specific parameter file?
    //if (lang.equals("de")) {
      /*tt.setModel(System.getenv("TREETAGGER_HOME") + separator + "lib"
                                                   + separator + "german-utf8.par");*/
    //} else {
    //  throw new IllegalArgumentException("Language \"" + lang + "\" unsupported.");
    //}
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
