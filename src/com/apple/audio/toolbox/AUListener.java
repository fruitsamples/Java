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
//  AUListener.java
//  CoreAudio.proj
//
//  Copyright (c) 2002 __Apple Computer__. All rights reserved.
//
//		Authors:Michael Hopkins, Bill Stewart
//
package com.apple.audio.toolbox;

import com.apple.audio.*;
import com.apple.audio.jdirect.Accessor;

/**
 * This is used to both register notifications of changes to AudioUnit
 * parameter values and to change those values so other listeners
 * can receive notifictions of those changes.
 * <P>
 * This is typically used when some kind of UI widget is presenting
 * the value of an AudioUnit parameter and you wish to keep that
 * UI up to date when any other source (say a MusicSequence playback)
 * changes one of those parameters.
 */
public class AUListener extends CAObject {
	static {
		System.load (com.apple.audio.CASession.caBundleName);
	}
	
	private static native int GetJavaVM();
	private static native int GetListenerProcAddress();
	private static native Object CreateGlobalReference (Object obj);
	private static native void RemoveGlobalReference (Object obj);
	
	private static final int sProcAddress = GetListenerProcAddress();
	private static final int sJavaVM = GetJavaVM();
	private static final int firstArg4Ptr = JNIToolbox.malloc(4);

//_________________________ STATIC METHODS
	/**
	 * Create a AUListener that can both issue parameter changes to 
	 * audio units as well as register instances of the AUParameterListener interface
	 * that can respond to changes in audio unit parameters that occur through
	 * other users of these APIs.
	 * <BR><BR><b>CoreAudio::AUListenerCreate</b>
	 * @param inNotificationInterval if there are a number of parameter changes that
	 * effect this listener, this value determines how frequently you are notified
	 * of changes to those parameter values you've declared an interest in receiving.
	 */
	public AUListener (float inNotificationInterval) throws CAException { 
		super (CreateListener(inNotificationInterval), null); 
	}

	private static int CreateListener (float inNotificationInterval) throws CAException {
		int res = AUListenerCreate (sProcAddress, 
							sJavaVM,
							0, // run loop
							0, // run loop mode
							inNotificationInterval, 
							firstArg4Ptr);
		CAException.checkError (res);
		return Accessor.getIntFromPointer (firstArg4Ptr, 0);
	}
	
	public void addListener (AUParameterListener listener, AUParameter param) throws CAException 
	{
// this needs to be properly managed!!!!!
// we need a table of listeners, the global refs that are created for them
// and the number of times they are added (ie. ref count them!)
		Object obj = CreateGlobalReference (listener);
		
		int res = AUListenerAddParameter (_ID(), obj, CAObject.ID(param));
		CAException.checkError (res);
	}

	public void removeListener (AUParameterListener listener, AUParameter param) throws CAException 
	{
//		int res = AUListenerRemoveParameter (_ID(), listener, CAObject.ID(param));
//		CAException.checkError (res);

// this needs to be properly managed!!!!!
//		RemoveGlobalReference (listener->global ref);
	}

	// used when you want to change the specific parameter
	// and notify ANY listeners of this parameter change.
	public static void parameterSet (AUParameter	inParameter,
							float					inValue,
							int						inBufferOffsetInFrames) throws CAException
	{
		int res = AUParameterSet(0,
							null,
							CAObject.ID(inParameter),
							inValue,
							inBufferOffsetInFrames);
		CAException.checkError (res);
	}

	// used when you don't want your AUListener's listeners
	// to receive notification of this parameter change
	public void setParameter (AUParameter		inParameter,
							float				inValue,
							int					inBufferOffsetInFrames) throws CAException
	{
		int res = AUParameterSet(_ID(),
							null,
							CAObject.ID(inParameter),
							inValue,
							inBufferOffsetInFrames);
		CAException.checkError (res);
	}

	// used when you don't want this particular AUListener's listener
	// to receive notification of this parameter change
	public void setParameter (AUParameterListener 	inListener, 
							AUParameter			inParameter,
							float				inValue,
							int					inBufferOffsetInFrames) throws CAException
	{
		int res = AUParameterSet(_ID(),
							inListener,
							CAObject.ID(inParameter),
							inValue,
							inBufferOffsetInFrames);
		CAException.checkError (res);
	}
	
						
	private static native int AUParameterSet(
						int 	inListener,	//AUParameterListenerRef			inListener,
						Object 	inObject, //void *							inObject,
						int 	inParameter,//const AudioUnitParameter *		inParameter,
						float	inValue,
						int		inBufferOffsetInFrames);
	
	private static native int AUListenerCreate (
				int inProc /* AUParameterListenerProc */, 
				int inRefCon /* void* */, 
				int inRunLoop /* CFRunLoopRef */,
				int inRunLoopMode /* CFStringRef */, 
				float inNotificationInterval /* Float32 */, 
				int outListener /* AUParameterListenerRef* */
				); 

	private static native int AUListenerAddParameter (int listenerRef, Object obj, int param);
	private static native int AUListenerRemoveParameter (int listenerRef, Object obj, int param);
}

/*
 * $Log: 
 */
