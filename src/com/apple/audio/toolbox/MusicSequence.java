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
//
//  MusicSequence.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.toolbox;

import com.apple.audio.*;
import com.apple.audio.util.*;
import com.apple.audio.midi.MIDIEndpoint;
import com.apple.audio.jdirect.Accessor;
import java.util.Vector;
import java.io.*;

/**
 * A MusicSequence contains an arbitrary number of tracks (MusicTrack)
 * each of which contains time-stamped (typically in units of beats, or seconds )
 * events in time-increasing order.  There are various types of events, defined below,
 * including the expected MIDI events, tempo, and extended events.
 * A MusicTrack has properties which may be inspected and assigned, including support
 * for looping, muting/soloing, and time-stamp interpretation.
 * APIs exist for iterating through the events in a MusicTrack, and for performing
 * basic editing operations on them.
 * <P>
 * Each MusicSequence may have an associated AUGraph object, which represents a set
 * of AudioUnits and the connections between them.  Then, each MusicTrack of the
 * MusicSequence may address its events to a specific AudioUnit within the AUGraph.
 * In such a manner, it's possible to automate arbitrary parameters of AudioUnits,
 * and for example to schedule notes to be played to MusicDevices 
 * (AudioUnit software synthesizers) within an arbitrary audio processing network (AUGraph).
 * <P>
 * To address the sequence events to a particular AUGraph, use MusicSequenceSetAUGraph()
 * By default the first AUNode representing a DLS MusicDevice will be used as the destination
 * Please see MusicTrackSetDestNode() in order to change the addressing on a track by track basis.
 * <P>
 * Instead of addressing a sequence to an AUGraph, the MusicSequenceSetMIDIEndpoint() call may
 * be used to set all tracks to address the particular MIDI device.
 * It is then possible to override individual tracks to address either a different MIDIEndpoint
 * with MusicTrackSetDestMIDIEndpoint() or an AudioUnit with MusicTrackSetDestNode().
 * This allows the mixing of sequencing software synths (and other AudioUnits) along with
 * MIDI devices. 
 * <UL>MusicSequence global information is:
 * <LI>an AUGraph
 * <LI>copyright and other textual info
 * </UL>
 */
public final class MusicSequence extends CAObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}
	
	static final int firstArg4Ptr = JNIToolbox.malloc(4);
	static final int secondArg8Ptr = JNIToolbox.malloc(8);
	static final Object syncObject = new Object();
