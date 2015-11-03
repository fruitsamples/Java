/*	Copyright: 	© Copyright 2003 Apple Computer, Inc. All rights reserved.

	Disclaimer:	IMPORTANT:  This Apple software is supplied to you by Apple Computer, Inc.
			("Apple") in consideration of your agreement to the following terms, and your
			use, installation, modification or redistribution of this Apple software
			constitutes acceptance of these terms.  If you do not agree with these terms,
			please do not use, install, modify or redistribute this Apple software.

			In consideration of your agreement to abide by the following terms, and subject
			to these terms, Apple grants you a personal, non-exclusive license, under AppleÕs
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
//  AUDispatcher.java
//  CoreAudio.proj
//
//  Copyright (c) 2000 __Apple Computer__. All rights reserved.
//
//		Authors:Bill Stewart
//
package com.apple.audio.units;

import com.apple.audio.*;
import com.apple.audio.util.*;
import com.apple.audio.jdirect.MethodClosure;
import java.util.*;

final class AUDispatcher {
//_________________________ STATIC FIELDS
// this has to be kept as global state because different
// objects can be created for the same underlying audio unit

// the render proc notification is stored where the key->input is -1

	static Hashtable cbTable = new Hashtable();
	// key is the AU
	//			value is hashtable -> key input, value dispatcher

	static final int renderProc = GetRenderCallbackAddress();

	private static final Integer notificationKey = new Integer (-1);
	
	static Hashtable removeRenderNotification (Integer key) 
	{
		Hashtable value = null;
		if (AUDispatcher.cbTable.containsKey(key)) {
			value = (Hashtable)cbTable.get (key);
				// remove old one
			if (value.containsKey(notificationKey)) {
				((AUDispatcher)value.remove(notificationKey)).cleanup();
			}
			return value;
		}
		return null;
	}
	
	static void addRenderNotification (Integer key, Hashtable table, AUDispatcher disp) 
	{
		if (table == null)
			table = new Hashtable();
		table.put (notificationKey, disp);
		cbTable.put (key, table);
	}

/*	-> used in set render callback property
struct AudioUnitInputCallback
{
	AudioUnitRenderCallback		inputProc;
	void *						inputProcRefCon;
};
*/
	static final int kNativeSize_InCB = 8;
		
		// this is used by the AudioUnit to remove callbacks
	static CAMemoryObject clearCBStruct = new CAMemoryObject (kNativeSize_InCB, true);
		
		// is a render CB
	AUDispatcher (AudioUnit u, AURenderCallback cb, boolean isRenderFlag) {
		callback = cb;
		unit = u;
		this.isRender = isRenderFlag;
		cacheBuffer = new AUBuffer (this);
		cacheTimeStamp = new AUTimeStamp (this);
		mMethodClosure = MethodClosure.JNewMethodClosure (this, "renderCallback", "(IIII)I");
		if (isRender) {
			cbStruct = new CAMemoryObject (kNativeSize_InCB, true);
			cbStruct.setIntAt (0, renderProc);
			cbStruct.setIntAt (4, mMethodClosure);
		}
	}
	
		// notification
	AUDispatcher (AudioUnit u, AUPropertyListener list) {
		listener = list;
		unit = u;
		mMethodClosure = MethodClosure.JNewMethodClosure (this, "listenerProc", "(IIII)V");
	}

	private CAMemoryObject cbStruct;
	private int mMethodClosure;
	private boolean isRender = false; //true IFF is a render CB (false if notification)
	
	private AURenderCallback callback;
	private AUPropertyListener listener;
	
	private AudioUnit unit;
	private AUBuffer cacheBuffer;
	private AUTimeStamp cacheTimeStamp;
	
	private int renderCallback (int inActionFlags, int inTimeStampPtr, 
									int inBusNumber, int ioDataPtr) {
			// activate cache objects
		try {
			return callback.execute (unit, 
								inActionFlags, 
								cacheTimeStamp._setNR(inTimeStampPtr),
								inBusNumber,
								(ioDataPtr != 0 ? cacheBuffer._setNR (ioDataPtr) : null));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}

	private void listenerProc (int ci, int inID, int inScope, int inElement) {
		try {
			listener.execute (unit, inID, inScope, inElement);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int ID () {
		if (callback != null) {
			return (isRender ? CAObject.ID(cbStruct) : GetListenerProcAddress());
		} else if (listener != null) {
			return GetListenerProcAddress();
		} else {
			throw new RuntimeException ("Problem with AUDispatcher:");
		}
	}
	
	int refCon () { return mMethodClosure; }
	
	void cleanup () {
		if (callback != null) {
			if (cbStruct != null && mMethodClosure != 0) {
				MethodClosure.JDisposeMethodClosure(mMethodClosure);
				cbStruct = null;
				cacheBuffer = null;
				cacheTimeStamp = null;
				mMethodClosure = 0;
			}
		} else if (listener != null) {
			if (mMethodClosure != 0) {
				MethodClosure.JDisposeMethodClosure(mMethodClosure);
				mMethodClosure = 0;
			}
		}			
	}

	static native int GetRenderCallbackAddress();
	static native int GetListenerProcAddress();
	
// INNER CLASSES	
	static class AUBuffer extends AudioBuffer {
		AUBuffer (Object obj) {
			super (obj);
		}
	
		AUBuffer _setNR (int ptr) {
			super.setNR(ptr);
			return this;
		}	
	}

	static class AUTimeStamp extends AudioTimeStamp {
		AUTimeStamp (Object obj) {
			super (obj);
		}
	
		AUTimeStamp _setNR (int ptr) {
			super.setNR(ptr);
			return this;
		}	
	}
}

/*
 */
