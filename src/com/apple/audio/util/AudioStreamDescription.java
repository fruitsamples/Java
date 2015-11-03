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
//  AudioStreamBasicDescription.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.util;

/**
 * This structure encapsulates all the information for describing
 * the basic properties of a stream of audio data. This structure
 * is sufficient to describe any constant bit rate format that 
 * has chanels that are the same size. Extensions are required
 * for variable bit rate data and for constant bit rate data where
 * the channels have unequal sizes. However, where applicable,
 * the appropriate fields will be filled out correctly for these
 * kinds of formats (the extra data is provided via separate
 * properties). In all fields, a value of 0 indicates that the
 * field is either unknown, not applicable or otherwise is
 * inapproprate for the format and should be ignored.
 * <P>
 * The extended description data, if applicable, is avaiable via
 * a property with the same ID as the format ID. The contents of
 * the data is specific to the format.
 */
public class AudioStreamDescription extends CAMemoryObject { 
		/*
		struct AudioStreamBasicDescription
		{								offset	size
			Float64	mSampleRate;		0		8
			UInt32	mFormatID;			8		4
			UInt32	mFormatFlags;		12		4
			UInt32	mBytesPerPacket;	16		4
			UInt32	mFramesPerPacket;	20		4
			UInt32	mBytesPerFrame;		24		4
			UInt32	mChannelsPerFrame;	28		4
			UInt32	mBitsPerChannel;	32		4 => 36 + 4 for a pad
		};
		*/
//_________________________ CLASS FIELDS
	/** The size in bytes of objects of this class*/
	public static final int kNativeSize = 36 + 4; //pad

//_________________________ CLASS METHODS
	/**
	 * Create a AudioStreamDescription object, initializing the object's fields to 0.
	 */
	public AudioStreamDescription () {
		super (kNativeSize, true);
	}

	/**
	 * Create an AudioStreamDescription object
	 * @param clear if true all the fields are set to zero, otherwise the object is uninitialized
	 */
	public AudioStreamDescription (boolean clear) {
		super (kNativeSize, clear);
	}

//_________________________ INSTANCE METHODS
	/**
	 * Set the native sample rate of the audio stream
	 */
	public void setSampleRate (double value) {
		setDoubleAt (0, value);
	}
	
	/**
	 * Get the native sample rate of the audio stream
	 */
	public double getSampleRate () {
		return getDoubleAt (0);
	}


	/**
	 * Set the specific encoding type of audio stream
	 */
	public void setFormatID (int value) {
		setIntAt (8, value);
	}
	
	/**
	 * Get the specific encoding type of audio stream
	 */
	public int getFormatID () {
		return getIntAt (8);
	}

	/**
	 * Set the flags specific to each format
	 */
	public void setFormatFlags (int value) {
		setIntAt (12, value);
	}
	
	/**
	 * Get the flags specific to each format
	 */
	public int getFormatFlags () {
		return getIntAt (12);
	}

	/**
	 * Set the number of bytes in a packet
	 */
	public void setBytesPerPacket (int value) {
		setIntAt (16, value);
	}
	
	/**
	 * Get the number of bytes in a packet
	 */
	public int getBytesPerPacket () {
		return getIntAt (16);
	}

	/**
	 * Set the number of frames in each packet
	 */
	public void setFramesPerPacket (int value) {
		setIntAt (20, value);
	}
	
	/**
	 * Get the number of frames in each packet
	 */
	public int getFramesPerPacket () {
		return getIntAt (20);
	}

	/**
	 * Set the number of bytes in a frame
	 */
	public void setBytesPerFrame (int value) {
		setIntAt (24, value);
	}
	
	/**
	 * Get the number of bytes in a frame
	 */
	public int getBytesPerFrame () {
		return getIntAt (24);
	}

	/**
	 * Set the number of channels in each frame
	 */
	public void setChannelsPerFrame (int value) {
		setIntAt (28, value);
	}
	
	/**
	 * Get the number of channels in each frame
	 */
	public int getChannelsPerFrame () {
		return getIntAt (28);
	}

	/**
	 * Set the number of bits in each channel
	 */
	public void setBitsPerChannel (int value) {
		setIntAt (32, value);
	}
	
	/**
	 * Get the number of bits in each channel
	 */
	public int getBitsPerChannel () {
		return getIntAt (32);
	}
	
	/**
	 * @return a String representatio of this object
	 */
	public String toString () {
		return getClass().getName()
			+ "[SampleRate=" + getSampleRate()
			+ ",FormatID=" + CAUtils.fromOSType(getFormatID())
			+ ",FormatFlags=0x" + Integer.toHexString(getFormatFlags())
			+ ",BytesPerPacket=" + getBytesPerPacket()
			+ ",FramesPerPacket=" + getFramesPerPacket()
			+ ",BytesPerFrame=" + getBytesPerFrame()
			+ ",ChannelsPerFrame=" + getChannelsPerFrame()
			+ ",BitsPerChannel=" + getBitsPerChannel() 
			+ "]";
	}
}

/*
 */
