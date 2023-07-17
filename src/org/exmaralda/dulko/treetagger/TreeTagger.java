// TreeTagger.java -- A class for tagging tokens by TreeTagger
// Version 2.4
// Andreas Nolda 2023-07-17

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
    String TT_ABBR_FALLBACK_PATH;
    String TT_ABBR_PATH;
    String TT_ABBR_ENCODING;

    public TreeTagger() throws IOException {
        System.err.println("Initialising TreeTagger");

        TT_HOME_FALLBACK_PATH = System.getenv("TREETAGGER_HOME");
        TT_HOME_PATH = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("directory", TT_HOME_FALLBACK_PATH);
        if (TT_HOME_PATH == null) {
            throw new IOException("TreeTagger directory is unset.");
        } else if (TT_HOME_PATH.isEmpty()) {
            if (TT_HOME_FALLBACK_PATH != null) {
                TT_HOME_PATH = TT_HOME_FALLBACK_PATH;
            } else {
                throw new IOException("TreeTagger directory is unset.");
            }
        }
        File f1 = new File(TT_HOME_PATH);
        if (! f1.exists()) {
            throw new IOException("TreeTagger directory does not exist.");
        } else if (! f1.canRead()) {
            throw new IOException("TreeTagger directory cannot be read.");
        } else {
            System.setProperty("treetagger.home", TT_HOME_PATH);
        }

        TT_MODEL_FALLBACK_PATH = TT_HOME_PATH + separator + "lib" + separator + "german.par";
        TT_MODEL_PATH = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("parameter-file", TT_MODEL_FALLBACK_PATH);
        TT_MODEL_ENCODING = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("parameter-file-encoding", "UTF-8");
        if (TT_MODEL_PATH == null) {
            throw new IOException("TreeTagger parameter file is unset.");
        } else if (TT_MODEL_PATH.isEmpty()) {
            TT_MODEL_PATH = TT_MODEL_FALLBACK_PATH;
        }
        File f2 = new File(TT_MODEL_PATH);
        if (! f2.exists()) {
            throw new IOException("TreeTagger parameter file does not exist.");
        } else if (! f2.canRead()) {
            throw new IOException("TreeTagger parameter file cannot be read.");
        }

        TT_ABBR_FALLBACK_PATH = TT_HOME_PATH + separator + "lib" + separator + "german-abbreviations";
        TT_ABBR_PATH = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("abbreviations-file", TT_ABBR_FALLBACK_PATH);
        TT_ABBR_ENCODING = Preferences.userRoot().node(TaggingProfiles.PREFERENCES_NODE).get("abbreviations-file-encoding", "UTF-8");
        if (TT_ABBR_PATH == null) {
            System.err.println("TreeTagger abbreviations file is unset.");
        } else if (TT_ABBR_PATH.isEmpty()) {
            TT_ABBR_PATH = TT_ABBR_FALLBACK_PATH;
        }
        File f3 = new File(TT_ABBR_PATH);
        if (! f3.exists()) {
            throw new IOException("TreeTagger abbreviations file does not exist.");
        } else if (! f3.canRead()) {
            throw new IOException("TreeTagger abbreviations file cannot be read.");
        }
    }

    public String getHomePath() {
        System.err.println("Using TreeTagger in " + TT_HOME_PATH);
        return TT_HOME_PATH;
    }

    public String getModelPath() {
        System.err.println("Using TreeTagger parameter file " + TT_MODEL_PATH);
        return TT_MODEL_PATH;
    }

    public String getModelEncoding() {
        System.err.println("Using TreeTagger parameter file in " + TT_MODEL_ENCODING + " encoding");
        return TT_MODEL_ENCODING;
    }

    public String getAbbrPath() {
        System.err.println("Using TreeTagger abbreviations file " + TT_ABBR_PATH);
        return TT_ABBR_PATH;
    }

    public String getAbbrEncoding() {
        System.err.println("Using TreeTagger abbreviations file in " + TT_ABBR_ENCODING + " encoding");
        return TT_ABBR_ENCODING;
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
            System.err.println("Running TreeTagger");
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
            System.err.println("Seeking POS tags");
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
            System.err.println("Seeking lemmas");
            tag(tokenArray);
            lemmaArray = lemmaList.toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lemmaArray;
    }
}
