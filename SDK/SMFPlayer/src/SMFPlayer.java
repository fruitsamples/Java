/*	Copyright: 	© Copyright 2003 Apple Computer, Inc. All rights reserved.

	Disclaimer:	IMPORTANT:  This Apple software is supplied to you by Apple Computer, Inc.
			("Apple") in consideration of your agreement to the following terms, and your
			use, installation, modification or redistribution of this Apple software
			constitutes acceptance of these terms.  If you do not agree with these terms,
			please do not use, install, modify or redistribute this Apple software.

			In consideration of your agreement to abide by the following terms, and subject
			to these terms, Apple grants you a personal, non-exclusive license, under Apple’s
			copyrights in this original Apple software (the "Apple Software"), to use,
			reproduce, modify and redistribute the Apple Software, with or without
			modifications, in source and/or binary forms; provided that if you redistribute
			the Apple Software in its entirety and without modifications, you must retain
			this notice and the following text and disclaimers in all such redistributions of
			the Apple Software.  Neither the name, trademarks, service marks or logos of
			Apple Computer, Inc. may be used to endorse or promote products derived from the
			Apple Software without specific prior written permission from Apple.  Except as
			expressly stated in this notice, no other rights or licenses, express or implied,
			are granted by Apple herein, including but not limited to any patent rights that
			may be infringed by your derivative works or by other works in which the Apple
			Software may be incorporated.

			The Apple Software is provided by Apple on an "AS IS" basis.  APPLE MAKES NO
			WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED
			WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR
			PURPOSE, REGARDING THE APPLE SOFTWARE OR ITS USE AND OPERATION ALONE OR IN
			COMBINATION WITH YOUR PRODUCTS.

			IN NO EVENT SHALL APPLE BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL OR
			CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
			GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
			ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION, MODIFICATION AND/OR DISTRIBUTION
			OF THE APPLE SOFTWARE, HOWEVER CAUSED AND WHETHER UNDER THEORY OF CONTRACT, TORT
			(INCLUDING NEGLIGENCE), STRICT LIABILITY OR OTHERWISE, EVEN IF APPLE HAS BEEN
			ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
/*
        File:			SMFPlayer.java
        
        Description:	Creates an AUGraph and uses the AudioToolbox to play back MIDI files
						- Will scan the ~/Music/MIDI directory (which you'll need to create) for any MIDI files
						- creates a list of them you can choose from to playback
						- Will scan both System and User Library/Audio/Sounds/Banks for both DLS and SoundFont files
						- creates a list of them you can choose from to use as the source of sample data for the synth
						
						Uses the DefferedRender AudioUnit to take the load off the main I/O thread - introduces a buffer
						of latency, but its MIDI file playback, so that latency could be easily accounted for if needing
						to synchronize this with some other events...
						
        Author:			William Stewart
*/

import java.io.*;
import java.util.*;

import com.apple.component.*;
import com.apple.audio.hardware.*;
import com.apple.audio.midi.*;
import com.apple.audio.toolbox.*;
import com.apple.audio.units.*;
import com.apple.audio.util.*;
import com.apple.audio.*;

public class SMFPlayer {
	
	static SMFPlayer player;
	static UIWindow window;
	private static final boolean logging = true; //if false ALL debug code is NOT compiled
		
