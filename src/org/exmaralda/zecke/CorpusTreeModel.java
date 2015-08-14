/*
 * CorpusTreeModel.java
 *
 * Created on 10. Juni 2004, 15:29
 */

package org.exmaralda.zecke;

import javax.swing.tree.*;

/**
 *
 * @author  thomas
 */
public class CorpusTreeModel extends DefaultTreeModel {
        
    
    public String[] E5_FILES =
        {"EFE04dt_Alt_0352a_f_ENF_010992\\EFE04dt_Alt_0352a_1_ENF",
        "EFE04dt_Bin_0722_f_SKO_100203\\EFE04dt_Bin_0722_SKO",
        "EFE04dt_Gök_0321a_f_ENF_191292\\EFE04dt_Gök_0321a_1_ENF",
        "EFE04dt_Gök_0321a_f_ENF_191292\\EFE04dt_Gök_0321a_2_ENF",
        "EFE04dt_Kub_0717_f_SKO_190501\\EFE04dt_Kub_0717_1_SKO",
        "EFE04dt_Kub_0717_f_SKO_190501\\EFE04dt_Kub_0717_2_SKO",
        "EFE04dt_Kub_0717_f_SKO_190501\\EFE04dt_Kub_0717_3_SKO",
        "EFE04dt_Nad_0469_f_ENF_140796\\EFE04dt_Nad_0469_1_ENF",
        "EFE04dt_Nad_0469_f_ENF_140796\\EFE04dt_Nad_0469_2_ENF",
        "EFE04dt_Per_0272a_f_ENF_160892\\EFE04dt_Per_0272a_1_ENF",
        "EFE04dt_Seh_0328a_f_ENF_131192\\EFE04dt_Sem_0328a_1_ENF",
        "EFE04dt_Sey_0184b_f_ENF_260492\\EFE04dt_Sey_0184b_1_ENF",
        "EFE04dt_Sey_0184b_f_ENF_260492\\EFE04dt_Sey_0184b_2_ENF",
        "EFE04dt_Sey_0184b_f_ENF_260492\\EFE04dt_Sey_0184b_3_ENF",
        "EFE04dt_Sey_0184b_f_ENF_260492\\EFE04dt_Sey_0184b_4_ENF",
        "EFE04dt_Zer_0068_f_ENF_020792\\EFE04dt_Zer_0068_1_ENF",
        "EFE04dt_Zer_0068_f_ENF_020792\\EFE04dt_Zer_0068_2_ENF",
        "EFE04tk_Alt_0352a_f_ENF_010992\\EFE04tk_Alt_0352a_1_ENF",
        "EFE04tk_Alt_0352a_f_ENF_010992\\EFE04tk_Alt_0352a_2_ENF",
        "EFE04tk_Azi_0305a_f_ENF_010593\\EFE04tk_Azi_0305a_1_ENF",
        "EFE04tk_Can_0338a_f_ENF_290393\\EFE04tk_Can_0338a_1_ENF",
        "EFE04tk_Eme_0826_f_SKO_250801\\EFE04tk_Eme_0826_1_SKO",
        "EFE04tk_Eme_0826_f_SKO_250801\\EFE04tk_Eme_0826_2_SKO",
        "EFE04tk_Gök_0321_f_ENF_191292\\EFE04tk_Gök_0321_1_ENF",
        "EFE04tk_Gök_0321_f_ENF_191292\\EFE04tk_Gök_0321_2_ENF",
        "EFE04tk_Gök_0321_f_ENF_191292\\EFE04tk_Gök_0321_3_ENF",
        "EFE04tk_Kub_0736_f_SKO_190501\\EFE04tk_Kub_0736_1_SKO",
        "EFE04tk_Per_0272a_f_ENF_160892\\EFE04tk_Per_0272a_1_ENF",
        "EFE04tk_Seh_0328a_f_ENF_131192\\EFE04tk_Seh_0328a_1_ENF",
        "EFE04tk_Seh_0328a_f_ENF_131192\\EFE04tk_Seh_0328a_2_ENF",
        "EFE04tk_Seh_0328a_f_ENF_131192\\EFE04tk_Seh_0328a_3_ENF",
        "EFE04tk_Zer_0068a_f_ENF_020792\\EFE04tk_Zer_0068a_1_ENF",
        "EFE04tk_Zer_0068a_f_ENF_020792\\EFE04tk_Zer_0068a_2_ENF"};
        
     public String[] K2_FILES = 
        {"10\\P-10",
        "11\\P-11",
        "12\\P-12",
        "14\\T-AUF-14",
        "37_1\\PSD-37-1",
        "38\\P-38",
        "39\\PSD-39",
        "43\\T-AUF-43",
        "45\\T-AUF-45",
        "5\\T-AUF-5",
        "52\\D-AUF-52",
        "56\\D-AUF-56",
        "8\\T-AUF-8"};

    /** Creates a new instance of CorpusTreeModel */
    public CorpusTreeModel(TreeNode root) {
        super(root);
        DefaultMutableTreeNode r = (DefaultMutableTreeNode)(root);
        DefaultMutableTreeNode subCorpus1 = new DefaultMutableTreeNode("DiK");
        DefaultMutableTreeNode subCorpus2 = new DefaultMutableTreeNode("SKOBI");
        for (int pos=0; pos<K2_FILES.length; pos++){
            String transPath = K2_FILES[pos];
            String transName = transPath.substring(transPath.lastIndexOf('\\')+1);
            subCorpus1.add(new DefaultMutableTreeNode(transName));
        }
        for (int pos=0; pos<E5_FILES.length; pos++){
            String transPath = E5_FILES[pos];
            String transName = transPath.substring(transPath.lastIndexOf('\\')+1);
            subCorpus2.add(new DefaultMutableTreeNode(transName));
        }
        r.add(subCorpus1);
        r.add(subCorpus2);
    }
    
}
