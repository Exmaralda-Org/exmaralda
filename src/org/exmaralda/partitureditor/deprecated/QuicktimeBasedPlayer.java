package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.sound.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.media.ControllerAdapter;
import javax.media.EndOfMediaEvent;
import javax.media.StopAtTimeEvent;
import javax.media.Time;

import quicktime.QTException;
import quicktime.QTSession;
import quicktime.app.view.QTComponent;
import quicktime.app.view.QTFactory;
import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;
import quicktime.std.clocks.TimeRecord;
import quicktime.std.movies.ExecutingWiredAction;
import quicktime.std.movies.Movie;
import quicktime.std.movies.MovieController;
import quicktime.std.movies.media.DataRef;

public class QuicktimeBasedPlayer extends AbstractPlayer {

	private Thread endThread;	
	private Thread playThread;	
	private Movie m;
	private MovieController mc;	
	private QTComponent qtc = null;
	private  int UPDATE_INTERVAL = 100;
        
	int stopTime = 0;

        // constructor
	public QuicktimeBasedPlayer() throws QTException, java.lang.UnsatisfiedLinkError{
		
		// Inherit from the constructor of AbstractPlayer
		super();

		// When an instance of QuicktimeBasedPlayer is created, the method declareQuicktime() is called to declare necessary Quicktime components
		// for later initialization done by the method setSoundFile() below.
		this.declareQuicktime();
		
		
	}
	
	
	// Declare necessary Quicktime components for later initialization done by the method setSoundFile() below.
	public void declareQuicktime() throws QTException, java.lang.UnsatisfiedLinkError{
		
            QTSession.open();
	
	}
	
	/*
	 *  Method setSoundFile(...) is implemented based on the method createNewMovieFromURL() in the PlayMovie.java from Quicktime.
	 *  
	 *  The method setSoundFile() is called in the method openSoundFileButtonActionPerformed() of AudioPanel 
	 *  to initialize the Quicktime components ( declared by the method QTSession.open() ) based on the specified media file 
	 *  
	 *  The method setSoundfile() is the core of Quicktime implementation.
	 */
        
	// This will resize the window to the size of the new movie

	// This method initializes essential Quicktime components:
	//		Movie m
	//		MovieController mc
	//		QTComponent qtc

	public void setSoundFile(String filename) throws IOException {
		// filename received: D:\Testing media files\conversation.wav
		// Required format of Quicktime: file://D:\Testing media files\pear_film.mov
		filename = "file://" + filename;
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
                        if (qtc == null)
                        {
                            qtc = QTFactory.makeQTComponent(mc);
                            
                            // Append Quicktime component "qtc" to the end of the Container
                            // This code line was disabled because it is involved only with GUI
                            //add((Component)qtc);
                        } else {
                            qtc.setMovieController(mc);
                        }
                        
		} catch (QTException err) {
			err.printStackTrace();
                        // added by TS
                        IOException ioe = new IOException(err.getLocalizedMessage());
                        throw ioe;
		}
		
		// End of Quicktime implement---------------------------------
		

		
		
