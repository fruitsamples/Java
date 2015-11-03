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
//  MusicEventIterator.java
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
import com.apple.audio.jdirect.Accessor;

/**
 * Some Notes on using the iterator to move through events 
 * in the track.
<pre>
To use the iterator going forward when looping...
 	New Iterator (points at first event)
 	while (iter.hasCurrentEvent()) {
 		do work with the current event
		
		iter.nextEvent ()
	}
 
Or to go back...
 	iter.seek (kMusicTimeStamp_EndOfTrack) -> will point it past the last event
 	while (iter.hasPreviousEvent()) {
		iter.previousEvent();
 		do work with the current event
 	}
</pre>
 * You can "rock" the Iterator back and forwards as well of course
 */ 
public final class MusicEventIterator extends CAObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}
	
	private static final int firstArg4Ptr = JNIToolbox.malloc(4);
	private static final int secondArg4Ptr = JNIToolbox.malloc(4);
	private static final int thirdArg4Ptr = JNIToolbox.malloc(4);
	private static final int fourthArg8Ptr = JNIToolbox.malloc(8);
	private static final Object syncObject = new Object();
	
//_________________________ STATIC METHODS
	/**
	 * Create a MusicEventIterator object.
	 */
	MusicEventIterator (int ptr) {
		super (ptr);
	}	
	
