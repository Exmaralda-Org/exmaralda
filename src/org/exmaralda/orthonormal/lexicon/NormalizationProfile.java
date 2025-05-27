/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.orthonormal.lexicon;

/**
 *
 * @author bernd
 */
public class NormalizationProfile {
    
    public String name;
    public String segmentationAlgorithmName;
    public String lexiconPath;
    public boolean switchTiers = false;

    public NormalizationProfile(String name, String segmentationAlgorithmName, String lexiconPath) {
        this.name = name;
        this.segmentationAlgorithmName = segmentationAlgorithmName;
        this.lexiconPath = lexiconPath;
    }
    
    public NormalizationProfile(String name, String segmentationAlgorithmName, String lexiconPath, boolean switchTiers) {
        this.name = name;
        this.segmentationAlgorithmName = segmentationAlgorithmName;
        this.lexiconPath = lexiconPath;
        this.switchTiers = switchTiers;
    }
    
    
}