		/*
		 * Implement: Realize end of the media file playing 
		 *
		 * When the playing of media file comes to the end, the playing must be stopped.
		 * The JMFPlayer already supports the method (in setSoundFile() from JMFPlayer.java):
		 * 
		
		        player.addControllerListener(new ControllerAdapter() {
		            public void endOfMedia(EndOfMediaEvent e) {
		                System.out.println("End of media");
		                firePlaybackStopped();
		            }
		            public void stopAtTime(StopAtTimeEvent e) {
		                System.out.println("Stop at time");
		                playThread.interrupt();
		                firePlaybackStopped();
		            }
		       });                
		       
		    
		    But Quicktime doesn't support any ControllerListener.
		    Witness: http://lists.apple.com/archives/QuickTime-java/2003/Feb/msg00098.html    

            
            Therefore, must manually implement the fact that how end of media file playing can be realize
            
            -> Solution:
            
            	Use Thread.
            	When the Thread is started, it will wait until the end of media file playing shows up.
            	And after that, the Thread will call the method stopPlayback() to stop the playing.

		*/
		
		
		realizeMediaPlayingEnd();
                this.fireSoundfileSet();		
	}
	
	public void realizeMediaPlayingEnd(){
		
            endThread = new Thread(new Runnable(){
                public void run(){
                    // to realize the end of media file playing by using the method isDone()
                    try {
                            // determine if a particular movie has completely finished playing.
                            while ((!m.isDone()) && (getCurrentPosition()<stopTime));
                        } catch (StdQTException e) {
                            e.printStackTrace();
                        }
                    // After the while loop, it is the end of media file playing
                    // Call the stopPlayback() to stop the media playing
                    System.out.println("End of media");
                    stopPlayback();
                    firePlaybackStopped();
                    // Remember to stop the current thread
                    //endThread.stop(); 
                    endThread.interrupt();
                }
            });

            endThread.start(); 
		
		
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

	
	// ----end of the core of Quicktime implementation-----------------------------------------------
			
	
	
    /*
     *  startTime and stopTime must be properly set for the movie to ensure the functionality of playing at a specified position.
     */	
    private void play(double startTime, double endTime)  {


    /*
     *  Set startTime and stopTime for the movie before playing to ensure the functionality of playing at a specified position.
     *  
     *  Quicktime support the method setTimeValue(int) from the class Movie to set the starting time,
     *  but Quicktime doesn't support any method to set the endTime.
     *  Therefore, to realize the endTime or end of media file playing, the method realizeMediaPlayingEnd() implemented manually is called.
     *  Refer to the method realizeMediaPlayingEnd() for detail.
     *  
     *  The passed variable endTime is no longer needed.
     */    	
		
         try {
                // set start time for the movie
                // VIP Notice: time used in any Quicktime method is in millisecond and the startTime and endTime specified here is in second
                m.setTimeValue(new Double(startTime*1000).intValue() );  
                stopTime = new Double(stopTime*1000).intValue();
            } catch (StdQTException e) {
                    e.printStackTrace();
            } //this cause functionality of pausing work wrongly

         
         // realize the endTime or end of media file playing
         realizeMediaPlayingEnd();
  
//----------------------------------------------------------------         

         playThread = new Thread(new Runnable(){
            public void run(){
                while (playThread != null) {
                    if (playThread.isInterrupted()) break;
                    firePosition();
                    try{
                        playThread.sleep(UPDATE_INTERVAL);
                    } catch (InterruptedException ie){
                        playThread.interrupt();
                    }
                }
            }
        });
        
        // start Quicktime player
        try {
			if (m != null)
				//m.setRate(1);
				m.start();
			//m.set
		} catch (QTException err) {
			err.printStackTrace();
		}
		// -------------------------
        
        playThread.start();        
    }

	
	
	
    // JMFPlayer player  ~~ Movie m of Quicktime

	public double getCurrentPosition() {
		
            double currentTime = 0;

            try {					
                // time is returned in milisecond -> must be divided by 1000		
                return m.getTime() / 1000.0f;
            } catch (StdQTException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }

            return currentTime;
	}
	
	

	public double getTotalLength()  {

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

	private double haltTime=0;
	
	public void haltPlayback() {
		
            System.out.println("Halt playback.");
            playThread.interrupt();
            haltTime = getCurrentPosition();

            // Can not use stopPlayback() for the method haltPlayback()
            // due to m.setTimeValue(0); -> reset the movie's time to 0
            //stopPlayback();      

            try {
                    if (m != null){
                        // the stop() of Quicktime behaves as pausing only!
                        m.stop();
                    }
            } catch (QTException err) {
                    err.printStackTrace();
            }

            // Must have
            firePlaybackHalted();        
	}
	
    public void decreaseCurrentPosition(double time) {
        haltTime = Math.max(startTime, haltTime-time);
        this.firePosition();
    }
    
    public void increaseCurrentPosition(double time) {
        haltTime = Math.min(endTime, haltTime+time);
        this.firePosition();
    }


    public void resumePlayback() {
		
        System.out.println("Resume playback.");        
        play(haltTime, endTime);
        
        // Must have
	firePlaybackResumed();
		
		
    }


	
    public void startPlayback() {		
        play(startTime, endTime);
        // Must have
        firePlaybackStarted();	
    }
	

	// the method fire...() ensures displaying of stop and resume button
	
	public void stopPlayback() {
		
		try {
			if (m != null)
			    m.stop(); // the stop() of Quicktime behaves as pausing only!
			
			    //m.setTimeValue(0); // therefore, must set the movie's time to 0
			    m.goToBeginning();
			
		} catch (QTException err) {
			err.printStackTrace();
		}
		
                // Must have
		firePlaybackStopped();
		
	}
	
	

}
