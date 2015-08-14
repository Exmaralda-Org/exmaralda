/*
 * ContributionTextPane.java
 *
 * Created on 23. Juni 2008, 16:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import java.util.regex.*;
import javax.swing.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.text.Utilities;
import org.exmaralda.folker.data.Contribution;
import org.exmaralda.folker.data.Event;
import org.exmaralda.folker.data.PatternReader;
import org.exmaralda.folker.data.Timepoint;
import org.jdom.JDOMException;
/**
 *
 * @author thomas
 */
public class ContributionTextPane extends javax.swing.JTextPane 
                                    implements KeyListener,
                                               DocumentListener {
    
    Contribution contribution;
    javax.swing.text.StyledEditorKit sek = new javax.swing.text.StyledEditorKit();
    public boolean MAKE_LINE_BREAK_AFTER_BOUNDARY_SYMBOLS = true;
    //Pattern SPLIT_PATTERN = Pattern.compile(org.exmaralda.folker.utilities.Constants.GAT_SEGMENT);
    Pattern SPLIT_PATTERN;
    Pattern NON_PHO_PATTERN;
    Pattern PAUSE_PATTERN;
    
    javax.swing.border.LineBorder RED_BORDER =
            new javax.swing.border.LineBorder(java.awt.Color.RED, 3);
    
    Hashtable<org.exmaralda.folker.data.Event,javax.swing.text.Position> eventPositionMappings = new Hashtable<org.exmaralda.folker.data.Event,javax.swing.text.Position>();        
    
    Vector<ContributionTextPaneListener> listeners = new Vector<ContributionTextPaneListener>();
    
    /** Creates a new instance of ContributionTextPane */
    public ContributionTextPane() {
        try {
            PatternReader pr = new PatternReader("/org/exmaralda/folker/data/Patterns.xml");
            String splitPattern = pr.getPattern(3, "GAT_PHRASE_BOUNDARY", "default");
            String nonPhoPattern = pr.getPattern(3, "GAT_NON_PHO", "default");
            String pausePattern = pr.getPattern(3, "GAT_PAUSE", "default");
            SPLIT_PATTERN = Pattern.compile(splitPattern);
            NON_PHO_PATTERN = Pattern.compile(nonPhoPattern);
            PAUSE_PATTERN = Pattern.compile(pausePattern);
            setEditorKit(sek);
            setFont(new java.awt.Font("Serif", 0, 14));
            //setFont(getFont().deriveFont(14.0f));
            //setMinimumSize(new java.awt.Dimension(100,100));
            //setPreferredSize(new java.awt.Dimension(200,200));
            setMargin(new java.awt.Insets(10,10,10,10));
            addKeyListener(this);
            getDocument().addDocumentListener(this);
        } catch (JDOMException ex) {
            Logger.getLogger(ContributionTextPane.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ContributionTextPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addContributionTextPaneListener(ContributionTextPaneListener l){
        listeners.addElement(l);
    }
    
    public void fireContributionValidated(){
        for (ContributionTextPaneListener l : listeners){
            l.validateContribution();
        }
    }
    
    public void fireTimepointPressed(Timepoint tp){
        for (ContributionTextPaneListener l : listeners){
            l.processTimepoint(tp);
        }        
    }
    
    void fireTimepointPressed(Timepoint tp, MouseEvent e) {
        for (ContributionTextPaneListener l : listeners){
            l.processTimepoint(tp, e);
        }        
    }
    
    
    public Contribution getContribution(){
        validateContribution();
        return contribution;
    }
    
    public void setContribution(Contribution c) {
        contribution = c;       
        setText(null);   
        if (c==null) return;
        if (contribution.isOrdered()){
            if (contribution.eventlist.getEvents().size()>0){
                Event firstEvent = contribution.eventlist.getEventAt(0);
                TimepointComponent tpc = new TimepointComponent(firstEvent.getStartpoint(), this);
                insertComponent(tpc);                                      
            }
            for (Event e : contribution.eventlist.getEvents()){
                insertEventText(e, MAKE_LINE_BREAK_AFTER_BOUNDARY_SYMBOLS);
                TimepointComponent tpc = new TimepointComponent(e.getEndpoint(), this);
                insertComponent(tpc);                      
            }                    
            //setBackground(java.awt.Color.WHITE);
            setBorder(null);
        } else {
            for (Event e : contribution.eventlist.getEvents()){
                TimepointComponent tpc1 = new TimepointComponent(e.getStartpoint(), this);
                insertComponent(tpc1);                      
                
                insertEventText(e, false);
                
                TimepointComponent tpc2 = new TimepointComponent(e.getEndpoint(), this);
                insertComponent(tpc2);     
                try {
                    getDocument().insertString(getCaretPosition(), "\n", null);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                
            }
            //setBackground(java.awt.Color.RED);            
            setBorder(RED_BORDER);
        }
        this.setEditable(contribution.isOrdered());
        setCaretPosition(0);
    }
    
    void insertEventText(org.exmaralda.folker.data.Event e, boolean lineBreaks){
        String text = e.getText();
        javax.swing.text.SimpleAttributeSet attributeSet = new javax.swing.text.SimpleAttributeSet();
        attributeSet.addAttribute("event", e);
        try {
            javax.swing.text.Position position = getDocument().createPosition(getCaretPosition()-1);
            eventPositionMappings.put(e, position);
            if (!lineBreaks){
                getDocument().insertString(getCaretPosition(), text, attributeSet);
            } else {
                String escapedText = escapeText(text);
                Matcher m = SPLIT_PATTERN.matcher(escapedText);
                int lastMatchPosition = 0;
                while (m.find()){
                    String part = text.substring(lastMatchPosition, m.end());
                    getDocument().insertString(getCaretPosition(), part + "\n", attributeSet);                    
                    lastMatchPosition = m.end();
                }
                String last = text.substring(lastMatchPosition, text.length());
                getDocument().insertString(getCaretPosition(), last, attributeSet);                    
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
    
    String escapeText(String originalText){
        StringBuilder result = new StringBuilder();
        Matcher m = NON_PHO_PATTERN.matcher(originalText);
        int lastMatchPosition = 0;
        while (m.find()){
            String part = originalText.substring(lastMatchPosition, m.start());
            result.append(part);
            result.append("((");
            String match = originalText.substring(m.start(), m.end());
            for (int i=0; i<match.length()-4; i++){
                result.append("*");
            }
            result.append("))");
            lastMatchPosition = m.end();
        }
        String last = originalText.substring(lastMatchPosition, originalText.length());
        result.append(last);
        
        String intermediateText = result.toString();
        m = PAUSE_PATTERN.matcher(intermediateText);
        StringBuilder result2 = new StringBuilder();
        lastMatchPosition = 0;
        while (m.find()){
            String part = intermediateText.substring(lastMatchPosition, m.start());
            result2.append(part);
            result2.append("(");
            String match = intermediateText.substring(m.start(), m.end());
            for (int i=0; i<match.length()-2; i++){
                result2.append("*");
            }
            result2.append(")");
            lastMatchPosition = m.end();
        }
        last = intermediateText.substring(lastMatchPosition, intermediateText.length());
        result2.append(last);
        
        return result2.toString();
    }
    
    public void validateContribution(){
        if (contribution==null) return;
        for (int pos=0; pos<contribution.eventlist.getEvents().size()-1; pos++){
            Event event = contribution.eventlist.getEventAt(pos);
            Event followingEvent = contribution.eventlist.getEventAt(pos+1);
            
            int offset1 = eventPositionMappings.get(event).getOffset();
            if (pos>=0) offset1++;

            int offset2 = eventPositionMappings.get(followingEvent).getOffset();
            try {                
                String text = getDocument().getText(offset1, offset2-offset1);
                text = text.replaceAll("\\n", "");
                event.setText(text);
                //System.out.println(pos + " " + offset1 + " " + text);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
        
        int lastIndex = contribution.eventlist.getEvents().size()-1;
        Event lastEvent = contribution.eventlist.getEventAt(lastIndex);
        int lastOffset = eventPositionMappings.get(lastEvent).getOffset();
        //if (lastIndex>0) lastOffset++;
        try {
            // no freakin' idea where the minus three come from...
            String text = getDocument().getText(lastOffset+1, getDocument().getEndPosition().getOffset() - lastOffset-3);
            //System.out.println("LastOffset: " + getDocument().getEndPosition().getOffset());
            text = text.replaceAll("\\n", "");
            lastEvent.setText(text);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
    
    public Event getEventAtPosition(int realPosition){
        int count=1;
        Event resultEvent = null;
        for (Event e : contribution.eventlist.getEvents()){
            count+=e.getText().length()+1;
            if (count>realPosition){
                resultEvent = e;
                break;
            }
        }
        return resultEvent;
    }
    
    public void split(ContributionListTableModel tableModel) {
        int cursorPosition = getCaretPosition();
        validateContribution();
        Event splitEvent=null;
        int startOffset = -1;
        int endOffset = -1;
        for (int pos=0; pos<contribution.eventlist.getEvents().size()-1; pos++){
            Event event = contribution.eventlist.getEventAt(pos);
            Event followingEvent = contribution.eventlist.getEventAt(pos+1);

            int offset1 = eventPositionMappings.get(event).getOffset()+1;
            int offset2 = eventPositionMappings.get(followingEvent).getOffset();
            if ((offset1<=cursorPosition) && (cursorPosition<offset2)){
                startOffset=offset1;
                endOffset=offset2;
                splitEvent = event;
                break;
            }
        }
        if (splitEvent==null){
            int lastIndex = contribution.eventlist.getEvents().size()-1;
            Event lastEvent = contribution.eventlist.getEventAt(lastIndex);
            int lastOffset = eventPositionMappings.get(lastEvent).getOffset();
            startOffset = lastOffset+1;
            endOffset = lastOffset + lastEvent.getText().length();
            splitEvent = lastEvent;
        }

        int splitPosition = cursorPosition-startOffset;
        try {
            if (MAKE_LINE_BREAK_AFTER_BOUNDARY_SYMBOLS && Utilities.getRowStart(this, cursorPosition)==cursorPosition){
                splitPosition--;            
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(ContributionTextPane.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Offset1: " + startOffset);
        System.out.println("Offset2: " + endOffset);
        System.out.println("Cursor position: " + cursorPosition);
        System.out.println("Event: " + splitEvent.getText());
        
        String firstPart = splitEvent.getText().substring(0, splitPosition);
        String lastPart = splitEvent.getText().substring(splitPosition);
        
        System.out.println("Part 1: " + firstPart);
        System.out.println("Part 2: " + lastPart);
        
        tableModel.split(splitEvent, splitPosition);
        
        
        //contribution.eventlist.addEvent(secondEvent);
        //setContribution(contribution);        
        
    }
    

    // **************************
    // KEY LISTENER METHODS
    // **************************

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // catch enter key and make it validate the editing
        if (e.getKeyCode() == KeyEvent.VK_ENTER){
            //selectAll();
            validateContribution();
            e.consume();
            firePropertyChange("contributionValidated", true, true);
            SwingUtilities.invokeLater(new Runnable(){
               public void run() {
                   setContribution(contribution);
                   fireContributionValidated();
               }
            });
        }
    }

    // **************************
    // DOCUMENT LISTENER METHODS
    // **************************
    
    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
    }



    
    
}
