/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.lexicon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 *
 * @author thomas
 */
public class MakeCapitalWordList {

    String IN = "S:\\Mitarbeiter\\Thomas\\IDS\\GAT\\orthonormal\\derewo-v-ww-bll-250000g-2011-12-31-0.1.txt";
    String IN2 = "S:\\Mitarbeiter\\Thomas\\IDS\\GAT\\orthonormal\\derewo-v-100000t-2009-04-30-0.1.txt";
    private String OUT = "C:\\EXMARaLDA_FRESHEST\\src\\org\\exmaralda\\orthonormal\\lexicon\\dereko_capital_only.txt";
    
    HashSet<String> allForms = new HashSet<String>();
    ArrayList<String> capitalForms = new ArrayList<String>();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new MakeCapitalWordList().doit();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SQLException ex){
            ex.printStackTrace();            
        }
    }

    private void doit() throws FileNotFoundException, IOException, SQLException {
        FileReader fr = new FileReader(new File(IN));
        BufferedReader br = new BufferedReader(fr);
        String nextLine = new String();
        System.out.println("Started reading document");        
        while ((nextLine = br.readLine()) != null){
            if (nextLine.startsWith("#")) continue; // comment line
            int index = nextLine.indexOf(" ");
            if (index<0) continue;
            String form = nextLine.substring(0,index);
            if (!(form.matches("[A-Za-zÄÖÜäöüß]+"))) continue;
            //System.out.println(form);
            allForms.add(form);
        }
        br.close();
        
        FileReader fr2 = new FileReader(new File(IN2));
        BufferedReader br2 = new BufferedReader(fr2);
        System.out.println("Started reading document");        
        while ((nextLine = br2.readLine()) != null){
            if (nextLine.startsWith("#")) continue; // comment line
            int index = nextLine.indexOf(" ");
            if (index<0) continue;
            String form = nextLine.substring(0,index);
            if (!(form.matches("[A-Za-zÄÖÜäöüß]+"))) continue;
            //System.out.println(form);
            allForms.add(form);
        }
        br.close();

        for (String form : allForms){
            if (!(form.matches("^[A-ZÄÖÜ].*"))) continue;
            String lowerForm = form.toLowerCase();
            if (allForms.contains(lowerForm)) continue;
            capitalForms.add(form);
            //System.out.println(form);
        }
        
        System.out.println(capitalForms.size() + " of " + allForms.size());
        Collections.sort(capitalForms);
        
        FileOutputStream fos = new FileOutputStream(OUT);
        int count=0;
        for (String f : capitalForms){
            fos.write(f.getBytes());
            fos.write(System.getProperty("line.separator").getBytes());                   
            count++;
            if (count%1000==0) System.out.println(count);
        }
        fos.close();                
        
        /*String RDB_URL = "jdbc:oracle:thin:@193.196.8.35:1521:orc11";
        String RDB_USERNAME = "orthonormal";
        String RDB_PASSWORD = "---";
        Connection conn = DriverManager.getConnection(RDB_URL, RDB_USERNAME, RDB_PASSWORD);
        Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);        
        stmt.execute("DELETE FROM capital_forms");        

        PreparedStatement insertStatement;
        String query = "insert into CAPITAL_FORMS(FORM) values(?)";
        insertStatement = conn.prepareStatement(query); 
        
        
        for (String form : capitalForms){
            insertStatement.setString(1, form); // set input parameter 1
            insertStatement.executeUpdate(); // execute insert statement)            
        }*/
        
    }
}
