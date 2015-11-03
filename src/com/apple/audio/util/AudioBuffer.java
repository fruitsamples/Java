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
//  HardwareDispatcher.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.util;

import com.apple.audio.*;

/**
 * Represents a single buffer of audio passed to and/or from the IOProc.
 * This structure is passed to an application by the IOProc and 
 * <B>IS ONLY VALID</B> for the duration of that IOProc.execute call.
 * <P>
 * If an application wishes to retain copies or references to the data
 * of a particular IO execution cycle, the user code <B>must</B> copy
 * the data.
 */
public class AudioBuffer extends CAMemoryObject {
		/*
		struct AudioBuffer
		{								offset	size
			UInt32	mNumberChannels;	0		4
			UInt32	mDataByteSize;		4		4
			void*	mData;				8 		4 => 12
		};
		*/
	static final int kNativeSize = 12;
	
//_________________________ CLASS METHODS	
	/**
	 * This creates a buffer of the specified number of channels.
	 * This will also create the data section of this buffer with
	 * the specified number of bytes (and will clear that data). If
	 * this is zero, then no allocation will be done, but consequent
	 * usage of this buffer with AudioUnit.renderSlice may place
	 * data in this buffer that can then be retreived.
	 */
	public AudioBuffer (int mNumberOfChannels, int numBytesToAllocate) {
		super (kNativeSize, true);
		setIntAt (0, mNumberOfChannels);
		if (numBytesToAllocate > 0) {
			mData = new CAMemoryObject (numBytesToAllocate, true);
			setIntAt (8, CAObject.ID(mData));
			setIntAt (4, numBytesToAllocate);
		}
	}
	
	protected AudioBuffer (Object owner) {
		super (0, kNativeSize, owner);
		if (owner == null)
			throw new CANullPointerException ("Audio Buffer cannot be unowned");
		mData = new CAMemoryObject (0, 0, this);
	}
	
//_________________________ INSTANCE FIELDS
	private CAMemoryObject mData = null;
	
//_________________________ INSTANCE METHODS
	/**
	 * Returns the number of channels in this particular buffer of audio.
	 * @return an int
	 */ 
	public int getNumberOfChannels () {
		return getIntAt (0);
	}
		
	/**
	 * Returns the number of channels in this particular buffer of audio.
	 * @return an int
	 */ 
	public CAMemoryObject getData () {
		mData.setNR (getIntAt (8), getIntAt (4));
		return mData;
	}
	
	protected void setNR (int nr) {
		super.setNR (nr, kNativeSize);
	}
}

/*
 */
