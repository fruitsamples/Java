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
import com.apple.audio.jdirect.Accessor;
/**
 * Represents a single buffer of audio passed to and/or from the IOProc.
 * This structure is passed to an application by the IOProc and 
 * <B>IS ONLY VALID</B> for the duration of that IOProc.execute call.
 * <P>
 * If an application wishes to retain copies or references to the data
 * of a particular IO execution cycle, the user code <B>must</B> copy
 * the data.
 */
public class AudioBufferList extends CAMemoryObject {
			/*
			struct AudioBufferList
			{								offset	size
				UInt32		mNumberBuffers;	0		4
				AudioBuffer	mBuffers[1];	4		...
			};
			*/

	protected AudioBufferList (Object owner, int numBuffers) {
		super (0, 4 + AudioBuffer.kNativeSize, owner);
		if (owner == null)
			throw new CANullPointerException ("Audio Buffer List cannot be unowned");
		this.numBuffers = numBuffers;
		allocateBuffers ();
	}
	
	private AudioBuffer[] cachedBuffers;
	private int numBuffers; // this gets set each IO Proc
	
	private void allocateBuffers () {
		cachedBuffers = new AudioBuffer [numBuffers];
		for (int i = 0; i < numBuffers; i++)
			cachedBuffers[i] = new AudioBuffer(this);
	}	

	protected void setNR (int ptr) {
		if (ptr == 0) {
			setNR (0, 0);
			for (int i = 0; i < numBuffers; i++) {
				cachedBuffers[i].setNR (0);
			}
		} else {
			numBuffers = Accessor.getIntFromPointer (ptr, 0);
			setNR (ptr, numBuffers * AudioBuffer.kNativeSize + 4);
			if (numBuffers > cachedBuffers.length)
				allocateBuffers();
		}
	}
	
	/**
	 * Return the number of buffers in this buffer list.
	 */
	public int getNumberBuffers () {
		return numBuffers;
	}
	
	/**
	 * @return the AudioBuffer at the specified index. This index is <B>ZERO<B> based
	 * @exception CAOutOfBoundsException is thrown if trying to retrieve a buffer not in this list
	 */
	public AudioBuffer getBuffer (int index) throws CAOutOfBoundsException {
		if (index < 0 || index >= getNumberBuffers()) {
			throw new CAOutOfBoundsException ("Can't reach buffer beyond the buffers contained in the list");
		}
		AudioBuffer buf = cachedBuffers[index];
		int baseAddress = _ID();
		baseAddress += 4; //add the offset for the numBuffers field
		baseAddress = baseAddress + (index * AudioBuffer.kNativeSize);
			// shouldn't need to check the address is in range
			//		if (baseAddress > (_ID() + 4 + getNumberOfBuffers() * AudioBuffer.kNativeSize - AudioBuffer.kNativeSize))
		buf.setNR (baseAddress);
		return buf;
	}	
}

/*
 */
