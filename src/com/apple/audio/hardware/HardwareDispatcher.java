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
package com.apple.audio.hardware;

import com.apple.audio.util.*;
import com.apple.audio.*;
import com.apple.audio.jdirect.MethodClosure;

final class HardwareDispatcher {

	HardwareDispatcher (AHardwarePropertyListener listener) {
		mMethodClosure = new MethodClosure (this, "hardwareListenerProc", "(II)I");
		hardwarePropListener = listener;
	}

	HardwareDispatcher (AudioDevice dev, ADevicePropertyListener listener) {
		mMethodClosure = new MethodClosure (this, "deviceListenerProc", "(IIBII)I");
		devicePropListener = listener;
		device = dev;
	}
	
	HardwareDispatcher (AudioStream str, AStreamPropertyListener listener) {
		mMethodClosure = new MethodClosure (this, "streamListenerProc", "(IIII)I");
		streamPropListener = listener;
		stream = str;
	}

	HardwareDispatcher (AudioDevice dev, AudioDeviceIOProc proc, int inNumBuffers, int outNumBuffers) {
		mMethodClosure = new MethodClosure (this, "ioProcFunc", "(IIIIIII)I");
		ioProcCB = proc;
		device = dev;
		cacheInNow = new AHTimeStamp (this);
		cacheInInputTime = new AHTimeStamp (this);
		cacheInOutputTime = new AHTimeStamp (this);
		cacheInBufferList = new AHBufferList (this, inNumBuffers);
		cacheOutBufferList = new AHBufferList (this, outNumBuffers);
	}
	
	private MethodClosure mMethodClosure;

	private AHardwarePropertyListener hardwarePropListener;

	private AudioDevice device;
	private AudioStream stream;
	
	private ADevicePropertyListener devicePropListener;
	private AStreamPropertyListener streamPropListener;
	
	private AudioDeviceIOProc ioProcCB;	
	private AHTimeStamp cacheInNow, cacheInInputTime, cacheInOutputTime;
	private AHBufferList cacheInBufferList, cacheOutBufferList;
/*
typedef OSStatus
(*AudioDeviceIOProc)(	AudioDeviceID			inDevice,
						const AudioTimeStamp*	inNow,
						const AudioBufferList*	inInputData,
						const AudioTimeStamp*	inInputTime,
						AudioBufferList*		outOutputData, 
						const AudioTimeStamp*	inOutputTime,
						void*					inClientData);
*/
	private int ioProcFunc (int inDevice, int inNowPtr, int inInputDataPtr, int inInputTimePtr, int outOutputDataPtr, int inOutputTimePtr, int clientDataPtr) {		
		
		try {
				// activate cache objects
			return ioProcCB.execute (device, 
						cacheInNow._setNR (inNowPtr), 
						(inInputDataPtr != 0 ? cacheInBufferList._setNR (inInputDataPtr) : null),
						(inInputTimePtr != 0 ? cacheInInputTime._setNR (inInputTimePtr) : null), 
						(outOutputDataPtr != 0 ? cacheOutBufferList._setNR (outOutputDataPtr) : null),
						(inOutputTimePtr != 0 ? cacheInOutputTime._setNR (inOutputTimePtr) : null));

		} catch (Exception e) {
			e.printStackTrace();
			try {
				device.stop (ioProcCB);
			} catch (CAException ex) {}
		} 
		return -1;
	}
	
	private int hardwareListenerProc (int inPropertyID, int inClientData) {
		try {
			return hardwarePropListener.execute (inPropertyID);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}

	private int deviceListenerProc (int devPtr, int channel, byte isInput, int inPropertyID, int inClientData) {
		try {
			return devicePropListener.execute (device, channel, (isInput != 0), inPropertyID);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}

	private int streamListenerProc (int inStream, int inChannel, int inPropertyID, int cd) {
		try {
			return streamPropListener.execute (stream, inChannel, inPropertyID);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}
	
	int ID () {
		return mMethodClosure.getClosure();
	}
		
	void cleanup () {
		if (mMethodClosure != null) {
			mMethodClosure.dispose();
			hardwarePropListener = null;
			device = null;
			stream = null;
			devicePropListener = null;
			ioProcCB = null;	
			cacheInNow = null;
			cacheInInputTime = null;
			cacheInOutputTime = null;
			cacheInBufferList = null;
			cacheOutBufferList = null;
			mMethodClosure = null;
		}
	}
	
	AHardwarePropertyListener getAHListener () {
		return hardwarePropListener;
	}
	
	static class AHBufferList extends AudioBufferList {
		AHBufferList (Object obj, int numBuffers) {
			super (obj, numBuffers);
		}
	
		AHBufferList _setNR (int ptr) {
			super.setNR(ptr);
			return this;
		}	
	}

	static class AHTimeStamp extends AudioTimeStamp {
		AHTimeStamp (Object obj) {
			super (obj);
		}
	
		AHTimeStamp _setNR (int ptr) {
			super.setNR(ptr);
			return this;
		}	
	}
}

/*
 */
