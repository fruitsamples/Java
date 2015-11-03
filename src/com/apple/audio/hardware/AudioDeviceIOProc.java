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
//  AudioHardware.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.hardware;

import com.apple.audio.util.*;
/**
 *	AudioDeviceIOProc
 *
 *	This is a client supplied routine that the hardware calls to do an
 *	IO transaction for a given device. All input and output is presented
 *	to the client simultaneously for processing. The inNowSamples parameter
 *	is the sample time that should be used as the basis of now rather than
 *	what might be provided by a query to the device's clock. This is necessary
 *	because time will continue to advance while this routine is executing
 *	making retrieving the current time from the appropriate parameter
 *	unreliable for synch operations. The time stamp for theInputData represents
 *	when the data was recorded. For the output, the time stamp represents
 *	when the first sample will be played. In all cases, each time stamp is
 *	accompanied by its mapping into host time.
 * <P>
 *	The format of the actual data depends of the sample format
 *	of the device as specified by its properties. It may be raw or compressed,
 *	interleaved or not interleaved as determined by the requirements of the
 *	device and its settings.
 * <P>
 *	If the data for either the input or the output is invalid, the time stamp
 *	will have a value of 0. This happens when a device doesn't have any inputs
 *	or outputs. It will also happen the very first time an IOProc is called
 *	after starting the device if the device has both inputs and outputs. The
 *	reason for the latter behavior is that no input data is available when
 *	the device first starts, but the output still needs to be primed. It
 *	should otherwise never happen.
 */
public interface AudioDeviceIOProc {
	/**
	 * The execute method is called by the AudioHardware lib for each specified IO cycle of the supplied AudioDevice.
	 * <P>
	 * The objects that are passed in to each execution are <B>ONLY VALID</B> for the particular duration of the
	 * execution. Applications cannot cache these objects.
	 * @param device the device executing this IO cycle
	 * @param inNow the time "Now" - when the IO proc was called
	 * @param inInputData the list of buffers delivered by the input service of the device
	 * @param inInputTime the time when the first data of the input data arrived
	 * @param outOutputData the data to be deliverd to the output service of the device
	 * @param inOutputTime the time when the first data of of the output data should be scheduled
	 */
	public int execute (AudioDevice device, AudioTimeStamp inNow, AudioBufferList inInputData, AudioTimeStamp inInputTime, AudioBufferList outOutputData, AudioTimeStamp inOutputTime);
}
/*
 */
