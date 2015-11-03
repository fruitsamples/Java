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
//  ATDispatcher.java
//  CoreAudio.proj
//
//  Copyright (c) 2001 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.toolbox;

import com.apple.audio.*;
import com.apple.audio.util.*;
import com.apple.audio.jdirect.*;

final class ATDispatcher {
	
	ATDispatcher (AUGraph graph, AUGraphRenderNotification cb) {
		callback = cb;
		this.graph = graph;
		mMethodClosure = new MethodClosure (this, "renderNotification", "(IIIII)I");
		cacheBuffer = new ATBuffer (this);
		cacheTimeStamp = new ATTimeStamp (this);
	}
	
	ATDispatcher (AudioConverter conv, AudioConverterDataSupplier icb) {
		inputCallback = icb;
		this.conv = conv;
		mMethodClosure = new MethodClosure (this, "converterInputProc", "(IIII)I");
	}
	
	private MethodClosure mMethodClosure;
	
	private AUGraphRenderNotification callback;
	private AUGraph graph;
	private ATBuffer cacheBuffer;
	private ATTimeStamp cacheTimeStamp;
		
		// used to hold returned buffer to save it from being gc'd
	private AudioConverterInputData suppliedData;
	private AudioConverterDataSupplier inputCallback;
	private AudioConverter conv;
	
/*
typedef CALLBACK_API_C
	( OSStatus , AudioUnitRenderCallback )
								(void *inRefCon, 
								AudioUnitRenderActionFlags inActionFlags, 
								const AudioTimeStamp *inTimeStamp, 
								UInt32 inBusNumber, 
								AudioBuffer *ioData);
*/
	private int renderNotification (int inRefCon, int inActionFlags, int inTimeStampPtr, int inBusNumber, int ioDataPtr) {
		try {
				// activate cache objects
			return callback.execute (graph, 
							inActionFlags, 
							cacheTimeStamp._setNR (inTimeStampPtr),
							inBusNumber,
							(ioDataPtr != 0 ? cacheBuffer._setNR (ioDataPtr) : null));
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				graph.removeRenderNotification();
			} catch (CAException helpE) { // help....
			}
		}
		return -1;
	}

/*
typedef OSStatus
(*AudioConverterInputDataProc)(	AudioConverterRef			inAudioConverter,
								UInt32*						ioDataSize,
								void**						outData,
								void*						inUserData);
*/	
	private int converterInputProc (int inAudioConverter, int ioDataSizePtr, int outDataPtrPtr, int userDataPtr) {
		try {
			suppliedData = inputCallback.execute (conv, Accessor.getIntFromPointer(ioDataSizePtr, 0));
			if (suppliedData != null && suppliedData.inputData != null) {
				if (suppliedData.byteOffset < 0
					|| suppliedData.byteOffset + suppliedData.numBytes > suppliedData.inputData.getSize()) {
						new Exception("Out of Bounds Error with AudioConverter.fillBuffer's input Supplier").printStackTrace();
						return CAErrors.paramErr;
				}
				Accessor.setIntInPointer (ioDataSizePtr, 0, suppliedData.numBytes);
				Accessor.setIntInPointer (outDataPtrPtr, 0, (CAObject.ID (suppliedData.inputData) + suppliedData.byteOffset));
			} else {
				Accessor.setIntInPointer (ioDataSizePtr, 0, 0);
				Accessor.setIntInPointer (outDataPtrPtr, 0, 0);
			}
			return 0;
		} catch (CAException e) {
			e.printStackTrace();
			return e.getErrorCode();
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	int ID () {
		return mMethodClosure.getClosure();
	}
		
	void cleanup () {
		if (mMethodClosure != null) {
			mMethodClosure.dispose();
			cacheBuffer = null;
			cacheTimeStamp = null;
			suppliedData = null;
			mMethodClosure = null;
		}
	}
	
	static class ATBuffer extends AudioBuffer {
		ATBuffer (Object obj) {
			super (obj);
		}
	
		ATBuffer _setNR (int ptr) {
			super.setNR(ptr);
			return this;
		}	
	}

	static class ATTimeStamp extends AudioTimeStamp {
		ATTimeStamp (Object obj) {
			super (obj);
		}
	
		ATTimeStamp _setNR (int ptr) {
			super.setNR(ptr);
			return this;
		}	
	}
}

/*
 */
