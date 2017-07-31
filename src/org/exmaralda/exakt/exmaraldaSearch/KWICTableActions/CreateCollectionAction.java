package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.exmaralda.exakt.exmaraldaSearch.COMACorpusInterface;
import org.exmaralda.exakt.exmaraldaSearch.swing.CreateCollectionDialog;
import org.exmaralda.exakt.search.SearchResultList;
import org.jdom.Document;

/*
 * Created on 03. Juni 2017, 14:45
 * This class is used to transform an EXAKT search result (XML) into a collection of EXBs derived from the originals.  
 * Several parameters are set in and read from a dialog, e.g. the range of left and right context.
 */


/**
 * @author Daniel Jettka
 */

public class CreateCollectionAction extends org.exmaralda.exakt.exmaraldaSearch.swing.AbstractEXAKTAction {

    public static final String CREATE_COLLECTION_FROM_SEARCH_RESULTS_XSLT = "/org/exmaralda/exakt/resources/SearchResults2EXBs.xsl";

    
    /** Creates a new instance of CreateCollectionAction */
    public CreateCollectionAction(org.exmaralda.exakt.exmaraldaSearch.swing.EXAKT ef, String title, javax.swing.ImageIcon icon){
        super(ef, title, icon);
    }

    
    public void actionPerformed(ActionEvent e) {

        //open modal dialogue
        CreateCollectionDialog dialog = new CreateCollectionDialog(new JFrame(), true);
        dialog.setVisible(true);

        //proceeding when dialog was closed with valid parameters
        if(dialog.isDialogApproved()){

            //fetch information about COMA
            COMACorpusInterface corpus = exaktFrame.getActiveSearchPanel().getCorpus();
            String ComaPath = corpus.getCorpusPath();

            //get the current KWIC results
            StreamSource searchResultSource = null;
            String searchResultString = null;
            try{
                SearchResultList list = exaktFrame.getActiveSearchPanel().getSearchResultList();
                Vector<String[]> meta = exaktFrame.getActiveSearchPanel().getMeta();
                Document searchResultDoc = org.exmaralda.exakt.utilities.FileIO.COMASearchResultListToXML(list, corpus, meta, exaktFrame.getActiveSearchPanel().getCorpus().getCorpusPath());
                searchResultString = org.exmaralda.exakt.utilities.FileIO.getDocumentAsString(searchResultDoc);
                searchResultSource = new StreamSource(new StringReader(searchResultString));
            } catch(IOException ioE){
                String message = "Stylesheet transformation failed:";
                message += ioE.getMessage() + "\n";
                javax.swing.JOptionPane.showMessageDialog(exaktFrame, message);
                ioE.printStackTrace();
                return;
            }

            //XSLT transformation
            try {
                //get stylesheet
                java.io.InputStream is = getClass().getResourceAsStream(CREATE_COLLECTION_FROM_SEARCH_RESULTS_XSLT);
                StreamSource xsltSource = new StreamSource(is);

                //create TransformerFactory and TransformerInstance
                TransformerFactory tf = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
                Transformer t = tf.newTransformer(xsltSource);
                t.setParameter("COMA_PATH", ComaPath);
                t.setParameter("OUTPUT_DIRECTORY", dialog.getOutputDirectory());
                t.setParameter("TEMPLATE_FILE", dialog.getTemplateFile());
                t.setParameter("LEFT_CONTEXT", dialog.getLeftContext().intValue());
                t.setParameter("RIGHT_CONTEXT", dialog.getRightContext().intValue());
                t.setParameter("RESET_TIMES", dialog.getResetTime().toString());
                t.setParameter("ANNOTATION_TEXT", dialog.getAnnotationText());                
                t.setParameter("OPERATING_SYSTEM", System.getProperty("os.name"));

                //transform and fetch result as string
                String result = "";
                StreamSource resultSource = new StreamSource(new StringReader(result));
                StringWriter resultWriter = new StringWriter();
                t.transform(searchResultSource, new StreamResult(resultWriter));
                
                // result is not saved anywhere (could be done for debugging), 
                // because all result EXBs should be returned by stylesheet directly)
                result = resultWriter.toString();                

            } catch (TransformerException ex) {
                String message = "Stylesheet transformation failed:";
                message += ex.getMessage() + "\n";
                javax.swing.JOptionPane.showMessageDialog(exaktFrame, message);
                ex.printStackTrace();
                return;
            }

        }
    }
}

