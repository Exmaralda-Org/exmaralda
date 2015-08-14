/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.sound;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.locks.ReentrantLock;
import mpi.eudico.client.annotator.ElanLayoutManager;

import mpi.eudico.client.annotator.Preferences;
import mpi.eudico.client.annotator.player.DIBToImage;
import mpi.eudico.client.annotator.player.ElanMediaPlayer;
import mpi.eudico.client.annotator.player.JMMFMediaPlayer;
import mpi.eudico.client.annotator.player.NoPlayerException;
import mpi.eudico.client.annotator.player.VideoFrameGrabber;
import mpi.eudico.client.mediacontrol.ControllerEvent;
import mpi.eudico.client.mediacontrol.ControllerListener;
import mpi.eudico.client.mediacontrol.ControllerManager;
import mpi.eudico.server.corpora.clomimpl.abstr.MediaDescriptor;
import nl.mpi.jmmf.DIBInfoHeader;
import nl.mpi.jmmf.JMMFException;
import nl.mpi.jmmf.JMMFPanel;
import nl.mpi.jmmf.JMMFPlayer;

/**
 *
 * @author Schmidt
 */
public class StrippedJMMFMediaPlayer extends ControllerManager implements
		ElanMediaPlayer, ControllerListener, VideoFrameGrabber, ActionListener {
    
	private JMMFPlayer jmmfPlayer;
	private JMMFPanel jmmfPanel;
//	private JMMFCanvas jmmfPanel;
	private MediaDescriptor mediaDescriptor;
	private long offset = 0L;
	private long stopTime;
	private long duration;// media duration minus offset
	private long origDuration;// the original media duration
	// end of media buffer, don't set stop time or media time to 
	// the end of the media because then the media jumps to 0
	private long eomBuffer = 0;
	private float origAspectRatio = 0;
	private float aspectRatio = 0;
	private double millisPerSample;
	//private boolean playing;
	private StrippedJMMFMediaPlayer.PlayerStateWatcher stopThread = null;
	private StrippedJMMFMediaPlayer.EndOfTimeWatcher endTimeWatcher = null;
	
	private boolean isInited = false;
	private float cachedVolume = 1.0f;
	private float cachedRate = 1.0f;
	private float curSubVolume;
	private boolean mute;
	
        private boolean frameRateAutoDetected = true;
	/** if true frame forward and frame backward always jump to the begin
	 * of the next/previous frame, otherwise it jumps with the frame duration */
	private boolean frameStepsToFrameBegin = false;
	private boolean pre47FrameStepping = false;
	// gui
	private boolean detached;
	//private boolean allowVideoScaling = true;
	private float videoScaleFactor = 1f;
	//private int vx = 0, vy = 0, vw = 0, vh = 0;
	private int dragX = 0, dragY = 0;
	private int SET_MT_TIMEOUT = 1000;
	
	private final ReentrantLock syncLock = new ReentrantLock();
	
	/**
	 * Constructor.
	 * 
	 * @param mediaDescriptor
	 * @throws NoPlayerException
	 */
	public StrippedJMMFMediaPlayer(MediaDescriptor mediaDescriptor) throws NoPlayerException {
		
            this.mediaDescriptor = mediaDescriptor;
            offset = mediaDescriptor.timeOrigin;
		
            String urlString = mediaDescriptor.mediaURL;
            if (urlString.startsWith("file:") &&
                !urlString.startsWith("file:///")) {
                urlString = urlString.substring(5);
            }
        
            try {
                    boolean synchronousMode = false;
                    /*Object synVal = Preferences.get("Windows.JMMFPlayer.SynchronousMode", null);
                    if (synVal instanceof Boolean) {
                            synchronousMode = (Boolean) synVal;
                    }*/

                    jmmfPlayer = new JMMFPlayer(urlString, synchronousMode);
                    if (jmmfPlayer.isVisualMedia()) {
                            jmmfPanel = new JMMFPanel(jmmfPlayer);
                            System.out.println("JMMF Panel initialised");
                            jmmfPanel.setPreferredSize(new Dimension(480,320));
                            //initPopupMenu();
                            //JMMFMediaPlayer.MouseHandler mh = new JMMFMediaPlayer.MouseHandler();
                            //jmmfPanel.addMouseListener(mh);
                            //jmmfPanel.addMouseMotionListener(mh);

                            /*Object val = Preferences.get("Windows.JMMFPlayer.CorrectAtPause", null);

                            if (val instanceof Boolean) {
                                    JMMFPlayer.correctAtPause((Boolean) val);
                            }*/
                    }        	
        	// cannot get info from the player yet
            } catch (JMMFException je) {
                    throw new NoPlayerException("JMMFPlayer cannot handle the file: " + je.getMessage());
            } catch (Throwable tr) {
                    throw new NoPlayerException("JMMFPlayer cannot handle the file: " + tr.getMessage());
            }
	}
	
        @Override
	public void cleanUpOnClose() {
		if (jmmfPlayer != null) {
			if (endTimeWatcher != null) {
				endTimeWatcher.close();
			}
			if (jmmfPanel != null) {
				jmmfPanel.setPlayer(null);
			}
			
			jmmfPlayer.cleanUpOnClose();
			jmmfPlayer = null;//make sure no more calls are made to this player
		}

	}

	/**
	 * Returns the aspect ratio.
	 */
        @Override
	public float getAspectRatio() {
		if (aspectRatio != 0) {
			return aspectRatio;
		}
		if (jmmfPlayer != null) {
			if (origAspectRatio == 0) {
				origAspectRatio = jmmfPlayer.getAspectRatio();
			}
			aspectRatio = origAspectRatio;
		}
		return aspectRatio;
	}

        @Override
	public String getFrameworkDescription() {
		if (jmmfPlayer != null && jmmfPlayer.isSynchronousMode()) {
			return "JMMF - Java with Microsoft Media Foundation Player (Synchronous Mode)";
		}
		return "JMMF - Java with Microsoft Media Foundation Player";
	}

        @Override
	public MediaDescriptor getMediaDescriptor() {
		return mediaDescriptor;
	}

	/**
	 * Gets the duration from the player (and stores it locally).
	 * @return the media duration in ms
	 */
        @Override
	public long getMediaDuration() {
		if (duration <= 0) {
			if (jmmfPlayer != null) {
				if (origDuration == 0) {
					origDuration = jmmfPlayer.getDuration();					
				}
				duration = origDuration - offset;
			}
		}
		return duration;
	}

	/**
	 * Returns the current media time, in ms and corrected for the offset.
	 */
        @Override
	public long getMediaTime() {
		if (jmmfPlayer != null) {
			return jmmfPlayer.getMediaTime() - offset;
		}
		return 0;
	}

	/**
	 * Retrieves the duration per sample (and caches it locally).
	 */
        @Override
	public double getMilliSecondsPerSample() {
		if (millisPerSample == 0.0) {
			if (jmmfPlayer != null) {
				millisPerSample = jmmfPlayer.getTimePerFrame();
				if (millisPerSample == 0.0) {
					millisPerSample = 40.0;
					frameRateAutoDetected = false;
				}
			}
		}
		return millisPerSample;
	}

        @Override
	public long getOffset() {
		return offset;
	}

        @Override
	public float getRate() {
		if (jmmfPlayer != null) {
			return jmmfPlayer.getRate();
		}
		return 1;
	}

        @Override
	public int getSourceHeight() {
		if (jmmfPlayer != null) {
			return jmmfPlayer.getSourceHeight();
		}
		return 0;
	}

        @Override
	public int getSourceWidth() {
		if (jmmfPlayer != null) {
			return jmmfPlayer.getSourceWidth();
		}
		return 0;
	}

	/**
	 * After the first time this is called the panel will be added to a window,
	 * upon which the player will be initialized fully.
	 */
        @Override
	public Component getVisualComponent() {
		if (!isInited) {
                    System.out.println("Is not inited");
                    new StrippedJMMFMediaPlayer.InitWaitThread().start();
		}
		return jmmfPanel;
	}

        @Override
	public float getVolume() {
		if (jmmfPlayer != null) {
			return jmmfPlayer.getVolume();
		}
		return 0f;
	}

    @Override
    public void setSubVolume(float level) {
    	curSubVolume = level;
    }
    
    @Override
    public float getSubVolume(){
    	return curSubVolume;
    }

    @Override
    public void setMute(boolean mute) {
    	this.mute = mute;
    }
    
    @Override
    public boolean getMute() {
    	return mute;
    }
    
    @Override
    public boolean isFrameRateAutoDetected() {
	return frameRateAutoDetected;
    }


        @Override
	public boolean isPlaying() {
		if (jmmfPlayer != null) {
			return jmmfPlayer.isPlaying();
			//return playing;
			//return jmmfPlayer.getState() == JMMFPlayer.PlayerState.STARTED.value;
		}
		return false;
	}

        @Override
	public void nextFrame() {
		if (jmmfPlayer != null) {
			if (jmmfPlayer.isPlaying()) {
				stop();
			}
			
	        
			double nextTime = jmmfPlayer.nextFrame(frameStepsToFrameBegin);
			
			if (!jmmfPlayer.isSynchronousMode()) {
	        	long sysTime = System.currentTimeMillis();
				while (jmmfPlayer.getState() == JMMFPlayer.PlayerState.SEEKING.value) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException ie){}

					if (System.currentTimeMillis() - sysTime > SET_MT_TIMEOUT) {
						break;
					}
				}
			}
			
			setControllersMediaTime((long) Math.ceil(nextTime) - offset);
			
		}
	}
	
    /**
     * The pre 4.7 implementation of next frame.
     */
    /*private void nextFramePre47() {
    	// assumes a check for jmmfPlayer != null and player == paused has been performed 
        if (frameStepsToFrameBegin) {
        	long curFrame = (long)(getMediaTime() / millisPerSample);
    		setMediaTime((long)((curFrame + 1) * millisPerSample));
        } else {
        	long curTime = jmmfPlayer.getMediaTime();
        	//System.out.println("Current time: " + curTime);
        	curTime += millisPerSample;
        	//System.out.println("Current time 2: " + curTime);
        	jmmfPlayer.setMediaTime(curTime);
        	if (!jmmfPlayer.isSynchronousMode()) {
	        	long sysTime = System.currentTimeMillis();
				while (jmmfPlayer.getState() == JMMFPlayer.PlayerState.SEEKING.value) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException ie){}
	
					if (System.currentTimeMillis() - sysTime > SET_MT_TIMEOUT) {
						break;
					}
				}
        	}
			//jmmfPlayer.repaintVideo();
        	setControllersMediaTime(curTime - offset);
        }
    }*/

        @Override
	public void playInterval(long startTime, long stopTime) {
		if (jmmfPlayer != null) {
			if (jmmfPlayer.isPlaying()) {
				stop();
			}
			setStopTime(stopTime);
			if (getMediaTime() != startTime + offset) {
				setMediaTimeAndWait(startTime);
			}
			startInterval();
		}

	}
	
	void startInterval() {
		if (jmmfPlayer != null) {
			if (jmmfPlayer.isPlaying()) {
				return;
			}
	        syncLock.lock();
	        try {
		        // create a PlayerEndWatcher thread
		        if (stopThread != null && stopThread.isAlive()) {
		        	stopThread.setStopped();
		        }
		        int sleepTime = 200;	        
		        if (jmmfPlayer.isSynchronousMode()) {
		        	sleepTime = 20;
		        }
		        
		        stopThread = new StrippedJMMFMediaPlayer.PlayerStateWatcher(sleepTime);
		        if (jmmfPlayer.isSynchronousMode()) {
		        	jmmfPlayer.start();
		        	startControllers();
		        	stopThread.start();
		        } else {
			        stopThread.start();		        
			        jmmfPlayer.start();
			        startControllers();
		        }
	        } finally {
	        	syncLock.unlock();
	        }
		}
	}

        @Override
	public void previousFrame() {
		if (jmmfPlayer != null) {
			if (jmmfPlayer.isPlaying()) {
				stop();
			}
			
			
			double prevTime = jmmfPlayer.previousFrame(frameStepsToFrameBegin);
			
			if (!jmmfPlayer.isSynchronousMode()) {
	        	long sysTime = System.currentTimeMillis();
				while (jmmfPlayer.getState() == JMMFPlayer.PlayerState.SEEKING.value) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException ie){}

					if (System.currentTimeMillis() - sysTime > SET_MT_TIMEOUT) {
						break;
					}
				}
			}
			
			setControllersMediaTime((long) Math.ceil(prevTime) - offset);
		}

	}
	

        @Override
	public void setAspectRatio(float aspectRatio) {
		this.aspectRatio = aspectRatio;
		// hier update the visual component
	}

        @Override
	public void setFrameStepsToFrameBegin(boolean stepsToFrameBegin) {
		this.frameStepsToFrameBegin = stepsToFrameBegin;
	}

	/*public void setLayoutManager(ElanLayoutManager layoutManager) {
		this.layoutManager = layoutManager;
		if (this.layoutManager != null) {
			detached = !(this.layoutManager.isAttached(this));
		}
	}*/

        @Override
	public void setMediaTime(long time) {
		if (jmmfPlayer != null) {
			// works a bit better than just setting the position
//			if (jmmfPlayer.getState() == JMMFPlayer.PlayerState.SEEKING.value){
//				return;
//			}
			if (jmmfPlayer.isPlaying()) {
				stop();
			}
			if (time < 0) {
				time = 0;
			}
			if (time > duration - eomBuffer) {
				time = duration - eomBuffer;
			}

			// blocking
			jmmfPlayer.setMediaTime(time + offset);
			if (!jmmfPlayer.isSynchronousMode()) {
				long curTime = System.currentTimeMillis();
				while (jmmfPlayer.getState() == JMMFPlayer.PlayerState.SEEKING.value) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException ie){}
					if (System.currentTimeMillis() - curTime > SET_MT_TIMEOUT) {
	//					System.out.println("Set MT: time out");
						break;
					}
				}
			}
			//System.out.println("Set MT: " + (System.currentTimeMillis() - curTime));
			setControllersMediaTime(time);

		}
	}

	private void setMediaTimeAndWait(long time) {
		//System.out.println("T: " + time);
		if (jmmfPlayer != null) {
			// works a bit better than just setting the position
//			if (jmmfPlayer.getState() == JMMFPlayer.PlayerState.SEEKING.value){
//				return;
//			}
			if (jmmfPlayer.isPlaying()) {
				stop();
			}
			if (time < 0) {
				time = 0;
			}
			// don't check for the margin at the end of media
			if (time > duration /* - eomBuffer*/) {
				time = duration /* - eomBuffer*/;
			}
			
			jmmfPlayer.setMediaTime(time + offset);
			if (!jmmfPlayer.isSynchronousMode()) {
				long sysTime = System.currentTimeMillis();
				
				while (jmmfPlayer.getState() == JMMFPlayer.PlayerState.SEEKING.value) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException ie){}
					
					if (System.currentTimeMillis() - sysTime > SET_MT_TIMEOUT) {
						break;
					}
				}
			}
			setControllersMediaTime(time);
		}
	}

        @Override
	public void setMilliSecondsPerSample(long milliSeconds) {
		if (!frameRateAutoDetected) {
			this.millisPerSample = milliSeconds;
		}
	}

        @Override
	public void setOffset(long offset) {
		long diff = this.offset - offset;
        this.offset = offset;
        mediaDescriptor.timeOrigin = offset;
        if (jmmfPlayer != null) {
			if (origDuration == 0) {
				origDuration = jmmfPlayer.getDuration();
			}
        	duration = origDuration - offset;
        }
        stopTime += diff;
        setStopTime(stopTime);//??
	}

        @Override
	public void setRate(float rate) {
		if (!isInited) {
			cachedRate = rate;
		}
		if (jmmfPlayer != null) {
			jmmfPlayer.setRate(rate);
		}
		setControllersRate(rate);
	}

        @Override
	public void setStopTime(long stopTime) {
		this.stopTime = stopTime;
        // see if the stop time must be increased to ensure correct frame rendering at a frame boundary
		double msps = getMilliSecondsPerSample();
		if (msps != 0.0) {
	        long nFrames = (long) ((stopTime + offset) / msps);
	
	        if ((long) Math.ceil(nFrames * msps) == (stopTime + offset)) { // on a frame boundary
	            this.stopTime += 1;
	        }
		}
		if (jmmfPlayer != null) {
			jmmfPlayer.setStopTime(this.stopTime + offset);
		}
        setControllersStopTime(this.stopTime);
	}

        @Override
	public void setVolume(float level) {
		//System.out.println("Set volume: " + level);
		if (!isInited) {
			cachedVolume = level;
		}
		if (jmmfPlayer != null) {
			jmmfPlayer.setVolume(level);
		}
	}

        @Override
	public void start() {
		//System.out.println("start");
		if (jmmfPlayer != null) {
//			if (playing) {
//				return;
//			}
	        if (jmmfPlayer.isPlaying()) {
	        	return;
	        }
	        // play at start of media if at end of media
	        if ((getMediaDuration() - getMediaTime()) < 40) {
	            setMediaTime(0);
	        }

	        //playing = true;
	        jmmfPlayer.start();
	        if (!jmmfPlayer.isSynchronousMode()) {
				long sysTime = System.currentTimeMillis();
				while (jmmfPlayer.getState() != JMMFPlayer.PlayerState.STARTED.value) {
					//System.out.println("Poll: " + count + " " + getMediaTime());
					try {
						Thread.sleep(4);
					} catch (InterruptedException ie) {
						
					}
					if (System.currentTimeMillis() - sysTime > SET_MT_TIMEOUT) {
						break;
					}
				}
	        }
	        
	        startControllers();
	        
	        if (endTimeWatcher == null) {
	        	endTimeWatcher = new StrippedJMMFMediaPlayer.EndOfTimeWatcher(250);
	        	endTimeWatcher.setNormalPlayback(true);
	        	endTimeWatcher.setPlaying(true);
	        	endTimeWatcher.start();
	        } else {
	        	endTimeWatcher.setNormalPlayback(true);
	        	endTimeWatcher.setPlaying(true);
	        }

		}

	}

        @Override
	public void stop() {
		//System.out.println("stop");
		if (jmmfPlayer != null) {
//			if (!playing) {
//				return;
//			}
	        if (!jmmfPlayer.isPlaying()) {
	        	return;
	        }

			// stop a stop listening thread
			if (stopThread != null) {
				stopThread.setStopped();
			}
			
			//playing = false;
			jmmfPlayer.pause();
			// stop controller immediately without waiting until the player is actually stopped
			stopControllers();
			if (endTimeWatcher != null) {
				endTimeWatcher.setPlaying(false);
			}
			
			if (!jmmfPlayer.isSynchronousMode()) {
				// wait until the player is in the paused state, but not indefinitely
				long sysTime = System.currentTimeMillis();
				while (jmmfPlayer.getState() != JMMFPlayer.PlayerState.PAUSED.value) {
					//System.out.println("Poll: " + count + " " + getMediaTime());
					try {
						Thread.sleep(4);
					} catch (InterruptedException ie) {
						
					}
					if (System.currentTimeMillis() - sysTime > SET_MT_TIMEOUT) {
						break;
					}
				}
			}
			// to late to stop the controllers here?
//				stopControllers();
			//System.out.println("Paused at " + getMediaTime());
			setControllersMediaTime(getMediaTime());
			jmmfPlayer.repaintVideo();
			// stop a bit before the end of media because the player jumps to 0 when reaching the end
			// canceling the stop timer would be better
			if (jmmfPlayer.getStopTime() != duration - 10) {
				setStopTime(duration - 10);
			}

		}

	}


        @Override
	public void controllerUpdate(ControllerEvent event) {
	}

	/**
	 * Returns the current image; it is retrieved from the renderer, 
	 * so the size might not be the original size.
	 */
        @Override
	public Image getCurrentFrameImage() {
		return getFrameImageForTime(getMediaTime());
	}

	/**
	 * Currently returns the current image.
	 */
        @Override
	public Image getFrameImageForTime(long time) {
		if (jmmfPlayer == null) {
			return null;
		}

		if (jmmfPlayer.isPlaying()) {
			stop();
		}
		
        if (time != getMediaTime()) {
            setMediaTime(time);
        }

        // pass a header object as argument, it will be filled by the JNI code.
        // the image data array, without header, is returned
        BufferedImage image = null;
        DIBInfoHeader dih = new DIBInfoHeader();
        byte[] data = jmmfPlayer.getCurrentImageData(dih);
        image = DIBToImage.DIBDataToBufferedImage(dih, data);
		return image;
	}

	/**
     * Puts the specified text on the clipboard.
     * 
     * @param text the text to copy
     */
    private void copyToClipboard(String text) {
    	    if (text == null) {
    		    return;
    	    }
    	    //System.out.println(text);
    	    if (System.getSecurityManager() != null) {
            try {
                System.getSecurityManager().checkSystemClipboardAccess();
                StringSelection ssVal = new StringSelection(text);
                
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ssVal, null);
            } catch (SecurityException se) {
                //LOG.warning("Cannot copy, cannot access the clipboard.");
            } catch (IllegalStateException ise) {
            	   // LOG.warning("");
            }
        } else {
            try {
                StringSelection ssVal = new StringSelection(text);
                
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ssVal, null);
            } catch (IllegalStateException ise) {
            	   // LOG.warning("");
            }
        }
    }

    @Override
    public void setLayoutManager(ElanLayoutManager elm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateLocale() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void preferencesChanged() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	
//##############
	
	// hier test thread for setting media time ?
	/**
	 * Sets the media position of the player, waits till the operation is finished 
	 * and then updates the controllers.
	 */
	/*
	private class SetMediaPositionThread extends Thread {
		long time;
		long offset = 0;
		
		public SetMediaPositionThread(long time, long offset) {
			super();
			this.time = time;
			this.offset = offset;
		}

		public void run() {
			//jmmfPlayer.setMediaTime(time + offset);
			while (jmmfPlayer.getMediaTime() != time + offset) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException ie){
					return;
				}
			}
			setControllersMediaTime(time);
			//jmmfPlayer.repaintVideo();
		}
	}
	*/
	/*
	private class SetMediaPosQueued extends Thread {
		private ArrayDeque<long[]> queue;
		private final Object LOCK = new Object();
		
		public SetMediaPosQueued() {
			queue = new ArrayDeque<long[]>(10);
		}
		
		public void add(long[] timepair) {
			synchronized (LOCK) {
				queue.add(timepair);
			}
		}
		
		public void run () {
			while (true) {
				while (queue.isEmpty()) {
					try {
					    sleep(40);
					} catch (InterruptedException ie) {
						System.out.println("Interrupted while waiting...");
					}
				}
				
				long[] next;
				synchronized (LOCK) {
					next = queue.poll();
				}
				
				if (next != null) {
					System.out.println("Setting pos: " + next[0]);
					jmmfPlayer.setMediaTime(next[0] + next[1]);
					while (jmmfPlayer.getMediaTime() != next[0] + next[1]) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException ie){
							
						}
					}
					jmmfPlayer.repaintVideo();
					setControllersMediaTime(next[0]);
				}
			}
		}
	}
	*/
	/**
	 * TODO Revise, add a simple isInited to JMMFPlayer?
	 * Waits until the player is initiated and then stores, caches 
	 * some properties of the media.
	 */
	private class InitWaitThread extends Thread {
		final  int MAX_TRIES = 30;
		int count = 0;
		
                @Override
		public void run() {
			int state = 0;
			do {
				state = jmmfPlayer.getState();
				count++;
				if (state >= JMMFPlayer.PlayerState.STARTED.value && 
						state < JMMFPlayer.PlayerState.CLOSING.value) {
					isInited = true;
					System.out.println("JMMFMediaPlayer: Init Session");
					System.out.println("Aspect Ratio: " + jmmfPlayer.getAspectRatio());
					System.out.println("Duration: " + jmmfPlayer.getDuration());
					System.out.println("Time per frame: " + jmmfPlayer.getTimePerFrame());
					origDuration = jmmfPlayer.getDuration();
					//origAspectRatio = jmmfPlayer.getAspectRatio();
					int [] ar = jmmfPlayer.getPreferredAspectRatio();
					if (ar != null && ar.length == 2) {
						origAspectRatio = ar[0] / (float) ar[1];
						if (origAspectRatio != jmmfPlayer.getAspectRatio()) {
							System.out.println("Preferred Aspect Ratio: " + origAspectRatio);
						}
					}
					millisPerSample = jmmfPlayer.getTimePerFrame();
					eomBuffer = (long) (5 * millisPerSample);
					/*if (durationItem != null) {
						durationItem .setText(ElanLocale.getString("Player.duration") +
				                ":  " + TimeFormatter.toString(getMediaDuration()));
					}*/
					//System.out.println("Init set volume: " + cachedVolume);
					setVolume(cachedVolume);
					setRate(cachedRate);
					//layoutManager.doLayout();
					break;
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException ie) {
					
				}
				if (count > MAX_TRIES) {
					break;
				}
			} while (true);
		}
	}
	
	 /**
     * Class to take care of state changes after the player finished
     * playing an interval or reached end of media  Active
     * callback does not seem possible due to threading issues in JNI and MMF?
     */
    private class PlayerStateWatcher extends Thread {
    	// default sleep time of 250 ms
    	private int sleepInterval = 250;
		private boolean stopped = false;
		private final int MAX_SLEEP = 3; // break the waiting if sleep time is less than this
		
    	/**
    	 * Constructor.
    	 * 
    	 * @param sleepInterval the number of ms to sleep in between tests
    	 */
    	public PlayerStateWatcher(int sleepInterval) {
			super();
			if (sleepInterval > 0) {
				this.sleepInterval = sleepInterval;
			}
		}
    	
    	public void setStopped() {
    		stopped = true;
    	}
    	
        /**
         * DOCUMENT ME!
         */
            @Override
        public void run() {
        	long refTime = stopTime;
        	long curTime;
        	
            while (!stopped /*&& (getMediaTime() < refTime)*/) {
            	curTime = getMediaTime();
            	if (curTime >= refTime) {
            		break;
            	} else if (refTime - curTime <= sleepInterval) {
            		sleepInterval = (int) Math.max((refTime - curTime) / 2 - MAX_SLEEP, MAX_SLEEP);
            	}
//            	System.out.println("M time: " + getMediaTime() + " (" + refTime + ")");
//            	System.out.println("Sleep: " + sleepInterval);
                try {
                    Thread.sleep(sleepInterval);
                } catch (InterruptedException ie) {
                    //ie.printStackTrace();
                	return;
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                if (!jmmfPlayer.isPlaying()) {
                	break;
                }
            }
            
            if (stopped) {
            	return;
            }
            syncLock.lock();
            try {
	            if (jmmfPlayer.isPlaying()) {// in case pausing in the native player didn't succeed
	            	StrippedJMMFMediaPlayer.this.stop();
	                stopControllers();
	                jmmfPlayer.setMediaTime(refTime);
	                setControllersMediaTime(getMediaTime());
	                setStopTime(duration - eomBuffer);
	                //playing = false;
	            } else
	            /*if (playing)*/ { //if at stop time (i.e. not stopped by hand) do some extra stuff
	            	//System.out.println("Player at stop time");
	                stopControllers();
	                jmmfPlayer.setMediaTime(refTime);
	                
	                setControllersMediaTime(getMediaTime());
	                setStopTime(duration - eomBuffer);
	                //playing = false;
	            }
            } finally {
            	syncLock.unlock();
            }
        }
    }
    
    /**
     * A thread that tries to detect whether the media player already reached the end of media
     * and stops connected controllers if so. 
     * The native media player tries to stop playback a few hundred ms before the end of
     * media because when the media foundation player reaches the end the player is stopped 
     * (and as a result jumps back to 0 without "scrubbing" the first frame).
     * 
     * @author Han Sloetjes
     *
     */
    private class EndOfTimeWatcher extends Thread {
    	// default sleep time of 250 ms
    	private int sleepInterval = 250;
    	/** only detect end of file in case of normal playback */
    	private volatile boolean normalPlayback = true;
    	private volatile boolean isPlaying = false;
    	private boolean closed = false;
		/**
		 * Constructor that sets the sleep duration.
		 * 
		 * @param sleepInterval the sleep interval
		 */
		EndOfTimeWatcher(int sleepInterval) {
			super();
			if (sleepInterval > 0) {
				this.sleepInterval = sleepInterval;
			}
		}
    	
		/**
		 * Sets whether the player is in normal playback mode, i.e. whether it 
		 * is not playing a selection but plays until the end of the file.
		 * 
		 * @param normalPlayback a flag to indicate whether the player is in 
		 * normal playback mode
		 */
		public synchronized void setNormalPlayback(boolean normalPlayback) {
			this.normalPlayback = normalPlayback;
		}
		
		/**
		 * Sets the playing state.
		 * 
		 * @param playing
		 */
		public synchronized void setPlaying(boolean isPlaying) {
			this.isPlaying = isPlaying;
			if (isPlaying) {
				notify();
			}
		}
		
		/**
		 * Closes this thread, stops execution.
		 */
		public void close() {
			closed = true;
		}
		
		/**
		 * When active check if the player is at (or close to) the end of the media.
		 */
		public void run() {
			while (!closed) {
				try {
					Thread.sleep(sleepInterval);
					
					synchronized(this) {
						while (!isPlaying || !normalPlayback) {
							//System.out.println("Waiting...");
							wait();
						}
					}
				} catch (InterruptedException ie) {
					
				}
				// test for end of media, stop controllers etc.
				long curMediaTime = getMediaTime();
				if (curMediaTime >= getMediaDuration() - eomBuffer) {
					//System.out.println("At end: " + curMediaTime);
					if (jmmfPlayer.isPlaying()) {
						//System.out.println("At end: " + curMediaTime + " player still playing");
		            	StrippedJMMFMediaPlayer.this.stop();
		                stopControllers();

		                isPlaying = false;
		            } else {
		            	//System.out.println("At end: " + curMediaTime + " player already stopped.");
		            	// the player reached end of media and rewinded back to the beginning
		                stopControllers();

		                isPlaying = false;
		            }
				} else if (curMediaTime == 0){// maybe the media player isn't playing anymore, time = 0??
					if (jmmfPlayer.isPlaying()) {
						//System.out.println("Rewinded to: " + curMediaTime + " player is playing.");
		            	StrippedJMMFMediaPlayer.this.stop();
		                stopControllers();

		                isPlaying = false;
					} else {
						//System.out.println("Rewinded to: " + curMediaTime + " player stopped.");
		                stopControllers();

		                isPlaying = false;
					}
				} else if (jmmfPlayer.getState() == JMMFPlayer.PlayerState.PAUSED.value ||
						jmmfPlayer.getState() == JMMFPlayer.PlayerState.STOPPED.value) {
					if (isPlaying) {
		            	StrippedJMMFMediaPlayer.this.stop();
		                stopControllers();
		                
						isPlaying = false;
					}
				}
			}
		}
    }
	
    
}
