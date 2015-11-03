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
//  MIDIPacketList.java
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

 // ISSUES:
 //	1. Future construct from array of MIDIPackets
/**
 * Implements the MIDIPacketList as defined in MIDIServices.h. A list of MIDI events being received from, 
 * or being sent to one endpoint.
 */
public final class MIDIPacketList extends CAMemoryObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}
/*! -----------------------------------------------------------------------------
    @struct MIDIPacketList
    @discussion 
    
    @field          numPackets  The number of MIDIPackets in the list.
    @field          packet      An open-ended array of variable-length MIDIPackets.
		struct MIDIPacketList {
		    UInt32                          numPackets;
		    MIDIPacket                      packet[1];
		};
*/
	private static final int kNumPacketsAt = 0;

	private static final int kNumPacketsSize = 4;
	private static final int kPacketsStartAt = 4;
	private static final int kPacketTimeStampSize = 8;
	private static final int kPacketLengthSize = 2;

	private static final int kPacketHeaderSize = kPacketTimeStampSize + kPacketLengthSize; // 10
	private static final int kDefaultDataSize = kPacketHeaderSize + kNumPacketsSize + 256;

	private static final Object owner = new Object();
//_________________________ CLASS METHODS

    /**
     * Create a new MIDIPacketList, with space for 256 bytes of MIDI data
     * constructor allocates memory for 256 bytes of MIDI data
     */
	public MIDIPacketList () {
		this (kDefaultDataSize);	// alloc's space for header and 256 bytes
	}
	
    /**
     * Create a new MIDIPacketList copying the specified MIDIData data.
     * This constructor allocates memory for <B>only</B> the one MIDI data object.
     * @param time the time value associated with this MIDI data
     * @param data The MIDIData
	 */
	public MIDIPacketList (long time, MIDIData data) throws CAException {
		this (kNumPacketsSize + kPacketHeaderSize + data.getMIDIDataLength());
		add (time, data);
	}
	
    /**
     * Create a new MIDIPacketList, with space for specified bytes of MIDI data
     * that will be packed into MIDIPacket structures. This can be longer than you'll 
     * need at any one time, but should be big enough for the maximum amount of data (and
     * its associated headers) that you will expect to add.
     * @param size The number of bytes to be allocated
	 */
	public MIDIPacketList (int dataSize) {
		super (kNumPacketsSize + dataSize, true);	// also alloc's space for header
		init();
		resetPacketArray (16);
	}
	
    /**
     * constructor from native MIDIPacketList
     * - package scoped -
     * @param ptr The native MIDIPacketList pointer
     * @param size_unused Not used (two int params distinguishes from memory allocating ctr)
     * The actual size is calculated from the native object
     * @exception if native object is invalid
     */
	MIDIPacketList (boolean readOnly) {
		super (0, 0, owner); // do _not_ dispose
		this.readOnly = readOnly;
		resetPacketArray (16);
	}
	
//_________________________ INSTANCE VARIABLES
	private int  curPacket;
	private boolean	readOnly;
	private int currentWritePacketIndex = 0;
	private MIDIPacket[] cachedMIDIPacketArray;
	private int numPacketsForReadOnly = 0;

