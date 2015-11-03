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
//  CAFString.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart, Jon Summers
//
package com.apple.audio.util;

import com.apple.audio.*;
import com.apple.audio.jdirect.*;
/**
 * Implements the CoreFoundation CFString for Core Audio usage
 */
public final class CAFString extends CAObject {
//_________________________ INITIALIZATION
	static {
		System.load (CASession.caBundleName);
	}

//_________________________ CLASS FIELDS	
	private static final int kCFAllocatorNull = 0;
	private static final CAMemoryObject tempAllocator = new CAMemoryObject (256, true);
	private static CAMemoryObject range = null;
	
//_________________________ CLASS METHODS
    /**
     * Create a new _immutable_ CAFString
     * <BR><BR><b>CFString::CFStringCreateWithCharacters()</b><BR><BR>
     * 
	 */
	public CAFString (String str) {
		super (allocateNewBuffer (str), null);
	}	
	
	/**
	 * constructor encapsulates a native CoreFoundation CFString object
     * @param ptr native CFString
	 */
	CAFString (int ptr) {
		super (ptr);
	}

	/**
	 *	allocateNewBuffer creates an 'own buffer' native CFString
	 */
	private static synchronized int allocateNewBuffer (String str) {
		char[] ar = str.toCharArray();
		CAMemoryObject tempChars = (ar.length * 2) > tempAllocator.getSize() 
										? new CAMemoryObject (ar.length * 2, false)
										: tempAllocator;
		tempChars.copyFromArray (0, ar, 0, ar.length);
		int id = CFStringCreateWithCharacters (
						CAFString.kCFAllocatorNull, 
						CAObject.ID(tempChars), 
						ar.length);
		return id;
	}	
	
//_________________________ INSTANCE VARIABLES	
	private String strRep;
	
//_________________________ INSTANCE METHODS	
	
	/**
	 * Number of 16-bit Unicode characters in the string
     * <BR><BR><b>CFString::CFStringGetLength()</b><BR><BR>
	 * @return length of CFString
	 */
	public int length () {
		return CFStringGetLength (_ID());
	}

	/**
	 * Return String of the CAFString
	 * @return The java.lang.String representation of the CFString
	 */
	public String asString () {
		if (strRep == null) {
			int len = CFStringGetLength (_ID());
			if (range == null)
				range = new CAMemoryObject (8, false);
			CAMemoryObject strBuffer = new CAMemoryObject (len * 2, false);
			synchronized (range) {
				range.setIntAt (0, 0);
				range.setIntAt (4, len);
				CFStringGetCharacters(_ID(), CAObject.ID(range), CAObject.ID(strBuffer));
			}
			char[]  ar = new char[len];
			strBuffer.copyToArray (0, ar, 0, ar.length);
			strRep = new String(ar);
		}
		return strRep;
	}
	
	/** Return a String representation of this object */
	public String toString () {
		return asString();
	}
	
//_ NATIVE METHODS
	private static native int CFStringCreateWithCharacters (int alloc, int charsPtr, int numChars);
	private static native int CFStringGetLength (int theString);
	private static native void CFStringGetCharacters(int theString, int rangePtr, int bufferPtr);
}

/*
 */
