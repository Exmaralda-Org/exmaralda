/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.helpers;

import java.util.List;
import java.util.Vector;

/**
 *
 * @author thomas
 */
public class LevenshteinComparator {

    /** goes through each item list1 and tries to find items in list 2
     * whose Levenshtein distance is greater than 0 less than maxDistance
     * returns all those items as a list
     * @param list1
     * @param list2
     * @param maxDistance
     * @return
     */
    public static List<String> getNeighbours(List<String> list1, List<String> list2, double maxDistance){
        LevenshteinDistance levenshtein = new LevenshteinDistance();
        Vector<String> result = new Vector<String>();
        for (String item1 : list1){
            for (String item2 : list2){
                double distance = levenshtein.levenshteinDistance(item1, item2);
                if ((distance<=maxDistance) && (distance>0) && (!(list1.contains(item2)))){
                    result.add(item2);
                }
            }
        }
        return result;
    }

    public static List<String> getNeighbours(String s, List<String> list2, double maxDistance){
        Vector<String> list = new Vector<String>();
        list.addElement(s);
        return getNeighbours(list, list2, maxDistance);
    }

    public static List<String> getNeighbours(String pho, Vector<String> list2, double maxDistance, Vector<String> combinations) {
        Vector<String> list1 = new Vector<String>();
        list1.addElement(pho);
        LevenshteinDistance levenshtein = new LevenshteinDistance();
        // make a vector for storing the results
        Vector<String> result = new Vector<String>();
        // go through the first list and
        // replace symbol combinations specified in the combinations list
        // with single symbols
        for (String item1 : list1){
            String modifiedItem1 = item1;
            for (int pos=0; pos<combinations.size(); pos++){
                String combination = combinations.elementAt(pos);
                // as a replacement: choose capital letters
                // this will work for IPA strings because IPA has
                // no capital letters
                //char r = (char)(65+pos);
                // I'd rather choose cyrillic letters because
                // sampe does hove captital letters
                char r = (char)(1024+pos);
                String replacement = new Character(r).toString();
                modifiedItem1 = modifiedItem1.replace(combination, replacement);
            }
            // now go through the other list (this comes from the lexicon) item by item
            for (String item2 : list2){
                // do the same replacement as above
                String modifiedItem2 = item2;
                for (int pos=0; pos<combinations.size(); pos++){
                    String combination = combinations.elementAt(pos);
                    //char r = (char)(65+pos);
                    char r = (char)(1024+pos);
                    String replacement = new Character(r).toString();
                    modifiedItem2 = modifiedItem2.replace(combination, replacement);
                }

                // determine the Levenshtein distance between the current item of the first list
                // and the current item of the other list
                double distance = levenshtein.levenshteinDistance(modifiedItem1, modifiedItem2);
                // if it is smaller than the specified distance...
                if ((distance<=maxDistance) && (distance>0) && (!(list1.contains(item2)))){
                    // add it to the result
                    result.add(item2);
                }
            }
        }
        // return the result
        return result;

    }


}
