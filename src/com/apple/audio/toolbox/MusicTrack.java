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
//  MusicTrack.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.toolbox;

import com.apple.audio.*;
import com.apple.audio.jdirect.*;
import com.apple.audio.util.*;
import com.apple.audio.midi.*;
import com.apple.audio.jdirect.Accessor;

/**
 * A Music track is attached from an MusicSequence and contains events that
 * are ordered by their time values.
 * <P>
 * <UL>MusicTrack properties are:
 * <LI> AUNode (in the AUGraph) of the AudioUnit addressed by the MusicTrack
 * <LI> textual info
 * <LI> mute / solo state
 * <LI> offset time
 * <LI> loop time and number of loops
 * <LI> time units for the event timestamps (beats, seconds, ...) 
 * beats go through tempo map, seconds map absolute time
 * </UL>
 * For the track editing operations, all time ranges are as follows [starttime, endtime].
 * In other words, the range includes the start time, but includes events only up
 * to, but NOT including the end time...
*/
public final class MusicTrack extends CAObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}
	
//_________________________ STATIC METHODS
	/**
	 * Create a MusicTrack object.
	 */
	MusicTrack (int ptr) {
		super (ptr, MusicSequence.syncObject);
	}	
	
//_________________________ INSTANCE METHODS
	void _setNR (int ptr) {
		setNR(ptr);
	}

