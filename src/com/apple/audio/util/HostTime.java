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
//  HostTime.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.util;

import com.apple.audio.jdirect.*;
/**
 * This class contains methods to deal with the time
 * services and conversion methods.
 * <P>
 * The long values that are taken or returned by these calls
 * are unsigned values in their native representation. Thus
 * any negative values will be interpreted as very large long
 * numbers. The facilities of the BigDecimal class can be used
 * to determine their unsigned value if required.
 */
public final class HostTime {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}		

//_________________________ CLASS METHODS
	private HostTime () {}
	
	/**
	 * Retrieve the current host time value.
	 */
	public static long getCurrentHostTime () {
		return AudioGetCurrentHostTime();
	}
		
	/**
	 * Retrieve the number of ticks per second of the host clock.
	 */
	public static double getHostClockFrequency () {
		return AudioGetHostClockFrequency();
	}

	/**
	 * Retrieve the smallest number of ticks difference between two succeeding
	 * values of the host clock. For instance, if this value is 5 and
	 * the first value of the host clock is X then the next time after X
	 * will be at greater than or equal X+5.
	 */
	public static int getHostClockMinimumTimeDelta () {
		return AudioGetHostClockMinimumTimeDelta();
	}

	/**
	 * Convert the given host time to a time in Nanoseconds.
	 */
	public static long convertHostTimeToNanos (long inHostTime) {
		return AudioConvertHostTimeToNanos(inHostTime);
	}
	
	/**
	 * Convert the given Nanoseconds time to a time in the host clock's
	 * time base.
	 */	
	public static long convertNanosToHostTime (long inNanos) {
		return AudioConvertNanosToHostTime(inNanos);
	}
	
//_ NATIVE METHODS
	private static native long AudioConvertHostTimeToNanos(long inHostTime);
	private static native long AudioConvertNanosToHostTime(long inNanos);
	private static native int AudioGetHostClockMinimumTimeDelta();
	private static native double AudioGetHostClockFrequency();
	private static native long AudioGetCurrentHostTime();
}
/*
 */
