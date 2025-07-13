/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.orthonormal.lexicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bernd
 */
public class NORMALIZATION_PROFILES {
    
    public static final List<NormalizationProfile> PROFILES = new ArrayList<>();
    static final Map<String, NormalizationProfile> MAP = new HashMap<>();
    
    static {
       NormalizationProfile cGATProfile = new NormalizationProfile (
               "German / FOLK", "cGAT_MINIMAL", 
               XMLLexicon.DEFAULT_LEXICON);
       PROFILES.add(cGATProfile);
       
       NormalizationProfile gosProfile = new NormalizationProfile (
               "Slovene / GOS", "GENERIC", 
               "/org/exmaralda/orthonormal/lexicon/GOS_Normalization_Lexicon_MAY_2025.xml",
               true
       );
       PROFILES.add(gosProfile);
       
       
       NormalizationProfile kompasProfile = new NormalizationProfile (
               "Swiss German / KompAS", "cGAT_MINIMAL", 
               "/org/exmaralda/orthonormal/lexicon/KOMPAS_Normalization_Lexicon_JULY_2025.xml"
       );
       PROFILES.add(kompasProfile);

       for (NormalizationProfile profile : PROFILES){
           MAP.put(profile.name, profile);
       }
    }
    
    public static NormalizationProfile getProfileForName(String name){
        return MAP.get(name);
    }
    
    
    
}
