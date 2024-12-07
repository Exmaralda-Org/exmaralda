// package jexmaralda;

package org.exmaralda.partitureditor.jexmaralda;

import java.io.*;
import java.net.*;
import java.util.*;
import org.exmaralda.partitureditor.interlinearText.RTFUtilities;
/*
 * MetaInformation.java
 *
 * Created on 2. Februar 2001, 15:27
 */

/* Revision History
 *  0   02-Feb-2001 Creation according to revision 0 of 'exmaralda-time-transcription.dtd'
 *                  and 'exmaralda-segment-transcription.dtd'
 */

/**
 *
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de) 
 * @version 1
 */

public class MetaInformation extends Object {

    private String projectName;
    private String transcriptionName;
    //private String referencedFile;
    private Vector<String> referencedFiles = new Vector<>();;
    private UDInformationHashtable udMetaInformation;
    private String comment;
    private String transcriptionConvention;
    
    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new MetaInformation */
    public MetaInformation() {
        projectName = new String();
        transcriptionName = new String();
        //referencedFile = new String();
        addReferencedFile(new String());
        udMetaInformation = new UDInformationHashtable();
        comment = new String();
        transcriptionConvention = new String();
    }

    /** creates new MetaInformation with project name pn, transcription name tn, referenced file rf,
      * udMetaInformation udi, comment c and transcription convention tc
     * @param pn
     * @param tn
     * @param rf
     * @param udi
     * @param c
     * @param tc */
    public MetaInformation(String pn, String tn, String rf, UDInformationHashtable udi, String c, String tc){
        projectName = pn;
        transcriptionName = tn;
        addReferencedFile(rf);
        udMetaInformation = new UDInformationHashtable();
        udMetaInformation = udi;
        comment = c;
        transcriptionConvention = tc;
    }
    
    /** returns a copy of this MetaInformation
     * @return  */
    public MetaInformation makeCopy(){
        MetaInformation mi = new MetaInformation(this.getProjectName(), this.getTranscriptionName(), this.getReferencedFile(),
                        this.getUDMetaInformation().makeCopy(), this.getComment(), this.getTranscriptionConvention());
        if (this.referencedFiles.size()>1){
            for (int pos=1; pos<this.referencedFiles.size(); pos++){
                mi.addReferencedFile(this.referencedFiles.elementAt(pos));
            }
        }
        return mi;
    }
    
    // ********************************************
    // ********** GET AND SET METHODS *************
    // ********************************************

    /** returns the project name
     * @return  */
    public String getProjectName(){
        return projectName;
    }
    
    /** sets the project name to the specified value
     * @param name */
    public void setProjectName(String name){
        projectName = name;
    }
    
    /** returns the transcription name
     * @return  */
    public String getTranscriptionName(){
        return transcriptionName;
    }
    
    /** sets the transcription name to the specified value
     * @param name */
    public void setTranscriptionName(String name){
        transcriptionName = name;
    }
    
    /** returns the referenced file
     * @return  */
    public String getReferencedFile(){
        if (!referencedFiles.isEmpty()){
            return referencedFiles.elementAt(0);
        }
        return "";
    }

    /** returns the first file in the list of referenced files with the
     * specified extension
     * returns null if there is no such file
     * @param extension
     * @return 
     */
    public String getReferencedFile(String extension){
        for (String rf : getReferencedFiles()){
            if (rf.toLowerCase().endsWith("." + extension.toLowerCase())){
                return rf;
            }
        }
        return null;
    }
    
    /** sets the referenced file to the specified value
     * @param url */
    public void setReferencedFile(String url){
        //referencedFile = new String(url);
        if (referencedFiles.isEmpty()){
            referencedFiles.addElement(url);
        } else {
            referencedFiles.setElementAt(url, 0);
        }
    }
    
    public final void addReferencedFile(String url){
        if ((referencedFiles.size()==1) && (referencedFiles.elementAt(0).length()==0)){
            setReferencedFile(url);
        } else {
            referencedFiles.addElement(url);
        }
    }
    
    public Vector<String> getReferencedFiles(){
        return referencedFiles;
    }
    
    public void setReferencedFiles(Vector<String> rf){
        this.referencedFiles = rf;
    }
    
    /** returns the user defined information
     * @return  */
    public UDInformationHashtable getUDMetaInformation(){
        return udMetaInformation;
    }
    
