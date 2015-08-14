/*
 * ELANWaveSamplerWrapper.java
 *
 * Created on 5. Maerz 2008, 10:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.timeview;

/**
 *
 * @author thomas
 */
public class ELANWaveSamplerWrapper {
    
    ELANWaveSampler elanWaveSampler;
    long noSamples;
    int minSample;
    int maxSample;
    
    int lastStart;
    int lastEnd;
    int lastSize;
    int lastNrOfChannels;
    byte[][] lastValues;
    
    /** Creates a new instance of ELANWaveSamplerWrapper */
    public ELANWaveSamplerWrapper(ELANWaveSampler ews) {
        elanWaveSampler = ews;
        noSamples = elanWaveSampler.getNrOfSamples();  
        //determineMaxAndMinSamples();
    }
    
    void determineMaxAndMinSamples(){
        int max = 0;
        int min = 0;
        for (int pos=0; pos<elanWaveSampler.getNrOfSamples(); pos+=4096){
            elanWaveSampler.seekSample(pos);
            elanWaveSampler.readInterval(4096,2);
            int[] lca = elanWaveSampler.getFirstChannelArray();
            for (int sample : lca) {
                max = Math.max(max, sample);
                min = Math.min(min, sample);
            }
        }
        minSample = min;
        maxSample = max;
        System.out.println("Min " + min);
        System.out.println("Max " + max);        
    }
           
    
    public byte[][] getValuesForPixels(int start, int end, int size, int nrOfChannels){
        
        // if the size has changed or a different number of channels is required:
        // calculate all values
        if ((size!=lastSize) || (nrOfChannels!=lastNrOfChannels)){
            lastValues = calculateValuesForPixels(start, end, size, nrOfChannels);
            lastStart = start;
            lastEnd = end;
            lastSize = size;
            lastNrOfChannels = nrOfChannels;
            return lastValues;            
        }
        
        // if there is no overlap between this region and the last region:
        // calculate all values
        if ((start>=lastEnd) || (end<=lastStart)){
            lastValues = calculateValuesForPixels(start, end, size, nrOfChannels);
            lastStart = start;
            lastEnd = end;
            lastSize = size;
            lastNrOfChannels = nrOfChannels;
            return lastValues;                        
        }
        
        // The new and the old region overlap in one of the following ways:
        //0) New                       A�---------------- �B
        //1) Old 	           C�----------------�D
        //2) Old 		               C�----------------�D
        //3) Old 		   	     C�---�D
        //4) Old                    C�--------------------------�D
        //      Copy	        Calculate
        //1)    A	D	D	B
        //2)	C	B	A	C
        //3)	C	D	A       C
        //                      D       B
        //4)	A	B	-	-
        
        byte[][] returnValue = new byte[nrOfChannels*2][end-start];
        
        //case 1
        if ((lastStart<start) && (lastEnd>=start) && (lastEnd<=end)){
            //System.out.println("CASE 1");
            byte[][] copiedValues = copyValuesForPixels(start,lastEnd, size, nrOfChannels);
            byte[][] calculatedValues = calculateValuesForPixels(lastEnd, end, size, nrOfChannels);
            for (int pos=0; pos<nrOfChannels*2; pos++){
                int count=0;
                for (byte b : copiedValues[pos]) {
                    returnValue[pos][count] = b;
                    count++;
                }
                for (byte b : calculatedValues[pos]) {
                    returnValue[pos][count] = b;
                    count++;
                }
            }
        }
        
        //case 2
        else if ((lastStart>=start) && (lastStart<=end) && (lastEnd>end)){
            //System.out.println("CASE 2");
            byte[][] calculatedValues = calculateValuesForPixels(start, lastStart, size, nrOfChannels);
            byte[][] copiedValues = copyValuesForPixels(lastStart,end, size, nrOfChannels);
            for (int pos=0; pos<nrOfChannels*2; pos++){
                int count=0;
                for (byte b : calculatedValues[pos]) {
                    returnValue[pos][count] = b;
                    count++;
                }
                for (byte b : copiedValues[pos]) {
                    returnValue[pos][count] = b;
                    count++;
                }
            }
        }
        
        //case 3
        else if ((lastStart>=start) && (lastStart<=end) && (lastEnd<end) && (lastEnd>start)){
            //System.out.println("CASE 3");
            byte[][] calculatedValues1 = calculateValuesForPixels(start, lastStart, size, nrOfChannels);
            byte[][] copiedValues = copyValuesForPixels(lastStart,lastEnd, size, nrOfChannels);
            byte[][] calculatedValues2 = calculateValuesForPixels(lastEnd, end, size, nrOfChannels);
            for (int pos=0; pos<nrOfChannels*2; pos++){
                int count=0;
                for (byte b : calculatedValues1[pos]) {
                    returnValue[pos][count] = b;
                    count++;
                }
                for (byte b : copiedValues[pos]) {
                    returnValue[pos][count] = b;
                    count++;
                }
                for (byte b : calculatedValues2[pos]) {
                    returnValue[pos][count] = b;
                    count++;
                }
            }
        }
        
        //case 4
        else {
            //System.out.println("CASE 4");
            returnValue = copyValuesForPixels(start,end, size, nrOfChannels);
        }
        
        lastValues = returnValue;
        lastStart = start;
        lastEnd = end;
        lastSize = size;
        lastNrOfChannels = nrOfChannels;
        return returnValue;        
    }
    
