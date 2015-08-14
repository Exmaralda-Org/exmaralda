/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.JDOMException;


/**
 *
 * @author Schmidt
 */
public class QueryTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            try {
                try {
                    new QueryTest().doit();
                } catch (JDOMException ex) {
                    Logger.getLogger(QueryTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(QueryTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(QueryTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(QueryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static String XML_STRING="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private Connection connection;
    //String query = "select extract(object_value, '//contribution[descendant::w[@lemma=\"gehen\"]]').getClobVal() from pragdb.t_folker_dgd where existsNode(OBJECT_VALUE, '//w[@lemma=\"gehen\"]')=1";
    //String query = "select extract(object_value, '//w[@lemma=\"gehen\"]').getClobVal() from pragdb.t_folker_dgd where existsNode(OBJECT_VALUE, '//w[@lemma=\"gehen\"]')=1";
    //String query = "select extract(object_value, '//folker-transcription/@dgd-id).getClobVal(),extract(object_value, '//w[@lemma=\"gehen\"]').getClobVal() from pragdb.t_folker_dgd where existsNode(OBJECT_VALUE, '//w[@lemma=\"gehen\"]')=1";
    String WORD ="laufen";
    String query = "select "
            + "extract(object_value, '/folker-transcription/@dgd-id').getStringVal(), "
            + "extract(object_value, '//w[@lemma=\"" + WORD + "\"]/@id').getClobVal(), "
            + "extract(object_value, '//contribution[descendant::w[@lemma=\""+ WORD + "\"]]').getClobVal()"
            + "from pragdb.t_folker_dgd where "
            + "existsNode(OBJECT_VALUE, '//w[@lemma=\"" + WORD + "\"]')=1";

    private void doit() throws SQLException, ClassNotFoundException, IOException, JDOMException {
        setupConnection();
        Statement stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        System.out.println(query);
        ResultSet srs = stmt.executeQuery(query);
        
        //XMLType x;
        int count = 1;
        StringBuffer buffer = new StringBuffer();
        buffer.append(XML_STRING);
        buffer.append("<results>");
        while (srs.next()) {
            //srs.getString("C_TRANSCR_FILE");
            //BufferedReader reader = new BufferedReader(new
            //    InputStreamReader(srs.getClob(1).getAsciiStream()));
            //String read = null;
            buffer.append("<result transcript-id='" + srs.getString(1) + "'>");
            String[] wordIDs = srs.getString(2).substring(1).split("[w]");
            for (String wordID : wordIDs){
                buffer.append("<wordID>w" + wordID + "</wordID>");
            }
            
            //buffer.append(srs.getString(2));
            buffer.append(srs.getString(3));
            /*while((read = reader.readLine()) != null ){
                buffer.append(read);
            }*/
            buffer.append("</result>");
            //System.out.println(buffer.toString());
            //Document doc = FileIO.readDocumentFromString(buffer.toString());
            //FileIO.writeDocumentToLocalFile(new File("C:\\Users\\Schmidt\\Desktop\\query\\" + "result" + Integer.toString(count) + ".xml"), doc);
                /*oracle.sql.OPAQUE x = (oracle.sql.OPAQUE) srs.getObject(1);          
                System.out.println(x.getValue().toString());
                System.out.println(x.getDescriptor().toXMLString());*/
            count++;
       }        
       buffer.append("</results>");
       Document doc = FileIO.readDocumentFromString(buffer.toString());
       FileIO.writeDocumentToLocalFile(new File("C:\\Users\\Schmidt\\Desktop\\query\\" + "allResults.xml"), doc);
    }
    
    private void setupConnection() throws SQLException, ClassNotFoundException {
        Class.forName ("oracle.jdbc.OracleDriver");
        java.sql.DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());        
        String rdbPath = "jdbc:oracle:thin:@10.0.1.22:1521:orcl";
        String user = "tomcat_user";
        String password = "TomCat_22";
        connection = DriverManager.getConnection(rdbPath, user, password);
        //connection = DriverManager.getConnection ("jdbc:odbc:thin@192.168.2.107:1521:Schemaname", "username", "password"); 
        System.out.println("************** Conncetion established *************** ");
    }
    
}
