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
//  AUParameter.java
//  CoreAudio.proj
//
//  Copyright (c) 2002 __Apple Computer__. All rights reserved.
//
//		Authors:Michael Hopkins, Bill Stewart
//
package com.apple.audio.toolbox;

import com.apple.audio.*;
import com.apple.audio.util.*;
import com.apple.audio.units.*;

public class AUParameter extends CAMemoryObject {

/* 
struct AudioUnitParameter 				offset		size
	AudioUnit				mAudioUnit;			0		4
	AudioUnitParameterID	mParameterID;		4		4
	AudioUnitScope			mScope;				8		4
	AudioUnitElement		mElement;			12		4	= 16
} AudioUnitParameter; 
*/

	/** the native size of the memory for this structure*/
	public static final int kNativeSize = 16;
	
	private static Object sRef = new Object();
	
//_________________________ STATIC METHODS
	/**
	 * Create an AudioUnitConnection.
	 * @param sourceAudioUnit the source unit
	 * @param sourceOutputNumber the number of the output of the source unit
	 * @param destInputNumber the number of the input of the dest unit
	 */
	public AUParameter (AudioUnit audioUnit, int paramID, int scope, int element) {
		super (kNativeSize, false);
		setAudioUnit (audioUnit);
		setParameterID (paramID);
		setScope(scope);
		setElement(element);
	}
	
	// for JNI
	private AUParameter (int id) {
		super (id, kNativeSize, sRef);
	}
	
	private AudioUnit mUnit;
	
//_________________________ INSTANCE METHODS
	/** Gets the AudioUnit */
	public AudioUnit getAudioUnit () {
		if (mUnit == null && getIntAt (0) != 0)
			mUnit = JNIToolbox.findClassForComponentInstance (getIntAt(0), this);
		return mUnit;
	}
	
	/** Sets the AudioUnit */
	public void setAudioUnit (AudioUnit audioUnit) {
		setIntAt (0, CAObject.ID(audioUnit));
		mUnit = audioUnit;
	}
	
	/** Gets the parameter ID*/
	public int getParameterID () {
		return getIntAt (4);
	}
	
	/** Sets the parameter ID */
	public void setParameterID (int paramID) {
		setIntAt (4, paramID);
	}

	/** Gets the scope */
	public int getScope () {
		return getIntAt (8);
	}

	/** Sets the scope */
	public void setScope (int scope) {
		setIntAt (8, scope);
	}
	
	/** Gets the element */
	public int getElement () {
		return getIntAt (12);
	}

	/** Sets the scope */
	public void setElement (int element) {
		setIntAt (12, element);
	}

	/** Return a string representation of this class*/
	public String toString () {
		return getClass().getName() +
					"[audioUnit=" + getAudioUnit ()	
					+ ",parameterID=" + getParameterID ()
					+ ",scope=" + getScope ()
					+ ",element=" + getElement() + "]";
	}
}

/*
 */
