/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.lexicon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import org.jdom.Document;

/**
 *
 * @author thomas
 */
public class SimpleRDBLexicon extends AbstractNormalizationLexicon {

    Connection conn;
    Statement stmt;
    PreparedStatement stmt2;

    public void put(String form, String correspondingForm, String transcriptionID, String wordID) throws LexiconException {
        try {
            stmt2.setString(1, form); // set input parameter 1
            stmt2.setString(2, correspondingForm); // set input parameter 1
            stmt2.executeUpdate(); // execute insert statement)
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new LexiconException(ex);
        }
    }

    @Override
    public List<String> getCandidateForms(String form) throws LexiconException {
        HashSet<String> result = new HashSet<String>();
        try {
            ResultSet srs = stmt.executeQuery("SELECT form_lemma.LEMMA FROM form_lemma WHERE FORM='" + form + "'");
            while (srs.next()) {
               String lemma = srs.getString("Lemma");
               result.add(lemma);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new LexiconException(ex);
        }
        result.addAll(super.getCandidateForms(form));
        Vector<String> resultVector = new Vector<String>();
        for (String e : result){
            resultVector.addElement(e);
        }
        return resultVector;
    }



    public void read(Object source) throws IOException {
        String rdbPath = (String)source;
        try {
            conn = DriverManager.getConnection(rdbPath, "root", "Bernd!Moos");
            stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            String query = "insert into form_lemma(FORM, LEMMA) values(?, ?)";
            stmt2 = conn.prepareStatement(query); // create a statement
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IOException(ex);
        }

    }

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
        return -1;
    }

    @Override
    public boolean hasFrequencyInformation() {
        return false;
    }

    @Override
    public boolean hasCapitalInformation() {
        return false;
    }

    @Override
    public boolean isCapitalOnly(String form) {
        return false;
    }

    @Override
    public void update(Document d) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
