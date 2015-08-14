/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.sound;

import java.io.IOException;

import quicktime.QTException;
import quicktime.QTSession;
import quicktime.app.view.QTComponent;
import quicktime.app.view.QTFactory;
import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;
import quicktime.std.movies.Movie;
import quicktime.std.movies.MovieController;
import quicktime.std.movies.media.DataRef;

/**
 *
 * @author thomas
 */
@Deprecated
public class QuicktimePlayer extends AbstractPlayer implements PlayableListener {
    
    private Movie m;
    private MovieController mc;	
    private QTComponent qtc = null;
    private int UPDATE_INTERVAL = 100;
    private Thread playThread;	
    
    
    private double haltTime = 0;
    

    public static boolean isQuicktimeAvailable(){
        try {
            QTSession.open();
            QTSession.close();
            return true;
        } catch (UnsatisfiedLinkError e){
            System.out.println("**** QUICKTIME NOT AVAILABLE: " + e.getLocalizedMessage());
            return false;
        } catch (QTException qe){
            System.out.println("**** QUICKTIME NOT AVAILABLE: " + qe.getLocalizedMessage());
            return false;
        } catch (Throwable t){
            System.out.println("**** QUICKTIME NOT AVAILABLE: " + t.getLocalizedMessage());
            return false;
        }
    }
    
    public QuicktimePlayer() throws QTException {
        super();
        QTSession.open();
        addPlayableListener(this);
    }
    

    public void setSoundFile(String pathToSoundFile) throws IOException {
        System.out.println("This is the Quicktime player setting media file to " + pathToSoundFile);
        if ((pathToSoundFile==null) && (m!=null)){
            try {
                m.stop();
                m.disposeQTObject();
            } catch (QTException ex) {
                ex.printStackTrace();
            }

            return;
        }
        String filename = "file://" + pathToSoundFile;
        try {
            // create the DataRef that contains the information about where the movie is
            DataRef urlMovie = new DataRef(filename);
            // create the movie 
            m = Movie.fromDataRef (urlMovie,StdQTConstants.newMovieActive);
            // create the movie controller
            mc = new MovieController (m);

            // the TimeScale must be set to 1000 which corresponds to 100% of the real time value 
            // the default TimeScale is 600 which corresponds to 60% of the real time value
            m.setTimeScale(1000);

            // create and add a QTComponent if we haven't done so yet, otherwise set qtc's movie controller
            if (qtc == null) {
                qtc = QTFactory.makeQTComponent(mc);
            }
            qtc.setMovieController(mc);

        } catch (QTException err) {
            err.printStackTrace();
            // added by TS
            IOException ioe = new IOException(err.getLocalizedMessage());
            throw ioe;
        }

    }

    public double getTotalLength() {
        double duration=0;
        try {

                // setTimeScale(1000) should be done in the method setSoundFile()
                // the TimeScale must be set to 1000 which corresponds to 100% of the real time value 
                // the default TimeScale is 600 which corresponds to 60% of the real time value
                // m.setTimeScale(1000);
                // time is returned in milisecond -> must be divided by 1000
                duration = (double)m.getDuration()/1000.0f;// returns the duration of a movie

        } catch (StdQTException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        return duration;

    }

    public void startPlayback() {
        System.out.println("Start playback.");        
        play(startTime, endTime);
        firePlaybackStarted();	
    }

    public void haltPlayback() {
        System.out.println("Halt playback.");
        playThread.interrupt();
        haltTime = getCurrentPosition();
        try {
            m.stop();
        } catch (StdQTException ex) {
            ex.printStackTrace();
        }
        firePlaybackHalted();        

    }

    public void resumePlayback() {
        System.out.println("Resume playback.");        
        play(haltTime, endTime);
        firePlaybackResumed();
    }

    public void stopPlayback() {
        try {
            System.out.println("Stop playback.");
            if (playThread!=null){
                playThread.interrupt();
            }
            if (m!=null){
                m.stop();
                m.goToBeginning();
                firePlaybackStopped();
            }
        } catch (StdQTException ex) {
            ex.printStackTrace();
        }
    }

    public double getCurrentPosition() {
        double currentTime = 0;

        try {					
            // time is returned in milisecond -> must be divided by 1000		
            return m.getTime() / 1000.0f;
        } catch (StdQTException e) {
            e.printStackTrace();
        }

        return currentTime;

    }

    public void decreaseCurrentPosition(double time) {
        haltTime = Math.max(startTime, haltTime-time);
        this.firePosition();
    }
    
    public void increaseCurrentPosition(double time) {
        haltTime = Math.min(endTime, haltTime+time);
        this.firePosition();
    }
    
    private void play(double startTime, double endTime)  {
        try {
            // set start time for the movie
            // VIP Notice: time used in any Quicktime method is in millisecond and the startTime and endTime specified here is in second
            m.setTimeValue(new Double(startTime * 1000).intValue());
            this.endTime = endTime; //* 1000.0;
            playThread = new Thread(new Runnable() {

                public void run() {
                    while (playThread != null) {
                        if (playThread.isInterrupted()) {
                            break;
                        }
                        firePosition();
                        try {
                            playThread.sleep(UPDATE_INTERVAL);
                        } catch (InterruptedException ie) {
                            playThread.interrupt();
                        }
                    }
                }
            });


            m.start();
            playThread.start();
        } catch (StdQTException ex) {
            ex.printStackTrace();
        }
    }

    public void processPlayableEvent(PlayableEvent e) {
        if (e.getType()!=PlayableEvent.POSITION_UPDATE) return;
        double p = this.getCurrentPosition();//e.getPosition();
        if (p>Math.min(getTotalLength(),endTime)) {
            stopPlayback();
            System.out.println("*****: " + p + " > " + endTime);
        }
    }
    
    public MovieController getMovieController () { 
        return mc; 
    }

    public Movie getMovie () throws QTException {
            return m;
    }

    public QTComponent getQtc() {
            return qtc;
    }

    public void setQtc(QTComponent qtc) {
            this.qtc = qtc;
    }

    
    

}
