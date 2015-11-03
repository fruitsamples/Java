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
//  SMPTETime.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.util;

public class SMPTETime extends CAMemoryObject {
			/*
			struct SMPTETime
			{								offset	size
				UInt64	mCounter;			0		8
				UInt32	mType;				8		4
				UInt32	mFlags;				12		4
				SInt16	mHours;				16		2
				SInt16	mMinutes;			18		2
				SInt16	mSeconds;			20		2
				SInt16	mFrames;			22		2 => 24
			};
			*/
//_________________________ CLASS FIELDS
	/** The size in bytes of objects of this class*/
	public static final int kNativeSize = 24;
	
//_________________________ CLASS METHODS
	/**
	 * Create a SMPTETime object, initializing the object's fields to 0.
	 */
	public SMPTETime () {
		super (kNativeSize, true);
	}

	/**
	 * Create a SMPTETime object 
	 * @param clear if true all the fields are set to zero, otherwise the object is uninitialized
	 */
	public SMPTETime (boolean clear) {
		super (kNativeSize, clear);
	}
	

//_________________________ INSTANCE METHODS
	/**
	 * Set the total number of messages received. Native value is unsigned.
	 */
	public void setCounter (long unsignedValue) {
		setLongAt (0, unsignedValue);
	}

	/**
	 * Get the total number of messages received. Native value is unsigned.
	 */
	public long getCounter () {
		return getLongAt (0);
	}
	
	/**
	 * Set the SMPTE type (see constants)
	 */
	public void setType (int type) {
		setIntAt (8, type);
	}
	
	/**
	 * Get the SMPTE type (see constants)
	 */
	public int getType () {
		return getIntAt (8);
	}
	
	/**
	 * Set the flags indicating state (see constants)
	 */
	public void setFlags (int flags) {
		setIntAt (12, flags);
	}

	/**
	 * Get the flags indicating state (see constants)
	 */
	public int getFlags () {
		return getIntAt (12);
	}

	/**
	 * Set number of hours in the full message
	 */
	public void setHours (int value) {
		setIntAt (16, value);
	}

	/**
	 * Get number of hours in the full message
	 */
	public int getHours () {
		return getIntAt (16);
	}

	/**
	 * Set number of Minutes in the full message
	 */
	public void setMinutes (int value) {
		setIntAt (18, value);
	}

	/**
	 * Get number of Minutes in the full message
	 */
	public int getMinutes () {
		return getIntAt (18);
	}

	/**
	 * Set number of Seconds in the full message
	 */
	public void setSeconds (int value) {
		setIntAt (20, value);
	}

	/**
	 * Get number of Seconds in the full message
	 */
	public int getSeconds () {
		return getIntAt (20);
	}

	/**
	 * Set number of Frames in the full message
	 */
	public void setFrames (int value) {
		setIntAt (22, value);
	}

	/**
	 * Get number of Frames in the full message
	 */
	public int getFrames () {
		return getIntAt (22);
	}

	/**
	 * @return a String representatio of this object
	 */
	public String toString () {
		return getClass().getName()
			+ "[Counter=" + getCounter()
			+ ",Type=" + getType()
			+ ",Flags=" + getFlags()
			+ ",Time=" + getHours() + ":" + getMinutes() + ":" + getSeconds() + ":" + getFrames()
			+ "]";
	}
}

/*
 */