//_________________________ INSTANCE METHODS	
   	/** @return true if the MIDIPacketList is read only, otherwise false*/
   	boolean isReadOnly () {
		return readOnly;
	}
	   	
	/*
     * count number of MIDIPackets in list
     */
	public int numPackets () {
		return getIntAt (kNumPacketsAt);
	}
	
     /**
     * Prepare a MIDIPacketList to be built up dynamically
     * Note MIDIPacketList is created already prepared to add MIDI data.
     * init allows reuse of a packet list.
     * <BR><BR><b>CoreAudio::MIDIPacketListInit()</b><BR><BR>
     */
	public void init () {
		curPacket = MIDIPacketListInit (_ID());
	}
	
   /**
     * Add a MIDI event to a MIDIPacketList. Packet Lists received from a MIDIReadProc are <B>READ ONLY</B>.
     * In that case this call will throw an exception, as the packet list cannot be added too.
     * <BR><BR><b>CoreAudio::MIDIPacketListAdd()</b><BR><BR>
     * @param time  The new event's time.
     * @param data  The new event.  May be a single MIDI event, or a partial sys-ex event.  Running status is <b>not</b> permitted.
     * @exception "out of bounds" if initial size allocation is insufficient
     */
	public void add (long time, MIDIData data) throws CAException {
		if (readOnly) {
			throw new CAException("read only");
		}
		if (curPacket == 0) {
			this.init();
		}
		if (data.getMIDIDataLength() > 65536)
			throw new CAException ("Can't add MIDIData with greater size than 65536");
		int curPkt = MIDIPacketListAdd (_ID(), 
							getSize(), 
							curPacket, 
							time, 
							data.getMIDIDataLength(), 
							(CAObject.ID(data) + data.getMIDIDataOffset()));
		if (curPkt == 0) {
			throw new CAException("out of bounds"); // out of bounds
		}
		curPacket = curPkt;
		int numPackets = numPackets();
		if (numPackets > cachedMIDIPacketArray.length)
			resetPacketArray (numPackets);
	}
				
   /**
     * Get MIDIPacket from list.
     * <BR><BR><b>CoreAudio::MIDIPacketNext()</b><BR><BR>
     * @param index  The zero-based index of the required MIDIPacket
     * @return MIDIPacket instance
     * @exception if index is "out of bounds" if initial size allocation is insufficient
     */
	public MIDIPacket getPacket (int index0) {
		if (readOnly) {
			if (index0 >= numPacketsForReadOnly)
				throw new CAOutOfBoundsException ("Can't Read past the number of packets in this list");
			return cachedMIDIPacketArray[index0];
		} else {
			int	numPackets = numPackets();
			if ( index0 >= numPackets ) {
				throw new CAOutOfBoundsException ("Can't Read past the number of packets in this list");
			}
				
			int offset = kNumPacketsSize;			// point at pkt[0]
			int	packetDataSize = 0;
			for (int pkt = 0; pkt < index0; ++pkt) {
				packetDataSize = Accessor.getShortFromPointer (_ID(), offset + kPacketTimeStampSize);
				offset += kPacketHeaderSize + packetDataSize;
			}
			if (currentWritePacketIndex >= cachedMIDIPacketArray.length)
				currentWritePacketIndex = 0;
			MIDIPacket packet = cachedMIDIPacketArray[currentWritePacketIndex++];
			packet.initialize (_ID() + offset, packetDataSize); //addressOffsetOfPacket
			return packet;
		}
	}
	
    /**
     * helper for constructor from native MIDIPacketList
     * as it requires actual size for bounds checking...
     * @exception if native object is invalid
     */
	void _setNR (int ptr) {
		if (ptr == 0) {
			setNR (0, 0);
			numPacketsForReadOnly = 0;
			return;
		}
		numPacketsForReadOnly = Accessor.getIntFromPointer (ptr, kNumPacketsAt);
		if (numPacketsForReadOnly <= 0) {
			setNR (0, 0);
			numPacketsForReadOnly = 0;
			return;
		}
		if (numPacketsForReadOnly > cachedMIDIPacketArray.length)
			resetPacketArray (numPacketsForReadOnly);
			// OK so now we have the array set up
					
		int packetPtrOffset = kNumPacketsSize;
		MIDIPacket packet = null;
		for (int i = 0; i < numPacketsForReadOnly; ++i) {
			int	packetLen = Accessor.getShortFromPointer (ptr, packetPtrOffset + kPacketTimeStampSize);
			packet = cachedMIDIPacketArray[i];
			packet.initialize (ptr + packetPtrOffset, packetLen);
			packetPtrOffset += kPacketHeaderSize + packetLen;
		}
		setNR (ptr, packetPtrOffset);
	}
	
	private static final int arrayGrowSize = 16;
	private void resetPacketArray (int minPacketsRequired) {
		if (cachedMIDIPacketArray != null) {
			int reqSize = cachedMIDIPacketArray.length + arrayGrowSize;
			if (reqSize < minPacketsRequired)
				reqSize = minPacketsRequired;
			MIDIPacket[] tempArray = new MIDIPacket[reqSize];
			for (int i = 0; i < cachedMIDIPacketArray.length; ++i)
				tempArray[i] = cachedMIDIPacketArray[i];
			for (int i = cachedMIDIPacketArray.length; i < tempArray.length; ++i)
				tempArray[i] = new MIDIPacket (this);
			cachedMIDIPacketArray = tempArray;
		} else {
			cachedMIDIPacketArray = new MIDIPacket[arrayGrowSize];
			for (int i = 0; i < arrayGrowSize; ++i)
				cachedMIDIPacketArray[i] = new MIDIPacket (this);
		}
	}
	
//_ NATIVE METHODS
	// MIDIPacket* MIDIPacketListInit (MIDIPacketList * pktlist);
	private static native int MIDIPacketListInit (int pktlist);
	
	// MIDIPacket* MIDIPacketListAdd (MIDIPacketList * pktlist, ByteCount listSize, MIDIPacket * curPacket, MIDITimeStamp time, ByteCount nData, Byte * data);
	private static native int MIDIPacketListAdd (int pktlist, int listSize, int curPacket, long time, int nData, int dataPtr);

}

/*
 */