//_________________________ STATIC METHODS
	/**
	 * Create a MusicSequence object.
	 * <BR><BR><b>CoreAudio::NewMusicSequence</b>
	 */
	public MusicSequence () throws CAException {
		super (allocate());
	}
	
	private static int allocate () throws CAException {
		synchronized (syncObject) {
			int res = NewMusicSequence (firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
	}

	/**
	 * Create a MusicSequence object from a SMF or the MIDI File portion of a RMID file.
	 * <BR><BR><b>CoreAudio::NewMusicSequence</b>
	 * @param inFile the SMF file
	 */
	public MusicSequence (File inFile) throws CAException, IOException {
		this ();
		loadSMF (inFile);
	}
//_________________________ INSTANCE FIELDS
	private CAMemoryObject cacheObj = new CAMemoryObject (4, false);
	private Vector tracks = new Vector();
	private AUGraph theGraph;
	
//_________________________ INSTANCE METHODS
	/**
	 * Adds sequence tracks for each track of the specified SMF file to the existing MusicSequence.
	 * <BR><BR><b>CoreAudio::MusicSequenceLoadSMF</b>
	 * @param inFile the SMF file
	 */
	public void loadSMF (File inFile) throws CAException, IOException {
		String path = inFile.getCanonicalPath();
		File f = new File (path);
		if (CASession.hasSecurityRestrictions()) {
			SecurityManager sm = System.getSecurityManager();
			if (sm != null)
				sm.checkRead (path);
		}
		if (f.exists() == false)
			throw new FileNotFoundException (path);
					
/*
  short               vRefNum;
  long                parID;
  StrFileName         name;                   give it 256 to be safe!!!
*/
		int fsspec = JNIToolbox.malloc (262);
		int res = JNIToolbox.makeFSSpec (path, fsspec);
		if (res == 0)
			res = MusicSequenceLoadSMF (_ID(), fsspec);
		JNIToolbox.free (fsspec);
		CAException.checkError (res);
	}

	/**
	 * Adds a track to the sequence.
	 * <BR><BR><b>CoreAudio::MusicSequenceNewTrack</b>
	 * @return the new track
	 */
	public MusicTrack newTrack () throws CAException {
		int ptr = 0;
		synchronized (syncObject) {
			int res = MusicSequenceNewTrack(_ID(), firstArg4Ptr);
			CAException.checkError (res);
			ptr = Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
		MusicTrack track = new MusicTrack (ptr);
		tracks.addElement(track);
		return track;
	}

	/**
	 * Removes the track from the Sequence and disposes it.
	 * <BR><BR><b>CoreAudio::MusicSequenceDisposeTrack</b>
	 * @param inTrack the track to remove.
	 */
	public void disposeTrack (MusicTrack inTrack) throws CAException {
		int res = MusicSequenceDisposeTrack(_ID(), CAObject.ID(inTrack));
		CAException.checkError (res);
		tracks.removeElement (inTrack);
		inTrack._setNR(0);
	}

	/**
	 * Gets the number of tracks attache to the sequence.
	 * Parameter should be null, as it is not used.
	 * <BR><BR><b>CoreAudio::MusicSequenceGetTrackCount</b>
	 * @deprecated don't need to provide parameter.
	 * @return the number of tracks
	 */
	public int getTrackCount (MusicTrack inTrack) throws CAException {
		return getTrackCount ();
	}

	/**
	 * Gets the number of tracks attache to the sequence.
	 * <BR><BR><b>CoreAudio::MusicSequenceGetTrackCount</b>
	 * @return the number of tracks
	 */
	public int getTrackCount () throws CAException {
		synchronized (syncObject) {
			int res = MusicSequenceGetTrackCount(_ID(), firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
	}

	/**
	 * Gets the track at the specified index.
	 * <BR><BR><b>CoreAudio::MusicSequenceGetIndTrack</b>
	 * @param inTrackIndex0 the index
	 * @return the track
	 */
	public MusicTrack getIndTrack (int inTrackIndex0) throws CAException {
		int ptr = 0;
		synchronized (syncObject) {
			int res = MusicSequenceGetIndTrack(_ID(), inTrackIndex0, firstArg4Ptr);
			CAException.checkError (res);
			ptr = Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
		return (ptr != 0	
					? new MusicTrack (ptr)
					: null);
	}
	
	/**
	 * Gets the index of the specified track.
	 * <BR><BR><b>CoreAudio::MusicSequenceGetTrackIndex</b>
	 * @param inTrack the track - if not found will throw an Exception
	 * @return the track's index0
	 */
	public int getTrackIndex (MusicTrack inTrack) throws CAException {
		synchronized (syncObject) {
			int res = MusicSequenceGetTrackIndex(_ID(), CAObject.ID(inTrack), firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
	}
	
	/**
	 * Sets the AUGraph that is controlled by this sequence.
	 * <BR><BR><b>CoreAudio::MusicSequenceSetAUGraph</b>
	 * @param inGraph the graph
	 */
	public void setAUGraph (AUGraph inGraph) throws CAException {
		int res = MusicSequenceSetAUGraph(_ID(), CAObject.ID(inGraph));
		CAException.checkError (res);
		theGraph = inGraph;
	}

	/**
	 * Gets the AUGraph that is controlled by this sequence.
	 * <BR><BR><b>CoreAudio::MusicSequenceGetAUGraph</b>
	 * @return the graph
	 */
	public AUGraph getAUGraph () throws CAException {
		int ptr = 0;
		synchronized (syncObject) {
			int res = MusicSequenceGetAUGraph(_ID(), firstArg4Ptr);
			CAException.checkError (res);
			ptr = Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
		if (ptr == 0)
			return null;
		if (ptr != CAObject.ID(theGraph))
			theGraph = new AUGraph (ptr, this);
		return theGraph;
	}
	
	/**
	 * Save the sequence to a Standard MIDI File.
	 * <BR><BR><b>CoreAudio::MusicSequenceSaveSMF</b>
	 * @param inFile the file that will contain the sequence data as a SMF
	 * @param inResolution is relationship between "tick" and quarter note for saving to SMF
	 * - pass in zero to use default (480 PPQ, normally)
	 */
	public void saveSMF (File inFile, int inResolution) throws CAException, IOException {
		String path = inFile.getCanonicalPath();
		if (CASession.hasSecurityRestrictions()) {
			SecurityManager sm = System.getSecurityManager();
			if (sm != null)
				sm.checkWrite (path);
		}

/*
  short               vRefNum;
  long                parID;
  StrFileName         name;                   give it 256 to be safe!!!
*/
			// this is a hack to create a file, so that makeFSSpec will work
		if (inFile.exists() == false) {
			FileOutputStream fos = new FileOutputStream (inFile);
			fos.write (0);
			fos.close();
		}
		
		int fsspec = JNIToolbox.malloc (262);
		int res = JNIToolbox.makeFSSpec (path, fsspec);
		if (res == 0)
			res = MusicSequenceSaveSMF(_ID(), fsspec, (short)inResolution );
		JNIToolbox.free (fsspec);
		CAException.checkError (res);
	}
	
	/**
	 * "Reverses" (in time) all events (including tempo events).
	 * <BR><BR><b>CoreAudio::MusicSequenceReverse</b>
	 */
	public void reverse () throws CAException {
		int res = MusicSequenceReverse(_ID());	
		CAException.checkError (res);
	}
	
	/**
	 * Sets a MIDIEndpoint for all of the tracks in this sequence.
	 * <BR><BR><b>CoreAudio::MusicSequenceSetMIDIEndpoint</b>
	 * @param inEndpoint the MIDIEndpoint for this sequence
	 */
	public void setMIDIEndpoint (MIDIEndpoint inEndpoint) throws CAException {
		int res = MusicSequenceSetMIDIEndpoint (_ID(), CAObject.ID(inEndpoint));
		CAException.checkError (res);
	}

	/**
	 * Tempo events are segregated onto a separate track when imported from a MIDI File.
	 * Only tempo events in this special tempo track (which is not accessible with 
	 * MusicSequenceGetTrackCount / MusicSequenceGetIndTrack) have effect on playback.
	 * This track may be edited like any other, however.
	 * <BR><BR><b>CoreAudio::MusicSequenceGetTempoTrack</b>
	 */
	public MusicTrack getTempoTrack () throws CAException {
		int ptr = 0;
		synchronized (syncObject) {
			int res = MusicSequenceGetTempoTrack (_ID(), firstArg4Ptr);
			CAException.checkError(res);
			ptr = Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
		return (ptr != 0	
					? new MusicTrack (ptr)
					: null);
	}
	
	/**
	 * Returns a seconds value that would correspond to the supplied beats. 
	 * <BR><BR><b>CoreAudio::MusicSequenceGetSecondsForBeats</b>
	 */
	public double getSecondsForBeats (double inBeats) throws CAException {
		synchronized (syncObject) {
			int res = MusicSequenceGetSecondsForBeats (_ID(), inBeats, secondArg8Ptr);
			CAException.checkError(res);
			return Accessor.getDoubleFromPointer (secondArg8Ptr, 0);
		}
	}

	/**
	 * Returns a beats value that would correspond to the supplied seconds. 
	 * <BR><BR><b>CoreAudio::MusicSequenceGetBeatsForSeconds</b>
	 */
	public double getBeatsForSeconds (double inSeconds) throws CAException {
		synchronized (syncObject) {
			int res = MusicSequenceGetBeatsForSeconds (_ID(), inSeconds, secondArg8Ptr);
			CAException.checkError(res);
			return Accessor.getDoubleFromPointer (secondArg8Ptr, 0);
		}
	}

//_ NATIVE METHODS
	private static native int NewMusicSequence(int outSequencePtr);
	private static native int MusicSequenceNewTrack(int inSequence, int outTrackPtr);
	private static native int MusicSequenceDisposeTrack(int inSequence, int inTrack);
	private static native int MusicSequenceGetTrackCount(int inSequence, int outNumberOfTracksPtr);
	private static native int MusicSequenceGetIndTrack(int inSequence, int inTrackIndex, int outTrackPtr);
	private static native int MusicSequenceGetTrackIndex(int inSequence,int inTrack, int outTrackIndexPtr);
	private static native int MusicSequenceSetAUGraph(int inSequence, int inGraph);
	private static native int MusicSequenceGetAUGraph(int inSequence, int outGraphPtr);
	private static native int MusicSequenceLoadSMF(int inSequence, int inFileSpec);								
	private static native int MusicSequenceSaveSMF(int inSequence, int inFileSpec, short inResolution );
	private static native int MusicSequenceReverse(int inSequence);	
	private static native int MusicSequenceSetMIDIEndpoint (int inSequence, 
															int	inEndpoint);
	private static native int MusicSequenceGetTempoTrack (int inSequence, 
															int outTrackPtr);
	private static native int MusicSequenceGetSecondsForBeats (int	inSequence,
 												  			double inBeats,
 												  			int outSeconds);
	private static native int MusicSequenceGetBeatsForSeconds (int	inSequence,
 												  			double 	inSeconds,
 												  			int  outBeats);
}

/*
 */
