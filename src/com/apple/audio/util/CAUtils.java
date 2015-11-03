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
//  CAUtils.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.util;

/**
 * This provides a set of routines to convert between types that the 
 * CoreAudio calls expect and the basic Java classes and types
 */
public final class CAUtils {	
	private CAUtils () {}
	
//__________________ X <=> Fix calls

	/** converts an unsigned byte to int. */
	public static int UByte2Int(byte b) { return (b & 0xff); }
	/** converts an unsigned short to int. */
	public static int UShort2Int(short s) { return (s & 0xffff); }
	/** converts an unsigned int to long. */
	public static long UInt2Long(int i) { return (i & 0xFFFFFFFFL); }	
	
//____________ OSType Conversions
	/** Converts an OSType (4 character) string into an int. */
	public static final int toOSType (String str) {
		while (str.length() < 4)
			str += " ";
		return toOSType(str.charAt(0), str.charAt(1), str.charAt(2), str.charAt(3)); 
	}
	
	/** Converts an OSType (4 character) stored in a MemoryObject into an int.
	public static final int toOSType (MemoryObject mo, int offset) {
		return mo.getIntAt(offset); 
	}*/

	/** Converts four characters into an OSType int */
	public static final int toOSType (char a, char b, char c, char d) {
		return ((a << 24) | ((b & 0xFF) << 16) | ((c & 0xFF) << 8) | (d & 0xFF));
	}
	
	/**
	 * Converts an int into a 4 character string representing an OSType. 
	 * The OSType int is presumed to be the right endian layout.
	 */
	public static String fromOSType (int osType) {
		char val[] = { (char) ((osType & 0xFF000000) >>> 24),
					   (char) ((osType & 0xFF0000) >>> 16),
					   (char) ((osType & 0xFF00) >>> 8),
					   (char) (osType & 0xFF) };
		return new String(val);
	}


//____________ Endian Flipping
	/** This will flip the endian order of a 16bit value
	 * @param s the value to flip
	 * @return the flipped value
	 * @see EndianOrder
	 */
	public static short endianFlip16 (short s) {
		return (short)(((s<<8) & 0xFF00) | ((s>>>8) & 0x00FF));
	}
	
	/** This will flip the endian order of a 32bit value
	 * @param i the value to flip
	 * @return the flipped value
	 * @see EndianOrder
	 */
	public static int endianFlip32 (int i) {
		return (((i<<24) & 0xFF000000) |
			 		((i<< 8) & 0x00FF0000)  |
			 		((i>>> 8) & 0x0000FF00) |
			 		((i>>>24) & 0x000000FF));
	}
	
	/** This will flip the endian order of a 64bit value
	 * @param l the value to flip
	 * @return the flipped value
	 * @see EndianOrder
	 */
	public static long endianFlip64 (long l) {
		return (((l<<56) & 0xFF00000000000000L)  |
				 ((l<<40) & 0x00FF000000000000L)  |
				 ((l<<24) & 0x0000FF0000000000L)  |
				 ((l<< 8) & 0x000000FF00000000L)  |
				 ((l>>> 8) & 0x00000000FF000000L)  |
				 ((l>>>24) & 0x0000000000FF0000L)  |
				 ((l>>>40) & 0x000000000000FF00L)  |
				 ((l>>>56) & 0x00000000000000FFL));
	}
}

/*
 */
