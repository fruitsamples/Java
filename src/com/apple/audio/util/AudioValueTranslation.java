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
//  AudioValueTranslation.java
//  CoreAudio.proj
//
//  Copyright (c) 2001 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.util;

import com.apple.audio.CAObject;
/**	This structure holds the buffers necessary for translation operations.
 * It is used with AudioDevice property calls. The usage will determine
 * what the type of the input data object is and the type of the output
 * data. 
 * @see com.apple.audio.AudioDevice 
 */
public class AudioValueTranslation extends CAMemoryObject {
/*
struct AudioValueTranslation
{
	void*	mInputData;
	UInt32	mInputDataSize;
	void*	mOutputData;
	UInt32	mOutputDataSize;
};
*/
//_________________________ CLASS FIELDS
	/** The native size of this Object*/
	public static final int kNativeSize = 16;
	
	public AudioValueTranslation () {
		super (kNativeSize, true);
	}
	
	private CAObject inData, outData;
	
	/** Set the input data as some kind of CoreAudio object. You must specify the size of the object. */
	public void setInputData (CAObject obj, int size) {
		setIntAt (0, CAObject.ID(obj));
		setIntAt (4, size);
		inData = obj;
	}
	
	/** Set the input data as a buffer of "appropriate" memory */
	public void setInputData (CAMemoryObject obj) {
		setInputData (obj, obj.getSize());
	}
	
	/** Set the input data as a 4-byte value*/
	public void setInputData (int value) {
		CAMemoryObject obj = new CAMemoryObject (4, false);
		obj.setIntAt (0, value);
		setInputData (obj);
	}

	/** Set the output data as some kind of CoreAudio object. You must specify the size of the object. */
	public void setOutputData (CAObject obj, int size) {
		setIntAt (8, CAObject.ID(obj));
		setIntAt (12, size);
		outData = obj;
	}
	
	/** Set the output data as a buffer of "appropriate" memory */
	public void setOutputData (CAMemoryObject obj) {
		setOutputData (obj, obj.getSize());
	}
	
	/** Set the output data as a 4-byte value*/
	public void setOutputData (int value) {
		CAMemoryObject obj = new CAMemoryObject (4, false);
		obj.setIntAt (0, value);
		setOutputData (obj);
	}

	/** Return the out data as a CAFString*/
	public CAFString getOutputDataAsCAFString () {
		return new CAFString (getIntAt (8));
	}
	
	/** Return the out data as a java.lang.String*/
	public String getOutputDataAsString () {
		CAMemoryObject obj = new CAMemoryObject (getIntAt(8), getIntAt(12), this);
		return obj.getCStringAt (0);
	}
	
	/** Return a String representation of this class */
	public String toString () {
		return getClass().getName();
	}
}

/*
 */
