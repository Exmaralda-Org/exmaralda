/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.lexicon;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;


/**
 *
 * @author thomas
 */
public class RDBLexicon extends AbstractNormalizationLexicon {

    Connection conn;
    Statement stmt;
    PreparedStatement insertStatement;
    PreparedStatement countStatement;
    
    Hashtable<String, Vector<String>> candidateCache = new Hashtable<String, Vector<String>>();


    @Override
    public void put(String form, String lemma, String transcriptionID, String wordID) throws /*up*/ LexiconException {
        try {
            // delete the entry for this word if there already is one
            stmt.execute("DELETE FROM form_lemma WHERE TRANSCRIPTION_ID='" + transcriptionID + "' AND WORD_ID='" + wordID + "'");
            
            //if (!(form.equals(lemma))){
                // then insert the new entry for this word
                //System.out.println("######## Putting " + form + " ---> " + lemma);
                insertStatement.setString(1, transcriptionID); // set input parameter 1
                insertStatement.setString(2, wordID); // set input parameter 2
                insertStatement.setString(3, form); // set input parameter 3
                insertStatement.setString(4, lemma); // set input parameter 4
                insertStatement.executeUpdate(); // execute insert statement)
            //}
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new LexiconException(ex);
        }
    }

    @Override
    public List<String> getCandidateForms(String form) throws /*up*/ LexiconException {
        if (candidateCache.containsKey(form)){
            return candidateCache.get(form);
        }
        long t0 = System.currentTimeMillis();
        Hashtable<String,Integer> formsWithFrequencies = new Hashtable<String,Integer>();
        try {
            String escapedForm = form.replaceAll("'", "''");
            ResultSet srs = stmt.executeQuery("SELECT form_lemma.LEMMA FROM form_lemma WHERE FORM='" + escapedForm + "'");
            while (srs.next()) {
               String lemma = srs.getString("Lemma");
               if (!(formsWithFrequencies.containsKey(lemma))){
                   formsWithFrequencies.put(lemma, new Integer(1));
               } else {
                   formsWithFrequencies.put(lemma, new Integer(formsWithFrequencies.get(lemma).intValue()+1));
               }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Form: " + form);
            throw new LexiconException(ex);
        }
        
        Vector<FormAndFrequency> v = new Vector<FormAndFrequency>();
        for (String lemma : formsWithFrequencies.keySet()){
            v.add(new FormAndFrequency(lemma, formsWithFrequencies.get(lemma).intValue()));
        }
        Collections.sort(v, new Comparator<FormAndFrequency>(){
            public int compare(FormAndFrequency o1, FormAndFrequency o2) {
                if (o1.frequency>o2.frequency) return -1;
                if (o1.frequency<o2.frequency) return +1;
                return (o1.form.compareTo(o2.form));
            }            
        });

        Vector<String> resultVector = new Vector<String>();
        for (FormAndFrequency faf : v){
            resultVector.addElement(faf.form);
        }
        resultVector.addAll(super.getCandidateForms(form));
        long t1 = System.currentTimeMillis();
        if ((t1-t0)>100){
            //cache those which take long
            candidateCache.put(form, resultVector);
        }
        
        return resultVector;
    }



    @Override
    public void read(Object source) throws IOException {
        String[] parameters = (String[]) source;
        String rdbPath = parameters[0];
        String user = parameters[1];
        String password = parameters[2];
        try {
            conn = DriverManager.getConnection(rdbPath, user, password);
            stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            String query = "insert into form_lemma(TRANSCRIPTION_ID, WORD_ID, FORM, LEMMA) values(?, ?, ?, ?)";
            insertStatement = conn.prepareStatement(query); // create a statement

            String query2 = "SELECT COUNT(*) FROM form_lemma WHERE FORM=? AND LEMMA=?";
            countStatement = conn.prepareStatement(query2);
            
            /*ResultSet srs = stmt.executeQuery("SELECT form FROM capital_forms");
            while (srs.next()){
                capitals.add(srs.getString("FORM"));
                System.out.println("adding" +  srs.getString("FORM"));
            }*/
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IOException(ex);
        }
        
        

    }
    

    @Override
    public void write(Object target) throws IOException {
        try {
            // do nothing, needs not be written, is a database, don't worry, everything will be fine, fine, just fine
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IOException(ex);
        }
    }

    @Override
    public int getFrequency(String form, String correspondingForm) {
        try {
            countStatement.setString(1, form);
            countStatement.setString(2, correspondingForm);
            ResultSet srs = countStatement.executeQuery();
            srs.next();
            int freq = srs.getInt(1);
            //System.out.println("Frequency " + form + " " + correspondingForm + " " + freq);
            return freq;
            
            /*countStatement.getResultSet().
            ResultSet srs = stmt.executeQuery("SELECT COUNT(*) FROM form_lemma WHERE FORM='" + form + "' AND LEMMA='" + correspondingForm + "'");
            stmt.
            int count = 0;
            while (srs.next()) {
                count++;
            }
            return count;*/
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean hasFrequencyInformation() {
        return true;
    }
    
    public void clear() throws SQLException{
        stmt.execute("DELETE FROM form_lemma");        
    }
    
    
    public void update(File[] files) throws JDOMException, IOException, LexiconException, SQLException{
        for (File f : files){
            update(f);
        }
    }
    
    public void update(File file) throws JDOMException, IOException, LexiconException, SQLException{
            System.out.println("Processing " + file.getName());
            Document d = FileIO.readDocumentFromLocalFile(file);
            update(d);
    }
    
    public void update(Document d) throws SQLException, JDOMException, LexiconException{
        String transcriptionID = d.getRootElement().getAttributeValue("id");
        stmt.execute("DELETE FROM form_lemma WHERE TRANSCRIPTION_ID='" + transcriptionID + "'");
        List l = XPath.newInstance("//w").selectNodes(d);
        for (Object o : l){
            Element w = (Element)o;
            // <w id="w67" n="frage">frag</w>
            String wordID = w.getAttributeValue("id");
            String form = WordUtilities.getWordText(w);
            String lemma = form;
            if (w.getAttribute("n")!=null){
                  lemma = w.getAttributeValue("n");
            }
            put(form, lemma, transcriptionID, wordID);
        }        
        
    }
    
    
    public Document dumpXML() throws SQLException{
        Document result = new Document(new Element("XML-Lexicon-Dump"));
        ResultSet srs = stmt.executeQuery("SELECT * FROM form_lemma");
        while (srs.next()) {
           String transcriptID = srs.getString("TRANSCRIPTION_ID");
           String wordID = srs.getString("WORD_ID");
           String lemma = srs.getString("LEMMA");
           String form = srs.getString("FORM");
           Element entry = new Element("entry");
           entry.setAttribute("t-id", transcriptID);
           entry.setAttribute("w-id", wordID);
           entry.setAttribute("lemma", lemma);
           entry.setAttribute("form", form);
           result.getRootElement().addContent(entry);
        }        
        return result;
        
    }

    @Override
    public boolean hasCapitalInformation() {
        return true;
    }

    @Override
    public boolean isCapitalOnly(String form) throws LexiconException {
        try {
            String escapedForm = form.replaceAll("'", "''");
            ResultSet srs = stmt.executeQuery("SELECT * FROM capital_forms WHERE FORM='" + escapedForm + "'");
            return srs.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("FORM: " + form);
            throw new LexiconException(ex);
        }
        //return capitals.contains(form);
    }

}
class FormAndFrequency {
    String form;
    int frequency;

    public FormAndFrequency(String form, int frequency) {
        this.form = form;
        this.frequency = frequency;
    }


}
