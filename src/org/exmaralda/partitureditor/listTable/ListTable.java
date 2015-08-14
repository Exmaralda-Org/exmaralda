/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.listTable;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.exmaralda.partitureditor.sound.AbstractPlayer;
import org.exmaralda.partitureditor.sound.ELANDSPlayer;
import org.exmaralda.partitureditor.sound.ELANQTPlayer;
import org.exmaralda.partitureditor.sound.JMFPlayer;

/**
 *
 * @author thomas
 */
public class ListTable extends JTable implements MouseListener {


    public AbstractPlayer player;

    public ListTable() {
        setModel(new ListTableModel(new ListTranscription()));
        getSelectionModel().addListSelectionListener(this);
        player = makePlayer();
        addMouseListener(this);
    }

    public AbstractPlayer makePlayer(){
        String os = System.getProperty("os.name").substring(0,3);
        if (os.equalsIgnoreCase("mac")){
            return new ELANQTPlayer();
        } else if (os.equalsIgnoreCase("win")){
            return new ELANDSPlayer();
        } else {
            return new JMFPlayer();
        }
    }


    @Override
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        if (dataModel.getColumnCount()>2){
            getColumnModel().getColumn(0).setCellRenderer(new CountTableCellRenderer());
            getColumnModel().getColumn(0).setPreferredWidth(50);
            getColumnModel().getColumn(1).setCellRenderer(new SpeakerTableCellRenderer());
            getColumnModel().getColumn(1).setPreferredWidth(80);
            getColumnModel().getColumn(2).setCellRenderer(new SpeakerContributionTableCellRenderer());
            getColumnModel().getColumn(2).setPreferredWidth(2000);

            getColumnModel().getColumn(0).setHeaderValue("");
            getColumnModel().getColumn(1).setHeaderValue("Speaker");
            getColumnModel().getColumn(2).setHeaderValue("Text");
        }

        if (dataModel instanceof ListTableModel){
            ListTableModel model = (ListTableModel) dataModel;
            String soundFilePath = model.listTranscription.getHead().getMetaInformation().getReferencedFile();
            if (soundFilePath.trim().length()>0){
                try {
                    player.setSoundFile(soundFilePath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                if (player!=null){
                    player.reset();
                }
            }
        }
    }

    public void makeVisible(String tierID, String timeID){
        ListTableModel model = (ListTableModel) getModel();
        int index = model.find(tierID, timeID);
        setRowSelectionInterval(index, index);
        Rectangle rect = getCellRect(index, 0, true);
        rect.grow(0, 40);
        scrollRectToVisible(rect);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        super.valueChanged(e);
        int index = this.getSelectedRow();
        if (index<0) return;
        if (getModel() instanceof ListTableModel){
            ListTableModel model = (ListTableModel)getModel();
            double startTime = model.getStartTime(index);
            double endTime = model.getEndTime(index);
            if ((startTime>=0.0) && (endTime>=0.0)){
                player.setStartTime(startTime);
                player.setEndTime(endTime);
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        player.stopPlayback();
        if (e.getClickCount()==2){
            player.startPlayback();
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }







}
