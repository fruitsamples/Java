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
//  ExtendedNoteOnEvent.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.toolbox;

import com.apple.audio.util.*;
import com.apple.audio.jdirect.Accessor;

public class ExtendedNoteOnEvent extends CAMemoryObject {
/*
struct ExtendedNoteOnEvent					offset	size
{
	UInt32					instrumentID;	0		4
	UInt32					groupID;		4		4
	Float32					duration;		8		4
	MusicDeviceNoteParams	saslParams;		12		(extParams.length() * 4 + 4) => size
};
*/
	private static int 
		kParamsOffset = 12,
		kParamsArgCountSize = 4,
		kParamsFloatDataOffset = kParamsOffset + kParamsArgCountSize;
	
//_________________________ STATIC METHODS
	/**
	 * 
	 * @param extParams COPIES the data from the extParams to this object
	 */
	public ExtendedNoteOnEvent (int instrumentID, int groupID, float duration, float[] extParams) {
		this (instrumentID, groupID, duration, extParams.length);
		copyFromArray (kParamsOffset + kParamsArgCountSize, extParams, 0, extParams.length);
	}
	
	public ExtendedNoteOnEvent (int instrumentID, int groupID, float duration, ExtendedNoteParams extParams) {
		super (kParamsOffset + extParams.getSize(), false);
		setInstrumentID(instrumentID);
		setGroupID (groupID);
		setDuration (duration);
		setIntAt (kParamsOffset, extParams.length());
		for (int i = 0; i < extParams.length(); i++)
			setFloatAt (kParamsFloatDataOffset + i * 4, extParams.getValueAt (i));
	}
	
	public ExtendedNoteOnEvent (int instrumentID, int groupID, float duration, int numExtParams) {
		super (kParamsOffset + kParamsArgCountSize + numExtParams * 4, true);
		setInstrumentID(instrumentID);
		setGroupID (groupID);
		setDuration (duration);
		setIntAt (kParamsOffset, numExtParams);
	}
	
	public ExtendedNoteOnEvent (int numExtParams) {
		this (0, 0, 0, numExtParams);
	}
	
	ExtendedNoteOnEvent (int ptr, Object owner) {
		super (ptr, determineSize(ptr), owner);
	}
	
	private static int determineSize (int ptr) {
		return (kParamsOffset + kParamsArgCountSize + Accessor.getIntFromPointer (ptr, kParamsOffset) * 4);
	}
	
//_________________________ INSTANCE VARIABLES
	private ExtendedNoteParams params = null;
	
//_________________________ INSTANCE METHODS
	public void setInstrumentID(int instrumentID) {
		setIntAt (0, instrumentID);
	}

	public int getInstrumentID() {
		return getIntAt (0);
	}

	public void setGroupID(int groupID) {
		setIntAt (4, groupID);
	}

	public int getGroupID() {
		return getIntAt (4);
	}

	public void setDuration (float duration) {
		setFloatAt (8, duration);
	}

	public float getDuration() {
		return getFloatAt (8);
	}
	
	/**
	 * This returns this part of this data structure. It does not copy
	 * it so any editing done on this affects this structure. If set
	 * is called then the contents of the
	 */
	public ExtendedNoteParams getExtendedNoteParams () {
		if (params == null)
			params = JNIToolbox.newExtendedNoteParams (_ID() + kParamsOffset, getIntAt (kParamsOffset), this);
		return params;
	}
}

/*
 */
