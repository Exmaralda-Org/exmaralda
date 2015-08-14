/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch;

import java.sql.SQLException;

/**
 *
 * @author thomas
 */
public class TestDBCorpus {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new TestDBCorpus().doit();
    }

    private void doit() {
        try {
            String corpusName = "Dolmetschen im Krankenhaus";
            String connection = "jdbc:mysql://134.100.126.70:3306/exmaraldawww";
            String usr = "root";
            String pwd = "*****";
            COMADBCorpus corpus = new COMADBCorpus();
            corpus.readCorpus(corpusName, connection, usr, pwd);
            corpus.getCommunicationAttributes();
            System.out.println(corpus.getCommunicationData(connection, "Name*"));
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

}
