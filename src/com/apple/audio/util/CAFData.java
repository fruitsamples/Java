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
//  CAFData.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Jon Summers
//
package com.apple.audio.util;

import com.apple.audio.*;
import com.apple.audio.jdirect.*;
import com.apple.audio.jdirect.Accessor;

/**
 * Implements the CoreFoundation CFData for MIDIServices.
 * These objects are not created by hand by the application, but are 
 * returned from MIDIObjects and MIDIClient as a means of making their state
 * persistent. The application can retrieve that state, save it to file, etc,.
 * then recreate the state by setting the property later.
 * <P>
 * The byte[] constructor is provided as a means of reconstructing a CAFData object
 * from one that was previously obtained and persisted.
 */
public final class CAFData extends CAObject {

	static {
		System.load (CASession.caBundleName);
	}
		
//_________________________ CLASS FIELDS	
	private static final int	kCFAllocatorNull = 0;

//_________________________ CLASS METHODS
	/**
	 * constructor creates a native CoreFoundation CFData object
     * <BR><BR><b>CoreFoundation::CFDataCreate()</b><BR><BR>
     * @param data 
	 */
	public CAFData (byte[] data) {
		super (allocate(data), null);	// native CFData object released in CASession.remove 
	}	
		
	/**
	 * constructor encapsulates a native CoreFoundation CFData object
     * @param ptr native CFData object
     * @param owner The owning object. If non-null, native CFData object is not to be released
	 */
	private CAFData (int ptr) {
		super (ptr, null);
	}
	
	private static int allocate (byte[] data) {
		CAMemoryObject mem = new CAMemoryObject (data.length, true);
		mem.copyFromArray (0, data, 0, data.length);
		return CFDataCreate (CAFData.kCFAllocatorNull, CAObject.ID(mem), mem.getSize());
	}
	
//_________________________ INSTANCE METHODS
	/**
	 * Number of bytes of data
     * <BR><BR><b>CoreFoundation::CFDataGetLength()</b><BR><BR>
	 */
	public int length() {
		return CFDataGetLength (_ID());
	}

	/**
	 * Returns a range of data into a byte array.
     * <BR><BR><b>CoreFoundation::CFDataGetBytes()</b><BR><BR>
	 * @param srcOffset
	 * @param numBytes
	 * @return the copied data
	 */
	public byte[] toArray (int srcOffset, int numBytes) {
		int size =  CFDataGetLength (_ID());
		if ((srcOffset < 0) || ((srcOffset + numBytes) > size)) {
				throw new CAOutOfBoundsException ("Can't read passed end of CFData");
		}
		CAMemoryObject range = new CAMemoryObject (8, false);
		range.setIntAt (0, srcOffset);
		range.setIntAt (4, numBytes);
		CAMemoryObject tempMem = new CAMemoryObject (numBytes, false);
		CFDataGetBytes (_ID(), CAObject.ID(range), CAObject.ID(tempMem));
		byte[] ar = new byte[numBytes];
		tempMem.copyToArray (0, ar, 0, numBytes);
		return ar;
	}

//_ NATIVE METHODS
	private static native int CFDataCreate (int allocator, int bytesPtr, int length);
	private static native int CFDataGetLength (int cfData);
	private static native void CFDataGetBytes(int theData, int range, int bufferPtr); 
}

/*
 */
