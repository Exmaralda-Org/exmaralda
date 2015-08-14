/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.tokenlist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author thomas
 */
public class IOUtilities {

    static String HTML_PREFIX = "<html><head>"
                                + "<style type=\"text/css\">"
                                + "body {font-family:sans-serif}"
                                + "table{border-collapse:collapse}"
                                + "td.token_odd {background-color:rgb(230,230,230); text-align:left;}"
                                + "td.token_even {background-color:white; text-align:left;}"
                                + "td.count_odd {background-color:rgb(230,230,230); text-align:right;}"
                                + "td.count_even {background-color:white; text-align:right;}"
                                + "</style>"
                                + "<title>Word list</title>"
                                + "<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />"
                                + "</head>"
                                + "<body><table>";
    
    static String HTML_SUFFIX = "</table></body></html>";
    
    public static void writeHTML(AbstractTokenList list, File file) throws FileNotFoundException, IOException{
        writeHTML(list, file, AbstractTokenList.ALPHABETICALLY_SORTED);
    }

    public static void writeHTML(AbstractTokenList list, File file, int sorting) throws FileNotFoundException, IOException{
        System.out.println("started writing document..." + file.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(HTML_PREFIX.getBytes("UTF-8"));
        List<String> l = list.getTokens(sorting);
        int count = 0;
        for (String t : l){
            count++;
            String eo = "even";
            if (count%2==0) eo ="odd";
            String text =   "<tr><td class=\"token_" + eo + "\">"
                            + t
                            + "</td><td class=\"count_" + eo + "\">"
                            + Integer.toString(list.getTokenCount(t))
                            + "</td></tr>";
            fos.write(text.getBytes("UTF-8"));
        }
        fos.write(HTML_SUFFIX.getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");
    }

    public static void writeText(AbstractTokenList list, File file) throws FileNotFoundException, IOException{
        System.out.println("started writing document..." + file.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(file);
        List<String> l = list.getTokens(AbstractTokenList.ALPHABETICALLY_SORTED);
        for (String t : l){
            String text = t + "\t" + Integer.toString(list.getTokenCount(t)) + "\n";
            fos.write(text.getBytes("UTF-8"));
        }
        fos.close();
        System.out.println("document written.");
    }

}
