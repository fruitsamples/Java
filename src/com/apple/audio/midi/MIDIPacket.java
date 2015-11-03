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
//  MIDIPacket.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Jon Summers
//
package com.apple.audio.midi;

import com.apple.audio.util.*;
import com.apple.audio.*;

import com.apple.audio.jdirect.*;

/**
 * Implements the MIDIPacket as defined in MIDIServices.h
 */
/*
 * ISSUES:
 *	1. Future extension to construct from (unimplemted) MIDIData class
 */
public final class MIDIPacket extends CAMemoryObject {
/*! -----------------------------------------------------------------------------
	struct MIDIPacket {								offset	size
    	MIDITimeStamp                   timeStamp;	0		8			// 8 @0		// typedef UInt64  MIDITimeStamp;
    	UInt16                          length;		8		2			// 2 @8 
    	Byte                            data[256];	10		lengthBytes	// length @10 
	};
    @struct         MIDIPacket
    @discussion     One or more MIDI events occuring at a particular time.
    @field          timeStamp
                        The time at which the events occurred, if receiving MIDI,
                        or, if sending MIDI, the time at which the events are to
                        be played.  Zero means "now."
    @field          length      
                        The number of bytes which follow, in data.
    @field          data            
                        A stream of MIDI messages.  Running status is not
                        allowed. A system-exclusive message, or partial
                        system-exclusive message, must be the only MIDI event in
                        the packet. (This is declared to be 256 bytes in length
                        to clients don't have to create custom data structures in
                        simple situations.)
*/
//_________________________ CLASS METHODS

	private static final int kTimeStampFieldAt = 0;
	private static final int kLengthFieldAt = 8;
	private static final int kDataFieldAt = 10;

	MIDIPacket (Object owner) {
		super (0, kDataFieldAt, owner);
		dataObj = new MPMidiData(this);//JNIMidi.newMIDIData (this);
	}

//_________________________ INSTANCE VARIABLES
	private MPMidiData dataObj;
	
//_________________________ INSTANCE METHODS	
 	void initialize (int ptr, int packetSize) {
		setNR (ptr);
		dataObj._initMIDIData (ptr + kDataFieldAt, packetSize, MIDIData.kMIDIPacketData);
	}
		
   /**
	 * Get the size in bytes of the MIDI data.
     * @return The number of bytes of MIDI data
     */
	public int getLength () {
		return getShortAt (kLengthFieldAt);
	}
	
    /**
	 * Get the bytes of the MIDI data. Editing this data will edit the data of this packet in place.
     * @return An array of bytes containing the MIDI data
     */
	public MIDIData getData () {
		return dataObj;
	}
		
    /**
	 * Get the time at which the events occurred.
     * @return time An AbsoluteTime as returned
     */
	public long getTimeStamp () {
		return getLongAt (kTimeStampFieldAt);
	}
	
    /**
	 * Set the time at which the events occurred.
     * @param newTime An AbsoluteTime as returned
     */
	public void setTimeStamp (long newTime) {
		setLongAt (kTimeStampFieldAt, newTime);
	}
	
	static class MPMidiData extends MIDIData {
		MPMidiData (Object owner) {
			super (owner);
		}
		
		void _initMIDIData (int ptr, int size, int mdType) {
			mpInit (ptr, size, mdType);
		}
	}
}

/*
 */
