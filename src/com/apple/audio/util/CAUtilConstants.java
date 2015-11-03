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
//  CAUtilConstants.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.util;

/**
 * This class contains constants that are defined in CoreAudioTypes.h
 */
public final class CAUtilConstants {
	private CAUtilConstants () {}
	
		/**
		 * The format can use any sample rate (usually because it does
		 * its own rate conversion). Note that this constant can only
		 * appear in listings of supported descriptions. It should never
		 * appear in the current description as a device must always
		 * have a "current" nominal sample rate.
		 */
	public static final int kAudioStreamAnyRate = 0;

    public static final int kAudioFormatLinearPCM	= 0x6c70636d;  //'lpcm'FOUR_CHAR_CODE('lpcm');


    	/** set for floating point, clear for integer*/
    public static final int kLinearPCMFormatFlagIsFloat			= (1 << 0);	
		/**	set for big endian, clear for little*/
    public static final int kLinearPCMFormatFlagIsBigEndian		= (1 << 1);	
		/**	set for signed integer, clear for unsigned integer; only valid if kLinearPCMFormatFlagIsFloat is clear*/
    public static final int kLinearPCMFormatFlagIsSignedInteger	= (1 << 2);	
		/**	set if the sample bits are packed as closely together as possible, clear if they are high or low aligned within the channel*/
    public static final int kLinearPCMFormatFlagIsPacked		= (1 << 3);	
		/**	set if the sample bits are placed into the high bits of the channel, clear for low bit placement, only valid if kLinearPCMFormatFlagIsPacked is clear*/
    public static final int kLinearPCMFormatFlagIsAlignedHigh	= (1 << 4);		


		/** constants describing SMPTE types (taken from the MTC spec) */
    public static final int
		kSMPTETimeType24		= 0,
		kSMPTETimeType25		= 1,
		kSMPTETimeType30Drop	= 2,
		kSMPTETimeType30		= 3,
		kSMPTETimeType2997		= 4,
		kSMPTETimeType2997Drop	= 5;
		
    	/** flags describing a SMPTE time stamp - the full time is valid*/
    public static final int kSMPTETimeValid		= (1 << 0);	
		/** flags describing a SMPTE time stamp - time is running*/
    public static final int kSMPTETimeRunning	= (1 << 1);	

		/** flags for the AudioTimeStamp sturcture*/
    public static final int
		kAudioTimeStampSampleTimeValid		= (1 << 0),
		kAudioTimeStampHostTimeValid		= (1 << 1),
		kAudioTimeStampRateScalarValid		= (1 << 2),
		kAudioTimeStampWordClockTimeValid	= (1 << 3),
		kAudioTimeStampSMPTETimeValid		= (1 << 4);
}

/*
 */