//_________________________ INSTANCE METHODS
	/**
	 * Moves the iterator to the specified time position. Passing in -1 
	 * for inTimeStamp will position the iterator to the end of track
	 * - (which is pointing to the space just AFTER the last event -
	 * use MusicEventIteratorPreviousEvent() to backup one, if you want the last event)
	 * <BR><BR><b>CoreAudio::MusicEventIteratorSeek</b><BR>
	 * @param inTimeStamp the time that the iterator will be set too.
	 */
	public void seek (double inTimeStamp) throws CAException {
		int res = MusicEventIteratorSeek (_ID(), inTimeStamp);
		CAException.checkError (res);
	}
	
	/**
	 * Returns true if there is an event before the current postion of the iterator.
	 * <BR><BR><b>CoreAudio::MusicEventIteratorHasPreviousEvent</b><BR>
	 * @return a boolean
	 */
	public boolean hasPreviousEvent () throws CAException {
		synchronized (syncObject) {
			int res = MusicEventIteratorHasPreviousEvent(_ID(), firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getByteFromPointer (firstArg4Ptr, 0) != 0;
		}
	}

	/**
	 * Returns true if there is an event after the current postion of the iterator.
	 * <BR><BR><b>CoreAudio::MusicEventIteratorHasNextEvent</b><BR>
	 * @return a boolean
	 */
	public boolean hasNextEvent () throws CAException {
		synchronized (syncObject) {
			int res = MusicEventIteratorHasNextEvent(_ID(), firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getByteFromPointer (firstArg4Ptr, 0) != 0;
		}
	}

	/**
	 * Returns true if there is an event at the current postion of the iterator.
	 * <BR><BR><b>CoreAudio::MusicEventIteratorHasCurrentEvent</b><BR>
	 * @return a boolean
	 */
	public boolean hasCurrentEvent () throws CAException {
		synchronized (syncObject) {
			int res = MusicEventIteratorHasCurrentEvent(_ID(), firstArg4Ptr);
			CAException.checkError (res);
			return Accessor.getByteFromPointer (firstArg4Ptr, 0) != 0;
		}
	}
	
	/**
	 * Moves the iterator to the next event from its current position.
	 * If the iterator is at the last event, then it moves past 
	 * the last event and no longer points to an event. So always 
	 * check to see if there is an event at the iterator
	 * <BR><BR><b>CoreAudio::MusicEventIteratorNextEvent</b><BR>
	 */
	public void nextEvent () throws CAException {
		int res = MusicEventIteratorNextEvent (_ID());
		CAException.checkError (res);
	}

	/**
	 * Moves the iterator to the previous event from its current position.
	 * If the iterator is already at the first event, then it remains 
	 * unchanged and an error is returned.
	 * <BR><BR><b>CoreAudio::MusicEventIteratorPreviousEvent</b><BR>
	 */
	public void previousEvent () throws CAException {
		int res = MusicEventIteratorPreviousEvent (_ID());
		CAException.checkError (res);
	}

  	/**
	 * Sets the time of the event. 
	 * The iterator is moved and will still point to the same event, 
	 * but at the events new time.
	 * <BR><BR><b>CoreAudio::MusicEventIteratorSetEventTime</b><BR>
	 * @param inTimeStamp the time that the current event will be set too.
	 */
	public void setEventTime (double inTimeStamp) throws CAException {
		int res = MusicEventIteratorSetEventTime (_ID(), inTimeStamp);
		CAException.checkError (res);
	}
	
	/**
	 * Deletes the event that the iterator is currently pointing too.
	 * <BR><BR><b>CoreAudio::MusicEventIteratorDeleteEvent</b><BR>
	 */
	public void deleteEvent () throws CAException {
		int res = MusicEventIteratorDeleteEvent (_ID());
		CAException.checkError (res);
	}

	/**
	 * Get the event type at the current iterator position.
	 * <BR><BR><b>CoreAudio::MusicEventIteratorGetEventInfo</b><BR>
	 * @return the event type
	 */
	public int getEventType () throws CAException {
		synchronized (syncObject) {
			int res = MusicEventIteratorGetEventInfo (_ID(),
													0,
													firstArg4Ptr,
													0,
													0);
			CAException.checkError (res);
			return Accessor.getIntFromPointer (firstArg4Ptr, 0);
		}
	}

	/**
	 * Get the event time at the current iterator position.
	 * <BR><BR><b>CoreAudio::MusicEventIteratorGetEventInfo</b><BR>
	 * @return the event time
	 */
	public double getEventTime () throws CAException {
		synchronized (syncObject) {
			int res = MusicEventIteratorGetEventInfo (_ID(),
													fourthArg8Ptr,
													0,
													0,
													0);
			CAException.checkError (res);
			return Accessor.getDoubleFromPointer (fourthArg8Ptr, 0);
		}
	}

	/**
	 * Get the event at the current iterator position.
	 * These events are read only, to modify them 
	 * you should use the other iterator methods
	 * <BR><BR><b>CoreAudio::MusicEventIteratorGetEventInfo</b><BR>
	 * <UL>
 	 * <LI>kMusicEventType_ExtendedNote->ExtendedNoteOnEvent
	 * <LI>kMusicEventType_ExtendedControl->ExtendedControlEvent
	 * <LI>kMusicEventType_ExtendedTempo->ExtendedTempoEvent
	 * <LI>kMusicEventType_User->MusicUserEvent
	 * <LI>kMusicEventType_Meta->MIDIData.kMIDIMetaEvent
	 * <LI>kMusicEventType_MIDINoteMessage->MIDIData.kMIDINoteMessage
	 * <LI>kMusicEventType_MIDIChannelMessage->MIDIData.kMIDIChannelMessage
	 * <LI>kMusicEventType_MIDIRawData->MIDIData.kMIDIRawData
	 * <LI>kMusicEventType_Parameter->ParameterEvent
	 * </UL>
	 * @return the event as a CAMemoryObject - it can be cast to the desired event class
	 */
	public CAMemoryObject getEvent () throws CAException {
		int eventType = 0;
		int eventDataPtr = 0;
		int thirdArgPtr = 0;
		synchronized (syncObject) {
			int res = MusicEventIteratorGetEventInfo (_ID(),
													0,
													firstArg4Ptr,
													secondArg4Ptr,
													thirdArg4Ptr); // need a size argument
			CAException.checkError (res);
			eventType = Accessor.getIntFromPointer (firstArg4Ptr, 0);
			eventDataPtr = Accessor.getIntFromPointer (secondArg4Ptr, 0);
			thirdArgPtr = Accessor.getIntFromPointer (thirdArg4Ptr, 0);
		}
		switch (eventType) {
			case ATConstants.kMusicEventType_MIDINoteMessage:
				return JNIToolbox.newMIDIData (eventDataPtr, 
									MIDIData.kMIDINoteMessage, 
									this, 
									MIDIData.kMIDINoteMessage);
			case ATConstants.kMusicEventType_MIDIChannelMessage:
				return JNIToolbox.newMIDIData (eventDataPtr, 
									MIDIData.kMIDIChannelMessage, 
									this, 
									MIDIData.kMIDIChannelMessage);
			case ATConstants.kMusicEventType_MIDIRawData:
				return JNIToolbox.newMIDIData (eventDataPtr, 
									Accessor.getIntFromPointer(eventDataPtr, 0) + 4, 
									this, 
									MIDIData.kMIDIRawData);
			case ATConstants.kMusicEventType_ExtendedNote:
				return new ExtendedNoteOnEvent (eventDataPtr, this);
			case ATConstants.kMusicEventType_ExtendedControl:
				return new ExtendedControlEvent (eventDataPtr, this);
			case ATConstants.kMusicEventType_ExtendedTempo:
				return new ExtendedTempoEvent (eventDataPtr, this);
			case ATConstants.kMusicEventType_User:
				return JNIToolbox.newMusicUserEvent (eventDataPtr + 4, 
										thirdArgPtr, 
										this);
			case ATConstants.kMusicEventType_Parameter:
				return JNIToolbox.newParameterEvent (eventDataPtr, 
										thirdArgPtr, 
										this);
			case ATConstants.kMusicEventType_Meta:
				return JNIToolbox.newMIDIData (eventDataPtr, 
									Accessor.getIntFromPointer(eventDataPtr, 4) + 8, 
									this, 
									MIDIData.kMIDIMetaEvent);
			default:
				throw new ClassCastException ("Unknown event type:" + CAUtils.fromOSType(eventType));
		}
	}

	/**
	 * Replaces the current event; 
	 * note that its type may change but its time may not
	 * <BR><BR><b>CoreAudio::MusicEventIteratorGetEventInfo</b><BR>
	 */
	public void setEventInfo (int inEventType, CAMemoryObject inEventData) throws CAException {
		int res = MusicEventIteratorSetEventInfo(_ID(),
											inEventType,
 											CAObject.ID(inEventData));
		CAException.checkError(res);
	}

//_ NATIVE METHODS
	private static native int MusicEventIteratorSeek (int inIterator, double inTimeStamp);
	private static native int MusicEventIteratorNextEvent (int inIterator);
	private static native int MusicEventIteratorPreviousEvent (int inIterator);
	private static native int MusicEventIteratorSetEventTime (int inIterator, double inTimeStamp);
	private static native int MusicEventIteratorDeleteEvent (int inIterator);
	private static native int MusicEventIteratorGetEventInfo (int inIterator,
														int outTimeStampPtr,
														int outEventTypePtr,
														int outEventDataPtrPtr,
														int outEventDataSizePtr);
	private static native int MusicEventIteratorHasPreviousEvent(int inIterator,
														int outHasPreviousEventPtr);
	private static native int MusicEventIteratorHasNextEvent(int inIterator,
														int outHasPreviousEventPtr);
	private static native int MusicEventIteratorHasCurrentEvent (int 	inIterator,
 												        int		outHasCurrentEvent);
	private static native int MusicEventIteratorSetEventInfo(int inIterator,
 														int 	inEventType,
 														int 	inEventData );
}

/*
 */
