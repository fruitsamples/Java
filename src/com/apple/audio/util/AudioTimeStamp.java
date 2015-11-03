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
//  AudioTimeStamp.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.util;

import com.apple.audio.*;

public class AudioTimeStamp extends CAMemoryObject {
		/*
		struct AudioTimeStamp
		{										offset	size
			Float64			mSampleTime;		0		8
			UInt64			mHostTime;			8		8
			Float64			mRateScalar;		16		8
			UInt64			mWordClockTime;		24		8
			SMPTETime		mSMPTETime;			32		24
			UInt32			mFlags;				56		4	=> 60
		};
		*/

//_________________________ CLASS FIELDS
	/** The size in bytes of objects of this class*/
	public static final int kNativeSize = 60 + 4; //for pad
	
//_________________________ CLASS METHODS
	/**
	 * Create a AudioTimeStamp object, initializing the object's fields to 0.
	 */
	public AudioTimeStamp () {
		super (kNativeSize, true);
	}

	/**
	 * Create an AudioTimeStamp object
	 * @param clear if true all the fields are set to zero, otherwise the object is uninitialized
	 */
	public AudioTimeStamp (boolean clear) {
		super (kNativeSize, clear);
	}

	/**
	 * Create an AudioTimeStamp object
	 * @param clear if true all the fields are set to zero, otherwise the object is uninitialized
	 */
	protected AudioTimeStamp (Object owner) {
		super (0, kNativeSize, owner);
		if (owner == null)
			throw new CANullPointerException ("Audio Buffer cannot be unowned");
	}
	

//_________________________ INSTANCE METHODS
	/**
	 * Set the absolute sample time
	 */
	public void setSampleTime (double value) {
		setDoubleAt (0, value);
	}
	
	/**
	 * Get the absolute sample time
	 */
	public double getSampleTime () {
		return getDoubleAt (0);
	}

	/**
	 * Set the host's root timebase's time
	 */
	public void setHostTime (long value) {
		setLongAt (8, value);
	}
	
	/**
	 * Get the host's root timebase's time
	 */
	public long getHostTime () {
		return getLongAt (8);
	}

	/**
	 * Set the system rate scalar
	 */
	public void setRateScalar (double value) {
		setDoubleAt (16, value);
	}
	
	/**
	 * Get the system rate scalar
	 */
	public double getRateScalar () {
		return getDoubleAt (16);
	}
	
	/**
	 * Set the word clock time. Native representation is an unsigned UInt64 value
	 */
	public void setWordClockTime (long value) {
		setLongAt (24, value);
	}
	
	/**
	 * Get the word clock time. Native representation is an unsigned UInt64 value
	 */
	public long getWordClockTime () {
		return getLongAt (24);
	}

	/**
	 * Set the SMPTE time
	 */
	public void setSMPTETime (SMPTETime value) {
		copyFromTo (value, 0, this, 32, SMPTETime.kNativeSize);
	}
	
	/**
	 * Get the SMPTE time
	 */
	public SMPTETime getSMPTETime () {
		SMPTETime ret = new SMPTETime(false);
		copyFromTo (this, 32, ret, 0, SMPTETime.kNativeSize);
		return ret;
	}

	/**
	 * Set the flags indicate which fields are valid
	 */
	public void setFlags (int value) {
		setIntAt (56, value);
	}
	
	/**
	 * Get the flags indicate which fields are valid
	 */
	public int getFlags () {
		return getIntAt (56);
	}

	/**
	 * @return a String representatio of this object
	 */
	public String toString () {
		return getClass().getName()
			+ "[SampleTime=" + getSampleTime()
			+ ",HostTime=" + getHostTime()
			+ ",RateScalar=" + getRateScalar()
			+ ",WordClockTime=" + getWordClockTime()
			+ ",Flags=" + getFlags()
			+ "]";
	}
}

/*
 */