    /** sets the user defined information to the specified value */
    public void setUDMetaInformation (UDInformationHashtable information){
        udMetaInformation = information;
    }
    
    /** returns the comment
     * @return  */
    public String getComment(){
        return comment;
    }
    
    /** sets the comment to the specified value */
    public void setComment(String text){
        comment = text;
    }
    
    public String getTranscriptionConvention(){
        return transcriptionConvention;
    }
    
    public void setTranscriptionConvention(String tc){
        transcriptionConvention = new String(tc);
    }
    
//***********************************************************
    
    public static int OLD_METHOD = 0;
    public static int NEW_METHOD = 1;

    public boolean relativizeReferencedFile(String relativeToWhat){
        return relativizeReferencedFile(relativeToWhat, OLD_METHOD);
    }
    
    
    public boolean relativizeReferencedFile(String relativeToWhat, int method){
        boolean returnValue = false;
        int count = 0;
        for (String referencedFile : referencedFiles){
            if (referencedFile.length()==0) continue;
//            if (method==OLD_METHOD){
                //System.out.println("Referenced:" +  referencedFile);
                //System.out.println("To what:" +  relativeToWhat);
                URI uri1 = new File(referencedFile).toURI();
                URI uri2 = new File(relativeToWhat).getParentFile().toURI();
                URI relativeURI = uri2.relativize(uri1);
                //referencedFile = relativeURI.toString();
                String newReferencedFile = relativeURI.toString();
                if (newReferencedFile.equals(uri1.toString())){
                    // i.e. the relativization has brougth no change


                    if (method==NEW_METHOD){
                        // try with the new method
                        File file1 = new File(referencedFile);
                        File file2 = new File(relativeToWhat).getParentFile();
                        //System.out.println("File1: " + file1.getAbsolutePath());
                        //System.out.println("File2: " + file2.getAbsolutePath());
                        // changed 24-09-2010: wrong order?
                        newReferencedFile = org.exmaralda.common.helpers.RelativePath.getRelativePath(file2, file1);
                    }
                }
                referencedFiles.setElementAt(newReferencedFile, count);
/*            } /*else if (method==NEW_METHOD){
                File file1 = new File(referencedFile);
                File file2 = new File(relativeToWhat).getParentFile();
                referencedFile = org.exmaralda.common.helpers.RelativePath.getRelativePath(file2, file1);
                referencedFiles.setElementAt(referencedFile, count);
            }*/
            returnValue= true;
            count++;
        }
        return returnValue;
    }


    
    public boolean resolveReferencedFile(String relativeToWhat) {
        return resolveReferencedFile(relativeToWhat, OLD_METHOD);
    }

