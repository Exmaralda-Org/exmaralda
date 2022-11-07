### Test Protocol for EXMARaLDA distributions

We are very grateful for any reports on failures or success to support@exmaralda.org. 
If you write to us, please make sure to give us information on what system you are on. The easiest way to do this is to go to "Help > About..." in any of the tools and then press "Copy debug info". This will copy the log information to the clipboard and you can paste it into an E-Mail.

1. Remove existing installations

- on Windows: run the uninstaller
- on MAC OS: move the *.app to the bin


2. Download EXMARaLDA Media Test Battery

Download from https://exmaralda.org/en/utilities/. This is an excerpt of an EXB transcript with WAV, MPG and MP4 audio/video files in the formats which EXMARaLDA promises to support


3. Download distribution

- official distributions (updated in longer intervals): https://exmaralda.org/en/release-version/
- preview distributions (updated frequently, new features, bug fixes, etc.): https://exmaralda.org/en/preview-version/
- experimental distributions (only when distribution process itself is modified): https://exmaralda.org/files/prevDL/experimental.html

4. Install distribution

- on Windows: run setup.exe
- on MAC OS: unpack DMG and move app to application folder

5. First start

- Start Partitur-Editor by double-clicking on it. Does the OS open it? Or does it give a message, refusing to open it. If it does, what is the message (screenshot)?
- If Partitur-Editor does not open with a simple double click, try to open it as follows:
  - on Windows: click on "More info" (or something like it), then choose "Run anyway"
  - on MAC OS: right click on the app and choose "Open" from the context menu, then choose "Open" in the dialog that appears

6. Test players

- In the lower right corner of the Partitur-Editor, check what player the tool is currently working with
- In the Partitur-Editor, choose "File > Open..." and open "Beckhams.exb" from the EXMARaLDA Media Test Battery.
  - Does the file open?
  - Does the waveform display show the waveform?
  - Is the video image displayed in the Media panel?
  - Can you play the audio/video? (Play button underneath the waveform display)
  - Can you select a stretch of the transcript (click on an interval in the timeline) and can you play the corresponding part of the audio/video ("Play selection" button)
  - Can you move boundaries of the selection in the waveform (click and drag with the mouse or use mouse wheel) and does the video image change when you move boundaries?
  - Can you slow down/speed up playback (will only work for JDS Player, AVF Player and JavaFX player)?
- If you get an error message for the selected media file, try to change the order of associated media files. Remember that JDS player will not play *.MP4, BAS player will only play *.WAV.
- Test this with all available players
  - Go to "Edit > Preferences... > Media" to change the media player
  - Restart the Partitur-Editor after you have changed the media player