// Notes on properties
//
//	kSequenceTrackProperty_LoopInfo
//		The default looping behaviour is to loop once through the entire track
//		pass zero in for inNumberOfLoops to loop forever
//		
	/**
	 * Gets the current value of the specified property and returns the number
	 * of bytes that were written to the supplied memory object.
	 * <BR><BR><b>CoreAudio::MusicTrackGetProperty</b><BR>
	 * @param inPropertyID the identifier for the property
	 * @param outPropertyValue the contents of this object will be written too.
	 * @return the number of bytes written to the outPropertyValue object
	 */
	public int getProperty (int inPropertyID, CAMemoryObject outPropertyValue) throws CAException {
		synchronized (MusicSequence.syncObject) {
			Accessor.setIntInPointer (MusicSequence.firstArg4Ptr, 0, outPropertyValue.getSize());
			int res = MusicTrackGetProperty(_ID(), 
								inPropertyID, 
								CAObject.ID(outPropertyValue), 
								MusicSequence.firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (MusicSequence.firstArg4Ptr, 0);
		}
	}

	/**
	 * Gets the size (num of bytes) required to retrieve the value of this property
	 * <BR><BR><b>CoreAudio::MusicTrackGetProperty</b><BR>
	 * @param inPropertyID the identifier for the property
	 * @return the number of bytes that will be written to the outPropertyValue object in getProperty()
	 */
	public int getPropertySize (int inPropertyID) throws CAException {
		synchronized (MusicSequence.syncObject) {
			Accessor.setIntInPointer (MusicSequence.firstArg4Ptr, 0, 0);
			int res = MusicTrackGetProperty(_ID(), inPropertyID, 0, MusicSequence.firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (MusicSequence.firstArg4Ptr, 0);
		}
	}

	/**
	 * Sets the specified property of this MusicTrack.
	 * <BR><BR><b>CoreAudio::MusicTrackSetProperty</b><BR>
	 * @param inPropertyID the identifier for the property
	 * @param inPropertyValue the new value for the specified property
	 */
	public void setProperty (int inPropertyID, CAMemoryObject inPropertyValue) throws CAException {
		int res = MusicTrackSetProperty(_ID(), inPropertyID, CAObject.ID(inPropertyValue), inPropertyValue.getSize());
		CAException.checkError (res);
	}

	/**
	 * Moves the events in the track within the time range of start to end times, by inMoveTime time.
	 * <BR><BR><b>CoreAudio::MusicTrackMoveEvents</b><BR>
	 * @param inStartTime the start time of events
	 * @param inEndTime the end time of events (events are covered up to but not including events at end time)
	 * @param inMoveTime may be negative to move events backwards in time
	 */
	public void moveEvents (double inStartTime, double inEndTime, double inMoveTime) throws CAException {
		int res = MusicTrackMoveEvents(_ID(),
								inStartTime,
								inEndTime,
								inMoveTime);
		CAException.checkError (res);
	}

	/**
	 * Creates a new track from the events in the track within the time range of start to end times.
	 * <BR><BR><b>CoreAudio::NewMusicTrackFrom</b><BR>
	 * @param inSourceStartTime the start time of events
	 * @param inSourceEndTime the end time of events (events are covered up to but not including events at end time)
	 */
	public MusicTrack newFrom (double inSourceStartTime, double inSourceEndTime) throws CAException {
		int ptr = 0;
		synchronized (MusicSequence.syncObject) {
			int res = NewMusicTrackFrom(_ID(),
										inSourceStartTime,
										inSourceEndTime,
										MusicSequence.firstArg4Ptr);
			CAException.checkError (res);
			ptr = Accessor.getIntFromPointer (MusicSequence.firstArg4Ptr, 0);
		}
		return (ptr == 0
					? null
					: new MusicTrack (ptr));
	}
												
	/**
	 * Removes all of the events in the track within the time range of start to end times.
	 * <BR><BR><b>CoreAudio::MusicTrackClear</b><BR>
	 * @param inStartTime the start time of events
	 * @param inEndTime the end time of events (events are covered up to but not including events at end time)
	 */
	public void clear (double inStartTime, double inEndTime) throws CAException {
		int res = MusicTrackClear(_ID(), inStartTime, inEndTime);
		CAException.checkError (res);
	}
		
	/**
	 * Removes all of the events in the track within the time range of start to end times, moving events
	 * that occur at and after end time to start at the specified start time.
	 * <BR><BR><b>CoreAudio::MusicTrackCut</b><BR>
	 * @param inStartTime the start time of events
	 * @param inEndTime the end time of events (events are covered up to but not including events at end time)
	 */
	public void cut (double inStartTime, double inEndTime) throws CAException {
		int res = MusicTrackCut(_ID(), inStartTime, inEndTime);
		CAException.checkError (res);
	}

	/**
	 * Inserts all of the events in the track within the time range of start to end times to the destination
	 * track at inDestInsertTime time. All events at and after inDestInsertTime in inDestTrack are moved forward by the
	 * range's duration
	 * <BR><BR><b>CoreAudio::MusicTrackCopyInsert</b><BR>
	 * @param inSourceEndTime the start time of events
	 * @param inSourceEndTime the end time of events (events are covered up to but not including events at end time)
	 * @param inDestTrack the destination track to insert events into
	 * @param inDestInsertTime the start time for where these events in the destination track will be moved to.
	 */
	public void copyInsert (double inSourceStartTime, double inSourceEndTime, MusicTrack inDestTrack, double inDestInsertTime) throws CAException {
		int res = MusicTrackCopyInsert(_ID(),
										inSourceStartTime,
										inSourceEndTime,
										CAObject.ID(inDestTrack),
										inDestInsertTime);
		CAException.checkError (res);
	}

	/**
	 * Merges all of the events in the track within the time range of start to end times into the destination
	 * track starting at inDestInsertTime time.
	 * <BR><BR><b>CoreAudio::MusicTrackMerge</b><BR>
	 * @param inSourceEndTime the start time of events
	 * @param inSourceEndTime the end time of events (events are covered up to but not including events at end time)
	 * @param inDestTrack the destination track to insert events into
	 * @param inDestInsertTime the start time for where these events in the destination track will be moved to.
	 */
	public void merge (double inSourceStartTime, double inSourceEndTime, MusicTrack inDestTrack, double inDestInsertTime) throws CAException {
		int res = MusicTrackMerge(_ID(),
								inSourceStartTime,
								inSourceEndTime,
								CAObject.ID(inDestTrack),
								inDestInsertTime);
		CAException.checkError (res);
	}
	
	public void newMIDINoteEvent (double inTimeStamp, MIDIData noteData) throws CAException {
		if (!noteData.isType(MIDIData.kMIDINoteMessage))
			throw new ClassCastException ("Must pass in a kMIDINoteMessage type to newMIDINoteEvent");
		int res = MusicTrackNewMIDINoteEvent(_ID(), inTimeStamp, CAObject.ID(noteData));
		CAException.checkError (res);
	}
								
	public void newMIDIChannelEvent (double inTimeStamp, MIDIData channelData) throws CAException {
		if (!channelData.isType(MIDIData.kMIDIChannelMessage))
			throw new ClassCastException ("Must pass in a kMIDIChannelMessage type to newMIDIChannelEvent");
		int res = MusicTrackNewMIDIChannelEvent(_ID(), inTimeStamp, CAObject.ID(channelData));
		CAException.checkError (res);
	}

	public void newMIDIRawDataEvent (double inTimeStamp, MIDIData rawData) throws CAException {
		if (!rawData.isType(MIDIData.kMIDIRawData))
			throw new ClassCastException ("Must pass in a kMIDIRawData type to newMIDIRawDataEvent");
		int res = MusicTrackNewMIDIRawDataEvent(_ID(), inTimeStamp, CAObject.ID(rawData));
		CAException.checkError (res);
	}

	public void newExtendedNoteEvent (double inTimeStamp, ExtendedNoteOnEvent extNoteData) throws CAException {
		int res = MusicTrackNewExtendedNoteEvent(_ID(), inTimeStamp, CAObject.ID(extNoteData));//const ExtendedNoteOnEvent	*
		CAException.checkError (res);
	}

	public void newExtendedControlEvent (double inTimeStamp, ExtendedControlEvent extControlData) throws CAException {
		int res = MusicTrackNewExtendedControlEvent(_ID(), inTimeStamp, CAObject.ID(extControlData));
		CAException.checkError (res);
	}

	public void newExtendedTempoEvent (double inTimeStamp, double inBPM) throws CAException {
		int res = MusicTrackNewExtendedTempoEvent(_ID(), inTimeStamp, inBPM);
		CAException.checkError (res);
	}
	
	/**
	 * @deprecated use correctly spelled method
	 * @see MusicTrack.newExtendedTempoEvent
	 */
	public void newExtendedTempEvent (double inTimeStamp, double inBPM) throws CAException {
		newExtendedTempoEvent (inTimeStamp, inBPM);
	}
	
	/**
	 * @deprecated
	 */
	public void newMetaEvent (double inTimeStamp, CAMemoryObject metaData) throws CAException {
		newMetaEvent (inTimeStamp, (MIDIData)metaData);
	}

	/**
	 * Adds a MIDI Meta Event to the track.
	 * <BR><BR><b>CoreAudio::MusicTrackNewMetaEvent</b><BR>
	 * @param inTimeStamp the time (in beats) of the event
	 * @param metaData the meta event
	 */
	public void newMetaEvent (double inTimeStamp, MIDIData metaData) throws CAException {
		if (!metaData.isType(MIDIData.kMIDIMetaEvent))
			throw new ClassCastException ("Must pass in a kMIDIMetaEvent type to newMetaEvent");
		int res = MusicTrackNewMetaEvent(_ID(), inTimeStamp, CAObject.ID(metaData));
		CAException.checkError (res);
	}

	/**
	 * @deprecated
	 */
	public void newUserEvent (double inTimeStamp, CAMemoryObject userData) throws CAException {
		newUserEvent (inTimeStamp, (MusicUserEvent)userData);
	}

	/**
	 * Adds a Music User Event to the track.
	 * <BR><BR><b>CoreAudio::MusicTrackNewUserEvent</b><BR>
	 * @param inTimeStamp the time (in beats) of the event
	 * @param userData the user event
	 */
	public void newUserEvent (double inTimeStamp, MusicUserEvent userData) throws CAException {
		int res = MusicTrackNewUserEvent(_ID(), inTimeStamp, CAObject.ID(userData));
		CAException.checkError (res);
	}

	/**
	 * Adds a Parameter Event to the track.
	 * <BR><BR><b>CoreAudio::MusicTrackNewParameterEvent</b><BR>
	 * @param inTimeStamp the time (in beats) of the event
	 * @param userData the parameter event
	 */
	public void newParameterEvent (double inTimeStamp, ParameterEvent inEvent) throws CAException {
		int res = MusicTrackNewParameterEvent(_ID(), inTimeStamp, CAObject.ID(inEvent));
		CAException.checkError (res);
	}
	
	public MusicEventIterator events() throws CAException {
		int ptr = 0;
		synchronized (MusicSequence.syncObject) {
			int res = NewMusicEventIterator(_ID(), MusicSequence.firstArg4Ptr);
			CAException.checkError (res);
			ptr = Accessor.getIntFromPointer (MusicSequence.firstArg4Ptr, 0);
		}
		return new MusicEventIterator (ptr);
	}

	/**
	 * Sets the destination AUNode that the events in this track will address.
	 * <BR><BR><b>CoreAudio::MusicTrackSetDestNode</b><BR>
	 * @param node the new node
	 */
	public void setDestNode (AUNode node) throws CAException {
		int err = MusicTrackSetDestNode (_ID(), CAObject.ID(node));
		CAException.checkError(err);
	}

	/**
	 * Sets the destination MIDIEndpoint that the events in this track will address.
	 * <BR><BR><b>CoreAudio::MusicTrackSetDestMIDIEndpoint</b><BR>
	 * @param endPoint the new destination end point
	 */
	public void setDestMIDIEndpoint (MIDIEndpoint endPoint) throws CAException {
		int err = MusicTrackSetDestMIDIEndpoint (_ID(), CAObject.ID(endPoint));
		CAException.checkError(err);
	}

	/**
	 * Returns the AUNode that is the destination node of this track. If the track's
	 * destination is a MIDIEndpoint this method will return null.
	 * <BR><BR><b>CoreAudio::MusicTrackGetDestNode</b><BR>
	 */
	public AUNode getDestNode () throws CAException {
		int ptr = 0;
		synchronized (MusicSequence.syncObject) {
			int res = MusicTrackGetDestNode (_ID(), MusicSequence.firstArg4Ptr);
			if (res == CAErrors.kAudioToolboxErr_IllegalTrackDestination)
				return null;
			CAException.checkError (res);
			ptr = Accessor.getIntFromPointer (MusicSequence.firstArg4Ptr, 0);
		}
		return AUNode.getNode (ptr);
	}
	
	/**
	 * Returns the MIDIEndpoint that is the destination node of this track. If the track's
	 * destination is an AUNode this method will return null.
	 * <BR><BR><b>CoreAudio::MusicTrackGetDestMIDIEndpoint</b><BR>
	 */
	public MIDIEndpoint getDestMIDIEndpoint () throws CAException {
		int ptr = 0;
		synchronized (MusicSequence.syncObject) {
			int res = MusicTrackGetDestMIDIEndpoint (_ID(), MusicSequence.firstArg4Ptr);
			if (res == CAErrors.kAudioToolboxErr_IllegalTrackDestination)
				return null;
			CAException.checkError (res);
			ptr = Accessor.getIntFromPointer (MusicSequence.firstArg4Ptr, 0);
		}
		return JNIToolbox.newMIDIEndpoint (ptr, this);
	}
	
//_ NATIVE METHODS
	private static native int MusicTrackGetSequence(int inTrack, int outSequencePtr);
	private static native int MusicTrackSetDestNode(int inTrack, int inNode);
	private static native int MusicTrackSetProperty(int inTrack,
											int inPropertyID,
											int inDataPtr,
											int inLength);

	private static native int MusicTrackGetProperty(int inTrack,
											int inPropertyID,
											int inDataPtr,
											int inOutLengthPtr);
	private static native int MusicTrackMoveEvents(int inTrack,
											double inStartTime,
											double inEndTime,
											double inMoveTime);


	private static native int NewMusicTrackFrom(int outNewTrackPtr,
										double inSourceStartTime,
										double inSourceEndTime,
										int inSourceTrack);
	private static native int MusicTrackClear(int inTrack,
										double inStartTime,
										double inEndTime);
	private static native int MusicTrackCut(int inTrack,
										double inStartTime,
										double inEndTime);
	private static native int MusicTrackCopyInsert(int inSourceTrack,
											double inSourceStartTime,
											double inSourceEndTime,
											int inDestTrack,
											double inDestInsertTime);
	private static native int MusicTrackMerge(int inSourceTrack,
										double inSourceStartTime,
										double inSourceEndTime,
										int inDestTrack,
										double inDestInsertTime);
	private static native int MusicTrackNewMIDINoteEvent(int inTrack,
													double inTimeStamp,
													int inMessagePtr);						
	private static native int MusicTrackNewMIDIChannelEvent(int inTrack,
													double inTimeStamp,
													int inMessagePtr);
	private static native int MusicTrackNewMIDIRawDataEvent(int inTrack,
													double inTimeStamp,
													int inRawDataPtr);
	private static native int MusicTrackNewExtendedNoteEvent(int inTrack,
													double inTimeStamp,
													int inInfoPtr);//const ExtendedNoteOnEvent	*
	private static native int MusicTrackNewExtendedControlEvent(int inTrack,
														double inTimeStamp,
														int inInfoPtr);//ExtendedControlEvent		
	private static native int MusicTrackNewExtendedTempoEvent(int inTrack,
														double inTimeStamp,
														double inBPM);
	private static native int MusicTrackNewMetaEvent(int inTrack,
													double inTimeStamp,
													int inMetaEventInfoPtr);
	private static native int MusicTrackNewUserEvent(int inTrack,
													double inTimeStamp,
													int inUserDataPtr);
	private static native int MusicTrackNewParameterEvent(int 	inTrack,
                                                   double inTimeStamp,
                                                   int inInfo);

	private static native int NewMusicEventIterator(int inTrack, int outIteratorPtr);

// This overrides any previous MIDIEndpoint destination or AUNode destination
	private static native int MusicTrackSetDestMIDIEndpoint(int inTrack, int inEndpoint);

// returns kAudioToolboxErr_IllegalTrackDestination if the track destination is
// a MIDIEndpointRef and NOT an AUNode
	private static native int MusicTrackGetDestNode (int inTrack, int outNodePtr);

// returns kAudioToolboxErr_IllegalTrackDestination if the track destination is
// an AUNode and NOT a MIDIEndpointRef
	private static native int MusicTrackGetDestMIDIEndpoint (int inTrack, int outEndpointPtr);
}

/*
 */