    public byte[][] copyValuesForPixels(int start, int end, int size, int nrOfChannels){
        byte[][] returnValue = new byte[nrOfChannels*2][end-start];
        for (int pos=0; pos<nrOfChannels*2; pos++){
            for (int index=start; index<end; index++){
                returnValue[pos][index-start] = lastValues[pos][index-lastStart];
            }
        }
        return returnValue;
    }
    
    public byte[][] calculateValuesForPixels(int start, int end, int size, int nrOfChannels){
        byte[][] returnValue = new byte[nrOfChannels*2][end-start];
        
        double startPerc = (double)start/(double)size;
        long firstSample = Math.round(startPerc*(double)noSamples);
        elanWaveSampler.seekSample(firstSample);        
        
        double lengthPerc = (double)(end-start)/(double)size;
        int samplesToRead = (int)Math.round(lengthPerc*(double)noSamples);      
        elanWaveSampler.readInterval(samplesToRead, nrOfChannels);
        
        double samplesPerPixel = (double)samplesToRead / (double)(end-start);
                        
        int[] firstChannelArray = elanWaveSampler.getFirstChannelArray();
        calculateValues(firstChannelArray, start, end, samplesPerPixel, samplesToRead, returnValue[0], returnValue[1]);
        
        if (nrOfChannels>1){
            int[] secondChannelArray = elanWaveSampler.getSecondChannelArray();
            calculateValues(secondChannelArray, start, end, samplesPerPixel, samplesToRead, returnValue[2], returnValue[3]);
        }

        return returnValue;
    }
    
    void calculateValues(int[] samplesArray, 
                         int startPixel, int endPixel, 
                         double samplesPerPixel, int samplesRead,
                         byte[] maximumArray, byte[] minimumArray){
        for (int index=startPixel; index<endPixel; index++){
            double firstSampleIndex = (double)(index-startPixel)/(double)(endPixel-startPixel)*(double)samplesRead;
            int max = 0;
            int min = 0;
            int max_pos = -1;
            int min_pos = -1;
            for (int i=(int)Math.round(firstSampleIndex); i<firstSampleIndex+samplesPerPixel-0.5; i++){
                if (i>=samplesArray.length) break;
                int thisSample = samplesArray[i];
                if (thisSample>max){
                    max = thisSample;
                    max_pos = i;
                }
                if (thisSample<min){
                    min = thisSample;
                    min_pos = i;
                }
            }
            if (max_pos>min_pos){
                maximumArray[index-startPixel]=(byte)Math.round(100.0*(double)max/(double)elanWaveSampler.getPossibleMaxSample());
                minimumArray[index-startPixel]=(byte)-Math.round(100.0*(double)min/(double)elanWaveSampler.getPossibleMinSample());
            } else {
                maximumArray[index-startPixel]=(byte)Math.round(100.0*(double)min/(double)elanWaveSampler.getPossibleMinSample());                
                minimumArray[index-startPixel]=(byte)-Math.round(100.0*(double)max/(double)elanWaveSampler.getPossibleMaxSample());                
            }
        }        
    }
    
    
}
