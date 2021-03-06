/*	Copyright: 	� Copyright 2003 Apple Computer, Inc. All rights reserved.

	Disclaimer:	IMPORTANT:  This Apple software is supplied to you by Apple Computer, Inc.
			("Apple") in consideration of your agreement to the following terms, and your
			use, installation, modification or redistribution of this Apple software
			constitutes acceptance of these terms.  If you do not agree with these terms,
			please do not use, install, modify or redistribute this Apple software.

			In consideration of your agreement to abide by the following terms, and subject
			to these terms, Apple grants you a personal, non-exclusive license, under Apple�s
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
//  AUGraphRenderNotification.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.toolbox;

import com.apple.audio.*;
import com.apple.audio.util.*;
/**
 * This is a callback that an app can register with an AUGraph to  
 * gain notifications before and after a renderSlice is called on an the graph
 * <P>   
 */
public interface AUGraphRenderNotification {
	/** 
	 * When the AUGraph is to render a slice of audio data this callback is called.
	 * The AUGraph calls this for each of its' AudioOutputUnit's inputs. After rendering
	 * the inBusNumber will identifier which input of the graph's head (its AudioOutputUnit)
	 * it is notifying you of, and the resultant buffer of audio data from that render slice.
	 * <BR><BR><b>CoreAudio::AUGraphSetRenderNotification</b><BR>
	 * @param renderGraph the AUGraph that is currently doing the rendering
	 * @param inActionFlags flags that instruct the unit on how to act on the buffer
	 * @param inTimeStamp the time for the start of the rendered audio output.
	 * @param inBusNumber the bus number for the audio rendered
	 * @param inData the buffer of data that was rendered
	 */
	public int execute (AUGraph renderGraph, 
									int inActionFlags, 
									AudioTimeStamp inTimeStamp, 
									int inBusNumber, 
									AudioBuffer inData);
}

/*
 */
