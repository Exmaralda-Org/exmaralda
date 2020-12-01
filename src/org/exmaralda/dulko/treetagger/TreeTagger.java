// TreeTagger.java -- A class for tagging tokens by TreeTagger
// Version 1.2
// Andreas Nolda 2020-12-01

// cf. TreeTaggerWrapper.java at https://github.com/reckart/tt4j
// and TreeTagger.java at https://github.com/Camille31/Swip

package org.exmaralda.dulko.treetagger;

import static java.io.File.separator;

import java.util.ArrayList;
import java.util.List;
import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerWrapper;

public class TreeTagger {
  TreeTaggerWrapper tt = null;
  List<String> tagList = null;

  public TreeTagger(String lang) throws Exception {
    tt = new TreeTaggerWrapper<String>();
    if (lang.equals("de")) {
      tt.setModel(System.getenv("TREETAGGER_HOME") + separator + "lib"
                                                   + separator + "german-utf8.par");
    } else {
      throw new IllegalArgumentException("Language \"" + lang + "\" unsupported.");
    }
  }

  public String[] pos(String[] tokenArray) throws Exception {
    tagList = new ArrayList<String>();
    tt.setHandler(new TokenHandler<String>() {
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
