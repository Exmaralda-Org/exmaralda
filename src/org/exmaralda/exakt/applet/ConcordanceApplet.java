/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.applet;

import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JApplet;
import javax.swing.SwingUtilities;
import org.exmaralda.common.helpers.RelativePath;
import org.exmaralda.exakt.exmaraldaSearch.COMADBCorpus;
import org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICSearchPanel;
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.exmaralda.exakt.search.SimpleSearchResult;
import org.exmaralda.exakt.search.swing.KWICTableEvent;
import org.exmaralda.exakt.search.swing.KWICTableListener;

/**
 *
 * @author thomas
 */
public class ConcordanceApplet extends JApplet implements KWICTableListener, SearchListenerInterface {

    COMAKWICSearchPanel kwicPanel;
    COMADBCorpus corpus;

    /**
     * Initialization method that will be called after the applet is loaded
     * into the browser.
     */
    @Override
    public void init() {
        // TODO start asynchronous download of heavy resources
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
        //corpus = new COMARemoteCorpus();
        corpus = new COMADBCorpus();
        corpus.addSearchListener(this);
        System.out.println("Corpus created.");
        try {
            String corpusName = "MAPTASK";
            String connection = "jdbc:mysql://vs.corpora.uni-hamburg.de/corpora";
            String usr = "WontTell";
            String pwd = "WontTell";
            corpus.readCorpus(corpusName, connection, usr, pwd);

            //corpus.readCorpus(new URL("file://S:/TP-Z2/DATEN/EXMARaLDA_DemoKorpus/MiniDemo.coma"));
            System.out.println("Corpus read.");
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) {
            System.err.println("createGUI didn't complete successfully");
            e.printStackTrace();
        }
    }

    void createGUI(){
        kwicPanel = new org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICSearchPanel(corpus, true);
        System.out.println("KWIC Panel created");
        kwicPanel.configureForApplet();
        System.out.println("KWIC Panel configured");
        kwicPanel.addKWICTableListener(this);
        this.setContentPane(kwicPanel);
        //getContentPane().add(kwicPanel);
    }

    @Override
    public void destroy() {
        System.out.println("Destroying");
        super.destroy();
    }

    @Override
    public void start() {
        System.out.println("Starting");
        super.start();
    }

    @Override
    public void stop() {
        System.out.println("Stopping");
        super.stop();
    }

    @Override
    public void processEvent(KWICTableEvent ev) {
        if (ev.getType() == KWICTableEvent.DOUBLE_CLICK){
            showTranscription(ev);
        }
    }

    @Override
    public void processSearchEvent(SearchEvent se) {
        switch(se.getType()){
            case SearchEvent.CORPUS_INIT_PROGRESS :
                String progressString = (String)(se.getData());
                int currentProgress = (int)Math.round(se.getProgress()*100);
                System.out.println(progressString + " " + Integer.toString(currentProgress) + "%");
                break;
            case SearchEvent.SEARCH_COMPLETED :
                System.out.println("Search completed!");
                break;
        }
    }

    // TODO overwrite start(), stop() and destroy() methods

    private void showTranscription(KWICTableEvent event) {
        SimpleSearchResult searchResult = (SimpleSearchResult)(event.getSelectedSearchResult());
        // this is the filename of the segmented transcription (hopefully...)
        String filename = searchResult.getSearchableSegmentLocator().getCorpusComponentFilename();
        String fullFilename = RelativePath.resolveRelativePath(filename,corpus.getCorpusPath());
        int index = fullFilename.lastIndexOf("/");
        String name = fullFilename.substring(index+1);
        int index2 = name.indexOf("_s.exs");
        String nakedName = name.substring(0,index2);
        String ulistFilename = fullFilename.substring(0, index) + "/presentation/" + nakedName + "_ulist.html";
        String timeID = searchResult.getAdditionalData()[2];
        try {
            // "http://vs.corpora.uni-hamburg.de/corpora/z2-hamatac/protected/Shirin_Zhi_Zhi/presentation/MT_270110_Shirin_ulist.html"
            System.out.println("******" + ulistFilename);
            URL url = new URL(ulistFilename + "#" + timeID);            
            getAppletContext().showDocument(url, "_blank");
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

    }

}
