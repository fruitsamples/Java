/*	Copyright: 	© Copyright 2003 Apple Computer, Inc. All rights reserved.

	Disclaimer:	IMPORTANT:  This Apple software is supplied to you by Apple Computer, Inc.
			("Apple") in consideration of your agreement to the following terms, and your
			use, installation, modification or redistribution of this Apple software
			constitutes acceptance of these terms.  If you do not agree with these terms,
			please do not use, install, modify or redistribute this Apple software.

			In consideration of your agreement to abide by the following terms, and subject
			to these terms, Apple grants you a personal, non-exclusive license, under AppleÕs
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
//  MIDIData.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart, Jon Summers
//
package com.apple.audio.util;

import com.apple.audio.*;

public class MIDIData extends CAMemoryObject {
	private static final int 
		kMIDIRawDataOffset = 4,
		kDataOffsetZero = 0,
		kMIDIMetaEventOffset = 8;
	
	public static final int kMIDIChannelMessage = 4;//this is also the size
	public static final int kMIDINoteMessage = 8;	//this is also the size
	public static final int kMIDIRawData = 3;		//this isn't!!!->variable size
	public static final int kMIDIPacketData = 2;	//nor is this!!!->variable size
	public static final int kMIDIMetaEvent = 1;		//this isn't!!!->variable size
/*
struct MIDINoteMessage
{
	UInt8		channel;
	UInt8		note;
	UInt8		velocity;
	UInt8		unused;
	Float32		duration;
};

struct MIDIChannelMessage
{
	UInt8		status;		// contains message and channel
	
	// message specific data
	UInt8		data1;		
	UInt8		data2;
};

struct MIDIRawData
{
	UInt32		length;
	UInt8		data[1];
};

struct MIDIPacket {								offset	size
	MIDITimeStamp                   timeStamp;	0		8			// 8 @0
	UInt16                          length;		8		2			// 2 @8 
	Byte                            data[256];	10		lengthBytes	// length @10 
};
struct MIDIMetaEvent
{
	UInt8		metaEventType;
	UInt8		pad1;
	UInt8		pad2;
	UInt8		pad3;	
	UInt32		dataLength;
	UInt8		data[1];
};
*/

//_________________________ CLASS METHODS	
	public static MIDIData newMIDIChannelMessage (int status, int data1, int data2) {
		MIDIData mdata = new MIDIData (4, kMIDIChannelMessage);
		mdata.addRawData (status);
		mdata.addRawData (data1);
		mdata.addRawData (data2);
		return mdata;
	}

	public static MIDIData newMIDINoteMessage (int channel, int note, int velocity, float duration) {
		MIDIData mdata = new MIDIData (8, kMIDINoteMessage);
		mdata.addRawData (channel);
		mdata.addRawData (note);
		mdata.addRawData (velocity);
		mdata.setFloatAt (4, duration);
		return mdata;
	}
	
	public static MIDIData newMIDIMetaEvent (int metaEventType, int numMetaBytes) {
		MIDIData mdata = new MIDIData (numMetaBytes + 8, kMIDIMetaEvent);
		mdata.setIntAt (1, numMetaBytes); 
		return mdata;
	}
	
	public static MIDIData newMIDIRawData (int numMIDIBytes) {
		MIDIData mdata = new MIDIData (numMIDIBytes + 4, kMIDIRawData);
		mdata.setIntAt (0, numMIDIBytes);
		return mdata;
	}

	public static MIDIData newMIDIPacketData (int numMIDIBytes) {
		MIDIData mdata = new MIDIData (numMIDIBytes, kMIDIPacketData);
		mdata.setIntAt (0, numMIDIBytes);
		return mdata;
	}
	
		// we're going to add valid data after the constructor
	private MIDIData (int size, int whichOne) {
		super (size, true);
		init (size, whichOne);
	}

			// THERE IS VALID DATA HERE -> used by JNI calls
	private MIDIData (int ptr, int size, Object owner, int whichOne) {
		super (ptr, size, owner);
		init (size, whichOne);
		validDataCounter = midiDataLength;	//valid data has to be accounted for
	}
		
		// used by subclass in MIDIPacket
	protected MIDIData (Object owner) {
		super (0, 0, owner);
	}
		
//_________________________ INSTANCE VARIABLES
	private int validDataCounter;
	private int midiDataLength;
	private int midiDataOffset;
	private int typeFlag;
	
