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
//  AudioValueRange.java
//  CoreAudio.proj
//
//  Copyright (c) 2001 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.util;

/**	This structure holds a pair of numbers representing a coninuous range
 *	of some value.
 */
public class AudioValueRange extends CAMemoryObject {
/*
struct AudioValueRange {
	Float64	mMinimum;
	Float64	mMaximum;
};
*/
//_________________________ CLASS FIELDS
	/** The native size of this object*/
	public static final int kNativeSize = 16;
	
//_________________________ CLASS METHODS
	/**
	 * Cretes an AudioValueRange with the supplied values
	 */
	public AudioValueRange (double min, double max) {
		super (kNativeSize, false);
		setMinimum (min);
		setMaximum (max);
	}
	
//_________________________ INSTANCE METHODS
	/** Set the minimum value*/
	public void setMinimum (double min) {
		setDoubleAt (0, min);
	}
	
	/** Get the minimum value*/
	public double getMinimum () {
		return getDoubleAt (0);
	}

	/** Set the maximum value*/
	public void setMaximum (double max) {
		setDoubleAt (8, max);
	}
	
	/** Get the maximum value*/
	public double getMaximum () {
		return getDoubleAt (8);
	}
	
	/** Return a String representation of this class */
	public String toString () {
		return getClass().getName()
			+ "[min=" + getMinimum()
			+ ",max=" + getMaximum() + "]";
	}
}

/*
 */
