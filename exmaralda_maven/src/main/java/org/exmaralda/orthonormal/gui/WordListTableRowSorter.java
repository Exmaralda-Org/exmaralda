/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.gui;

import java.util.Comparator;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableStringConverter;
import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class WordListTableRowSorter extends TableRowSorter<WordListTableModel> {

    public WordListTableRowSorter(WordListTableModel model) {
        super(model);
    }

    @Override
    public Comparator getComparator(int column) {
        return String.CASE_INSENSITIVE_ORDER;
    }

    @Override
    public TableStringConverter getStringConverter() {
        return new TableStringConverter(){

            @Override
            public String toString(TableModel model, int row, int column) {
                Element word = ((WordListTableModel)model).words.get(row);
                String text = "";
                switch (column) {
                    case 0 :
                        text = Integer.toString(row+1);
                        break;
                    case 0+1:
                        text = word.getText();
                        break;
                    case 1+1:
                        if (word.getAttribute("n")!=null){
                            text = word.getAttributeValue("n");
                        }   break;
                    case 2+1:
                        if (word.getAttribute("lemma")!=null){
                            text = word.getAttributeValue("lemma");
                        }   break;
                    case 3+1:
                        if (word.getAttribute("pos")!=null){
                            text = word.getAttributeValue("pos");
                        }   break;
                    case 4+1:
                        if (word.getAttribute("p-pos")!=null){
                            text = word.getAttributeValue("p-pos");
                        }   break;
                    default:
                        break;
                }
                return text;
            }

        };
    }



}