//_________________________ INSTANCE METHODS	
	protected void mpInit (int ptr, int size, int whichOne) {
		setNR (ptr, size);
		init (size, whichOne);
		validDataCounter = midiDataLength;	//valid data has to be accounted for
	}
	
	private void init (int givenSize, int whichOne) {
		switch (whichOne) {
			case kMIDIChannelMessage:
				if (givenSize != 4)
					throw new ClassCastException ("Wrong size for type");
				midiDataLength = 3;
				midiDataOffset = kDataOffsetZero;
				break;
			case kMIDINoteMessage:
				if (givenSize != 8)
					throw new ClassCastException ("Wrong size for type");
				midiDataLength = 3;
				midiDataOffset = kDataOffsetZero;
				break;
			case kMIDIRawData:
				if (givenSize < 4)
					throw new ClassCastException ("Wrong size for type");
				midiDataLength = givenSize - kMIDIRawDataOffset;
				midiDataOffset = kMIDIRawDataOffset;
				break;
			case kMIDIMetaEvent:
				if (givenSize < kMIDIMetaEventOffset)
					throw new ClassCastException ("Wrong size for type");
				midiDataLength = givenSize - kMIDIMetaEventOffset;
				midiDataOffset = kMIDIMetaEventOffset;
				break;
			case kMIDIPacketData:
				if (givenSize > 65536)
					throw new ClassCastException ("Wrong size for type");
				midiDataLength = givenSize;
				midiDataOffset = kDataOffsetZero;
				break;
			default:
				throw new ClassCastException ("Unknown MIDIData class Type");
		}
		validDataCounter = midiDataOffset;
		typeFlag = whichOne;
	}
	
	public boolean isType (int whichType) {
		return typeFlag == whichType;
	}
	
	public void clear () {
		validDataCounter = midiDataOffset;
	}
	
	public int getMIDIDataLength () {
		return midiDataLength;
	}
	
	public int getMIDIDataOffset () {
		return midiDataOffset;
	}
		
	// in progress
	/** @deprecated these should be bytes not ints */ 
	public void addNoteOn (int chan, int noteNum, int vel) {
		addNoteOn ((byte)chan, (byte)noteNum, (byte)vel);
	}
	
	public void addNoteOn (byte chan, byte noteNum, byte vel) {
		if (typeFlag == kMIDINoteMessage) {
			addRawData (chan);
		} else {
			addRawData (0x90 | chan);
		}
		addRawData (noteNum);
		addRawData (vel);
	}
	
	public void setDuration (float dur) {
		if (typeFlag == kMIDINoteMessage) {
			setFloatAt (4, dur);
		} else
			throw new ClassCastException ("Can't add duration information to a non-kMIDINoteMessage type of MIDIData");
	}

	public float getDuration () {
		if (typeFlag == kMIDINoteMessage) {
			return getFloatAt (4);
		} else
			throw new ClassCastException ("Can't get duration information from a non-kMIDINoteMessage type of MIDIData");
	}

	
	/** @deprecated these should be bytes not ints */ 
	public void addNoteOff (int chan, int noteNum, int vel) {
		addNoteOff ((byte)chan, (byte)noteNum, (byte)vel);
	}
	
	public void addNoteOff (byte chan, byte noteNum, byte vel) {
		if (typeFlag == kMIDINoteMessage)
			throw new ClassCastException ("Can't add Anything but Note-On to a kMIDINoteMessage type of MIDIData");
		
		addRawData (0x80 | chan);
		addRawData (noteNum);
		addRawData (vel);
	}
	
	/** @deprecated these should be bytes not ints */ 
	public void addRawData (int data1) {
		addRawData ((byte)data1);
	}
	
	/**
	 * This method is not checked for their validity of data to type - 
	 * use this with care of the type and usage of the MIDIData class
	 */
	public void addRawData (byte data1) {
		checkCanAdd(1);
		setByteAt(validDataCounter, data1);
		++validDataCounter;
	}
	
	/** @deprecated these should be bytes not ints */ 
	public void addRawData (int data1, int data2) {
		addRawData ((byte)data1, (byte)data2);
	}

	/**
	 * This method is not checked for their validity of data to type - 
	 * use this with care of the type and usage of the MIDIData class
	 */
	public void addRawData (byte data1, byte data2) {
		checkCanAdd(2);
		setByteAt(validDataCounter, data1);
		++validDataCounter;
		setByteAt(validDataCounter, data2);
		++validDataCounter;
	}
	
	/** @deprecated these should be bytes not ints */ 
	public void addRawData (int data1, int data2, int data3) {
		addRawData ((byte)data1, (byte)data2, (byte)data3);
	}
	
	/**
	 * This method is not checked for their validity of data to type - 
	 * use this with care of the type and usage of the MIDIData class
	 */
	public void addRawData (byte data1, byte data2, byte data3) {
		checkCanAdd(3);
		setByteAt(validDataCounter, data1);
		++validDataCounter;
		setByteAt(validDataCounter, data2);
		++validDataCounter;
		setByteAt(validDataCounter, data3);
		++validDataCounter;
	}
	
	/** @deprecated these should be bytes not ints */ 
	public void addRawData (int[] data) {
		int len = data.length;
		checkCanAdd(len);
		for (int i = 0; i < len; ++i) {
			setByteAt(validDataCounter, (byte)data[i]);
			++validDataCounter;
		}
	}
	
	/**
	 * This method is not checked for their validity of data to type - 
	 * use this with care of the type and usage of the MIDIData class
	 */
	public void addRawData (byte[] data) {
		int len = data.length;
		checkCanAdd(len);
		copyFromArray (validDataCounter, data, 0, len);
		validDataCounter += len;
	}

	/**
	 * Will add length bytes from the supplied data array, starting at
	 * srcOffset element in the array.
	 */
	public void addRawData (byte[] data, int srcOffset, int len) {
		if (srcOffset + len > data.length)
			throw new CAOutOfBoundsException ("not enough elements in array for specified length and offset");
		checkCanAdd(len);
		copyFromArray (validDataCounter, data, srcOffset, len);
		validDataCounter += len;
	}

 	private final void checkCanAdd (int bytesToAdd) {
		if (bytesToAdd > ((midiDataLength + midiDataOffset) - validDataCounter))
			throw new CAOutOfBoundsException ("can't add data after the valid range of the midi data chunk");
	}
}

