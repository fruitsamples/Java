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
//  ExtendedNoteParams.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.util;

/**
 * This class is used to create data structures to pass to the MusicDevice.startNote
 * method. It is an array of floats, where each float is nominally referred to as a
 * value. Thus the class provides get/set calls to set the float value
 * in the internal representation, and return the number of float values that are contained.
 * <P>
 * All of the calls on this class are made in terms of indexing into a float array, whereas
 * the super class' methods are still in terms of byte offsets.
 */
public class ExtendedNoteParams extends CAMemoryObject {
	/*
	  UInt32              argCount;
	  float               args[1];
	*/
	private static final int kDataBeginsOffset = 4;
	
//_________________________ STATIC METHODS
	/**
	 * Creates an ExtendedNoteParams to contain <CODE>numFloats</CODE> floats
	 */
	public ExtendedNoteParams (int numFloats) {
		super ((kDataBeginsOffset + (numFloats*4)), true);
		setIntAt (0, numFloats);
	}

	/**
	 * Creates an ExtendedNoteParams and copies the float array into the internal structure.
	 */
	public ExtendedNoteParams (float[] floats) {
		super ((kDataBeginsOffset + (floats.length * 4)), false);
		setIntAt (0, floats.length);
		copyFromArray (kDataBeginsOffset, floats, 0, floats.length);
	}
	
	private ExtendedNoteParams (int ptr, int numFloats, Object owner) {
		super (ptr, numFloats * 4 + 4, owner);
	}
	
//_________________________ INSTANCE METHODS
	
	/**
	 * Gets the float at the specified index of the internal float array
	 */
	public float getValueAt (int index0) {
		return getFloatAt (kDataBeginsOffset + index0 * 4);
	}
	
	/**
	 * Sets the float at the specified index of the internal float array
	 */
	public void setValueAt (int index0, float value) {
		setFloatAt (kDataBeginsOffset + index0 * 4, value);
	}
	
	/**
	 * Copies the length floats from the float array starting at specified offset
	 * to the index position.
	 */
	public void setValuesFrom (int index0, float[] floats, int srcOffset, int length) {
		copyFromArray (kDataBeginsOffset + index0 * 4, floats, srcOffset, length);
	}

	/**
	 * Copies and returns length floats from the specified index of the internal float array
	 */
	public float[] getValuesFrom (int index0, int length) {
		float[] floats = new float[length];
		copyToArray (kDataBeginsOffset + index0 * 4, floats, 0, floats.length);
		return floats;
	}
	
	/**
	 * Returns the length of the internal float array
	 */
	public int length () {
		return getIntAt (0);
	}

	/**
	 * Resets all of the values to 0.
	 */
	public void clear () {
		int len = length();
		for (int i = 0; i < len; i++)
			setIntAt (kDataBeginsOffset + i * 4, 0);
	}

	/** A String representation of the class.
	 * @return a String
	 */
	public String toString () { 
		return super.toString() + "[length=" + length() + "]"; 
	}
}

/*
 */

