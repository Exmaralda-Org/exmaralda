/*
 * Runen.java
 *
 * Created on 25. Juli 2002, 10:31
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class Runen {

    /** Creates new Runen */
    public Runen() {
    }

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        java.awt.Font runenfont = new java.awt.Font("Runenprojekt", java.awt.Font.PLAIN, 14);
        java.awt.Font unicodefont = new java.awt.Font("Arial Unicode MS", java.awt.Font.PLAIN, 14);
        int i=0;
        Format numberFormat = new Format();
        numberFormat.setProperty("font:name", "Arial Unicode MS");
        numberFormat.setProperty("chunk-border","lrb");
        numberFormat.setID("nf");
        Format runenFormat = new Format();
        runenFormat.setProperty("chunk-border","lrb");
        runenFormat.setProperty("font:name", "Runenprojekt");
        runenFormat.setID("rf");

        ItBundle itb = new ItBundle();
        ItLine numberLine = new ItLine();
        ItLabel numberLabel = new ItLabel();
        numberLabel.setFormat(numberFormat);
        Run nr = new Run();
        nr.setText("Unicode dezimal");
        numberLabel.addRun(nr);
        numberLine.setLabel(numberLabel);
        
        ItLine charLine1 = new ItLine();
        ItLabel charLabel1 = new ItLabel();
        charLabel1.setFormat(numberFormat);
        Run cr1 = new Run();
        cr1.setText("Runenfont");       
        charLabel1.addRun(cr1);
        charLine1.setLabel(charLabel1);

        ItLine charLine2 = new ItLine();
        ItLabel charLabel2 = new ItLabel();
        charLabel2.setFormat(numberFormat);
        Run cr2 = new Run();
        cr2.setText("Arial Unicode MS");       
        charLabel2.addRun(cr2);
        charLine2.setLabel(charLabel2);

        for (char c=0; c<Character.MAX_VALUE; c++){
            if ((c<=255) || (runenfont.canDisplay(c))){
                System.out.println(i + " " + c);
                SyncPoint sp = new SyncPoint();
                sp.setID("S" + Integer.toString(i));
                sp.setFormat(numberFormat);
                itb.getSyncPoints().addSyncPoint(sp);
                
                ItChunk itc = new ItChunk();
                Run run = new Run();
                run.setFormat(numberFormat);
                run.setText(Integer.toString(i));
                itc.setFormat(numberFormat);
                itc.setStart(sp);
                itc.addRun(run);
                numberLine.addItChunk(itc);

                itc = new ItChunk();
                run = new Run();
                run.setFormat(runenFormat);
                if (runenfont.canDisplay(c)){run.setText(String.valueOf(c));}
                else {run.setText("no glyph");}
                itc.setStart(sp);
                itc.setFormat(runenFormat);
                itc.addRun(run);
                charLine1.addItChunk(itc);

                itc = new ItChunk();
                run = new Run();
                run.setFormat(numberFormat);
                if (unicodefont.canDisplay(c)){run.setText(String.valueOf(c));}
                else {run.setText("no glyph");}
                itc.setStart(sp);
                itc.setFormat(numberFormat);
                itc.addRun(run);
                charLine2.addItChunk(itc);
                
            }
            i++;
         }
        InterlinearText it = new InterlinearText();
        it.getFormats().addFormat(numberFormat);
        it.getFormats().addFormat(runenFormat);
        itb.addItLine(numberLine);
        itb.addItLine(charLine1);
        itb.addItLine(charLine2);
        SyncPoint sp = new SyncPoint();
        sp.setID("END");
        sp.setFormat(numberFormat);
        itb.getSyncPoints().addSyncPoint(sp);
        itb.implyEnds();
        it.addItElement(itb);
        it.trim(new PageOutputParameters());
        try{
            it.writeHTMLToFile("c:\\out.html", new HTMLParameters());
            it.writeXMLToFile("c:\\out.xml");
            it.writeTestRTF("c:\\out.rtf", new RTFParameters());
        } catch (java.io.IOException ioe) {ioe.printStackTrace();}
        
    }

}