/*
 */
 /* 
 
 THESE CALLS ARE FOR MIDI FILE USAGE - PROBABLY NOT NEEDED
	/*
	 * A variable length quantity. Only the first 7 bits of each byte
	 * is significant. If you have a 32-bit value, you have to unpack
	 * it into a series of 7-bit bytes.
	 * The largest variable length quantity allowed is 0x0FFFFFFF
	 *
	public void addVariableLength (int value) {
		long buffer = value & 0x7F;
		while ((value >>= 7) != 0)
		{
			buffer <<= 8;
			buffer |= ((value & 0x7F) | 0x80);
		}
		while (true)
		{
			checkCanAdd (1);
			setByteAt (validDataCounter++, (byte)buffer);
			if ((buffer & 0x80) != 0) {
				buffer >>= 8;
			} else {
				break;
			}
		}
	}

	/*
	 * FF 00 02 ss ss
	 * Sequence Number
	 *
	public void addSequence (int sequence) {
		checkCanAdd (6);
		setByteAt (validDataCounter++, (byte)0xFF);
		setByteAt (validDataCounter++, (byte)0x00);
		setByteAt (validDataCounter++, (byte)0x02);
		setByteAt (validDataCounter++, (byte)((sequence >>= 8) & 0xFF));
		setByteAt (validDataCounter++, (byte)(sequence & 0xFF));
	}
	
	/*
	 * FF 51 03 tt tt tt
	 * Tempo
	 * Indicates a tempo change. The 3 data bytes of tt tt tt are the tempo
	 * in microseconds per quarter note
	 *
	public void addTempo (int tempo) {
		checkCanAdd (6);
		setByteAt (validDataCounter++, (byte)0xFF);
		setByteAt (validDataCounter++, (byte)0x51);
		setByteAt (validDataCounter++, (byte)0x03);
		setByteAt (validDataCounter++, (byte)((tempo >>= 16) & 0xFF));
		setByteAt (validDataCounter++, (byte)((tempo >>= 8) & 0xFF));
		setByteAt (validDataCounter++, (byte)(tempo & 0xFF));
	}
	
	/*
	 * FF 20 01 cc
	 * MIDI Channel
	 *
	public void addMidiChannel (int channel) {
		checkCanAdd (4);
		setByteAt (validDataCounter++, (byte)0xFF);
		setByteAt (validDataCounter++, (byte)0x20);
		setByteAt (validDataCounter++, (byte)0x01);
		setByteAt (validDataCounter++, (byte)channel);
	}
	
	/*
	 * FF 21 01 pp
	 * MIDI Port
	 * The MIDI spec has a limit of 16 MIDI channels per MIDI input/output
	 *
	public void addMidiPort (int port) {
		checkCanAdd (4);
		setByteAt (validDataCounter++, (byte)0xFF);
		setByteAt (validDataCounter++, (byte)0x21);
		setByteAt (validDataCounter++, (byte)0x01);
		setByteAt (validDataCounter++, (byte)(port & 0x0F));
	}
	
	/*
	 * FF 2F 00
	 * End of Track
	 * This event is NOT optional
	 *
	public void addEndOfTrack () {
		checkCanAdd (3);
		setByteAt (validDataCounter++, (byte)0xFF);
		setByteAt (validDataCounter++, (byte)0x2F);
		setByteAt (validDataCounter++, (byte)0x00);
	}
	
	/*
	 * FF type len text
	 *	where type is
	 *		0x01	Text
	 *		0x02	Copyright
	 *		0x03	Sequence/Track Name
	 *		0x04	Instrument
	 *		0x05	Lyric
	 *		0x06	Marker
	 *		0x07	Cue Point
	 * Len could be a series of bytes expressed as a variable length quantity
	 *
	public void addText (int type, String text) {
		int  len = text.length();
		checkCanAdd (2);
		setByteAt(validDataCounter++, (byte)0xFF);
		setByteAt (validDataCounter++, (byte)type);
		addVariableLength (len);
		checkCanAdd (len);
		for (int i = 0; i < len; ++i) {
			setByteAt (validDataCounter++, (byte)text.charAt (i));
		}	
		
	}
*/