    public boolean resolveReferencedFile(String relativeToWhat, int method) {
        boolean returnValue = false;
        int count = 0;
        for (String referencedFile : referencedFiles){
            if (referencedFile.length()==0) return false;
            //if (!relativeToWhat.startsWith("http://")){
            // changed 30-05-2011
            // this is a change suggested by Mathias-Christian Ott (Gewiss, Leipzig)
            if (!relativeToWhat.startsWith("http://")
                    && !relativeToWhat.startsWith("https://")){
                if (method==OLD_METHOD){
                    if (new File(referencedFile).isAbsolute()) continue;
                        URI uri2 = new File(relativeToWhat).getParentFile().toURI();
                        System.out.println("Resolving " + referencedFile + " relative to " + relativeToWhat);
                        String modRelPath = referencedFile.replaceAll(" ", "%20");
                        URI absoluteURI = uri2.resolve(modRelPath);
                        //URI absoluteURI = uri2.resolve(referencedFile);
                        referencedFile = new File(absoluteURI).getAbsolutePath();
                        referencedFiles.setElementAt(referencedFile, count);
                        returnValue= true;
                }  else if (method==NEW_METHOD){
                        referencedFile = org.exmaralda.common.helpers.RelativePath.resolveRelativePath(referencedFile, relativeToWhat);
                        referencedFiles.setElementAt(referencedFile, count);
                        returnValue= true;
                }
            } else {
                //N.B.: on http paths, no ".." will be resolved
                try {
                    if (new URI(referencedFile).isAbsolute()) continue;
                    URI absoluteURI = new URI(relativeToWhat).resolve(referencedFile);
                    referencedFile = absoluteURI.toString();
                    referencedFiles.setElementAt(referencedFile, count);
                    returnValue= true;
                } catch (URISyntaxException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            count++;
        }
        return returnValue;
    }
    
    
    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************

    /** returns a string corresponding to the XML-element &lt;meta-information&gt;
     *  as specified in the corresponding dt
     * @return d*/
    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<meta-information>");
        sb.append("<project-name>").append(StringUtilities.checkCDATA(getProjectName())).append("</project-name>");
        sb.append("<transcription-name>").append(StringUtilities.checkCDATA(getTranscriptionName())).append("</transcription-name>");
        for (String referencedFile : referencedFiles){
            // changed 11-10-2011: try to write URL for absolute paths
            //sb.append("<referenced-file url=\"" + StringUtilities.toXMLString(referencedFile) + "\"/>");
            sb.append("<referenced-file url=\"");
            File f = new File(referencedFile);
            if (f.isAbsolute()){
                try {
                    sb.append(StringUtilities.toXMLString(f.toURI().toURL().toString()));
                } catch (MalformedURLException ex) {
                    System.out.println(ex.getMessage());
                    sb.append(StringUtilities.toXMLString(referencedFile));
                }
            } else {
                sb.append(StringUtilities.toXMLString(referencedFile));
            }
            sb.append("\"/>");
        }
        // changed 30-05-2012
        //always write a referenced file even if there is none
        if (referencedFiles.isEmpty()){
            sb.append("<referenced-file url=\"\"/>");
        }
        sb.append("<ud-meta-information>").append(getUDMetaInformation().toXML()).append("</ud-meta-information>");
        sb.append("<comment>").append(StringUtilities.checkCDATA(getComment())).append("</comment>");
        sb.append("<transcription-convention>").append(StringUtilities.checkCDATA(getTranscriptionConvention())).append("</transcription-convention>");
        sb.append("</meta-information>");
        return sb.toString();
    }
    


    // ********************************************
    // ********** RTF OUTPUT *********************
    // ********************************************
    
    public String toRTF(){
        StringBuilder sb = new StringBuilder();
        sb.append("\\pard{\\fs28\\b ").append(RTFUtilities.toEscapedRTFString(getTranscriptionName())).append("}\\par\\par");
        if (getProjectName().length()>0){
            sb.append("{\\fs20\\b Project Name: \\plain\\fs20 ");
            //sb.append(StringUtilities.toANSI(getProjectName()) + "}\\par");
            sb.append(RTFUtilities.toEscapedRTFString(getProjectName())).append("}\\par");
        }
        if (getReferencedFile().length()>0){
            sb.append("{\\fs20\\b Referenced file: \\plain\\fs20 ");
            //sb.append(StringUtilities.toANSI(getReferencedFile()) + "}\\par");
            sb.append(RTFUtilities.toEscapedRTFString(getReferencedFile())).append("}\\par");
        }
        if (getTranscriptionConvention().length()>0){
            sb.append("{\\fs20\\b Transcription Convention: \\plain\\fs20 ");
            //sb.append(StringUtilities.toANSI(getTranscriptionConvention()) + "}\\par");
            sb.append(RTFUtilities.toEscapedRTFString(getTranscriptionConvention())).append("}\\par");
        }
        if (getComment().length()>0){
            sb.append("{\\fs20\\b Comment: \\plain\\fs20 ");
            //sb.append(StringUtilities.toANSI(getComment()) + "}\\par");
            sb.append(RTFUtilities.toEscapedRTFString(getComment())).append("}\\par");
        }
        if (!getUDMetaInformation().isEmpty()){
            sb.append("\\par{\\fs20\\ul User defined attributes: }\\par ");
            sb.append(getUDMetaInformation().toRTF());
            sb.append("\\par");
        }        
        return sb.toString();
    }
    


    // ********************************************
    // ******* MANIPULATION OF UD-ATTRIBUTES ******
    // ********************************************

    
    public void makeUDAttributesFromTemplate(MetaInformation template){
        String[] allAttributes = template.getUDMetaInformation().getAllAttributes();
        for (String allAttribute : allAttributes) {
            if (!getUDMetaInformation().containsAttribute(allAttribute)) {
                getUDMetaInformation().setAttribute(allAttribute, "---unknown---");
            }
        }            
    }
    
    
}