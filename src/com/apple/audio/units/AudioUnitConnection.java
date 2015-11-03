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
//  AudioUnitConnection.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.units;

import com.apple.audio.*;
import com.apple.audio.util.*;
/**
 * Used to contain information about the connection of an AudioUnit
 * to some destination. This is used to set the <CODE>kAudioUnitPropertyMakeConnection</CODE>
 * property of an AudioUnit.
 */
public final class AudioUnitConnection extends CAMemoryObject {
/*
	struct AudioUnitConnection { 				offset	size
	  AudioUnit           sourceAudioUnit;		0		4
	  UInt32              sourceOutputNumber;	4		4
	  UInt32              destInputNumber;		8		4 -> 12
	};
*/
	/** the native size of the memory for this structure*/
	public static final int kNativeSize = 12;
	
//_________________________ STATIC METHODS
	/**
	 * Create an AudioUnitConnection.
	 * @param sourceAudioUnit the source unit
	 * @param sourceOutputNumber the number of the output of the source unit
	 * @param destInputNumber the number of the input of the dest unit
	 */
	public AudioUnitConnection (AudioUnit sourceAudioUnit, int sourceOutputNumber, int destInputNumber) {
		super (kNativeSize, false);
		setSourceAudioUnit (sourceAudioUnit);
		setSourceOutputNumber (sourceOutputNumber);
		setDestInputNumber (destInputNumber);
	}
	
	private AudioUnit theUnit;
	
//_________________________ INSTANCE METHODS
	/** Gets the source AudioUnit */
	public AudioUnit getSourceAudioUnit () {
		return theUnit;
	}
	
	/** Sets the source AudioUnit */
	public void setSourceAudioUnit (AudioUnit sourceAudioUnit) {
		setIntAt (0, CAObject.ID(sourceAudioUnit));
		theUnit = sourceAudioUnit;
	}
	
	/** Gets the source output number */
	public int getSourceOutputNumber () {
		return getIntAt (4);
	}
	
	/** Sets the source output number */
	public void setSourceOutputNumber (int sourceOutputNumber) {
		setIntAt (4, sourceOutputNumber);
	}

	/** Gets the destination input number */
	public int getDestInputNumber () {
		return getIntAt (8);
	}

	/** Sets the destination input number */
	public void setDestInputNumber (int destInputNumber) {
		setIntAt (8, destInputNumber);
	}
}
/*
 */
