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
                if (column==0){
                    text = word.getText();
                } else if (column==1){
                    if (word.getAttribute("n")!=null){
                        text = word.getAttributeValue("n");
                    }
                } else if (column==2){
                    if (word.getAttribute("lemma")!=null){
                        text = word.getAttributeValue("lemma");
                    }
                } else if (column==3){
                    if (word.getAttribute("pos")!=null){
                        text = word.getAttributeValue("pos");
                    }
                } else if (column==4){
                    if (word.getAttribute("p-pos")!=null){
                        text = word.getAttributeValue("p-pos");
                    }
                }
                return text;
            }

        };
    }



}