	public static void main (String[] args) {
		try {
			window = new UIWindow ("MIDI File Player");
            		if (logging)
						setUpLogs();
			player = new SMFPlayer();		
            window.setPlayer (player);		
			window.add (player.cpuMonitor.createUIPanel(), "South");
			
			window.addPlayPanel ();
			window.addFilePanelAndShow ("/Library/Audio/Sounds/Banks", 
								System.getProperty("user.home") + "/Library/Audio/Sounds/Banks", 
								System.getProperty("user.home") + "/Music/MIDI");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private AUGraphCPUMonitor cpuMonitor;
	private AUGraph myAUGraph;
	private MusicPlayer myMusicPlayer;
	private MusicDevice musicSynth;
	private AudioUnit limiterUnit, outputUnit;
	private File useThisSMF;
	private boolean playing = false;
	private MusicSequence myMusicSequence;
	
	SMFPlayer () {
		createGraph();
		cpuMonitor = new AUGraphCPUMonitor();
		cpuMonitor.logGraph(myAUGraph, "SMFPlayer", 2000);
	}
	
	MusicDevice getMusicSynth () {
		return musicSynth;
	}
	
	synchronized void loadSoundBank (File soundBankFile) {
		try {
					if (logging) {
						clearLogs();
						startLog1();
					}
			if (soundBankFile != null) {
				musicSynth.setProperty_SoundBank (soundBankFile);
					if (logging) {
						endLog1 ("Load Sound Bank" + soundBankFile);
						printLogs();
					}
			}
									
			if (useThisSMF != null)
				loadMIDIFile (useThisSMF);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	synchronized void loadMIDIFile (File midiFile) {
		try {
					if (logging) {
						clearLogs();
						startLog1();
					}
			
			MusicSequence oldSeq = myMusicSequence;
					if (logging) startLog2();
			myMusicSequence = new MusicSequence (midiFile);
					if (logging) endLog2 ("Music Sequence Load SMF");
				
					if (logging) startLog2();
			myMusicSequence.setAUGraph(myAUGraph);
					if (logging) endLog2 ("Set AUGraph");
				
			if (myMusicPlayer == null)
				myMusicPlayer = new MusicPlayer();
				
					if (logging) startLog2();
			myMusicPlayer.setSequence (myMusicSequence);
					if (logging) endLog2 ("Set Sequence");
			
			if (oldSeq != null) {
						if (logging) startLog2();
				oldSeq.dispose();
						if (logging) endLog2 ("Dispose Old Sequence");
			}
					
			myMusicPlayer.setTime (0);
			
			useThisSMF = midiFile;
			
					if (logging) {
						endLog1 ("Load MIDI file:" + midiFile);
						printLogs();
					}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	boolean setReverbType (String whichReverb) {
		try {
			int reverbType = 0;
			if ("Small Room".equals(whichReverb))
				reverbType = AUProperties.kReverbRoomType_SmallRoom;
			else if ("Medium Room".equals(whichReverb))
				reverbType = AUProperties.kReverbRoomType_MediumRoom;
			else if ("Large Room".equals(whichReverb))
				reverbType = AUProperties.kReverbRoomType_LargeRoom;
			else if ("Medium Hall".equals(whichReverb))
				reverbType = AUProperties.kReverbRoomType_MediumHall;
			else if ("Large Hall".equals(whichReverb))
				reverbType = AUProperties.kReverbRoomType_LargeHall;
			else if ("Plate".equals(whichReverb))
				reverbType = AUProperties.kReverbRoomType_Plate;
			else {
				new Exception("Unknown Reverb Type").printStackTrace ();
				return false;
			}
			CAMemoryObject propValue = new CAMemoryObject (4, false);
			propValue.setIntAt (0, reverbType);
			musicSynth.setProperty (AUProperties.kAudioUnitProperty_ReverbRoomType, 
									AUConstants.kAudioUnitScope_Global,
									0,
									propValue);
		} catch (CAException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	void play () throws CAException {
		if (myMusicPlayer == null) {
			playing = false;
			return;
		}
		playing = true;
				if (logging) {
					clearLogs();
					startLog1();
				}
		myMusicPlayer.preroll(); // initializes myAUGraph
				if (logging) endLog1 ("Preroll");
				
				if (logging) startLog2();
		myMusicPlayer.start();
				if (logging) {
					endLog2 ("Start Player");
					printLogs();
				}
	}
	
	boolean isPlaying () {
		return playing;
	}
	
	void backToStart () throws CAException {
		setTime (0);
	}
	
	void stop () throws CAException {
		playing = false;
		if (myMusicPlayer != null)
			myMusicPlayer.stop();
	}
	
	int getTime () throws CAException {
		if (myMusicPlayer == null)
			return 0;
		double time = myMusicPlayer.getTime();
		return (int)(time + 0.5);
	}

	void setTime (double time) throws CAException {
		if (myMusicPlayer != null) {
					if (logging) {
						clearLogs();
						startLog1();
					}
			myMusicPlayer.setTime(time);
					if (logging) {
						endLog1 ("Set Time");
						printLogs();
					}
		}
	}
		
	void cleanUp() {
		try {
			cpuMonitor.cleanUp();
			if (myMusicPlayer != null)
				myMusicPlayer.stop();
			if (myAUGraph != null) {
				myAUGraph.uninitialize();
				myAUGraph.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void createGraph () {		
		try {			
					if (logging) {
						clearLogs();
						startLog1();
					}
				
			myAUGraph = new AUGraph();
				
					if (logging) startLog2();
			ComponentDescription cd = new ComponentDescription (
												AUConstants.kAudioUnitComponentType,
												AUConstants.kAudioUnitSubType_MusicDevice,
												AUConstants.kAudioUnitID_DLSSynth,
												0, 
												0);
			AUNode synth = myAUGraph.newNode (cd);
			
			cd.setSubType (AUConstants.kAudioUnitSubType_Effect);
			cd.setManufacturer (AUConstants.kAudioUnitID_PeakLimiter);
			AUNode limiter = myAUGraph.newNode (cd);
			
			cd.setSubType (AUConstants.kAudioUnitSubType_Output);
			cd.setManufacturer (AUConstants.kAudioUnitID_DefaultOutput);
			AUNode output = myAUGraph.newNode (cd);
					if (logging) endLog2 ("Create nodes");
					
					if (logging) startLog2();
					// this opens the Audio Units that the myAUGraph creates from the descriptions
			myAUGraph.open();				
					if (logging) endLog2("Open Graph");
	
					
					if (logging) startLog2();
				// do this AFTER the myAUGraph has been opened
			musicSynth = (MusicDevice)myAUGraph.getNodeInfo_AudioUnit (synth);
					if (logging) endLog2 ("Get First Audio Unit");
				
					if (logging) startLog2();
			limiterUnit = myAUGraph.getNodeInfo_AudioUnit (limiter);
			outputUnit = myAUGraph.getNodeInfo_AudioUnit (output);
					if (logging) endLog2 ("Get Audio Units");

					if (logging) startLog2();
			myAUGraph.initialize();
					if (logging) startLog2();
			myAUGraph.connectNodeInput (synth, 0, limiter, 0);
			myAUGraph.connectNodeInput (limiter, 0, output, 0);
					if (logging) endLog2 ("Connect nodes");

					if (logging) {
						endLog1 ("Create Graph");
						printLogs ();
					
						if (false) {
							System.out.println ("Node Count:" + myAUGraph.getNodeCount());
							for (int i = 0; i < myAUGraph.getNodeCount(); i++) {
								AUNode node = myAUGraph.getIndNode(i);
								System.out.println (myAUGraph.getNodeInfo_ComponentDescription (node));
							}
						}
				}
 		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static class LogEntry {
		long time;
		String message;
		public String toString () {
			return message + " took:" + time + " msecs";
		}
	}
	
	static LogEntry log1;
	static LogEntry[] log2;
	static int numLogs2;
	
	static void setUpLogs () {
		log1 = new LogEntry();
		log2 = new LogEntry[10];
		for (int i = 0; i < log2.length; i++)
			log2[i] = new LogEntry ();
	}
	
	static void clearLogs () {
		numLogs2 = 0;
	}
	
	static long time1;
	static private void startLog1 () {
		time1 = System.currentTimeMillis();
	}
	
	static long time2;
	static private void startLog2 () {
		time2 = System.currentTimeMillis();
	}
	
	static private void endLog1 (String str) {
		log1.message = str;
		log1.time = System.currentTimeMillis() - time1;
	}

	static private void endLog2 (String str) {
		log2[numLogs2].message = str;
		log2[numLogs2++].time = System.currentTimeMillis() - time2;
	}
		
	static private void printLogs () {
		System.out.println (log1);
		if (numLogs2 > 0)
		for (int i = 0; i < numLogs2; i++)
			System.out.println ("\t" + log2[i]);
	}

	static private void printMessage (String str) {
		System.out.println (str);
	}
}
